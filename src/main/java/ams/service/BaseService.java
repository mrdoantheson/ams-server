package ams.service;

import jakarta.annotation.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;

public interface BaseService<T, ID> {
    T createOrUpdate(T model);

    T create(T model);
    T update(T model);

    List<T> createOrUpdate(List<T> models);

    void delete(ID id);

    T findOne(ID id);

    Optional<T> findOneOpt(ID id);

    List<T> findAll();

    List<T> findAll(Specification<T> spec);

    Page<T> findAll(@Nullable Specification<T> spec, Pageable page);

    boolean exists(ID id);
}
