package ams.resource;

import ams.constant.AppConstant;
import ams.enums.ClassStatus;
import ams.enums.TraineeClassStatus;
import ams.enums.TraineeStatus;
import ams.enums.UserRole;
import ams.model.dto.*;
import ams.model.entity.*;
import ams.repository.CommonSpecifications;
import ams.security.SecurityUtil;
import ams.service.*;
import ams.sheet.TraineeSheetRowData;
import jakarta.persistence.criteria.*;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api/trainee")
public class TraineeResource extends BaseResource {
    private AccountService accountService;

    private SheetService sheetService;

    private final TraineeService traineeService;

    private final ClazzService clazzService;

    private final ClassTraineeService classTraineeService;

    private final UniversityService universityService;

    private final FacultyService facultyService;

    private final Specification<Trainee> baseSpec = new CommonSpecifications<Trainee>().unDeleted();

    private final String classAdminField = "classAdmin";

    private final String trainerField = "trainer";

    private final String clazzEntity = "clazz";

    private final String classTraineeSetInTrainee = "classTraineeSet";

    private final List<String> reservedParameters = Arrays.asList("page", "size", "sort");


    @GetMapping
    public ResponseEntity<BaseResponseDto> showListTrainee(
            @RequestParam(required = false, defaultValue = AppConstant.DEFAULT_PAGE_STR) Integer page,
            @RequestParam(required = false, defaultValue = AppConstant.DEFAULT_PAGE_SIZE_STR) Integer size,
            @RequestParam(required = false, name = "sort", defaultValue = AppConstant.DEFAULT_SORT_FIELD) List<String> sorts,
            @RequestParam(required = false) Map<String, String> searchParams
    ) {

        Specification<Trainee> spec = baseSpec;

        if (SecurityUtil.isRecruiter() || SecurityUtil.isTrainee()) {
            return forbidden("trainee.resource.permission.list");
        }

        if (SecurityUtil.isClassAdmin()) {
            spec = spec.and(buildSpecFor(classAdminField));
        }

        if (SecurityUtil.isTrainer()) {
            spec = spec.and(buildSpecFor(trainerField));
        }

        spec = spec.and(buildSpecBySearchParams(searchParams));

        Page<Trainee> traineePage = createSortOrdersAndPaging(page, size, sorts, spec);

        Page<TraineeListDisplayDto> result = convertToDisplayDto(traineePage, null);

        return success(result, "ok");
    }

    @GetMapping("/toadd/{clazzid}")
    public ResponseEntity<BaseResponseDto> showListTraineeToAdd(
            @RequestParam(required = false, defaultValue = AppConstant.DEFAULT_PAGE_STR) Integer page,
            @RequestParam(required = false, defaultValue = AppConstant.DEFAULT_PAGE_SIZE_STR) Integer size,
            @RequestParam(required = false, name = "sort", defaultValue = AppConstant.DEFAULT_SORT_FIELD) List<String> sorts,
            @RequestParam(required = false) Map<String, String> searchParams,
            @PathVariable Long clazzid
    ) {

        if (!SecurityUtil.isFaManager() && !SecurityUtil.isSystemAdmin() && !SecurityUtil.isDeliveryManager()) {
            return badRequest("trainee.resource.permission.addtrainee.toclass", null, null);
        }

        if (!clazzService.exists(clazzid)) {
            return notFound("clazz.dont.exist");
        }

        Specification<Trainee> spec = baseSpec.and(buildBaseSpecToAdd(clazzid));

        spec = spec.and(buildSpecBySearchParams(searchParams));

        Page<Trainee> traineePage = createSortOrdersAndPaging(page, size, sorts, spec);

        Page<TraineeListDisplayDto> result = convertToDisplayDto(traineePage, null);

        return success(result, "ok");
    }


