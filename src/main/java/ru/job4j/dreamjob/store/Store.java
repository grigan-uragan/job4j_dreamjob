package ru.job4j.dreamjob.store;

import java.util.Collection;

public interface Store<T> {

    Collection<T> findAll();

    void save(T element);

    T findById(int id);
}
