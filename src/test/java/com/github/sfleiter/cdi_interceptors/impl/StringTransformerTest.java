package com.github.sfleiter.cdi_interceptors.impl;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.TimeZone;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class StringTransformerTest {

    private StringTransformer stringTransformer;
    private StringBuilder sb;
    private Object input;
    private String expected;
    private int maximumCount;

    public StringTransformerTest(Object input, String expected, int maximumCount) {
        this.input = input;
        this.expected = expected;
        this.maximumCount = maximumCount;
    }
    
    @Before
    public void setUp() {
        TimeZone.setDefault(TimeZone.getTimeZone("GMT"));
        stringTransformer = new StringTransformer();
        sb = new StringBuilder();
    }
    @After
    public void tearDown() {
        TimeZone.setDefault(null);
    }
    
    @Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                { null, "null", Integer.MAX_VALUE },
                
                { "a" , "a", Integer.MAX_VALUE },
                { 1 , "1", Integer.MAX_VALUE },
                { -1 , "-1", Integer.MAX_VALUE },
                
                { new Date(0), "Thu Jan 01 00:00:00 GMT 1970", Integer.MAX_VALUE },
                
                { new Object[] { 1 }, "Collection[size=1, 1]", Integer.MAX_VALUE },
                { new Object[] { 1, "a", null, new Date(0) }, 
                    "Collection[size=4, 1, a, null, Thu Jan 01 00:00:00 GMT 1970]", 
                    Integer.MAX_VALUE },
                { new Object[] { 1, "a", null, new Date(0) }, 
                    "Collection[size=4, 1, a, null, ...]", 
                    3 },

                { new Object[] { 1, "a", new Object[] { "b", 2, new Date(1000) }, new Date(0) }, 
                    "Collection[size=4, 1, a, Collection[size=3, b, 2, Thu Jan 01 00:00:01 GMT 1970], Thu Jan 01 00:00:00 GMT 1970]", 
                    Integer.MAX_VALUE },

        });
    }
    
    @Test
    public void test() {
        stringTransformer.transform(sb, input, maximumCount);
        assertEquals(expected, sb.toString());
    }

}