    @GetMapping("/inclass/{clazzId}")
    public ResponseEntity<BaseResponseDto> showListTraineeInClass(@RequestParam(required = false, defaultValue = AppConstant.DEFAULT_PAGE_STR) Integer page,
                                                                  @RequestParam(required = false, defaultValue = AppConstant.DEFAULT_PAGE_SIZE_STR) Integer size,
                                                                  @RequestParam(required = false, name = "sort", defaultValue = AppConstant.DEFAULT_SORT_FIELD) List<String> sorts,
                                                                  @PathVariable Long clazzId,
                                                                  @RequestParam(required = false) Map<String, String> searchParams
    ) {

        if (!clazzService.exists(clazzId)) {
            return notFound("clazz.dont.exist");
        }

        if (SecurityUtil.isRecruiter() || SecurityUtil.isTrainee()) {
            return forbidden("trainee.resource.permission.list.inclass");
        }

        Specification<Trainee> spec = baseSpec.and(buildBaseSpecInClass(clazzId));

        if (SecurityUtil.isClassAdmin()) {
            List<Clazz> clazzList = clazzListByRole(classAdminField, clazzId);

            if (clazzList.isEmpty()) {
                return forbidden("clazz.not.manage");
            }
        }

        if (SecurityUtil.isTrainer()) {
            List<Clazz> clazzList = clazzListByRole(trainerField, clazzId);

            if (clazzList.isEmpty()) {
                return forbidden("clazz.not.manage");
            }
        }

        spec = spec.and(buildSpecBySearchParams(searchParams));

        Page<Trainee> traineePage = createSortOrdersAndPaging(page, size, sorts, spec);

        Page<TraineeListDisplayDto> result = convertToDisplayDto(traineePage, clazzId);

        return success(result, "ok");
    }


    @PostMapping
    public ResponseEntity<BaseResponseDto> createTrainee(@RequestBody @Valid TraineeFormDto traineeFormDto) {

        if (SecurityUtil.isRecruiter() || SecurityUtil.isTrainer() || SecurityUtil.isTrainee() || SecurityUtil.isClassAdmin()) {
            return forbidden("trainee.resource.permission.update");
        }

        if (traineeService.existPhoneTrainee(traineeFormDto.getPhone())) {
            return badRequest("trainee.resource.phone.exist", null, null);
        }

        if (traineeService.existEmailTrainee(traineeFormDto.getEmail())) {
            return badRequest("trainee.resource.email.exist", null, null);
        }

        if (traineeService.existAccountTrainee(traineeFormDto.getAccount())) {
            return badRequest("trainee.resource.account.exist", null, null);
        }

        Trainee trainee = copyFormDtoToTrainee(traineeFormDto, new Trainee());

        traineeService.createOrUpdate(trainee);

        String password = "$2a$10$jPpK/7oIVGI8zaSbbLe1ieaQKEYDdU60B0o2aOcKL4eCZnyNGlTyi";
        accountService.createAccount(new Account(trainee.getAccount(), password, UserRole.TRAINEE, null));

        return created(traineeFormDto, "trainee.create.success");
    }


    @GetMapping({"/{id}", "/profile"})
    public ResponseEntity<BaseResponseDto> showUpdateTrainee(@PathVariable(required = false) Long id) {
        Optional<Trainee> trainee;

        if (id == null) {
            id = traineeService.findTraineeByAccount(SecurityUtil.getCurrentUserLogin().get()).getId();
        }

        trainee = traineeService.findOneOpt(id);

        if (trainee.isEmpty()) {
            return notFound("trainee.resource.dont.exist");
        }

        if (SecurityUtil.isRecruiter()) {
            return forbidden("trainee.resource.permission.detail");
        }

        if (SecurityUtil.isTrainee()) {
            Specification<Trainee> specTrainee = (root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("account"), SecurityUtil.getCurrentUserLogin().get());

            List<Trainee> traineeList = traineeService.findAll(specTrainee);

            if (!traineeList.get(0).getId().equals(id)) {
                return forbidden("trainee.resource.permission.detail");
            }
        }

        if (SecurityUtil.isClassAdmin()) {

            List<Trainee> traineeList = traineeListByRole(classAdminField, id);

            if (traineeList.isEmpty()) {
                return forbidden("trainee.resource.not.manage");
            }
        }

        if (SecurityUtil.isTrainer()) {

            List<Trainee> traineeList = traineeListByRole(trainerField, id);

            if (traineeList.isEmpty()) {
                return forbidden("trainee.resource.not.manage");
            }
        }

        TraineeFormDto traineeFormDto = copyTraineeToDto(trainee.get());

        return success(traineeFormDto, "ok");
    }


