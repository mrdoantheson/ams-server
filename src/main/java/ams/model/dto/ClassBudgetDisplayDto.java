package ams.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClassBudgetDisplayDto{

    private Long classBudgetId;

    private String item;

    private String unit;

    private Double unitExpense;

    private Integer quantity;

    private Double amount;

    private Double tax;
    private Double sum;

    private String note;

    private Double total;

    private String overBudget;

    private Long clazzId;
}
