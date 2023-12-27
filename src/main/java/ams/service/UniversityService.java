package ams.service;

import ams.model.entity.University;
import org.aspectj.apache.bcel.classfile.Module;

import java.util.Optional;

public interface UniversityService
        extends BaseService<University, Long> {

    Optional<University> findUniByName(String uniName);
}
