package com.github.sfleiter.cdi_interceptors;

import static org.junit.Assert.*;

import javax.inject.Inject;

import static org.hamcrest.CoreMatchers.*;

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
    private InterceptedDefault def;

    @Inject
    private InterceptedConfigured conf;

    @Inject
    private InterceptedAnnotatedExtendingingClass extendingAnnotated;

    @Test
    public void testDefaultNoParamsNullResult() throws Exception {
        def.run();
        assertEquals("INFO call run() returns null", LogbackTestAppender.getMessage().trim());
    }

    @Test
    public void testDefaultIntParam() throws Exception {
        def.run(42);
        assertEquals("INFO call run(objectParam=42) returns 42", LogbackTestAppender.getMessage().trim());
    }

    @Test
    public void testDefaultStringIntParam() throws Exception {
        def.run("foo", 42);
        assertEquals("INFO call run(stringParam=foo, objectParam=42) returns 42", LogbackTestAppender.getMessage()
                .trim());
    }

    @Test
    public void testDefaultNullNullParamNullResult() throws Exception {
        def.run(null, null);
        assertEquals("INFO call run(stringParam=null, objectParam=null) returns null", LogbackTestAppender.getMessage()
                .trim());
    }

    @Test
    public void testDefaultStringArrayParam() throws Exception {
        def.run("foo", new Integer[] {1, 2, 3});
        assertEquals(
                "INFO call run(stringParam=foo, objectParam=Collection[size=3, 1, 2, 3]) returns Collection[size=3, 1, 2, 3]",
                LogbackTestAppender.getMessage().trim());
    }

    @Test
    public void testConfiguredStringArrayParam() throws Exception {
        conf.run("foo", new Integer[] { 1, 2, 3 });
        assertEquals(
                "DEBUG call run(stringParam=foo, objectParam=Collection[size=3, 1, ...]) returns Collection[size=3, 1, ...]",
                LogbackTestAppender.getMessage().trim());
    }

    @Test
    public void testDefaultStringPrimitiveArrayParam() throws Exception {
        def.run("foo", new int[] {1, 2, 3});
        assertEquals(
                "INFO call run(stringParam=foo, objectParam=Collection[size=3, 1, 2, 3]) returns Collection[size=3, 1, 2, 3]",
                LogbackTestAppender.getMessage().trim());
    }

    @Test
    public void testDefaultRuntimeException() throws Throwable {
        try {
            def.runAndThrow("foo", new RuntimeException("re message"));
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
    public void testConfiguredRuntimeException() throws Throwable {
        try {
            conf.runAndThrow("foo", new RuntimeException("re message"));
            fail();
        } catch (RuntimeException e) {
            assertThat(
                    LogbackTestAppender.getMessage(),
                    StringStartsWith
                            .startsWith("WARN call runAndThrow(objectParam=foo, throwable=java.lang.RuntimeException: re message) caused java.lang.RuntimeException: re message"
                                    + "\njava.lang.RuntimeException: re message"
                                    + "\n\tat com.github.sfleiter.cdi_interceptors.LoggingInterceptorTest.testConfiguredRuntimeException(LoggingInterceptorTest.java"));
        }
    }

    @Test
    public void testDefaultCheckedException() throws Throwable {
        try {
            def.runAndThrow("foo", new Exception("e message"));
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

    @Test
    public void testConfiguredCheckedException() throws Throwable {
        try {
            conf.runAndThrow("foo", new Exception("e message"));
            fail();
        } catch (Exception e) {
            assertThat(
                    LogbackTestAppender.getMessage().trim(),
                    is("DEBUG call runAndThrow(objectParam=foo, throwable=java.lang.Exception: e message) caused java.lang.Exception: e message"));
        }
    }

    @Test
    public void testExtendingDefaultNoParamsNullResult() throws Exception {
        extendingAnnotated.run();
        assertEquals("INFO call run() returns null", LogbackTestAppender.getMessage().trim());
    }

}
