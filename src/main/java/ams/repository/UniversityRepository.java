package ams.repository;

import ams.model.entity.University;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UniversityRepository
        extends BaseRepository<University, Long> {
    Optional<University> findUniversityByName(String uniName);
}
