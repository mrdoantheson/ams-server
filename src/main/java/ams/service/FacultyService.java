package ams.service;

import ams.model.entity.Faculty;

import java.util.Optional;

public interface FacultyService
        extends BaseService<Faculty, Long> {

    Optional<Faculty> findFacultyByName(String facultyName);
}
