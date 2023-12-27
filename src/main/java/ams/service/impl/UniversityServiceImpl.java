package ams.service.impl;

import ams.model.entity.University;
import ams.repository.UniversityRepository;
import ams.service.UniversityService;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@AllArgsConstructor
@Service
public class UniversityServiceImpl extends BaseServiceImpl<University, Long, UniversityRepository>
        implements UniversityService {

    private final UniversityRepository universityRepository;
    @Override
    public Optional<University> findUniByName(String uniName) {
        return universityRepository.findUniversityByName(uniName);
    }
}
