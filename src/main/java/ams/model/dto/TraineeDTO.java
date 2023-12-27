package ams.model.dto;

import ams.enums.AllowanceGroup;
import ams.enums.Gender;
import ams.enums.TraineeStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

@Getter
@Setter
@ToString
public class TraineeDTO {

    @NotNull(message = "{trainee.account.required}")
    private String account;

    @NotNull(message = "{trainee.fullName.required}")
    private String fullName;

    @NotNull(message = "{trainee.dateOfBirth.required}")
    private LocalDate dateOfBirth;

    @NotNull(message = "{trainee.gender.required}")
    private Gender gender;

    @NotNull(message = "{trainee.phone.required}")
    @Pattern(regexp = "^(\\+84|0)\\d{9}$")
    private String phone;

    @Email
    @NotNull(message = "{trainee.email.required}")
    private String email;

    private TraineeStatus traineeStatus;

    @NotNull(message = "{trainee.salary.required}")
    private Boolean salary;

    private String tpbAccount;

    private LocalDate contractStartDate;

    private Integer contractLength;

    private AllowanceGroup allowanceGroup;

    private String faculty;

    private String university;
}
