package com.github.sfleiter.cdi_interceptors;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.enterprise.util.Nonbinding;
import javax.interceptor.InterceptorBinding;

import uk.org.lidalia.slf4jext.Level;

/**
 * Activates automatic method-level logging for any annotated bean. For every
 * call a single slf4j log message is generated with the logger set to the name
 * of the class. The message contains the method name, the parameters, the
 * result object, the call duration and the exception if one got thrown.
 * 
 * @author Stefan Fleiter
 */
@InterceptorBinding
@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface Logging {

    /**
     * Default limit of items per collection or array instance that are logged.
     */
    final int LOG_ITEM_LIMIT_DEFAULT = 5;

    /**
     * Log Level for successful calls (not throwing an Exception) and those that
     * throw Exceptions not configured as severe. Defaults to {@Link
     * Level.INFO}.
     */
    @Nonbinding
    Level standardLogLevel() default Level.INFO;

    /**
     * Log Level for calls that throw an Exception configured as severe.
     * Defaults to {@Link Level.ERROR}.
     */
    @Nonbinding
    Level severeExceptionLogLevel() default Level.ERROR;

    /**
     * Maximum number of items in a single array or collection that are logged.
     * For recursive items (collection as part of an array, f.e.) the number of
     * items logged for a parameter or the result can be greater than that.
     * Defaults to {@link Logging#LOG_ITEM_LIMIT_DEFAULT}.
     */
    @Nonbinding
    int logItemLimit() default LOG_ITEM_LIMIT_DEFAULT;

    /**
     * If true the duration of the method call is added to the log message.
     * Defaults to true.
     */
    @Nonbinding
    boolean measureDuration() default true;
}
