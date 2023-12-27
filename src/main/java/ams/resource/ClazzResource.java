package ams.resource;

import ams.constant.AppConstant;
import ams.enums.ClassStatus;
import ams.enums.Unit;
import ams.enums.UserRole;
import ams.exception.ResourceNotFoundException;
import ams.model.dto.*;
import ams.model.entity.*;


import ams.security.SecurityUtil;
import ams.service.*;
import jakarta.persistence.criteria.Join;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/class")
public class ClazzResource extends BaseResource {
    private final ClazzService clazzService;

    private final ClassDetailService classDetailService;

    private final ClassBudgetService classBudgetService;

    private final ClassAuditService classAuditService;
    private final TraineeService traineeService;
    private final EmailService emailService;
    private final AccountService accountService;


    public ClazzResource(ClazzService clazzService, ClassDetailService classDetailService, ClassBudgetService classBudgetService, ClassAuditService classAuditService, TraineeService traineeService, EmailService emailService, AccountService accountService) {
        this.clazzService = clazzService;
        this.classDetailService = classDetailService;
        this.classBudgetService = classBudgetService;
        this.classAuditService = classAuditService;
        this.traineeService = traineeService;
        this.emailService = emailService;
        this.accountService = accountService;
    }

    @GetMapping
    public ResponseEntity<BaseResponseDto> show(
            @RequestParam(required = false, defaultValue = AppConstant.DEFAULT_PAGE_STR) Integer page,
            @RequestParam(required = false, defaultValue = AppConstant.DEFAULT_PAGE_SIZE_STR) Integer size,
            @RequestParam(required = false, name = "sort", defaultValue = AppConstant.DEFAULT_SORT_FIELD) List<String> sorts,
            @RequestParam(required = false) Map<String, String> searchParams) {

        List<Sort.Order> orders = new ArrayList<>();
        for (String sortField : sorts) {
            boolean isDesc = sortField.startsWith("-");
            orders.add(isDesc ? Sort.Order.desc(sortField.substring(1)) : Sort.Order.asc(sortField));
        }

        Specification<Clazz> spec = (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("deleted"), false);


        for (Map.Entry<String, String> entry : searchParams.entrySet()) {

            if (entry.getKey().equals("location")) {
                Specification<Clazz> specByStatus = (root, query, criteriaBuilder) ->
                        criteriaBuilder.equal(criteriaBuilder.toString(root.get(entry.getKey())), entry.getValue());
                spec = spec.and(specByStatus);
                continue;
            }
            if (entry.getKey().equals("classType")) {
                Specification<Clazz> specByStatus = (root, query, criteriaBuilder) ->
                        criteriaBuilder.equal(criteriaBuilder.toString(root.get(entry.getKey())), entry.getValue());
                spec = spec.and(specByStatus);
                continue;
            }
            if (entry.getKey().equals("classStatus")) {
                Specification<Clazz> specByStatus = (root, query, criteriaBuilder) ->
                        criteriaBuilder.equal(criteriaBuilder.toString(root.get(entry.getKey())), entry.getValue());
                spec = spec.and(specByStatus);
                continue;
            }


            if (!entry.getKey().equals("page") && !entry.getKey().equals("size") && !entry.getKey().equals("sort") && !entry.getKey().equals("classId")) {

                Specification<Clazz> specByKeyword = (root, query, criteriaBuilder) -> {
                    Join<Clazz, ClassDetail> classDetailJoin = root.join("classDetail"); // Assuming the relationship between Clazz and ClassDetail is named "classDetail"
                    return criteriaBuilder.like(classDetailJoin.get(entry.getKey()), "%" + entry.getValue() + "%");
                };

                spec = spec.and(specByKeyword);
            }

        }

        if(!SecurityUtil.isTrainee()) {

            if (SecurityUtil.isClassAdmin()) {
                Optional<String> classAdminAccountOpt = SecurityUtil.getCurrentUserLogin();
                String classAdminAccount = classAdminAccountOpt.orElseThrow(ResourceNotFoundException::new);

                Specification<Clazz> filterClassByClassAdmin = (root, query, criteriaBuilder) ->
                        criteriaBuilder.equal(root.get("classAdmin"), classAdminAccount);
                spec = spec.and(filterClassByClassAdmin);
            }

            if (SecurityUtil.isTrainer()) {
                Optional<String> trainerAccountOpt = SecurityUtil.getCurrentUserLogin();
                String trainerAccount = trainerAccountOpt.orElseThrow(ResourceNotFoundException::new);

                Specification<Clazz> filterClassByTrainer = (root, query, criteriaBuilder) ->
                {
                    Join<Clazz, ClassDetail> classDetailJoin = root.join("classDetail"); // Assuming the relationship between Clazz and ClassTrainee is named "classTrainees"
                    return criteriaBuilder.or(
                            criteriaBuilder.equal(classDetailJoin.get("trainer"), trainerAccount),
                            criteriaBuilder.equal(classDetailJoin.get("masterTrainer"), trainerAccount)
                    );
                };
                spec = spec.and(filterClassByTrainer);
            }

            PageRequest pageRequest = PageRequest.of(page - 1, size, Sort.by(orders));
            Page<Clazz> classPage = clazzService.findAll(spec, pageRequest);
            List<ClazzDisplayDto> clazzDisplayDtos = classPage.getContent().stream().map(clazz -> {
                ClazzDisplayDto clazzDisplayDto = new ClazzDisplayDto();
                BeanUtils.copyProperties(clazz, clazzDisplayDto);
                ClassDetail classDetail = clazz.getClassDetail();
                System.out.println("Clazz: " + clazz);
                System.out.println("ClassDetail: " + classDetail);
                if (classDetail != null) {
                    clazzDisplayDto.setActualStartDate(classDetail.getActualStartDate());
                    clazzDisplayDto.setActualEndDate(classDetail.getActualEndDate());

                }

                return clazzDisplayDto;
            }).toList();


            Page<ClazzDisplayDto> result = new PageImpl<>(clazzDisplayDtos, pageRequest, classPage.getTotalElements());

            return success(result, "OK");
        }
        else {
            Optional<String> traineeUsername = SecurityUtil.getCurrentUserLogin();
            if (traineeUsername.isPresent()) {
                Optional<Trainee> traineeOptional = traineeService.findByAccount(traineeUsername.get());
                if (traineeOptional.isPresent()) {
                    Trainee trainee = traineeOptional.get();
                    Specification<Clazz> specByTrainee = (root, query, criteriaBuilder) -> {
                        Join<Clazz, ClassTrainee> classTraineeJoin = root.join("classTraineeSet"); // Assuming the relationship between Clazz and ClassTrainee is named "classTrainees"
                        return criteriaBuilder.and(
                                criteriaBuilder.equal(classTraineeJoin.get("trainee"), trainee)
                        );
                    };
                    spec = spec.and(specByTrainee);
                }
            }

            PageRequest pageRequest = PageRequest.of(page - 1, size, Sort.by(orders));
            Page<Clazz> classPage = clazzService.findAll(spec, pageRequest);
            List<ClazzDisplayDto> clazzDisplayDtos = classPage.getContent().stream().map(clazz -> {
                ClazzDisplayDto clazzDisplayDto = new ClazzDisplayDto();
                BeanUtils.copyProperties(clazz, clazzDisplayDto);
                ClassDetail classDetail = clazz.getClassDetail();
                if (classDetail != null) {
                    clazzDisplayDto.setActualStartDate(classDetail.getActualStartDate());
                    clazzDisplayDto.setActualEndDate(classDetail.getActualEndDate());
                }
                return clazzDisplayDto;
            }).toList();

            Page<ClazzDisplayDto> result = new PageImpl<>(clazzDisplayDtos, pageRequest, classPage.getTotalElements());

            return success(result, "OK");
        }
    }


