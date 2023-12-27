package ams.enums;

public enum SubjectType {
    ORGANIZATIONAL("ORGANIZATIONAL OVERVIEW & CULTURE"),
    COMPANY_PROCESS("COMPANY PROCESS"),
    STANDARD_PROCESS("STANDARD PROCESS"),
    IT_TECHNICAL("IT TECHNICAL"),
    NON_IT_TECHNICAL("NON IT TECHNICAL"),
    FOREIGN_LANGUAGE("FOREIGN LANGUAGE"),
    SOFT_SKILLS("SOFT SKILLS"),
    MANAGEMENT("MANAGEMENT"),
    ;


    private final String name;
    SubjectType(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }
}
