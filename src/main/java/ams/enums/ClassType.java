package ams.enums;

public enum ClassType {
    FRESHER_DEVELOPER_JAVA("FRESHER DEVELOPER JAVA"),
    FRESHER_DEVELOPER_NET("FRESHER DEVELOPER .NET"),
    FRESHER_EMB("FRESHER EMB"),
    FRESHER_NRI("FRESHER NRI"),
    FRESHER_ANDROID("FRESHER ANDROID");

    private final String name;
    ClassType(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }
}