    @PutMapping("/{id}")
    public ResponseEntity<BaseResponseDto> updateTrainee(@RequestBody @Valid TraineeFormDto traineeFormDto,
                                                         @PathVariable Long id) {

        Optional<Trainee> traineeOpt = traineeService.findOneOpt(id);

        if (traineeOpt.isEmpty()) {
            return notFound("trainee.resource.dont.exist");
        }

        if (SecurityUtil.isRecruiter() || SecurityUtil.isTrainer() || SecurityUtil.isTrainee()) {
            return forbidden("trainee.resource.permission.update");
        }

        if (traineeService.existPhoneTraineeAndIdNot(traineeFormDto.getPhone(), id)) {
            return badRequest("trainee.resource.phone.exist", null, null);
        }

        if (traineeService.existEmailTraineeAndIdNot(traineeFormDto.getEmail(), id)) {
            return badRequest("trainee.resource.email.exist", null,  null);
        }

        if (traineeService.existAccountTraineeAndIdNot(traineeFormDto.getAccount(), id)) {
            return badRequest("trainee.resource.account.exist", null, null);
        }

        if (SecurityUtil.isClassAdmin()) {

            List<Trainee> traineeList = traineeListByRole(classAdminField, id);

            if (traineeList.isEmpty()) {
                return forbidden("trainee.resource.not.manage");
            }
        }

        Trainee trainee = copyFormDtoToTrainee(traineeFormDto, traineeOpt.get());

        traineeService.update(trainee);

        return success(traineeFormDto, "trainee.update.success");
    }


    @DeleteMapping()
    public ResponseEntity<BaseResponseDto> deleteTrainee(@RequestParam(name = "deleteId") List<Long> listIdDelete) {

        if (!SecurityUtil.isFaManager() && !SecurityUtil.isSystemAdmin() && !SecurityUtil.isDeliveryManager()) {
            return badRequest("trainee.resource.permission.delete", null, null);
        }

        Specification<Trainee> spec = (root, query, criteriaBuilder) ->
                criteriaBuilder.in(root.get("id")).value(listIdDelete);

        List<Trainee> traineeList = traineeService.findAll(spec);

        if (traineeList.size() != listIdDelete.size()) {
            return notFound("trainee.resource.dont.exist");
        }

        for (Trainee trainee : traineeList) {

            if (!trainee.getTraineeStatus().equals(TraineeStatus.DRAFT)) {
                return badRequest("trainee.resource.delete.status", null, null);
            }

            trainee.setDeleted(true);
        }

        traineeService.createOrUpdate(traineeList);

        return success("trainee.delete.success");
    }

    @PostMapping("/import")
    public ResponseEntity<BaseResponseDto> importTraineeFromExcel(@RequestParam("file") MultipartFile multipartFile) throws IOException {

        if (!SecurityUtil.isFaManager() && !SecurityUtil.isSystemAdmin() && !SecurityUtil.isDeliveryManager()) {
            return badRequest("trainee.resource.permission.import", null, null);
        }

        ImportResult<TraineeDTO> importResult = sheetService.importFile(multipartFile, TraineeSheetRowData.SHEET_NAME, TraineeSheetRowData.class, TraineeDTO.class);
        traineeService.saveAllByTraineeDTO(importResult.getData());
        if (!importResult.getErrors().isEmpty()) {
            return badRequest(null, null, importResult);
        }
        return created(importResult, "ok");
    }


    @PostMapping("/import/{clazzId}")
    public ResponseEntity<BaseResponseDto> importTraineeToClass(@RequestParam("file") MultipartFile multipartFile,
                                                                @PathVariable Long clazzId) throws IOException {

        if (!SecurityUtil.isFaManager() && !SecurityUtil.isSystemAdmin() && !SecurityUtil.isDeliveryManager()) {
            return badRequest("trainee.resource.permission.import", null,null);
        }

        Optional<Clazz> clazzOpt = clazzService.findOneOpt(clazzId);

        if (clazzOpt.isEmpty()) {
            return notFound("clazz.dont.exist");
        }

        Clazz clazz = clazzOpt.get();


        if (!clazz.getClassStatus().equals(ClassStatus.DRAFT)) {
            return badRequest("clazz.status.is.not.draft", null,null);
        }

        ImportResult<TraineeDTO> importResult = sheetService.importFileToClass(multipartFile,
                TraineeSheetRowData.SHEET_NAME, TraineeSheetRowData.class, TraineeDTO.class, clazzId);

        Integer possibleTrainees = clazz.getAcceptedTraineeNo() - classTraineeService.countTraineeInClass(clazzId);

        Integer traineeNo = importResult.getData().size();

        if (possibleTrainees == 0) {
            return badRequest("clazz.is.full", null, null);
        }

        if (traineeNo > possibleTrainees) {
            return badRequest("There is only " + possibleTrainees + " slot left in the class!");
        }

        for (TraineeDTO traineeDTO : importResult.getData()) {
            if (traineeDTO.getTraineeStatus().equals(TraineeStatus.DRAFT)) {
                return badRequest("trainee.status.class.must.not.draft", null, null);
            }
        }

        traineeService.importTraineeToClass(importResult.getData(), clazzId);
        if (!importResult.getErrors().isEmpty()) {
            return badRequest(null, null, importResult);
        }

        clazz.setActualTraineeNo(classTraineeService.countTraineeInClass(clazzId));
        clazzService.update(clazz);

        return success("trainee.import.toclass.success");
    }


