/**
 * Copyright (c) 2010 Benedict Elliott Smith
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.github.sfleiter.cdi_interceptors.impl;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * This class creates an <code>Iterator</code> from the supplied array
 *
 * @author b.elliottsmith
 *
 * @param <E>
 */
public final class ArrayIterator<E> implements Iterator<E> {

    private final E[] vals;
    private final int ub;
    int p;

    @SuppressWarnings("unchecked")
    public ArrayIterator(E[] vals) {
        this.vals = vals == null ? (E[]) new Object[0] : vals;
        p = 0;
        ub = this.vals.length;
    }

    public ArrayIterator(E[] vals, int lb, int ub) {
        if (lb > ub)
            throw new IllegalArgumentException(String.format("The provided lower bound (%d) is greater than the provided upper bound (%d)", lb, ub));
        if (ub > vals.length)
            throw new IllegalArgumentException(String.format("The provided upper bound (%d) is greater than the length of the array (%d)", ub, vals.length));
        if (lb < 0)
            throw new IllegalArgumentException(String.format("The provided lower bound (%d) is less than zero", lb));
        this.vals = vals;
        p = lb;
        this.ub = ub;
    }

    public boolean hasNext() {
        return p != ub;
    }

    public E next() {
        if (p == vals.length)
            throw new NoSuchElementException();
        return vals[p++];
    }

    public void remove() {
        throw new UnsupportedOperationException();
    }

}
