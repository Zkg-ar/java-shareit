package ru.practicum.shareit;

import java.util.List;

public abstract class Storage<T> {
    private Long id = 0L;

    public Long generateId() {
        return ++id;
    }

    public abstract T add(T t);

    public abstract T getById(Long id);

    public abstract T update(T t);

    public abstract List<T> getAll();


}
