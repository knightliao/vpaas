package com.github.knightliao.vpaas.common.utils.lang;

import java.util.AbstractList;
import java.util.List;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * @author knightliao
 * @date 2021/8/5 21:46
 */
public class Tuple extends AbstractList {

    private final Object[] contents;
    private int hashCode;

    public Tuple(Object[] contents) {
        if (contents == null) {
            throw new NullPointerException();
        }
        this.contents = contents;
    }

    public Object get(int index) {
        return contents[index];
    }

    public int size() {
        return contents.length;
    }

    @Override
    public boolean equals(Object o) {
        throw new NotImplementedException();
    }

    @Override
    public int hashCode() {
        if (hashCode == 0) {
            for (int i = 0; i < contents.length; i++) {
                Object value = contents[i];
                int hash = (value != null) ? value.hashCode() : 0xbabe;
                hashCode ^= hash;
            }
            if (hashCode == 0) {
                hashCode = 0xbabe;
            }
        }
        return hashCode;
    }

    @Override
    public List subList(int fromIndex, int toIndex) {
        int size = toIndex - fromIndex;
        Object[] newContent = new Object[size];
        System.arraycopy(contents, fromIndex, newContent, 0, size);
        return new Tuple(newContent);
    }
}
