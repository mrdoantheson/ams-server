package ams.model.dto;

import ams.enums.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class ClassDetailDto extends BaseResponseDto {

    //validate o day
    private SubjectType subjectType;

    private SubSubjectType subSubjectType;

    private DeliveryType deliveryType;

    private FormatType formatType;

    private Scope scope;

    private String supplier;

    private LocalDate actualStartDate;

    private LocalDate actualEndDate;

    private String masterTrainer;

    private String trainer;

    private String curriculum;
    //TODO: cho nay import file excel

    private String remarks;

    private Long clazzId;
}
