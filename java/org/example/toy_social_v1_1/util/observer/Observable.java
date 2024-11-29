package org.example.toy_social_v1_1.util.observer;

import org.example.toy_social_v1_1.util.event.Event;

public interface Observable<E extends Event> {
    void addObserver(Observer<E> e);
    void removeObserver(Observer<E> e);
    void notifyObservers(E e);
}