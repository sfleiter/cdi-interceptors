package com.github.sfleiter.cdi_interceptors;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

import uk.org.lidalia.slf4jext.Level;
import uk.org.lidalia.slf4jext.Logger;
import uk.org.lidalia.slf4jext.LoggerFactory;

import com.github.sfleiter.cdi_interceptors.impl.StringTransformer;
import com.thoughtworks.paranamer.AdaptiveParanamer;
import com.thoughtworks.paranamer.BytecodeReadingParanamer;
import com.thoughtworks.paranamer.CachingParanamer;
import com.thoughtworks.paranamer.Paranamer;
import com.thoughtworks.paranamer.PositionalParanamer;

/**
 * The Class LoggingInterceptor add a special kind of logging advice to classes
 * / methods. Calls with parameter names, results and exceptions are logged in a
 * single line.
 *
 * A {@link BytecodeReadingParanamer} is use to detect parameter names. If
 * paranamer does not deliver names, a default of arg0...n is used.
 *
 * @see Logging
 */
@Interceptor
@Logging
public class LoggingInterceptor {

    /** The paranamer used to get parameter names. */
    private final Paranamer paranamer = new CachingParanamer(
            new AdaptiveParanamer(
                    new BytecodeReadingParanamer(),
                    new PositionalParanamer()
                    ));

    @Inject
    private StringTransformer stringTransformer;

    /**
     * Logging method invoke.
     *
     * @param ctx
     *            the invocation context
     * @return the result object
     * @throws Exception
     *             only rethrows original exception from intercepted method call
     */
    @AroundInvoke
    public Object loggingMethodInvoke(final InvocationContext ctx) throws Exception {
        Logger logger = LoggerFactory.getLogger(ctx.getTarget().getClass()
                .getName());
        Logging annotation = getAnnotation(ctx.getMethod(), Logging.class);
        Level level;
        int maximumCount = annotation.logItemLimit();
        boolean measureDuration = annotation.measureDuration();

        StringBuilder sb;
        long start = currentTimeMillis(measureDuration);
        long duration;
        Object result;
        try {
            result = ctx.proceed();
            level = annotation.standardLogLevel();
            if (logger.isEnabled(level)) {
                duration = currentTimeMillis(measureDuration) - start;
                sb = getCallString(ctx, maximumCount);
                sb.append(" returns ");
                stringTransformer.transform(sb, result, maximumCount);
                appendDuration(sb, measureDuration, duration);
                logger.log(level, sb.toString());
            }
        } catch (Exception e) {
            boolean isSevere = isExceptionSevere(e, annotation);
            boolean logStackTrace = true;
            if (isSevere) {
                level = annotation.severeExceptionLogLevel();
            } else {
                level = annotation.standardLogLevel();
                if (!annotation.logStackTraceAtStandardLevel()) {
                    logStackTrace = false;
                }
            }
            if (logger.isEnabled(level)) {
                duration = currentTimeMillis(measureDuration) - start;
                sb = getCallString(ctx, maximumCount);
                sb.append(" caused {}");
                appendDuration(sb, measureDuration, duration);
                if (logStackTrace) {
                    logger.log(level, sb.toString(), e, e);
                } else {
                    logger.log(level, sb.toString(), e.toString());
                }
            }
            throw e;
        }
        return result;
    }

    /**
     * Finds out whether an exception should be logged as severe. Default is
     * severe, which can be overruled by nonSevereLoggingFor which can be
     * overruled again by nonSevereLoggingFor.
     *
     * @param e
     *            the exception to check
     * @param annotation
     *            the annotation to get the configuration from
     * @return true, if logging should be done as severe
     */
    private boolean isExceptionSevere(Exception e, Logging annotation) {
        boolean severe = true;
        Class<? extends Exception> exceptionClass = e.getClass();
        for (Class<? extends Throwable> clazz : annotation.nonSevereLoggingFor()) {
            if (clazz.isAssignableFrom(exceptionClass)) {
                severe = false;
                break;
            }
        }
        for (Class<? extends Throwable> clazz : annotation.severeLoggingFor()) {
            if (clazz.isAssignableFrom(exceptionClass)) {
                severe = true;
                break;
            }
        }
        return severe;
    }

    /**
     * Searches an annotation at the specified method of class and if not found
     * there at the respective class. This method can be used to to query the
     * annotations parameters.
     *
     * @param method
     *            the method to search at
     * @param annotationClazz
     *            the annotation to search
     * @param <T>
     *            the type of the annotation
     * @return found annotation
     * @throws RuntimeException
     *             if annotation is not found
     */
    private <T extends Annotation> T getAnnotation(final Method method, final Class<T> annotationClazz)
            throws RuntimeException {
        // first look at method-level annotations, since they take priority
        for (Annotation a : method.getAnnotations()) {
            if (annotationClazz.isInstance(a)) {
                return annotationClazz.cast(a);
            }
        }
        for (Annotation a : method.getDeclaringClass().getAnnotations()) {
            if (annotationClazz.isInstance(a)) {
                return annotationClazz.cast(a);
            }
        }
        throw new RuntimeException("@" + annotationClazz.getName() + " not found on method " + method.getName()
                + " or its class " + method.getClass().getName());
    }

    /**
     * Creates a string representation of the method call including method name
     * and parameter representation.
     *
     * @param ctx
     *            the InvocationContext
     * @param maximumCount
     *            maximum count of object to to represent per collection or
     *            array instance
     * @return StringBuilder with string describing call
     */
    private StringBuilder getCallString(final InvocationContext ctx, final int maximumCount) {
        StringBuilder sb = new StringBuilder();
        sb.append("call ");
        sb.append(ctx.getMethod().getName());
        sb.append("(");
        Object[] parameters = ctx.getParameters();
        String[] parameterNames = paranamer.lookupParameterNames(
                ctx.getMethod(), false);
        for (int i = 0; i < parameters.length; i++) {
            sb.append(parameterNames[i]);
            sb.append("=");
            stringTransformer.transform(sb, parameters[i], maximumCount);
            if (i < parameterNames.length - 1) {
                sb.append(", ");
            }
        }
        sb.append(")");
        return sb;
    }

    /**
     * Returns current system time or 0 if measurement is deactivated.
     *
     * @param measureDuration
     *            true if duration measurement should be active
     * @return current time in millis or 0
     */
    private long currentTimeMillis(final boolean measureDuration) {
        return measureDuration ? System.currentTimeMillis() : 0;
    }

    /**
     * Appends the duration of the call.
     *
     * @param sb
     *            the StringBuilder to append to
     * @param measureDuration
     *            true if measurement and appending should take place
     * @param duration
     *            time taken for call in milliseconds
     */
    private void appendDuration(final StringBuilder sb, final boolean measureDuration, final long duration) {
        if (measureDuration) {
            sb.append(" [duration=");
            sb.append(duration);
            sb.append("ms]");
        }
    }

}
