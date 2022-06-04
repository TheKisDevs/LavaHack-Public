package com.kisman.cc.util.optimization.java;

import java.util.*;

/**
 * @author https://habr.com/ru/post/325426/?
 * @param <E>
 */
public class LinkedSet<E> extends AbstractSet<E> {
    
    private static class LinkedElement<E> {
        E value;
        
        boolean exists;
        
        LinkedElement<E> prev; 
        LinkedElement<E> next;
    }
    
    private Map<E, LinkedElement<E>> map = new HashMap<>();

    private LinkedElement<E> placeholder = new LinkedElement<>(); 
    private LinkedElement<E> head = placeholder;

    @Override
    public boolean isEmpty() { return head == placeholder; }

    @Override
    public int size() { return map.size(); }

    @Override
    public boolean contains(Object o) { return map.containsKey(o); }

    @Override
    public boolean add(E e) {
        LinkedElement<E> element = map.putIfAbsent(e, placeholder);

        if (element != null) return false;

        element = placeholder;
        element.exists = true;
        element.value = e;

        placeholder = new LinkedElement<>();
        placeholder.prev = element;

        element.next = placeholder;

        return true;
    }

    @Override
    public boolean remove(Object o) {
        LinkedElement<E> removedElement = map.remove(o);

        if (removedElement == null) return false;

        removeElementFromLinkedList(removedElement);

        return true;
    }

    private void removeElementFromLinkedList(LinkedElement<E> element) {
        element.exists = false;
        element.value = null;

        element.next.prev = element.prev;

        if (element.prev != null) {
            element.prev.next = element.next;
            element.prev = null;
        } else head = element.next;
    }

    @Override
    public Iterator<E> iterator() {
        return new ElementIterator();
    }

    private class ElementIterator implements Iterator<E> {
        LinkedElement<E> next = head;
        LinkedElement<E> current = null;

        LinkedElement<E> findNext() {
            LinkedElement<E> n = next;

            while (!n.exists && n.next != null) next = n = n.next;

            return n;
        }

        @Override
        public boolean hasNext() {
            return findNext().exists;
        }

        @Override
        public E next() {
            LinkedElement<E> n = findNext();

            if (!n.exists) throw new NoSuchElementException();

            current = n;
            next = n.next;

            return n.value;
        }

        @Override
        public void remove() {
            if (current == null) throw new IllegalStateException();
            if (map.remove(current.value, current)) removeElementFromLinkedList(current);
            else if(map.containsKey(current.value) || map.containsValue(current)) throw new NoSuchElementException();
        }
    }
}