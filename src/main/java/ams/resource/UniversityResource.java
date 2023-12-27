package ams.resource;

import ams.model.dto.BaseResponseDto;
import ams.model.dto.UniversityDto;
import ams.model.entity.University;
import ams.service.UniversityService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/university")
public class UniversityResource extends BaseResource {
    private final UniversityService universityService;

    @GetMapping
    public ResponseEntity<BaseResponseDto> findAllUniversity() {
        List<University> universityList = universityService.findAll();

        return success(universityList, "ok");

    }

    @GetMapping("/find")
    public ResponseEntity<BaseResponseDto> findUniversity(@RequestParam(name = "name") String name){
        if (universityService.findUniByName(name).isEmpty()){
            return badRequest("not found");
        }
        String name1 = universityService.findUniByName(name).get().getName();
        System.out.println(universityService.findUniByName(name).get().getName());
        return success(name1, "ok");
    }

    @PostMapping
    public ResponseEntity<BaseResponseDto> createUniversity(@RequestBody @Valid UniversityDto universityDto) {

        University university = new University();
        university.setName(universityDto.getName());

        universityService.create(university);

        UniversityDto universityDto1 = new UniversityDto();
        BeanUtils.copyProperties(university, universityDto1);

        return created(universityDto1, "ok");
    }
}
