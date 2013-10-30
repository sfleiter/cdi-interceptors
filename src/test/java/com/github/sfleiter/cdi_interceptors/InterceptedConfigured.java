package com.github.sfleiter.cdi_interceptors;

import uk.org.lidalia.slf4jext.Level;

import com.github.sfleiter.cdi_interceptors.Logging;

// FIXME remove duplication between this class and InterceptedDefault
@Logging(measureDuration = false, logItemLimit = 1, logStackTraceAtStandardLevel = false, standardLogLevel = Level.DEBUG, severeExceptionLogLevel = Level.WARN)
public class InterceptedConfigured {

    public void run() {
    }

    public Object run(Object objectParam) {
        return objectParam;
    }

    public Object runAndThrow(Object objectParam, Throwable throwable) throws Throwable {
        throw throwable;
    }

    public Object run(String stringParam, Object objectParam) {
        return objectParam;
    }

}
