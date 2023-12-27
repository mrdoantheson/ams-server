package ams.utils;

import ams.enums.AllowanceGroup;
import ams.enums.Gender;
import ams.enums.TraineeClassStatus;
import ams.enums.TraineeStatus;
import ams.exception.InvalidEnumValueException;
import ams.model.entity.Faculty;
import ams.model.entity.Trainee;
import ams.model.entity.University;
import ams.service.FacultyService;
import ams.service.UniversityService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

@Component
public class ExcelUtils {

    static UniversityService universityService;

    static FacultyService facultyService;

    public ExcelUtils(@Autowired UniversityService universityService, @Autowired FacultyService facultyService) {
        this.universityService = universityService;
        this.facultyService = facultyService;
    }

    public static <T extends Enum<T>> boolean isEnumValueIgnoreCase(Class<T> enumClass, String value) {
        try {
            T[] enumValues = enumClass.getEnumConstants();
            for (T enumValue : enumValues) {
                if (enumValue.name().equalsIgnoreCase(value)) {
                    return true;
                }
            }
            return false;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private static LocalDate getLocalDateCellValue(Cell cell) {
        if (cell == null) {
            return null;
        }
        if (cell.getCellType() == CellType.NUMERIC) {
            return cell.getLocalDateTimeCellValue().toLocalDate();
        }
        return LocalDate.parse(cell.getStringCellValue().trim());
    }

    private static String getStringCellValue(Cell cell) {
        if (cell == null) {
            return null;
        }
        cell.setCellType(CellType.STRING);
        return cell.getStringCellValue().trim();
    }

    private static Gender getGenderCellValue(Row row) throws InvalidEnumValueException {
        String genderValue = getStringCellValue(row.getCell(3));
        boolean isValidGender = isEnumValueIgnoreCase(Gender.class, genderValue);
        if (isValidGender) {
            return Gender.valueOf(genderValue);
        } else {
            throw new InvalidEnumValueException("Invalid gender value: " + genderValue);
        }

    }

    private static AllowanceGroup getAllowanceGroupCellValue(Row row) {
        String allowanceGroupValue = getStringCellValue(row.getCell(11));
        boolean isValidAllowanceGroupValue = isEnumValueIgnoreCase(AllowanceGroup.class, allowanceGroupValue);
        if (isValidAllowanceGroupValue) {
            return AllowanceGroup.valueOf(allowanceGroupValue);
        } else
            return null;
    }

    private static TraineeStatus getTraineeStatusCellValue(Row row) throws InvalidEnumValueException {
        String traineeStatusValue = getStringCellValue(row.getCell(8));
        boolean isValidTraineeStatusValue = isEnumValueIgnoreCase(TraineeStatus.class, traineeStatusValue);
        if (isValidTraineeStatusValue) {
            return TraineeStatus.valueOf(traineeStatusValue);
        } else {
            throw new InvalidEnumValueException("Invalid trainee status value: " + traineeStatusValue);
        }
    }

    private static TraineeClassStatus getTraineeClassStatusCellValue(Row row) throws InvalidEnumValueException {
        String traineeClassStatus = getStringCellValue(row.getCell(14));
        boolean isValidTraineeClassStatusValue = isEnumValueIgnoreCase(TraineeClassStatus.class, traineeClassStatus);
        if (isValidTraineeClassStatusValue) {
            return TraineeClassStatus.valueOf(traineeClassStatus);
        } else {
            throw new InvalidEnumValueException("Invalid trainee class status value: " + traineeClassStatus);
        }
    }

    private static Double getDoubleCellValue(Cell cell) {
        if (cell != null && cell.getCellType() == CellType.NUMERIC) {
            return cell.getNumericCellValue();
        }
        return null;
    }

    private static Long getLongCellValue(Cell cell) {
        if (cell != null && cell.getCellType() == CellType.NUMERIC) {
            return (long) cell.getNumericCellValue();
        }
        return null;
    }

    private static Boolean getBooleanCellValue(Cell cell) {
        if (cell != null) {
            return cell.getBooleanCellValue();
        } else {
            throw new IllegalArgumentException("Invalid data type");
        }

    }

    private static <T> T getCellValueAsEntity(Cell cell, Class<T> entityClass) {
        String value = getStringCellValue(cell);

        return null;
    }

    private static Boolean getCellValueAsBoolean(Cell cell) {
        if (cell == null) {
            return false;
        }

        if (cell.getCellType() == CellType.BOOLEAN) {
            return cell.getBooleanCellValue();
        } else {
            return null;
        }
    }

    public static List<Trainee> readTraineesFromExcel(InputStream inputStream) throws IOException, InvalidEnumValueException {
        Workbook workbook = new XSSFWorkbook(inputStream);
        Sheet sheet = workbook.getSheetAt(0);
        Iterator<Row> rowIterator = sheet.iterator();

        List<Trainee> traineeList = new ArrayList<>();

        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();

            if (row.getRowNum() == 0) {
                continue;
            }

            Trainee trainee = new Trainee();
            trainee.setAccount(row.getCell(0).getStringCellValue());
            trainee.setFullName(row.getCell(1).getStringCellValue());
            trainee.setDateOfBirth(getLocalDateCellValue(row.getCell(2)));
            trainee.setGender(getGenderCellValue(row));

            // set University
            Optional<University> universityOptional = universityService.findUniByName(getStringCellValue(row.getCell(4)));

            if (universityOptional.isEmpty()) {
                throw new IOException("Can not find university with name: " + getStringCellValue(row.getCell(4)) + " in the database");
            }

            trainee.setUniversity(universityOptional.get());

            // set Faculty
            Optional<Faculty> facultyOptional = facultyService.findFacultyByName(getStringCellValue(row.getCell(5)));

            if (facultyOptional.isEmpty()) {
                throw new IOException("Can not find faculty with name: " + getStringCellValue(row.getCell(5)) + " in the database");
            }

            trainee.setFaculty(facultyOptional.get());

            trainee.setPhone(getStringCellValue(row.getCell(6)));
            trainee.setEmail(getStringCellValue(row.getCell(7)));

            trainee.setTraineeStatus(getTraineeStatusCellValue(row));

            trainee.setSalary(getCellValueAsBoolean(row.getCell(10)));
            if (getCellValueAsBoolean(row.getCell(10)) == true) {
                trainee.setTpbAccount(getStringCellValue(row.getCell(9)));

                trainee.setAllowanceGroup(getAllowanceGroupCellValue(row));
                trainee.setContractStartDate(getLocalDateCellValue(row.getCell(12)));
                trainee.setContractLength(getLongCellValue(row.getCell(13)));
            }

          //  trainee.setTraineeClassStatus(getTraineeClassStatusCellValue(row));
            traineeList.add(trainee);
        }

        workbook.close();

        return traineeList;
    }
}

