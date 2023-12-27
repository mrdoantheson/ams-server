package ams.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.Optional;

@NoRepositoryBean
public interface BaseRepository<T, ID> extends PagingAndSortingRepository<T, ID>,
        CrudRepository<T, ID>,
        JpaSpecificationExecutor<T>,
        JpaRepository<T, ID> {

    Optional<T> findByIdAndDeletedFalse(ID id);

    boolean existsByIdAndDeletedFalse(ID id);
}
