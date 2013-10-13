package com.github.sfleiter.cdi_interceptors.impl;

import java.util.Collection;
import java.util.Iterator;

/**
 * Helper class to transform objects to a string representation. Supports null,
 * primitive types, objects, collections and arrays of any kind. Collections and
 * arrays are transformed recursively. For objects their respective toString
 * method is used.
 * 
 * @author Stefan Fleiter
 */
public class StringTransformer {

    /**
     * Transforms any kind of object to a string representation.
     * 
     * @param sb
     *            StringBuilder to which the string representation is appended
     * @param o
     *            the object to transform
     * @param maximumCount
     *            the maximum number of items in a single collection or array
     *            instance that are transformed, through the recursive nature
     *            the aggregated number of transformed items per call is not
     *            limited
     */
    @SuppressWarnings("rawtypes")
    public void transform(final StringBuilder sb, final Object o, final int maximumCount) {
        Iterator iterator;
        int size = 0;
        if (o == null) {
            sb.append("null");
            return;
        } else if (o instanceof Collection) {
            Collection c = (Collection) o;
            size = c.size();
            iterator = c.iterator();
            // fall through
        } else if (o.getClass().isArray()) {
            ArrayIterator arrayIterator = new ArrayIterator(o);
            iterator = arrayIterator;
            size = arrayIterator.getLength();
            // fall through
        } else {
            // no array or collection
            sb.append(o.toString());
            return;
        }

        // array or collection
        sb.append("Collection[size=").append(size).append(", ");
        int i = 0;
        while (i < maximumCount && iterator.hasNext()) {
            Object elem = iterator.next();
            transform(sb, elem, maximumCount);
            i++;
            if (iterator.hasNext()) {
                sb.append(", ");
                if (i == maximumCount) {
                    sb.append("...");
                }
            }
        }
        sb.append("]");
    }

}
