package ams.model.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
@Builder
public class EmailDto {
    private MailerDto from;
    private List<MailerDto> to;
    private List<MailerDto> cc;
    private List<MailerDto> bcc;
    private String subject;
    private String textPart;
    private String htmlPart;
    @ToString.Exclude
    private List<AttachmentDto> attachments;
}
