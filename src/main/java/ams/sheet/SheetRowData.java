package ams.sheet;

public interface SheetRowData {

    String getCellName();
    int getColumnIndex();

    String getColumnCharacter();

    String getFieldName();

    boolean isRequired();

    Class getFieldType();



}
