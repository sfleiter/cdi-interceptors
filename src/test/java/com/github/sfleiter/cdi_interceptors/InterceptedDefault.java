package com.github.sfleiter.cdi_interceptors;

import com.github.sfleiter.cdi_interceptors.Logging;

@Logging(measureDuration = false)
public class InterceptedDefault {

    public void run() {
    }

    public Object run(Object objectParam) {
        return objectParam;
    }

    public Object run(String stringParam, Object objectParam) {
        return objectParam;
    }

}
