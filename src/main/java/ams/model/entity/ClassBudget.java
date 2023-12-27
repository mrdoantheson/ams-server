package ams.model.entity;

import ams.enums.Unit;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class ClassBudget extends BaseEntity{

    private String item;

    private Unit unit;

    @Min(value = 0, message = "unitExpense must be greater than 0")
    private Double unitExpense;

    @Min(value = 0, message = "quantity must be greater than 0")
    private Integer quantity;

    @Max(value = 100, message = "amount must be less than 100")
    private Double tax;



    private String note;

    @ManyToOne
    private Clazz clazz;


}
