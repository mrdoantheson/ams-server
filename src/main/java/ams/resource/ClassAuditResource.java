package ams.resource;

import ams.exception.ResourceNotFoundException;
import ams.model.dto.ClassAuditDisplayDto;
import ams.model.dto.ClassAuditDto;
import ams.model.entity.ClassAudit;
import ams.model.entity.Clazz;
import ams.service.ClassAuditService;
import ams.service.ClazzService;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/class/audit")
public class ClassAuditResource extends BaseResource {

    private final ClassAuditService classAuditService;
    private final ClazzService clazzService;

    public ClassAuditResource(ClassAuditService classAuditService, ClazzService clazzService) {
        this.classAuditService = classAuditService;
        this.clazzService = clazzService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClassAuditDisplayDto> showDetail(@PathVariable Long id) {
        Optional<ClassAudit> classAuditOpt = classAuditService.findOneOpt(id);
        ClassAudit classAudit = classAuditOpt.orElseThrow(ResourceNotFoundException::new);

        ClassAuditDisplayDto classAuditDisplayDto = new ClassAuditDisplayDto();
        BeanUtils.copyProperties(classAudit, classAuditDisplayDto);

        return ResponseEntity.ok(classAuditDisplayDto);
    }

    @GetMapping
    public ResponseEntity<List<ClassAuditDisplayDto>> showList() {
        List<ClassAudit> classAuditList = classAuditService.findAll();
        List<ClassAuditDisplayDto> classAuditDisplayDtoList = classAuditList.stream()
                .map(classAudit -> {
                    ClassAuditDisplayDto classAuditDisplayDto = new ClassAuditDisplayDto();
                    BeanUtils.copyProperties(classAudit, classAuditDisplayDto);
                    return classAuditDisplayDto;
                }).toList();
        return ResponseEntity.ok(classAuditDisplayDtoList);
    }

    @GetMapping("/find/{classId}")
    public ResponseEntity<List<ClassAuditDisplayDto>> showAllByClassId(@PathVariable Long classId) {
        List<ClassAudit> classAuditList = classAuditService.findAllByClassId(classId);
        List<ClassAuditDisplayDto> classAuditDisplayDtoList = classAuditList.stream()
                .map(classAudit -> {
                    ClassAuditDisplayDto classAuditDisplayDto = new ClassAuditDisplayDto();
                    BeanUtils.copyProperties(classAudit, classAuditDisplayDto);
                    return classAuditDisplayDto;
                }).toList();
        return ResponseEntity.ok(classAuditDisplayDtoList);
    }

    @PostMapping
    public ResponseEntity<ClassAuditDisplayDto> create(@RequestBody @Valid ClassAuditDto classAuditDto) {
        ClassAudit classAudit = new ClassAudit();
        BeanUtils.copyProperties(classAuditDto, classAudit);
        if (classAuditDto.getClazzId() != null) {
            Clazz clazz = clazzService.findOne(classAuditDto.getClazzId());
            classAudit.setClazz(clazz);
        }

        classAuditService.create(classAudit);

        ClassAuditDisplayDto classAuditDisplayDto = new ClassAuditDisplayDto();
        BeanUtils.copyProperties(classAudit, classAuditDisplayDto);

        // Check if clazz property is set and get its ID
        if (classAudit.getClazz() != null) {
            classAuditDisplayDto.setClazzId(classAudit.getClazz().getId());
        }
        return ResponseEntity.ok(classAuditDisplayDto);
    }
    @PutMapping("/{id}")
    public ResponseEntity<ClassAuditDisplayDto> update(@PathVariable Long id, @RequestBody @Valid ClassAuditDto
            classAuditDto) {
        Optional<ClassAudit> classAuditOpt = classAuditService.findOneOpt(id);
        if (classAuditOpt.isPresent()) {
            ClassAudit classAudit = classAuditOpt.get();
            BeanUtils.copyProperties(classAuditDto, classAudit);
            classAuditService.update(classAudit);
            ClassAuditDisplayDto classAuditDisplayDto = new ClassAuditDisplayDto();
            BeanUtils.copyProperties(classAudit, classAuditDisplayDto);

            return ResponseEntity.ok(classAuditDisplayDto);
        }
        return ResponseEntity.notFound().build();
    }



    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        Optional<ClassAudit> classAuditOpt = classAuditService.findOneOpt(id);
        ClassAudit classAudit = classAuditOpt.orElseThrow(ResourceNotFoundException::new);
        classAuditService.delete(classAudit.getId());
    }
}
