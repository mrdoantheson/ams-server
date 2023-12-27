package ams.model.entity;

import ams.enums.*;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
public class ClassDetail extends BaseEntity{

    @Enumerated(EnumType.STRING)
    private SubjectType subjectType;

    @Enumerated(EnumType.STRING)
    private SubSubjectType subSubjectType;

    @Enumerated(EnumType.STRING)
    private DeliveryType deliveryType;

    @Enumerated(EnumType.STRING)
    private FormatType formatType;

    @Enumerated(EnumType.STRING)
    private Scope scope;

    private String supplier;

    private LocalDate actualStartDate;

    private LocalDate actualEndDate;

    private String masterTrainer;

    private String trainer;

    private String curriculum;
    //TODO: cho nay import file excel


    private String remarks;

    @OneToOne
    private Clazz clazz;

}
