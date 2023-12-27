package ams.service;

import ams.model.dto.EmailDto;

public interface EmailService {
    boolean sendEmail(EmailDto emailDto);

    void sendEmail(String account,String classCode,String className,Long classId,String mode,String accountTrainer,String accountMasterTrainer);
}