    @PostMapping("/addtrainee/{clazzId}")
    public ResponseEntity<BaseResponseDto> addTraineeToClass(
            @PathVariable Long clazzId,
            @RequestParam(name = "traineeid") List<Long> traineeIdList
    ) {

        if (!SecurityUtil.isFaManager() && !SecurityUtil.isSystemAdmin() && !SecurityUtil.isDeliveryManager()) {
            return badRequest("trainee.resource.permission.add.trainee.toclass", null, null);
        }

        Optional<Clazz> clazzOpt = clazzService.findOneOpt(clazzId);

        if (clazzOpt.isEmpty()) {
            return notFound("clazz.dont.exist");
        }

        Clazz clazz = clazzOpt.get();

        Integer possibleTrainees = clazz.getAcceptedTraineeNo() - classTraineeService.countTraineeInClass(clazzId);

        Integer traineeNo = traineeIdList.size();

        if (possibleTrainees == 0) {
            return badRequest("clazz.is.full", null, null);
        }

        if (traineeNo > possibleTrainees) {
            return badRequest("There is only " + possibleTrainees + " slot left in the class!");
        }

        if (!clazz.getClassStatus().equals(ClassStatus.DRAFT)) {
            return badRequest("clazz.status.is.not.draft", null, null);
        }

        Specification<Trainee> spec = (root, query, criteriaBuilder) ->
                criteriaBuilder.in(root.get("id")).value(traineeIdList);

        List<Trainee> traineeList = traineeService.findAll(spec);

        if (traineeList.size() != traineeIdList.size()) {
            return notFound("trainee.resource.dont.exist");
        }

        List<ClassTrainee> classTraineeList = new ArrayList<>();

        for (Trainee trainee : traineeList) {
            if (trainee.getTraineeStatus().equals(TraineeStatus.DRAFT)) {
                return badRequest("trainee.status.class.must.not.draft", null, null);
            }

            ClassTrainee classTrainee = new ClassTrainee();

            if (classTraineeService.findTraineeInClass(clazzId, trainee.getId()).isPresent()) {
                return forbidden("trainee.resource.exist.inclass");
            }

            classTrainee.setTrainee(trainee);
            classTrainee.setClazz(clazz);
            classTrainee.setTraineeClassStatus(TraineeClassStatus.ACTIVE);
            trainee.setTraineeStatus(TraineeStatus.ENROLLED);

            classTraineeList.add(classTrainee);
        }

        classTraineeService.createOrUpdate(classTraineeList);

        clazz.setActualTraineeNo(classTraineeService.countTraineeInClass(clazzId));
        clazzService.update(clazz);

        traineeService.createOrUpdate(traineeList);

        return success("trainee.add.toclass.success");
    }


    @PutMapping("/inclass/{id}")
    public ResponseEntity<BaseResponseDto> updateTraineeStatusInClass(@PathVariable Long id, @RequestBody Map<Long, String> statusMap) {

        if (!SecurityUtil.isFaManager() && !SecurityUtil.isSystemAdmin() && !SecurityUtil.isDeliveryManager()) {
            return badRequest("trainee.resource.permission.update.traineeclassstatus", null, null);
        }

        Optional<Clazz> clazz = clazzService.findOneOpt(id);

        if (clazz.isEmpty()) {
            return badRequest("clazz.dont.exist", null, null);
        }

        for (Map.Entry<Long, String> entry : statusMap.entrySet()) {
            Optional<ClassTrainee> classTraineeOpt = classTraineeService.findTraineeInClass(id, entry.getKey());

            if (classTraineeOpt.isEmpty()) {
                return notFound("trainee.not.in.class");
            }
            ClassTrainee classTrainee = classTraineeOpt.get();

            classTrainee.setTraineeClassStatus(TraineeClassStatus.valueOf(entry.getValue()));
            classTraineeService.update(classTrainee);
        }
        return success("trainee.update.success");
    }


