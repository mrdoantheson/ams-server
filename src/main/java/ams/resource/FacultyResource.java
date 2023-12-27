package ams.resource;

import ams.model.dto.BaseResponseDto;
import ams.model.dto.FacultyDto;
import ams.model.entity.Faculty;
import ams.service.FacultyService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/faculty")
public class FacultyResource extends BaseResource {

    private final FacultyService facultyService;


    @GetMapping
    public ResponseEntity<BaseResponseDto> findAllFaculty() {
        List<Faculty> facultyList = facultyService.findAll();

        return success(facultyList, "ok");
    }

    @PostMapping
    public ResponseEntity<BaseResponseDto> createFaculty(@RequestBody @Valid FacultyDto facultyDto) {

        Faculty faculty = new Faculty();
        faculty.setName(facultyDto.getName());

        facultyService.create(faculty);

        FacultyDto facultyDto1 = new FacultyDto();
        BeanUtils.copyProperties(faculty, facultyDto1);

        return created(facultyDto1, "ok");
    }
}
