package vn.vpgh.jobhunter.util.constant;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum GenderEnum {
    MALE,
    FEMALE,
    OTHER;

    @JsonCreator
    public static GenderEnum fromString(String value) {
        for (GenderEnum gender : GenderEnum.values()) {
            if (gender.name().equalsIgnoreCase(value)) {
                return gender;
            }
        }
        throw new IllegalArgumentException("Invalid gender: " + value);
    }

}
