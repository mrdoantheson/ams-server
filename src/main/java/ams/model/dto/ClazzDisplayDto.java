package ams.model.dto;

import ams.enums.BudgetCode;
import ams.enums.ClassType;
import ams.enums.ClassStatus;
import ams.enums.Location;
import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class ClazzDisplayDto {

    private Long id;

    @Column(unique = true)
    private String classCode;

    private ClassType classType;

    private ClassStatus classStatus;

    private LocalDate expectedStartDate;

    private LocalDate expectedEndDate;

    private LocalDate actualStartDate;

    private LocalDate actualEndDate;

    private Location location;

    private String detailLocation;

    private BudgetCode budgetCode;

    private Double estimatedBudget;

    private String classAdmin;

    private String learningPath;

    private String history;

    private Integer plannedTraineeNo;

    private Integer acceptedTraineeNo;

    private Integer actualTraineeNo;
}