    @DeleteMapping("/inclass/{id}")
    public ResponseEntity<BaseResponseDto> deleteTraineeInClass(@PathVariable Long id, @RequestParam(name = "traineeId") List<Long> traineeIds) {

        if (!SecurityUtil.isFaManager() && !SecurityUtil.isSystemAdmin() && !SecurityUtil.isDeliveryManager()) {
            return badRequest("trainee.resource.permission.remove.trainee.toclass", null, null);
        }

        Optional<Clazz> clazzOpt = clazzService.findOneOpt(id);
        if (clazzOpt.isEmpty()) {
            return badRequest("clazz.dont.exist", null, null);
        }

        Clazz clazz = clazzOpt.get();

        for (Long traineeId : traineeIds) {
            Optional<ClassTrainee> classTraineeOpt = classTraineeService.findTraineeInClass(id, traineeId);

            if (classTraineeOpt.isEmpty()) {
                return notFound("trainee.not.in.class");
            }

            ClassTrainee classTrainee = classTraineeOpt.get();

            if (classTrainee.getTraineeClassStatus().equals(TraineeClassStatus.ACTIVE)) {
                return badRequest("trainee.class.status.active", null, null);
            }
            classTrainee.setDeleted(true);
            classTraineeService.createOrUpdate(classTrainee);

            if (classTraineeService.countClassOfTrainee(traineeId) == 0) {
                classTrainee.getTrainee().setTraineeStatus(TraineeStatus.WAITING_FOR_CLASS);
            }
        }

        clazz.setActualTraineeNo(classTraineeService.countTraineeInClass(id));
        clazzService.update(clazz);

        return success("trainee.remove.fromclass.success");
    }

    private Specification<Trainee> buildBaseSpecInClass(Long clazzId) {
        return (root, query, criteriaBuilder) -> {

            Join<Trainee, ClassTrainee> classTraineeJoin = root.join(classTraineeSetInTrainee);
            Join<ClassTrainee, Clazz> traineeClazzJoin = classTraineeJoin.join(clazzEntity);

            return criteriaBuilder.and(
                    criteriaBuilder.equal(traineeClazzJoin.get("id"), clazzId),
                    criteriaBuilder.equal(classTraineeJoin.get("deleted"), false)
            );
        };
    }


    private Specification<Trainee> buildBaseSpecToAdd(Long Id) {

        return (root, query, criteriaBuilder) -> {
            Subquery<Long> subquery = query.subquery(Long.class);
            Root<ClassTrainee> fromClassTrainee = subquery.from(ClassTrainee.class);

            subquery.select(fromClassTrainee.get("trainee").get("id"))
                    .where(criteriaBuilder.and(
                                    criteriaBuilder.equal(fromClassTrainee.get("clazz").get("id"), Id),
                                    criteriaBuilder.equal(fromClassTrainee.get("deleted"), false)
                            )
                    );

            Predicate predicate1 = criteriaBuilder.not(criteriaBuilder.in(root.get("id")).value(subquery));
            Predicate predicate2 = criteriaBuilder.notEqual(criteriaBuilder.toString(root.get("traineeStatus")), "DRAFT");

            Predicate pre = criteriaBuilder.and(predicate1, predicate2);
            query.where(pre);

            return query.getRestriction();
        };
    }


    public Page<Trainee> createSortOrdersAndPaging(Integer page, Integer size, List<String> sorts, Specification<Trainee> spec) {
        List<Sort.Order> orders = new ArrayList<>();
        for (String sortField : sorts) {
            boolean isDesc = sortField.startsWith("-");
            orders.add(isDesc ? Sort.Order.desc(sortField.substring(1)) : Sort.Order.asc(sortField));
        }
        PageRequest pageRequest = PageRequest.of(page - 1, size, Sort.by(orders));

        return traineeService.findAll(spec, pageRequest);
    }


    private Specification<Trainee> buildSpecFor(String role) {
        return (root, query, criteriaBuilder) -> {
            Join<Trainee, ClassTrainee> classTraineeJoin = root.join(classTraineeSetInTrainee);
            Join<Clazz, ClassTrainee> traineeClazzJoin = classTraineeJoin.join(clazzEntity);
            if (role.equals(trainerField)){
                return criteriaBuilder.equal(traineeClazzJoin.get("classDetail").get(role), SecurityUtil.getCurrentUserLogin().get());
            }
            return criteriaBuilder.equal(traineeClazzJoin.get(role), SecurityUtil.getCurrentUserLogin().get());
        };
    }


