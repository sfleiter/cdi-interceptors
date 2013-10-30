package com.github.sfleiter.cdi_interceptors;

import com.github.sfleiter.cdi_interceptors.Logging;

@Logging(measureDuration = false)
public class InterceptedDefault {

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
