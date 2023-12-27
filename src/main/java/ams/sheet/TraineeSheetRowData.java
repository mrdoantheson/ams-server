package ams.sheet;


import ams.enums.AllowanceGroup;
import ams.enums.Gender;
import ams.enums.TraineeStatus;

import java.time.LocalDate;

public enum TraineeSheetRowData implements SheetRowData {

    ACCOUNT("account", 1, "A", true, "account", String.class),
    FULL_NAME("full name", 2, "B", true, "fullName", String.class),
    DATE_OF_BIRTH("dob", 3, "C", true, "dateOfBirth", LocalDate.class),
    GENDER("gender", 4, "D", true, "gender", Gender.class),
    PHONE("phone", 5, "E", true, "phone", String.class),
    EMAIL("email", 6, "F", true, "email", String.class),
    TRAINEE_STATUS("trainee status", 7, "G", false, "traineeStatus", TraineeStatus.class),
    SALARY("salary", 8, "H", true, "salary", Boolean.class),
    TPB_ACCOUNT("tpb account", 9, "I", false, "tpbAccount", String.class),
    CONTRACT_START_DATE("contract start date", 10, "J", false, "contractStartDate", LocalDate.class),
    CONTRACT_LENGTH("contract length", 11, "K", false, "contractLength", Integer.class),
    ALLOWANCE_GROUP("allowance group", 12, "L", false, "allowanceGroup", AllowanceGroup.class),
    FACULTY("faculty", 13, "M", false, "faculty", String.class),
    UNIVERSITY("university", 14, "N", false, "university", String.class);

    private String cellName;
    private int columnIndex;
    private String columnCharacter;
    private boolean isRequired;
    private String fieldName;
    private Class fieldType;

    public static final String SHEET_NAME = "Trainee";

    TraineeSheetRowData(String cellName, int columnIndex, String columnCharacter, boolean isRequired, String fieldName, Class fieldType) {
        this.cellName = cellName;
        this.columnIndex = columnIndex;
        this.columnCharacter = columnCharacter;
        this.isRequired = isRequired;
        this.fieldName = fieldName;
        this.fieldType = fieldType;
    }

    @Override
    public String getCellName() {
        return cellName;
    }

    @Override
    public int getColumnIndex() {
        return columnIndex;
    }

    @Override
    public String getColumnCharacter() {
        return columnCharacter;
    }

    @Override
    public String getFieldName() {
        return fieldName;
    }

    @Override
    public boolean isRequired() {
        return isRequired;
    }

    @Override
    public Class getFieldType() {
        return fieldType;
    }
}
