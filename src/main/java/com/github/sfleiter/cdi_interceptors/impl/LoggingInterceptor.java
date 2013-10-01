package com.github.sfleiter.cdi_interceptors.impl;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Iterator;

import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

import uk.org.lidalia.slf4jext.Level;
import uk.org.lidalia.slf4jext.Logger;
import uk.org.lidalia.slf4jext.LoggerFactory;

import com.github.sfleiter.cdi_interceptors.api.Logging;
import com.thoughtworks.paranamer.BytecodeReadingParanamer;
import com.thoughtworks.paranamer.CachingParanamer;
import com.thoughtworks.paranamer.Paranamer;

/**
 * The Class LoggingInterceptor add a special kind of logging advice to classes / methods.
 * Calls with parameter names, results and exceptions are logged in a single line.
 * A {@link BytecodeReadingParanamer} is use to detect parameter names.
 * If paranamer does not deliver names, a default of arg0...n is used.
 */
@Interceptor
@Logging
public class LoggingInterceptor {

    /** The paranamer used to get parameter names. */
    Paranamer paranamer = new CachingParanamer(new BytecodeReadingParanamer());

    /**
     * Logging method invoke.
     *
     * @param ctx the invocation context
     * @return the result object
     */
    @AroundInvoke
    public Object loggingMethodInvoke(InvocationContext ctx) throws Exception {
        Logger logger = LoggerFactory.getLogger(ctx.getTarget().getClass().getName());
        Logging annotation = getAnnotation(ctx.getMethod(), Logging.class);
        Level level = annotation.level();
        int maximumCount = annotation.maximumIterableLoggingCount();
        boolean measureDuration = annotation.measureDuration();

        StringBuilder sb;
        long start = currentTimeMillis(measureDuration);
        long duration;
        Object result;
        try {
            result = ctx.proceed();
            if (logger.isEnabled(level)) {
                duration = currentTimeMillis(measureDuration) - start;
                sb = getCallString(ctx, maximumCount);
                sb.append(" returns ");
                prettyPrint(sb, result, maximumCount);
                appendDuration(sb, measureDuration, duration);
                logger.log(level, sb.toString());
            }
        } catch (Exception e) {
            if (e instanceof RuntimeException) {
                // TODO distinguish better here for ApplicationException f.e.
                //      Maybe a strategy to get Level for an Exception?
                level = annotation.exceptionLevel(); 
            }
            if (logger.isEnabled(level)) {
                duration = currentTimeMillis(measureDuration) - start;
                sb = getCallString(ctx, maximumCount);
                sb.append(" caused ");
                sb.append(e.getMessage());
                appendDuration(sb, measureDuration, duration);
                logger.log(level, sb.toString(), e);
            }
            throw e;
        }
        return result;
    }

    private long currentTimeMillis(boolean measureDuration) {
        return measureDuration ? System.currentTimeMillis() : 0;
    }

    private <T extends Annotation> T getAnnotation(Method m, Class<T> clz) {
        // first look at method-level annotations, since they take priority
        for (Annotation a : m.getAnnotations()) {
            if (clz.isInstance(a)) {
                return clz.cast(a);
            }
        }
        for (Annotation a : m.getDeclaringClass().getAnnotations()) {
            if (clz.isInstance(a)) {
                return clz.cast(a);
            }
        }
        throw new RuntimeException("@" + clz.getName() + " not found on method " + m.getName() + " or its class "
                + m.getClass().getName());
    }

    private StringBuilder getCallString(InvocationContext ctx, int maximumCount) {
        String method = ctx.getMethod().getName();
        String[] parameterNames = paranamer.lookupParameterNames(ctx.getMethod(), false);
        StringBuilder sb = new StringBuilder();
        sb.append("call ");
        sb.append(method);
        sb.append("(");
        Object[] parameters = ctx.getParameters();
        for (int i = 0; i < parameters.length; i++) {
            // if paranamer has found a name, then use it
            if (parameterNames != null && parameterNames.length > i) {
                sb.append(parameterNames[i]);
            } else {
                // render a generic name
                sb.append("arg");
                sb.append(i);
            }
            sb.append("=");
            if (parameters[i] != null) {
                prettyPrint(sb, parameters[i], maximumCount);
            }
            if (i < parameterNames.length - 1)
                sb.append(", ");
        }
        sb.append(")");
        return sb;
    }

    private void appendDuration(StringBuilder sb, boolean measureDuration, long duration) {
        if (measureDuration) {
            sb.append(" [duration=");
            sb.append(duration);
            sb.append("ms]");
        }
    }

    @SuppressWarnings("rawtypes")
    private void prettyPrint(StringBuilder sb, Object o, int maximumCount) {
        // TODO pretty print and limit Iterables, too
        Iterator iterator;
        int size = 0;
        if (o == null) {
            sb.append("null");
            return;
        } else if (o instanceof Collection) {
            Collection c = (Collection) o;
            size = c.size();
            iterator = c.iterator();
            // fall through
        } else if (o.getClass().isArray()) {
            // TODO support primitive arrays
            // class.getComponentType?
            Object[] array = (Object[]) o;
            size = array.length;
            iterator = new ArrayIterator(array);
            // fall through
        } else {
            // no array or collection
            sb.append(o.toString());
            return;
        }
        
        // array or collection
        sb.append("Collection[size=").append(size).append(", ");
        int i = 1;
        while (i < maximumCount && iterator.hasNext()) {
            Object elem = iterator.next();
            sb.append(toString(elem));
            if (iterator.hasNext()) {
                sb.append(", ");
            }
        }
        sb.append("]");
    }

    private String toString(Object object) {
        return object != null ? object.toString() : "null";
    }

}
