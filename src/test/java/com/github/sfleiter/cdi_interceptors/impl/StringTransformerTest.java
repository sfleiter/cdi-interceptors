package com.github.sfleiter.cdi_interceptors.impl;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class StringTransformerTest {
    
    static class IH {
        private int i;
        public IH(int i) { this.i = i; }
        public String toString() { return "IH: " + i; }
    }

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
        stringTransformer = new StringTransformer();
        sb = new StringBuilder();
    }
    
    @Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                { null, "null", Integer.MAX_VALUE },
                
                { "a" , "a", Integer.MAX_VALUE },
                { 1 , "1", Integer.MAX_VALUE },
                { -1 , "-1", Integer.MAX_VALUE },
                
                { new IH(5), "IH: 5", Integer.MAX_VALUE },
                
                { new Object[] { 1 }, "Collection[size=1, 1]", Integer.MAX_VALUE },
                { new Object[] { 1, "a", null, new IH(5) }, 
                    "Collection[size=4, 1, a, null, IH: 5]", 
                    Integer.MAX_VALUE },
                { new Object[] { 1, "a", null, new IH(5) }, 
                    "Collection[size=4, 1, a, null, ...]", 
                    3 },

                { new IH[] { new IH(1), new IH(3), new IH(5) }, 
                    "Collection[size=3, IH: 1, IH: 3, IH: 5]", 
                    Integer.MAX_VALUE },
                    
                { new int[] { 1, 2, 3 }, 
                    "Collection[size=3, 1, 2, 3]", 
                    Integer.MAX_VALUE },

                { new Object[] { 1, "a", new Object[] { "b", 2, new IH(6) }, new IH(5) }, 
                    "Collection[size=4, 1, a, Collection[size=3, b, 2, IH: 6], IH: 5]", 
                    Integer.MAX_VALUE },

        });
    }
    
    @Test
    public void test() {
        stringTransformer.transform(sb, input, maximumCount);
        assertEquals(expected, sb.toString());
    }

}
