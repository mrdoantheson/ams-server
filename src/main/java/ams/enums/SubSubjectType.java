package ams.enums;

public enum SubSubjectType {
    CLOUD("CLOUD"),
    BIG_DATA("BIG DATA"),
    CAD("CAD"),
    CAE("CAE"),
    SAP("SAP"),
    IT_GENERAL("IT GENERAL"),
    TEST("TEST"),
    OTHERS("OTHERS");

    private final String name;
    SubSubjectType(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }
}