    @GetMapping("/{id}")
    public ResponseEntity<ClazzDisplayDto> showDetail(@PathVariable Long id) {
        Optional<Clazz> classAuditOpt = clazzService.findOneOpt(id);
        Clazz clazz = classAuditOpt.orElseThrow(ResourceNotFoundException::new);

        ClazzDisplayDto clazzDisplayDto = new ClazzDisplayDto();
        BeanUtils.copyProperties(clazz, clazzDisplayDto);

        return ResponseEntity.ok(clazzDisplayDto);
    }

    @GetMapping("/find/{classId}")
    public ResponseEntity<TotalDisplayDto> showAllClassByClassId(@PathVariable Long classId) {
        Optional<Clazz> classOpt = clazzService.findByClassId(classId);
        Clazz clazz = classOpt.orElseThrow(ResourceNotFoundException::new);

        Optional<ClassDetail> classDetailOpt = classDetailService.findOneByClassId(classId);
        ClassDetail classDetail = classDetailOpt.orElseThrow(ResourceNotFoundException::new);

        List<ClassBudget> classBudgetList = classBudgetService.findAllByClassId(classId);

        List<ClassAudit> classAuditList = classAuditService.findAllByClassId(classId);

        TotalDisplayDto totalDisplayDto = new TotalDisplayDto();

        BeanUtils.copyProperties(clazz, totalDisplayDto.clazzDisplayDto);
        BeanUtils.copyProperties(classDetail, totalDisplayDto.classDetailDisplayDto);
        totalDisplayDto.classDetailDisplayDto.setClazzId(clazz.getId());


        if (SecurityUtil.isDeliveryManager() || SecurityUtil.isFaManager() || SecurityUtil.isSystemAdmin()) {
            List<ClassBudgetDisplayDto> classBudgetDisplayDtoList = classBudgetList.stream()
                    .map(classBudget -> {
                        ClassBudgetDisplayDto classBudgetDisplayDto = new ClassBudgetDisplayDto();
                        BeanUtils.copyProperties(classBudget, classBudgetDisplayDto);
                        classBudgetDisplayDto.setClassBudgetId(classBudget.getId());
                        classBudgetDisplayDto.setClazzId(clazz.getId());
                        return classBudgetDisplayDto;
                    }).toList();
            totalDisplayDto.setClassBudgetDisplayDto(classBudgetDisplayDtoList);
        }

        ClazzDisplayDto clazzDisplayDto = new ClazzDisplayDto();
        BeanUtils.copyProperties(clazz, clazzDisplayDto);

        List<ClassAuditDisplayDto> classAuditDisplayDtoList = classAuditList.stream()
                .map(classAudit -> {
                    ClassAuditDisplayDto classAuditDisplayDto = new ClassAuditDisplayDto();
                    BeanUtils.copyProperties(classAudit, classAuditDisplayDto);
                    classAuditDisplayDto.setClassAuditId(classAudit.getId());
                    classAuditDisplayDto.setClazzId(clazz.getId());
                    return classAuditDisplayDto;
                }).toList();
        totalDisplayDto.setClassAuditDisplayDto(classAuditDisplayDtoList);

        return ResponseEntity.ok(totalDisplayDto);
    }


