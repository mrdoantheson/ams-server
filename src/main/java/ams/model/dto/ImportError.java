package ams.model.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ImportError {

    private String sheet;

    private String address;

    private String detail;

}
