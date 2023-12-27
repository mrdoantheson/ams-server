package ams.enums;

public enum Location {
    CAU_GIAY("CAU GIAY"),
    HOA_LAC("HOA LAC"),
    THANH_CONG("THANH CONG"),
    KEANGNAM("KEANGNAM"),
    HA_NOI("HA NOI");

    private final String name;
    Location(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }
}
