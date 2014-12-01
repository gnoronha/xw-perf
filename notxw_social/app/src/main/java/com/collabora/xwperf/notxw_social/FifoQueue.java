package com.collabora.xwperf.notxw_social;

import android.support.annotation.NonNull;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Fixed size queue, first element will be removed If you add something to queue
 * @param <T>
 */
public class FifoQueue<T> implements Queue<T>, Iterable<T> {

    private final int limit;
    private final LinkedList<T> list = new LinkedList<>();

    public FifoQueue(int limit) {
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
    public boolean add(T o) {
        boolean changed = list.add(o);
        boolean trimmed = trim();
        return changed || trimmed;
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

    @NonNull
    @Override
    public Iterator<T> iterator() {
        return list.iterator();
    }

    @NonNull
    @Override
    public Object[] toArray() {
        return list.toArray();
    }

    @NonNull
    @Override
    public <T> T[] toArray(@NonNull T[] a) {
        return list.toArray(a);
    }

    @Override
    public boolean remove(Object o) {
        return list.remove(o);
    }

    @Override
    public boolean containsAll(@NonNull Collection<?> c) {
        return list.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        boolean changed = list.addAll(c);
        boolean trimmed = trim();
        return changed || trimmed;
    }

    @Override
    public boolean removeAll(@NonNull Collection<?> c) {
        return list.removeAll(c);
    }

    @Override
    public boolean retainAll(@NonNull Collection<?> c) {
        return list.retainAll(c);
    }

    @Override
    public void clear() {
        list.clear();
    }

    @Override
    public boolean offer(T e) {
        boolean changed = list.offer(e);
        boolean trimmed = trim();
        return changed || trimmed;
    }

    @Override
    public T remove() {
        return list.remove();
    }

    @Override
    public T poll() {
        return list.poll();
    }

    @Override
    public T element() {
        return list.element();
    }

    @Override
    public T peek() {
        return list.peek();
    }
}