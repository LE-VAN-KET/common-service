package com.dut.team92.common.service;

import java.util.List;

public interface RepositoryService<T> {
    List<T> getAll();
    T get(Long id);
    T create(T entity);
    T update(T entity);
    void delete(Long id);
}
