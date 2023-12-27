package ams.model.entity;

import ams.enums.BudgetCode;
import ams.enums.ClassType;
import ams.enums.ClassStatus;
import ams.enums.Location;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Set;

@Entity
@Getter
@Setter
public class Clazz extends BaseEntity{

    @Enumerated(EnumType.STRING)
    private ClassType classType;

//    @NotNull(message = "classCode is required")

    private String classCode;

//    @NotNull(message = "classStatus is required")
    @Enumerated(EnumType.STRING)
    private ClassStatus classStatus;


    private Integer plannedTraineeNo;

    private Integer acceptedTraineeNo;//validate acceptedTraineeNo <= plannedTraineeNo

    @Column(columnDefinition = "INT DEFAULT 0")
    private Integer actualTraineeNo;// validate actualTraineeNo <= acceptedTraineeNo

//    @NotNull(message = "expectedStartDate is required")
    private LocalDate expectedStartDate;//can validate expectedStartDate <= expectedEndDate

//    @NotNull(message = "expectedEndDate is required")
    private LocalDate expectedEndDate;

    @Enumerated(EnumType.STRING)
    private Location location;

    private String detailLocation;

    @Enumerated(EnumType.STRING)
    private BudgetCode budgetCode;

    @Min(value = 0, message = "estimatedBudget must be greater than 0")
    private Double estimatedBudget;

    private String classAdmin;

    private String trainer;

    private String learningPath;

    private String curriculum;
    private String history;

    @OneToMany(mappedBy = "clazz")
    private Set<ClassBudget> classBudgets;

    @OneToMany(mappedBy = "clazz")
    private Set<ClassAudit> classAudits;

    @OneToOne(mappedBy = "clazz")
    @PrimaryKeyJoinColumn
    private ClassDetail classDetail;

    @OneToMany(mappedBy = "clazz")
    private Set<ClassTrainee> classTraineeSet;

}
