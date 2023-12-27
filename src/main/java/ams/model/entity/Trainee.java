package ams.model.entity;

import ams.enums.AllowanceGroup;
import ams.enums.Gender;
import ams.enums.TraineeClassStatus;
import ams.enums.TraineeStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnTransformer;

import java.time.LocalDate;
import java.util.Set;

@Entity
@Getter
@Setter
public class Trainee extends BaseEntity {

    @NotNull(message = "{trainee.account.required}")
    private String account;

    @NotNull(message = "{trainee.fullName.required}")
    private String fullName;

    @NotNull(message = "{trainee.dateOfBirth.required}")
    private LocalDate dateOfBirth;

    @NotNull(message = "{trainee.gender.required}")
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Pattern(regexp = "^(\\+84|0)\\d{9}$")
    private String phone;

    @Email
    private String email;

    @Enumerated(EnumType.STRING)
    private TraineeStatus traineeStatus;



    private String tpbAccount;

    @NotNull(message = "{trainee.salary.required}")
    private Boolean salary;

    private LocalDate contractStartDate;

    private Long contractLength;

    @Enumerated(EnumType.STRING)
    private AllowanceGroup allowanceGroup;

    @ManyToOne
    private Faculty faculty;

    @ManyToOne
    private University university;

    @OneToMany(mappedBy = "trainee",cascade = CascadeType.PERSIST)
    private Set<ClassTrainee> classTraineeSet;
}
