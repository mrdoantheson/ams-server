package ams.model.dto;

import ams.enums.ContentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttachmentDto {
    private String fileName;
    private ContentType contentType;
    private String base64Content;
}
