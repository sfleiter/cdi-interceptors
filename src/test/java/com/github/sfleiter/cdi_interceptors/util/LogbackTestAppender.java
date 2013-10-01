package com.github.sfleiter.cdi_interceptors.util;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import ch.qos.logback.core.encoder.Encoder;

public class LogbackTestAppender extends AppenderBase<ILoggingEvent> {

    private static ILoggingEvent loggingEvent;
    private static Encoder<ILoggingEvent> encoder;

    @Override
    protected void append(ILoggingEvent iLoggingEvent) {
        loggingEvent = iLoggingEvent;
    }

    public void setEncoder(Encoder<ILoggingEvent> encoder) {
      this.encoder = encoder;
    }

    public static String getMessage() throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            encoder.init(bos);
            encoder.doEncode(loggingEvent);
            return bos.toString();
        } finally {
            bos.close();
        }
    }
    
}
