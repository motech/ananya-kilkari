package org.motechproject.ananya.kilkari.service;

interface Action<T> {
    void perform(T object, String operator);
}
