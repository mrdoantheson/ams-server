package ams.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
@Getter
@Setter
public class TotalDisplayDto {

    public ClazzDisplayDto clazzDisplayDto = new ClazzDisplayDto();

    public ClassDetailDisplayDto classDetailDisplayDto = new ClassDetailDisplayDto();

    public List<ClassBudgetDisplayDto> classBudgetDisplayDto = new ArrayList<>();

    public List<ClassAuditDisplayDto> classAuditDisplayDto = new ArrayList<>();
}
