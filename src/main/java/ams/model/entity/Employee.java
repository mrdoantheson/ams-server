package ams.model.entity;

import ams.enums.UserRole;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Employee extends BaseEntity{

    private String account;

    private String email;

    @Enumerated(EnumType.STRING)
    private UserRole role;
}
