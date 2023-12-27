package ams.model.dto;

import ams.enums.UserRole;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmployeeListDisplayDto {

    private Long id;

    private String account;

    private UserRole userRole;

    private String email;
}
