package com.collabora.xwperf.fps_measure_module;

import java.lang.Cloneable;import java.lang.Float;import java.lang.Iterable;import java.lang.Object;import java.lang.Override;import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

public class FifoFloatQueue implements Queue<Float>, Iterable<Float>, Cloneable {

    private final int limit;
    private final LinkedList<Float> list = new LinkedList<>();

    public FifoFloatQueue(int limit) {
        this.limit = limit;
    }

    private boolean trim() {
        boolean changed = list.size() > limit;
        while (list.size() > limit) {
            list.remove();
        }
        return changed;
    }

    @Override
    public boolean add(Float o) {
        boolean changed = list.add(o);
        boolean trimmed = trim();
        return changed || trimmed;
    }

    public float getTotal() {
        float result = 0;
        for (float value : list) {
            result += value;
        }
        return result;
    }

    public static FifoFloatQueue clone(FifoFloatQueue original) {
        FifoFloatQueue cloned = new FifoFloatQueue(original.limit);
        for (float value : original) {
            cloned.add(value);
        }
        return cloned;
    }

    @Override
    public int size() {
        return list.size();
    }

    @Override
    public boolean isEmpty() {
        return list.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return list.contains(o);
    }

    @Override
    public Iterator<Float> iterator() {
        return list.iterator();
    }

    @Override
    public Object[] toArray() {
        return list.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return list.toArray(a);
    }

    @Override
    public boolean remove(Object o) {
        return list.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return list.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends Float> c) {
        boolean changed = list.addAll(c);
        boolean trimmed = trim();
        return changed || trimmed;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return list.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return list.retainAll(c);
    }

    @Override
    public void clear() {
        list.clear();
    }

    @Override
    public boolean offer(Float e) {
        boolean changed = list.offer(e);
        boolean trimmed = trim();
        return changed || trimmed;
    }

    @Override
    public Float remove() {
        return list.remove();
    }

    @Override
    public Float poll() {
        return list.poll();
    }

    @Override
    public Float element() {
        return list.element();
    }

    @Override
    public Float peek() {
        return list.peek();
    }
}