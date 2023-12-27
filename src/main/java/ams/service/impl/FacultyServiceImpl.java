package ams.service.impl;

import ams.model.entity.Faculty;
import ams.repository.FacultyRepository;
import ams.service.FacultyService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class FacultyServiceImpl
        extends BaseServiceImpl<Faculty, Long, FacultyRepository>
        implements FacultyService {

    private final FacultyRepository facultyRepository;
    @Override
    public Optional<Faculty> findFacultyByName(String facultyName) {
        return facultyRepository.findFacultyByName(facultyName);
    }
}
