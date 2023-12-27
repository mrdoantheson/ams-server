package ams.resource;

import ams.exception.ResourceNotFoundException;
import ams.model.dto.BaseResponseDto;
import ams.model.dto.ClassBudgetDisplayDto;
import ams.model.dto.ClassBudgetDto;
import ams.model.entity.ClassBudget;
import ams.model.entity.Clazz;
import ams.service.ClassBudgetService;
import ams.service.ClazzService;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/class/budget")
public class ClassBudgetResource extends BaseResource {

    private final ClassBudgetService classBudgetService;
    private final ClazzService clazzService;

    public ClassBudgetResource(ClassBudgetService classBudgetService, ClazzService clazzService) {
        this.classBudgetService = classBudgetService;
        this.clazzService = clazzService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClassBudgetDisplayDto> showDetail(@PathVariable Long id) {
        Optional<ClassBudget> classBudgetOptional = classBudgetService.findOneOpt(id);
        ClassBudget classBudget = classBudgetOptional.orElseThrow(ResourceNotFoundException::new);

        ClassBudgetDisplayDto classBudgetDisplayDto = new ClassBudgetDisplayDto();
        BeanUtils.copyProperties(classBudget, classBudgetDisplayDto);

        return ResponseEntity.ok(classBudgetDisplayDto);
    }

    @GetMapping
    public ResponseEntity<List<ClassBudgetDisplayDto>> showAllClassBudget() {
        List<ClassBudget> classBudgetList = classBudgetService.findAll();
        List<ClassBudgetDisplayDto> classBudgetDisplayDtoList = classBudgetList.stream()
                .map(classBudget -> {
                    ClassBudgetDisplayDto classBudgetDisplayDto = new ClassBudgetDisplayDto();
                    BeanUtils.copyProperties(classBudget, classBudgetDisplayDto);
                    return classBudgetDisplayDto;
                }).toList();
        return ResponseEntity.ok(classBudgetDisplayDtoList);
    }

    @GetMapping("/find/{classId}")
    public ResponseEntity<List<ClassBudgetDisplayDto>> showAllByClassId(@PathVariable Long classId) {
        List<ClassBudget> classBudgetList = classBudgetService.findAllByClassId(classId);
        List<ClassBudgetDisplayDto> classBudgetDisplayDtoList = classBudgetList.stream()
                .map(classBudget -> {
                    ClassBudgetDisplayDto classBudgetDisplayDto = new ClassBudgetDisplayDto();
                    BeanUtils.copyProperties(classBudget, classBudgetDisplayDto);
                    return classBudgetDisplayDto;
                }).toList();
        return ResponseEntity.ok(classBudgetDisplayDtoList);
    }

    @PostMapping
    public ResponseEntity<BaseResponseDto> create(@RequestBody @Valid List<ClassBudgetDto> classBudgetDtos) {
        List<ClassBudgetDisplayDto> classBudgetDisplayDtos = new ArrayList<>();
        for (ClassBudgetDto classBudgetDto : classBudgetDtos) {
            ClassBudget classBudget = new ClassBudget();
            BeanUtils.copyProperties(classBudgetDto, classBudget);
            if (classBudgetDto.getClazzId() != null) {
                Clazz clazz = clazzService.findOne(classBudgetDto.getClazzId());
                classBudget.setClazz(clazz);
            }
            classBudgetService.create(classBudget);
            ClassBudgetDisplayDto classBudgetDisplayDto = new ClassBudgetDisplayDto();
            BeanUtils.copyProperties(classBudget, classBudgetDisplayDto);
            if (classBudget.getClazz() != null) {
                classBudgetDisplayDto.setClazzId(classBudget.getClazz().getId());
            }
            classBudgetDisplayDtos.add(classBudgetDisplayDto);
        }
        return created(classBudgetDisplayDtos,"create success");
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClassBudgetDisplayDto> update(@PathVariable("id") Long id, @RequestBody @Valid ClassBudgetDto classBudgetDto) {
        Optional<ClassBudget> optionalClassBudget = classBudgetService.findOneOpt(id);
        if (optionalClassBudget.isPresent()) {
            ClassBudget classBudget = optionalClassBudget.get();
            BeanUtils.copyProperties(classBudgetDto, classBudget);
            classBudgetService.update(classBudget);
            ClassBudgetDisplayDto classBudgetDisplayDto = new ClassBudgetDisplayDto();
            BeanUtils.copyProperties(classBudget, classBudgetDisplayDto);
            return ResponseEntity.ok(classBudgetDisplayDto);
        }
        return ResponseEntity.notFound().build();

    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        Optional<ClassBudget> classBudgetOptional = classBudgetService.findOneOpt(id);
        ClassBudget classBudget = classBudgetOptional.orElseThrow(ResourceNotFoundException::new);
        classBudgetService.delete(classBudget.getId());
    }
}
