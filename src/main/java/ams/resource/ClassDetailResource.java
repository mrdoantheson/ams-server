package ams.resource;

import ams.exception.ResourceNotFoundException;
import ams.model.dto.ClassDetailDisplayDto;
import ams.model.dto.ClassDetailDto;
import ams.model.entity.ClassDetail;
import ams.model.entity.Clazz;
import ams.service.ClassDetailService;
import ams.service.ClazzService;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/class/detail")
public class ClassDetailResource extends BaseResource {
    private final ClassDetailService classDetailService;
    private final ClazzService clazzService;


    public ClassDetailResource(ClassDetailService classDetailService, ClazzService clazzService) {
        this.classDetailService = classDetailService;
        this.clazzService = clazzService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClassDetailDisplayDto> findById(@PathVariable Long id) {
        Optional<ClassDetail> classDetailOptional = classDetailService.findOneOpt(id);
        if (classDetailOptional.isPresent()) {
            ClassDetailDisplayDto classDetailDisplayDto = new ClassDetailDisplayDto();
            BeanUtils.copyProperties(classDetailOptional.get(), classDetailDisplayDto);
            return ResponseEntity.ok(classDetailDisplayDto);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/find/{classId}")
    public ResponseEntity<ClassDetailDisplayDto> findOneByClassId(@PathVariable Long classId) {
        Optional<ClassDetail> classDetailOpt = classDetailService.findOneByClassId(classId);
        ClassDetail classDetail = classDetailOpt.orElseThrow(ResourceNotFoundException::new);

        ClassDetailDisplayDto classDetailDisplayDto = new ClassDetailDisplayDto();
        BeanUtils.copyProperties(classDetail, classDetailDisplayDto);

        return ResponseEntity.ok(classDetailDisplayDto);
    }

    @PostMapping
    public ResponseEntity<ClassDetailDisplayDto> create(@RequestBody @Valid ClassDetailDto classDetailDto) {
        ClassDetail classDetail = new ClassDetail();
        BeanUtils.copyProperties(classDetailDto, classDetail);

        // Check if clazz ID is provided and set the clazz property accordingly
        if (classDetailDto.getClazzId() != null) {
            Clazz clazz = clazzService.findOne(classDetailDto.getClazzId());
            classDetail.setClazz(clazz);
        }


        classDetailService.create(classDetail);

        ClassDetailDisplayDto classDetailDisplayDto = new ClassDetailDisplayDto();
        BeanUtils.copyProperties(classDetail, classDetailDisplayDto);

        // Check if clazz property is set and get its ID
//        if (classDetail.getClazz() != null) {
//            classDetailDisplayDto.setClazzId(classDetail.getClazz().getId());
//        }

        return ResponseEntity.ok(classDetailDisplayDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClassDetailDisplayDto> update(@PathVariable Long id, @RequestBody @Valid ClassDetailDto classDetailDto) {
        Optional<ClassDetail> classDetailOptional = classDetailService.findOneOpt(id);
        if (classDetailOptional.isPresent()) {
            ClassDetail classDetail = classDetailOptional.get();
            BeanUtils.copyProperties(classDetailDto, classDetail);
            classDetailService.update(classDetail);
            ClassDetailDisplayDto classDetailDisplayDto = new ClassDetailDisplayDto();
            BeanUtils.copyProperties(classDetail, classDetailDisplayDto);
            return ResponseEntity.ok(classDetailDisplayDto);
        }
        return ResponseEntity.notFound().build();
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<ClassDetailDisplayDto> delete(@PathVariable Long id) {
        Optional<ClassDetail> classDetailOptional = classDetailService.findOneOpt(id);
        if (classDetailOptional.isPresent()) {
            classDetailService.delete(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

}
