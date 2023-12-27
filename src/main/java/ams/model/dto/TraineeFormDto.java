package ams.model.dto;

import ams.enums.AllowanceGroup;
import ams.enums.Gender;
import ams.enums.TraineeClassStatus;
import ams.enums.TraineeStatus;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.NumberFormat;

import java.time.LocalDate;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TraineeFormDto {

  //  private Long traineeId;

    @NotNull(message = "{trainee.account.required}")
    private String account;

    @NotNull(message = "{trainee.fullName.required}")
    private String fullName;

    @NotNull(message = "{trainee.dateOfBirth.required}")
    @PastOrPresent(message = "{trainee.dob.notfuture}")
    private LocalDate dateOfBirth;

    @NotNull(message = "{trainee.gender.required}")
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @NotNull(message = "{trainee.phone.required}")
    @Pattern(regexp = "^(\\+84|0)\\d{9}$", message = "{trainee.phone.format}")
    private String phone;

    @NotNull(message = "{trainee.email.required}")
    @Email(message = "{trainee.email.format}")
    @Column(unique = true)
    private String email;

    @Enumerated(EnumType.STRING)
    private TraineeStatus traineeStatus;

    @Enumerated(EnumType.STRING)
    private TraineeClassStatus traineeClassStatus;

    @NotNull(message = "{trainee.salary.required}")
    private Boolean salary;

    private String tpbAccount;

    private LocalDate contractStartDate;

    private Long contractLength;

    private AllowanceGroup allowanceGroup;

    private Long universityId;

    private String universityName;

    private Long facultyId;

    private String facultyName;
}
