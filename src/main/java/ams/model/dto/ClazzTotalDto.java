package ams.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
@Getter
@Setter
public class ClazzTotalDto {
    public ClazzDto clazzDto = new ClazzDto();

    public ClassDetailDto classDetailDto = new ClassDetailDto();

    public List<ClassBudgetDto> classBudgetDto = new ArrayList<>();

}
