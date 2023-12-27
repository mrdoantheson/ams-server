package ams.service.impl;


import ams.model.dto.ImportError;
import ams.model.dto.ImportResult;
import ams.model.entity.ClassTrainee;
import ams.model.entity.Trainee;
import ams.service.*;
import ams.sheet.SheetRowData;
import ams.utils.WorkbookUtils;
import ams.validation.EmailValidator;
import ams.validation.PhoneValidator;
import lombok.AllArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.*;

@AllArgsConstructor
@Service
public class SheetServiceImpl implements SheetService {

    private TraineeService traineeService;

    private ClassTraineeService classTraineeService;

    private UniversityService universityService;

    private FacultyService facultyService;

    private EmailValidator emailValidator;

    private PhoneValidator phoneValidator;


    @Override
    public <T, R extends SheetRowData> ImportResult<T> importFile(MultipartFile multipartFile,
                                                                  String sheetName, Class<R> sheetRowData,
                                                                  Class<T> objectType) throws IOException {
        List<String> accountList = new ArrayList<>();
        List<String> phoneList = new ArrayList<>();
        List<String> emailList = new ArrayList<>();


        String originalFilename = multipartFile.getOriginalFilename();
        if (originalFilename == null || (!originalFilename.endsWith(".xls") && !originalFilename.endsWith(".xlsx"))) {
            throw new IOException("Only files ending in .xls or .xlsx  are accepted!");
        }

        long sizeInMb = multipartFile.getSize() / (1024 * 1024);
        if (sizeInMb > 1) {
            throw new IOException("Only files up to 1MB in size are accepted!");
        }

        try (XSSFWorkbook workbook = new XSSFWorkbook(multipartFile.getInputStream())) {
            XSSFSheet sheet = workbook.getSheet(sheetName);
            List<ImportError> errors = new LinkedList<>();
            List<T> importData = new LinkedList<>();

            for (Row row : sheet) {
                if (row.getRowNum() < 1) {
                    continue;
                }

                T dataObject;
                try {
                    dataObject = objectType.getConstructor().newInstance();
                } catch (Exception e) {
                    return new ImportResult<>(e.getMessage(), null, null);
                }

                for (SheetRowData cell : sheetRowData.getEnumConstants()) {
                    Object value;

                    if (!isValidateCell(errors, sheet, cell,
                            row.getRowNum() + 1, cell.getColumnIndex())) {
                        continue;
                    }

                    try {
                        value = WorkbookUtils.getValueAt(sheet, row.getRowNum() + 1, cell.getColumnIndex(), cell.getFieldType());

                        if (cell.getColumnIndex() == 1) {
                            if (traineeService.existAccountTrainee(value.toString())) {
                                addImportError(errors, sheetName, row, cell, "%s already exists!");
                            }
                            accountList.add(value.toString());
                        }

                        if (cell.getColumnIndex() == 5) {

                            if (!phoneValidator.isValidPhone(value.toString())) {
                                addImportError(errors, sheetName, row, cell, "Invalid %s number!");
                            }

                            if (traineeService.existPhoneTrainee(value.toString())) {
                                addImportError(errors, sheetName, row, cell, "%s already exists!");
                            }
                            phoneList.add(value.toString());
                        }

                        if (cell.getColumnIndex() == 6) {

                            if (!emailValidator.isValidEmail(value.toString())) {
                                addImportError(errors, sheetName, row, cell, "Invalid %s address!");
                            }

                            if (traineeService.existEmailTrainee(value.toString())) {
                                addImportError(errors, sheetName, row, cell, "%s already exists!");
                            }
                            emailList.add(value.toString());
                        }

                        if (cell.getColumnIndex() == 13) {
                            if (value == null) {
                                continue;
                            }

                            if (facultyService.findFacultyByName(value.toString()).isEmpty()) {
                                addImportError(errors, sheetName, row, cell, "%s is doen't exist!");
                            }
                        }

                        if (cell.getColumnIndex() == 14) {
                            if (value == null) {
                                continue;
                            }
                            if (universityService.findUniByName(value.toString()).isEmpty()) {
                                addImportError(errors, sheetName, row, cell, "%s is doen't exist!");
                            }
                        }




                    } catch (IllegalStateException e) {
                        ImportError importError = new ImportError();
                        importError.setSheet(sheetName);
                        importError.setAddress(cell.getColumnCharacter() + (row.getRowNum() + 1));
                        importError.setDetail(String.format("%s must be %s", cell.getCellName(), cell.getFieldType().getSimpleName()));
                        errors.add(importError);
                        continue;
                    } catch (IllegalArgumentException e) {
                        addImportError(errors, sheetName, row, cell, "%s is not valid!");
                        continue;
                    }

                    try {
                        Field field = objectType.getDeclaredField(cell.getFieldName());
                        field.setAccessible(true);
                        field.set(dataObject, value);
                    } catch (NoSuchFieldException | IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
                importData.add(dataObject);
            }

            if (!isUnique(accountList)){
                ImportError importError = new ImportError();
                importError.setSheet(sheetName);
                importError.setAddress("Columm A");
                importError.setDetail("Account must be unique!");
                errors.add(importError);
            }

            if (!isUnique(phoneList)){
                ImportError importError = new ImportError();
                importError.setSheet(sheetName);
                importError.setAddress("Columm E");
                importError.setDetail("Phone must be unique!");
                errors.add(importError);
            }

            if (!isUnique(emailList)){
                ImportError importError = new ImportError();
                importError.setSheet(sheetName);
                importError.setAddress("Columm F");
                importError.setDetail("Email must be unique!");
                errors.add(importError);
            }

            if (errors.isEmpty()) {
                return new ImportResult<>("Upload success", errors, importData);
            }
            return new ImportResult<>("Upload failed", errors, Collections.emptyList());
        }
    }


    @Override
    public <T, R extends SheetRowData> ImportResult<T> importFileToClass(MultipartFile multipartFile,
                                                                  String sheetName, Class<R> sheetRowData,
                                                                  Class<T> objectType, Long clazzId) throws IOException {
        List<String> accountList = new ArrayList<>();
        List<String> phoneList = new ArrayList<>();
        List<String> emailList = new ArrayList<>();


        String originalFilename = multipartFile.getOriginalFilename();
        if (originalFilename == null || (!originalFilename.endsWith(".xls") && !originalFilename.endsWith(".xlsx"))) {
            throw new IOException("Only files ending in .xls or .xlsx  are accepted!");
        }

        long sizeInMb = multipartFile.getSize() / (1024 * 1024);
        if (sizeInMb > 1) {
            throw new IOException("Only files up to 1MB in size are accepted!");
        }

        try (XSSFWorkbook workbook = new XSSFWorkbook(multipartFile.getInputStream())) {
            XSSFSheet sheet = workbook.getSheet(sheetName);
            List<ImportError> errors = new LinkedList<>();
            List<T> importData = new LinkedList<>();

            for (Row row : sheet) {
                if (row.getRowNum() < 1) {
                    continue;
                }

                T dataObject;
                try {
                    dataObject = objectType.getConstructor().newInstance();
                } catch (Exception e) {
                    return new ImportResult<>(e.getMessage(), null, null);
                }

                for (SheetRowData cell : sheetRowData.getEnumConstants()) {
                    Object value;

                    if (!isValidateCell(errors, sheet, cell,
                            row.getRowNum() + 1, cell.getColumnIndex())) {
                        continue;
                    }

                    try {
                        value = WorkbookUtils.getValueAt(sheet, row.getRowNum() + 1, cell.getColumnIndex(), cell.getFieldType());

                        if (cell.getColumnIndex() == 1) {
                            Optional<Trainee> traineeOpt = traineeService.findByAccount(value.toString());
                            if (traineeOpt.isPresent()){
                                Optional<ClassTrainee> classTraineeOpt = classTraineeService.findTraineeInClass(clazzId, traineeOpt.get().getId());
                                if (classTraineeOpt.isPresent()){
                                    addImportError(errors, sheetName, row, cell, "Trainee Already In Class!");
                                }
                            }
                            accountList.add(value.toString());
                        }

                        if (cell.getColumnIndex() == 5) {

                            if (!phoneValidator.isValidPhone(value.toString())) {
                                addImportError(errors, sheetName, row, cell, "Invalid %s number!");
                            }

                            if (traineeService.existPhoneTrainee(value.toString())) {
                                addImportError(errors, sheetName, row, cell, "%s already exists!");
                            }
                            phoneList.add(value.toString());
                        }

                        if (cell.getColumnIndex() == 6) {

                            if (!emailValidator.isValidEmail(value.toString())) {
                                addImportError(errors, sheetName, row, cell, "Invalid %s address!");
                            }

                            if (traineeService.existEmailTrainee(value.toString())) {
                                addImportError(errors, sheetName, row, cell, "%s already exists!");
                            }
                            emailList.add(value.toString());
                        }


                        if (cell.getColumnIndex() == 13) {
                            if (value == null) {
                                continue;
                            }

                            if (facultyService.findFacultyByName(value.toString()).isEmpty()) {
                                addImportError(errors, sheetName, row, cell, "%s is doen't exist!");
                            }
                        }

                        if (cell.getColumnIndex() == 14) {
                            if (value == null) {
                                continue;
                            }
                            if (universityService.findUniByName(value.toString()).isEmpty()) {
                                addImportError(errors, sheetName, row, cell, "%s is doen't exist!");
                            }
                        }




                    } catch (IllegalStateException e) {
                        ImportError importError = new ImportError();
                        importError.setSheet(sheetName);
                        importError.setAddress(cell.getColumnCharacter() + (row.getRowNum() + 1));
                        importError.setDetail(String.format("%s must be %s", cell.getCellName(), cell.getFieldType().getSimpleName()));
                        errors.add(importError);
                        continue;
                    } catch (IllegalArgumentException e) {
                        addImportError(errors, sheetName, row, cell, "%s is not valid!");
                        continue;
                    }

                    try {
                        Field field = objectType.getDeclaredField(cell.getFieldName());
                        field.setAccessible(true);
                        field.set(dataObject, value);
                    } catch (NoSuchFieldException | IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
                importData.add(dataObject);
            }

            if (!isUnique(accountList)){
                ImportError importError = new ImportError();
                importError.setSheet(sheetName);
                importError.setAddress("Columm A");
                importError.setDetail("Account must be unique!");
                errors.add(importError);
            }

            if (!isUnique(phoneList)){
                ImportError importError = new ImportError();
                importError.setSheet(sheetName);
                importError.setAddress("Columm E");
                importError.setDetail("Phone must be unique!");
                errors.add(importError);
            }

            if (!isUnique(emailList)){
                ImportError importError = new ImportError();
                importError.setSheet(sheetName);
                importError.setAddress("Columm F");
                importError.setDetail("Email must be unique!");
                errors.add(importError);
            }

            if (errors.isEmpty()) {
                return new ImportResult<>("Upload success", errors, importData);
            }
            return new ImportResult<>("Upload failed", errors, Collections.emptyList());
        }
    }




    @Override
    public <T, E extends SheetRowData> ByteArrayInputStream exportFile(InputStream inputStream,
                                                                       String sheetName,
                                                                       Class<E> sheetRowData,
                                                                       Iterable<T> data) throws IOException {

        try (XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
             ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            XSSFSheet sheet = workbook.getSheet(sheetName);
            int rowIdx = 1;
            for (T row : data) {
                for (SheetRowData cell : sheetRowData.getEnumConstants()) {
                    Field field;
                    Object value;

                    try {
                        field = row.getClass().getDeclaredField(cell.getFieldName());
                        field.setAccessible(true);
                        value = field.get(row);
                    } catch (NoSuchFieldException | IllegalAccessException e) {
                        e.printStackTrace();
                        continue;
                    }
                    WorkbookUtils.setValueAt(sheet, rowIdx, cell.getColumnIndex(), cell.getFieldType(), value);
                }
                rowIdx++;
            }
            workbook.write(byteArrayOutputStream);
            return new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
        }
    }

    private boolean isValidateCell(List<ImportError> importErrors, Sheet sheet, SheetRowData cell, int rowIndex, int columnIndex) {

        if (cell.isRequired() && WorkbookUtils.isEmptyCell(sheet, rowIndex, columnIndex)) {
            ImportError importError = new ImportError();
            importError.setSheet(sheet.getSheetName());
            importError.setAddress(cell.getColumnCharacter() + rowIndex);
            importError.setDetail(String.format("%s is required", cell.getCellName()));
            importErrors.add(importError);
            return false;
        }
        return true;
    }

    private void addImportError(List<ImportError> errorList,
                                String sheetName,
                                Row row,
                                SheetRowData cell,
                                String detail
    ) {

        ImportError importError = new ImportError();
        importError.setSheet(sheetName);
        importError.setAddress(cell.getColumnCharacter() + (row.getRowNum() + 1));
        importError.setDetail(String.format(detail, cell.getCellName()));
        errorList.add(importError);
    }

    public static boolean isUnique(List<String> list) {
        Set<String> set = new HashSet<String>();

        for(String s: list) {
            if(!set.add(s)) {
                return false;
            }
        }
        return true;
    }


}