    @PostMapping
    public ResponseEntity<BaseResponseDto> create(@RequestBody @Valid ClazzTotalDto clazzTotalDto
    ) {
        if (!SecurityUtil.isFaManager() && !SecurityUtil.isSystemAdmin() && !SecurityUtil.isDeliveryManager()) {
            return badRequest("class.resource.permission.delete", null, null);
        }
        Clazz clazz = new Clazz();
        ClassDetail classDetail = new ClassDetail();
        BeanUtils.copyProperties(clazzTotalDto.clazzDto, clazz);
        List<ClassBudget> classBudgetList = clazzTotalDto.classBudgetDto.stream()
                .map(classBudgets -> {
                    ClassBudget classBudgetDto = new ClassBudget();
                    BeanUtils.copyProperties(classBudgets, classBudgetDto);
                    return classBudgetDto;
                }).collect(Collectors.toList());
        BeanUtils.copyProperties(clazzTotalDto.classDetailDto, classDetail);
        clazz.setActualTraineeNo(0);

        if (clazzService.isClassCodeExists(clazz.getClassCode())) {
            return badRequest("classCode.resource.duplicate", null, null);
        }

        if (clazz.getExpectedEndDate() != null && clazz.getExpectedStartDate() != null) {
            if (clazz.getExpectedEndDate().isBefore(clazz.getExpectedStartDate())) {
                return badRequest("class.expectedEndDate.before.expectedStartDate", null, null);
            }
        }

        if (clazz.getPlannedTraineeNo() != null && clazz.getAcceptedTraineeNo() != null && clazz.getActualTraineeNo() != null) {
            if (clazz.getPlannedTraineeNo() < clazz.getAcceptedTraineeNo() && clazz.getAcceptedTraineeNo() < clazz.getActualTraineeNo()) {
                return badRequest("class.plannedTraineeNo.less.acceptedTraineeNo.less.actualTraineeNo", null, null);
            }
        }

        if (classDetail.getActualEndDate() != null && classDetail.getActualStartDate() != null) {
            if (classDetail.getActualEndDate().isBefore(classDetail.getActualStartDate())) {
                return badRequest("class.actualEndDate.before.actualStartDate", null, null);
            }
        }

        clazzService.create(clazz);
        classDetail.setClazz(clazz);
        classDetailService.create(classDetail);
        for (ClassBudget classBudget : classBudgetList) {
            if (classBudget.getTax() >= 100) {
                return badRequest("class.tax.greater.100", null, null);
            }
            classBudget.setUnit(Unit.h);
            classBudget.setClazz(clazz);

        }
        ;
        classBudgetService.createOrUpdate(classBudgetList);

        String account = clazzTotalDto.clazzDto.getClassAdmin();
        String classCode = clazzTotalDto.clazzDto.getClassCode();
        String className = String.valueOf(clazzTotalDto.clazzDto.getClassType());
        String accountTrainer = clazzTotalDto.classDetailDto.getTrainer();
        String accountMasterTrainer = clazzTotalDto.classDetailDto.getMasterTrainer();
        Long classId = clazz.getId();
        String mode = "created";
        emailService.sendEmail(account, classCode, className, classId, mode, accountTrainer, accountMasterTrainer);

        return success("class.create.success");
    }

