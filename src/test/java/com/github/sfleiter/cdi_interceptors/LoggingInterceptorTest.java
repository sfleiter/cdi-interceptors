package com.github.sfleiter.cdi_interceptors;

import static org.junit.Assert.*;

import javax.inject.Inject;

import org.hamcrest.core.StringStartsWith;
import org.jglue.cdiunit.AdditionalClasses;
import org.jglue.cdiunit.CdiRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.github.sfleiter.cdi_interceptors.LoggingInterceptor;
import com.github.sfleiter.cdi_interceptors.util.LogbackTestAppender;

@RunWith(CdiRunner.class)
@AdditionalClasses(value={LoggingInterceptor.class})
public class LoggingInterceptorTest {

    @Before
    public void setUp() throws Exception {}

    @Inject
    private InterceptedDefault sut;

    @Test
    public void testDefaultNoParamsNullResult() throws Exception {
        sut.run();
        assertEquals("INFO call run() returns null", LogbackTestAppender.getMessage().trim());
    }

    @Test
    public void testDefaultIntParam() throws Exception {
        sut.run(42);
        assertEquals("INFO call run(objectParam=42) returns 42", LogbackTestAppender.getMessage().trim());
    }

    @Test
    public void testDefaultStringIntParam() throws Exception {
        sut.run("foo", 42);
        assertEquals("INFO call run(stringParam=foo, objectParam=42) returns 42", LogbackTestAppender.getMessage()
                .trim());
    }

    @Test
    public void testDefaultNullNullParamNullResult() throws Exception {
        sut.run(null, null);
        assertEquals("INFO call run(stringParam=null, objectParam=null) returns null", LogbackTestAppender.getMessage()
                .trim());
    }

    @Test
    public void testDefaultStringArrayParam() throws Exception {
        sut.run("foo", new Integer[] {1, 2, 3});
        assertEquals(
                "INFO call run(stringParam=foo, objectParam=Collection[size=3, 1, 2, 3]) returns Collection[size=3, 1, 2, 3]",
                LogbackTestAppender.getMessage().trim());
    }

    @Test
    public void testDefaultStringPrimitiveArrayParam() throws Exception {
        sut.run("foo", new int[] {1, 2, 3});
        assertEquals(
                "INFO call run(stringParam=foo, objectParam=Collection[size=3, 1, 2, 3]) returns Collection[size=3, 1, 2, 3]",
                LogbackTestAppender.getMessage().trim());
    }

    @Test
    public void testDefaultRuntimeException() throws Throwable {
        try {
            sut.runAndThrow("foo", new RuntimeException("re message"));
            fail();
        } catch (RuntimeException e) {
            assertThat(
                    LogbackTestAppender.getMessage(),
                    StringStartsWith
                            .startsWith("ERROR call runAndThrow(objectParam=foo, throwable=java.lang.RuntimeException: re message) caused java.lang.RuntimeException: re message"
                                    + "\njava.lang.RuntimeException: re message"
                                    + "\n\tat com.github.sfleiter.cdi_interceptors.LoggingInterceptorTest.testDefaultRuntimeException(LoggingInterceptorTest.java"));
        }
    }

    @Test
    public void testDefaultCheckedException() throws Throwable {
        try {
            sut.runAndThrow("foo", new Exception("e message"));
            fail();
        } catch (Exception e) {
            assertThat(
                    LogbackTestAppender.getMessage(),
                    StringStartsWith
                            .startsWith("INFO call runAndThrow(objectParam=foo, throwable=java.lang.Exception: e message) caused java.lang.Exception: e message"
                                    + "\njava.lang.Exception: e message"
                                    + "\n\tat com.github.sfleiter.cdi_interceptors.LoggingInterceptorTest.testDefaultCheckedException(LoggingInterceptorTest.java"));
        }
    }

}
