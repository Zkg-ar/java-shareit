<<<<<<< HEAD
package ru.practicum.shareit;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public abstract class Storage<T> {
    private Long id = 0L;

    public Long generateId() {
        return ++id;
    }

    public abstract T add(T t);

    public abstract void delete(Long id);

    public abstract T getById(Long id);

    public abstract T update(T t);

    public abstract List<T> getAll();


}
=======
package ru.practicum.shareit;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public abstract class Storage<T> {
    private Long id = 0L;

    public Long generateId() {
        return ++id;
    }

    public abstract T add(T t);

    public abstract void delete(Long id);

    public abstract T getById(Long id);

    public abstract T update(T t);

    public abstract List<T> getAll();


}
>>>>>>> 2ee12ce350f088f712e5ee22ef9b28d0e6b19fc2