    @PutMapping("/{classId}")
    public ResponseEntity<BaseResponseDto> update(@PathVariable("classId") Long classId, @RequestBody @Valid ClazzTotalDto clazzTotalDto) {

        if (!SecurityUtil.isFaManager() && !SecurityUtil.isSystemAdmin() && !SecurityUtil.isDeliveryManager()) {
            return badRequest("class.resource.permission.delete", null, null);
        }

        Optional<Clazz> clazzOptional = clazzService.findOneOpt(classId);
        Optional<ClassDetail> classDetailOptional = classDetailService.findOneByClassId(classId);

        Clazz clazz = clazzOptional.orElseThrow(ResourceNotFoundException::new);
        BeanUtils.copyProperties(clazzTotalDto.clazzDto, clazz);
        clazz.setClassStatus(ClassStatus.DRAFT);
        clazz.setId(clazzTotalDto.clazzDto.getClazzId());

        clazzService.update(clazz);

        ClassDetail classDetail = classDetailOptional.orElseThrow(ResourceNotFoundException::new);
        BeanUtils.copyProperties(clazzTotalDto.classDetailDto, classDetail);
        classDetailService.update(classDetail);

        List<ClassBudget> classBudgetList = clazzTotalDto.classBudgetDto.stream()
                .map(classBudgetDto -> {
                    ClassBudget classBudget = new ClassBudget();
                    BeanUtils.copyProperties(classBudgetDto, classBudget);
                    classBudget.setClazz(clazz);
                    classBudget.setId(classBudgetDto.getClassBudgetId());
                    return classBudget;
                }).toList();


        // Check if classBudgetOld is not exist in newClassBudget then delete
        List<ClassBudget> classBudgetListOld = classBudgetService.findAllByClassId(classId);

        boolean isExist;

        for (ClassBudget classBudgetOld : classBudgetListOld) {
            isExist = false;
            for (ClassBudget classBudgetNew : classBudgetList) {
                if (Objects.equals(classBudgetOld.getId(), classBudgetNew.getId())) {
                    isExist = true;
                    break;
                }
            }
            if (!isExist) {
                classBudgetService.delete(classBudgetOld.getId());
            }
        }

        classBudgetService.createOrUpdate(classBudgetList);

        String account = clazzTotalDto.clazzDto.getClassAdmin();
        String classCode = clazzTotalDto.clazzDto.getClassCode();
        String className = String.valueOf(clazzTotalDto.clazzDto.getClassType());
        String accountTrainer = clazzTotalDto.classDetailDto.getTrainer();
        String accountMasterTrainer = clazzTotalDto.classDetailDto.getMasterTrainer();
        String mode = "updated";
        emailService.sendEmail(account, classCode, className, classId, mode, accountTrainer, accountMasterTrainer);


        return success("class.update.success");

    }

    @DeleteMapping("/delete")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<BaseResponseDto> deleteAll(@RequestParam("ids") List<Long> ids) {
        if (!SecurityUtil.isFaManager() && !SecurityUtil.isSystemAdmin() && !SecurityUtil.isDeliveryManager()) {
            return badRequest("class.resource.permission.delete", null, null);
        }
        Specification<Clazz> spec = (root, query, criteriaBuilder) ->
                criteriaBuilder.in(root.get("id")).value(ids);
        List<Clazz> classList = clazzService.findAll(spec);

        if (classList.size() != ids.size()) {
            return badRequest("class.resource.notFound", null, null);
        }
        for (Clazz clazz : classList) {
            if (clazz.getClassStatus().equals(ClassStatus.DRAFT) || clazz.getClassStatus().equals(ClassStatus.PLANNED)) {
                clazz.setDeleted(true);

                String classCode = clazz.getClassCode();
                String account = clazz.getClassAdmin();
                String className = String.valueOf(clazz.getClassType());
                Long classId = clazz.getId();
                String accountTrainer = clazz.getClassDetail().getTrainer();
                String accountMasterTrainer = clazz.getClassDetail().getMasterTrainer();
                String mode = "deleted";
                emailService.sendEmail(account, classCode, className, classId, mode, accountTrainer, accountMasterTrainer);
            } else {
                return badRequest("class.resource.delete.status", null, null);
            }

        }
        clazzService.createOrUpdate(classList);
        return success("class.delete.success");
    }


    @GetMapping("/classAdmins")
    public ResponseEntity<BaseResponseDto> getClassAdmins() {
        List<EmployeeListDisplayDto> classAdmins = clazzService.findAllByRole(UserRole.CLASS_ADMIN);
        return success(classAdmins, "ok");
    }

    @GetMapping("/trainers")
    public ResponseEntity<BaseResponseDto> getTrainers() {
        List<EmployeeListDisplayDto> classTrainer = clazzService.findAllByRole(UserRole.TRAINER);
        return success(classTrainer, "ok");
    }

}
