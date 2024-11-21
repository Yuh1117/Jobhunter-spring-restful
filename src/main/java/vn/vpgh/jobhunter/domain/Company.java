package vn.vpgh.jobhunter.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import vn.vpgh.jobhunter.util.SecurityUtil;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonFormat;

@Entity
@Table(name = "companies")
@Getter
@Setter
public class Company {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotBlank(message = "Name cannot be empty")
    private String name;

    @Column(columnDefinition = "MEDIUMTEXT")
    private String description;

    private String address;
    private String logo;

    // @JsonFormat(pattern = "MM-dd-yyyy hh:mm:ss a", timezone = "GMT+7")
    private Instant createAt;
    private Instant updateAt;
    private String createBy;
    private String updateBy;

    @PrePersist
    public void handleBeforeCreate() {
        this.createBy = SecurityUtil.getCurrentUserLogin().isPresent() ? SecurityUtil.getCurrentUserLogin().get() : "";
        this.createAt = Instant.now();
    }

    @PreUpdate
    public void handleBeforeUpdate() {
        this.updateBy = SecurityUtil.getCurrentUserLogin().isPresent() ? SecurityUtil.getCurrentUserLogin().get() : "";
        this.updateAt = Instant.now();
    }
}
