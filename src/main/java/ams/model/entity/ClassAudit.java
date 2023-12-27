package ams.model.entity;

import ams.enums.EventCategory;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
public class ClassAudit extends BaseEntity{

    private LocalDate auditDate;//validate auditDate < deadline


    private EventCategory eventCategory;

    private String relatedPeople;


    private String action;


    private String pic;


    private LocalDate deadline;


    private String note;

    @ManyToOne
    private Clazz clazz;

}
