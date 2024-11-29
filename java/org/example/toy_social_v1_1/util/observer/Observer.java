package org.example.toy_social_v1_1.util.observer;

import org.example.toy_social_v1_1.util.event.Event;

public interface Observer<E extends Event> {
    void update(E e);
}
