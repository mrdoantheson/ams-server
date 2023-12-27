package ams.model.entity;

import ams.enums.TraineeClassStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
public class ClassTrainee extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "class_id", nullable = false)
    private Clazz clazz;

    @ManyToOne
    @JoinColumn(name = "trainee_id", nullable = false)
    private Trainee trainee;

    @Enumerated(EnumType.STRING)
    private TraineeClassStatus traineeClassStatus;
}
