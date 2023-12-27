package ams.resource;

import ams.model.dto.BaseResponseDto;
import ams.model.entity.ClassDetail;
import ams.model.entity.Clazz;
import ams.service.ClassDetailService;
import ams.service.ClazzService;
import ams.service.FilesStorageService;
import ams.service.impl.FilesStorageServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@RestController
@AllArgsConstructor
@RequestMapping("/api")
public class FilesResource extends BaseResource {

    private ClazzService clazzService;
    private ClassDetailService classDetailService;


    @PostMapping("/upload/{classCode}")
    public ResponseEntity<BaseResponseDto> uploadFile(@RequestParam(value = "learningPath", required = false) MultipartFile fileLP,
                                                      @RequestParam(value = "curriculum", required = false) MultipartFile fileCC,
                                                      @PathVariable String classCode
    ) {
        if (fileLP != null) {
            long sizeInMb = fileLP.getSize() / (1024 * 1024);
            if (sizeInMb > 1) {
                return badRequest("Only files up to 1MB in size are accepted!");
            }
            String fileName = fileLP.getOriginalFilename();
            if (fileName == null || (!fileName.endsWith(".xls") && !fileName.endsWith(".xlsx"))) {
                return badRequest("Only files ending in .xls or .xlsx  are accepted!");
            }
        }

        if (fileCC != null) {
            long sizeInMb = fileCC.getSize() / (1024 * 1024);
            if (sizeInMb > 1) {
                return badRequest("Only files up to 1MB in size are accepted!");
            }
            String fileName = fileCC.getOriginalFilename();
            if (fileName == null || (!fileName.endsWith(".xls") && !fileName.endsWith(".xlsx"))) {
                return badRequest("Only files ending in .xls or .xlsx  are accepted!");
            }
        }

        FilesStorageService filesStorageService = new FilesStorageServiceImpl(classCode);
        filesStorageService.init();

        Optional<Clazz> clazzOpt = clazzService.findClazzByCode(classCode);
        Optional<ClassDetail> classDetailOpt = classDetailService.findOneByClassId(clazzOpt.get().getId());

        if (clazzOpt.isEmpty()) {
            return notFound("class.resource.notFound");
        }

        Clazz clazz = clazzOpt.get();
        String fieldLearningPath = clazz.getLearningPath();
        ClassDetail classDetail = classDetailOpt.get();

        String fieldCurriculum = classDetail.getCurriculum();

        String fileLearningPath = null;
        String fileCurriculum = null;

        try {
            fileLearningPath = filesStorageService.save(fileLP);

        } catch (RuntimeException e) {
        }

        try {
            fileCurriculum = filesStorageService.save(fileCC);

        } catch (RuntimeException e) {
        }

        if (fileLearningPath != null) {
            String lP = "http://localhost:8888/api/files/" + classCode + "/" + fileLearningPath;
            clazz.setLearningPath(lP);
            if (fieldLearningPath != null && !lP.equals(fieldLearningPath)) {
                int lastSlashIndex = fieldLearningPath.lastIndexOf('/');
                String filename = fieldLearningPath.substring(lastSlashIndex + 1);
                ResponseEntity<BaseResponseDto> deleteResponse = deleteFile(filename, classCode);

                if (deleteResponse.getStatusCode() == HttpStatus.BAD_REQUEST) {
                    return badRequest("Could not delete the old file!");
                }
            }
        }

        if (fileCurriculum != null) {
            String cC = "http://localhost:8888/api/files/" + classCode + "/" + fileCurriculum;
            classDetail.setCurriculum(cC);

            if (fieldCurriculum != null && !cC.equals(fieldCurriculum)) {
                int lastSlashIndex = fieldCurriculum.lastIndexOf('/');
                String filename = fieldCurriculum.substring(lastSlashIndex + 1);
                ResponseEntity<BaseResponseDto> deleteResponse = deleteFile(filename, classCode);

                if (deleteResponse.getStatusCode() == HttpStatus.BAD_REQUEST) {
                    return badRequest("Could not delete the old file!");
                }
            }
        }

        clazzService.update(clazz);

        if (fileLP != null || fileCC != null) {
            return success("upload.successfully");
        }

        return success("ok");
    }


    public ResponseEntity<BaseResponseDto> deleteFile(String filename, String classCode) {
        FilesStorageService filesStorageService = new FilesStorageServiceImpl(classCode);
        filesStorageService.init();


        try {
            boolean existed = filesStorageService.delete(filename);

            if (existed) {
                return success("Delete the file successfully: " + filename);
            }
            return notFound("The file does not exist!");
        } catch (Exception e) {
            return badRequest("Could not delete the file: " + filename + ". Error: " + e.getMessage());
        }
    }


    @GetMapping("/files/{classCode}/{filename:.+}")
    public ResponseEntity<Resource> getFile(@PathVariable String filename,
                                            @PathVariable String classCode) {

        FilesStorageService filesStorageService = new FilesStorageServiceImpl(classCode);
        filesStorageService.init();

        Resource file = filesStorageService.load(filename);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }
}
