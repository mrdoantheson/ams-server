package ams.model.dto;

import ams.enums.EventCategory;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class ClassAuditDto extends BaseResponseDto {

    private Long classAuditId;

    private LocalDate date;

    private EventCategory eventCategory;

    private String relatedPeople;

    private String action;

    private String pic;

    private LocalDate deadline;

    private String note;

    private Long clazzId;
}
