package ams.utils;

import ams.enums.AllowanceGroup;
import ams.enums.Gender;
import ams.enums.TraineeStatus;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.time.LocalDate;
import java.time.ZoneId;

public class WorkbookUtils {

    public static <T> T getValueAt(Sheet sheet, int rowIndex, int columnIndex, Class<T> classType) throws IllegalStateException {
        Row row = sheet.getRow(rowIndex - 1);
        if (row == null) {
            return null;
        }

        Cell cell = row.getCell(columnIndex - 1);
        if (isEmptyCell(cell)) {
            return null;
        }

        if (cell.getColumnIndex() == 3) {
            Gender gender = Gender.valueOf(cell.toString());
        }

        if (cell.getColumnIndex() == 6) {
            TraineeStatus traineeStatus = TraineeStatus.valueOf(cell.toString());
        }

        if (cell.getColumnIndex() == 11) {
           AllowanceGroup allowanceGroup = AllowanceGroup.valueOf(cell.toString());
        }

        return getValueAt1(sheet, cell, classType);
    }

    public static <T> T getValueAt1(Sheet sheet, Cell cell, Class<T> classType) throws IllegalStateException {

        if (classType.isEnum()) {
            String cellValue = cell.getStringCellValue();
            for (Object enumValue : classType.getEnumConstants()) {
                if (enumValue.toString().equals(cellValue.toUpperCase())) {
                    return (T) enumValue;
                }
            }
        }
        switch (classType.getSimpleName()) {
            case "Integer":
                return (T) Integer.valueOf((int) cell.getNumericCellValue());
            case "Boolean":
                return (T) Boolean.valueOf(cell.getBooleanCellValue());
            case "Double":
                return (T) Double.valueOf(cell.getNumericCellValue());
            case "String":
                return (T) cell.getStringCellValue();
            case "LocalDate":
                return (T) cell.getDateCellValue()
                        .toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate();
        }
        return null;
    }

    public static <T> void setValueAt(Sheet sheet, int rowIndex, int columnIndex, Class<T> classType, Object value) {
        Row row = sheet.getRow(rowIndex - 1);
        if (row == null) {
            row = sheet.createRow(rowIndex - 1);
        }
        Cell cell = row.getCell(columnIndex - 1);
        if (cell == null) {
            cell = row.createCell(columnIndex - 1);
        }
        switch (classType.getSimpleName()) {
            case "Integer":
                cell.setCellValue((int) value);
                break;
            case "Double":
                cell.setCellValue((double) value);
                break;
            case "String":
                cell.setCellValue((String) value);
                break;
            case "LocalDate":
                cell.setCellValue((LocalDate) value);
                break;
        }


    }

    public static boolean isEmptyCell(Sheet sheet, int rowIndex, int columnIndex) {
        Row row = sheet.getRow(rowIndex - 1);
        if (row == null) {
            return false;
        }
        Cell cell = row.getCell(columnIndex - 1);
        return isEmptyCell(cell);
    }

    public static boolean isEmptyCell(Cell cell) {
        return cell == null || cell.getCellType() == CellType.BLANK;
    }

}
