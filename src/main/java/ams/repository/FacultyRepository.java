package ams.repository;

import ams.model.entity.Faculty;

import java.util.Optional;

public interface FacultyRepository
        extends BaseRepository<Faculty, Long> {

    Optional<Faculty> findFacultyByName(String facultyName);
}
