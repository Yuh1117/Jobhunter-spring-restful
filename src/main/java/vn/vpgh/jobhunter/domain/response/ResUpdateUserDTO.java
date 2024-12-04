package vn.vpgh.jobhunter.domain.response;

import java.time.Instant;

import lombok.Getter;
import lombok.Setter;
import vn.vpgh.jobhunter.util.constant.GenderEnum;

@Getter
@Setter
public class ResUpdateUserDTO {
    private long id;
    private String name;
    private String email;
    private GenderEnum gender;
    private String address;
    private int age;
    private Instant updatedAt;
    private CompanyUser company;
    private RoleUser role;

    @Getter
    @Setter
    public static class CompanyUser {
        private long id;
        private String name;

    }

    @Getter
    @Setter
    public static class RoleUser {
        private long id;
        private String name;

    }
}
