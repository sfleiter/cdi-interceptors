package com.github.sfleiter.cdi_interceptors.impl;

import java.util.Collection;
import java.util.Iterator;

public class StringTransformer {
    
    @SuppressWarnings("rawtypes")
    void transform(StringBuilder sb, Object o, int maximumCount) {
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
