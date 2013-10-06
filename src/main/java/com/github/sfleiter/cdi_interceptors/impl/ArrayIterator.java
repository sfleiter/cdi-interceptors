package com.github.sfleiter.cdi_interceptors.impl;

import java.lang.reflect.Array;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Creates an <code>Iterator</code> from the supplied object that has to be any kind of array,
 * either <code>Object[]</code>, a custom class array or an array of a primitive type.
 * 
 * @author Stefan Fleiter
 */
public final class ArrayIterator implements Iterator<Object> {

    private final Object array;
    private final int length;
    private int pos;

    /**
     * Constructor.
     * @param array any kind of array
     * @throws IllegalArgumentException if param is no array
     */
    public ArrayIterator(Object array) {
        this.array = array;
        length = Array.getLength(array);
    }

    /**
     * {@inheritDoc}
     */
    public boolean hasNext() {
        return pos < length;
    }

    /**
     * {@inheritDoc}
     */
    public Object next() {
        if (pos == length) {
            throw new NoSuchElementException();
        }
        return Array.get(array, pos++);
    }

    /**
     * Not supported for this iterator.
     * @throws UnsupportedOperationException
     */
    public void remove() {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns the full length of the underlying array.
     * @return length of the array
     */
    public int getLength() {
        return length;
    }

}
