package org.motechproject.ananya.kilkari.subscription.service;

interface Action<T> {
    void perform(T object);
}
