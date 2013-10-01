package com.github.sfleiter.cdi_interceptors.impl;

import com.github.sfleiter.cdi_interceptors.api.Logging;

@Logging(measureDuration=false)
public class InterceptedDefault {

    public void run() { }
    public Object run(Object objectParam) { return objectParam; }
    public Object run(String stringParam, Object objectParam) { return objectParam; }
}