    private Specification<Trainee> buildSpecByStatus(String status) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(criteriaBuilder.toString(root.get("traineeStatus")), status);
    }

    private Specification<Trainee> buildSpecByKeyword(String key, String value) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(root.get(key), "%" + value + "%");
    }


    private Specification<Trainee> buildSpecBySearchParams(Map<String, String> searchParams) {
        Specification<Trainee> spec = (root, query, criteriaBuilder) -> criteriaBuilder.conjunction();

        for (Map.Entry<String, String> entry : searchParams.entrySet()) {
            if (entry.getKey().equals("traineeStatus")) {
                spec = spec.and(buildSpecByStatus(entry.getValue()));
                continue;
            }

            if (!reservedParameters.contains(entry.getKey())) {
                spec = spec.and(buildSpecByKeyword(entry.getKey(), entry.getValue()));
            }
        }

        return spec;
    }

    public List<Clazz> clazzListByRole(String role, Long clazzId) {
        Specification<Clazz> spec = (root, query, criteriaBuilder) ->
                criteriaBuilder.and(
                        criteriaBuilder.equal(root.get("deleted"), false),
                        criteriaBuilder.equal(root.get("id"), clazzId),
                        criteriaBuilder.equal(root.get(role), SecurityUtil.getCurrentUserLogin().get())
                );

        return clazzService.findAll(spec);
    }


    public List<Trainee> traineeListByRole(String role, Long id) {
        Specification<Trainee> specRole = (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("id"), id);

        specRole = specRole.and(buildSpecFor(role));

        return traineeService.findAll(specRole);
    }

    private Page<TraineeListDisplayDto> convertToDisplayDto(Page<Trainee> traineePage, Long clazzId) {
        List<TraineeListDisplayDto> displayDtos = traineePage.getContent().stream()
                .map(trainee -> {
                    TraineeListDisplayDto traineeListDisplayDto = new TraineeListDisplayDto();
                    BeanUtils.copyProperties(trainee, traineeListDisplayDto);

                    if (trainee.getUniversity() != null) {
                        traineeListDisplayDto.setUniversity(trainee.getUniversity().getName());
                    }

                    if (trainee.getFaculty() != null) {
                        traineeListDisplayDto.setFaculty(trainee.getFaculty().getName());
                    }

                    if (clazzId != null) {
                        Optional<ClassTrainee> classTrainee = classTraineeService.findTraineeInClass(clazzId, trainee.getId());

                        if (classTrainee.isPresent()) {
                            traineeListDisplayDto.setTraineeClassStatus(classTrainee.get().getTraineeClassStatus());
                        }
                    }

                    return traineeListDisplayDto;
                })
                .toList();

        return new PageImpl<>(displayDtos, traineePage.getPageable(), traineePage.getTotalElements());
    }

    public Trainee copyFormDtoToTrainee(TraineeFormDto traineeFormDto, Trainee trainee) {
        BeanUtils.copyProperties(traineeFormDto, trainee);

        if (traineeFormDto.getUniversityId() != null) {
            University university = universityService.findOne(traineeFormDto.getUniversityId());
            trainee.setUniversity(university);
        }

        if (traineeFormDto.getFacultyId() != null) {
            Faculty faculty = facultyService.findOne(traineeFormDto.getFacultyId());
            trainee.setFaculty(faculty);
        }

        if (!traineeFormDto.getSalary()) {
            trainee.setAllowanceGroup(null);
            trainee.setTpbAccount(null);
            trainee.setContractLength(null);
            trainee.setContractStartDate(null);
        }
        return trainee;
    }

    public TraineeFormDto copyTraineeToDto(Trainee trainee) {
        TraineeFormDto traineeFormDto = new TraineeFormDto();

        BeanUtils.copyProperties(trainee, traineeFormDto);

        if (trainee.getUniversity() != null) {
            traineeFormDto.setUniversityId(trainee.getUniversity().getId());
            traineeFormDto.setUniversityName(trainee.getUniversity().getName());
        }

        if (trainee.getFaculty() != null) {
            traineeFormDto.setFacultyId(trainee.getFaculty().getId());
            traineeFormDto.setFacultyName(trainee.getFaculty().getName());
        }

        return traineeFormDto;
    }


}
