package ams.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClassBudgetDto extends BaseResponseDto {

    private Long classBudgetId;

    private String item;

    private String unit;

    private Double unitExpense;

    private Integer quantity;

    private Double amount;

    private Double tax;

    private String note;

    private Long clazzId;
}
