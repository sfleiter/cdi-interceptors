package com.github.sfleiter.cdi_interceptors;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.enterprise.util.Nonbinding;
import javax.interceptor.InterceptorBinding;

import uk.org.lidalia.slf4jext.Level;

/**
 * Activates automatic method-level logging for any annotated bean.
 * @author Stefan Fleiter
 */
@InterceptorBinding
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Logging {
    
    @Nonbinding Level level() default Level.INFO;
    @Nonbinding Level exceptionLevel() default Level.ERROR;
    @Nonbinding int maximumIterableLoggingCount() default 5;
    @Nonbinding boolean measureDuration() default true;
}
