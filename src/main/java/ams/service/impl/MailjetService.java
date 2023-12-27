package ams.service.impl;

import ams.model.dto.EmailDto;
import ams.model.dto.MailerDto;
import ams.model.entity.Account;
import ams.model.entity.Trainee;
import ams.security.SecurityUtil;
import ams.service.AccountService;
import ams.service.EmailService;
import ams.service.TraineeService;
import com.google.gson.Gson;
import com.mailjet.client.ClientOptions;
import com.mailjet.client.MailjetClient;
import com.mailjet.client.MailjetRequest;
import com.mailjet.client.MailjetResponse;
import com.mailjet.client.resource.Emailv31;
import org.apache.commons.io.FileUtils;
import org.apache.commons.text.StringSubstitutor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.*;

@Service
public class MailjetService implements EmailService {
    private final String apiKey = "62208bcef359f8c853f78dcea025bb34";
    private final String secretKey = "751253f97483b9a22670cf5702c8fd1e";
    private final String mailJetVersion = "v3.1";
    private final TraineeService traineeService;
    private final AccountService accountService;

    public MailjetService(TraineeService traineeService, AccountService accountService) {
        this.traineeService = traineeService;
        this.accountService = accountService;
    }


    @Override
    public boolean sendEmail(EmailDto emailDto) {
        MailjetClient client = new MailjetClient(apiKey, secretKey, new ClientOptions(mailJetVersion));
        MailjetRequest request = new MailjetRequest(Emailv31.resource)
                .property(Emailv31.MESSAGES, new JSONArray()
                        .put(new JSONObject(new Gson().toJson(emailDto))));
        try {
            MailjetResponse response = client.post(request);
            System.out.println(response.getStatus());
            System.out.println(response.getData());
            return true;
        } catch (Exception e) {
            return false;
        }
    }


    @Override
    public void sendEmail(String account,String classCode,String className,Long classId,String mode,String accountTrainer,String accountMasterTrainer) {
        EmailService emailService = new MailjetService(traineeService, accountService);

        Optional<Account> classAdminEmailOptional=accountService.findTraineeByAccount(account);
        Optional<Account> trainerEmailOptional=accountService.findTraineeByAccount(accountTrainer);
        Optional<Account> masterTrainerEmailOptional=accountService.findTraineeByAccount(accountMasterTrainer);
        String classAdminEmail = null;
        String trainerEmail = null;
        String masterTrainerEmail = null;
        if (classAdminEmailOptional.isPresent()) {
           classAdminEmail = classAdminEmailOptional.get().getEmail();
        }
        if (trainerEmailOptional.isPresent()) {
            trainerEmail = trainerEmailOptional.get().getEmail();
        }
        if (masterTrainerEmailOptional.isPresent()) {
            masterTrainerEmail = masterTrainerEmailOptional.get().getEmail();
        }





        //Create host mail
        String hostEmail = getCurrentUserEmail();; //your host email
        String hostName = getCurrentUserAccount(); //your host name

        //Create "mail to" list
        List<MailerDto> toMailerList = new ArrayList<>();

        if (classAdminEmail != null && !classAdminEmail.isEmpty()) {
            MailerDto classAdminMailer = new MailerDto(classAdminEmail, "New mail");
            toMailerList.add(classAdminMailer);
        }
        if (masterTrainerEmail != null && !masterTrainerEmail.isEmpty()) {
            MailerDto masterTrainerMailer = new MailerDto(masterTrainerEmail, "New mail");
            toMailerList.add(masterTrainerMailer);
        }

        if (trainerEmail != null && !trainerEmail.isEmpty()) {
            MailerDto trainerMailer = new MailerDto(trainerEmail, "New mail");
            toMailerList.add(trainerMailer);
        }

        try {
            String mailPath = null;
            if(mode.equals("created")){
                mailPath = "created-mail.html";
            }else if(mode.equals("updated")){
                mailPath = "update-mail.html";
            } else if (mode.equals("deleted")) {
                mailPath = "deleted-mail.html";
            }

            //Create mail body
            String mailBody = FileUtils.readFileToString(new File(mailPath), "UTF-8");
            //Pasting dynamic content to mail template
            Map<String, String> mailAttributes = new HashMap<>();
            mailAttributes.put("classAdminAccount", account);
            mailAttributes.put("classCode", classCode);
            mailAttributes.put("className",className);
            mailAttributes.put("classId",String.valueOf(classId));
            mailAttributes.put("mode",mode);

            StringSubstitutor stringSubstitutor = new StringSubstitutor(mailAttributes);
            mailBody = stringSubstitutor.replace(mailBody);
            String subject = null;
            if(mode.equals("created")){
                subject = "The Class "+ classCode +" is assigned to you.";
            }else if(mode.equals("updated")){
                subject = "The Class "+ classCode +" is updated.";
            } else if (mode.equals("deleted")) {
                subject = "The Class " + classCode +" has been cancelled";
                
            }

            //Create email dto
            EmailDto email = EmailDto.builder()
                    .from(new MailerDto(hostEmail, hostName))
                    .to(toMailerList)
                    .subject(subject)
                    .htmlPart(mailBody)
                    .build();
            emailService.sendEmail(email);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public String getCurrentUserEmail() {
        Optional<String> usernameOptional = SecurityUtil.getCurrentUserLogin();
        if (usernameOptional.isPresent()) {
            String username = usernameOptional.get();
            Optional<Account> account = accountService.findTraineeByAccount(username);
            if (account.isPresent()) {

                return account.get().getEmail();
            }
        }
        return null;
    }

    public String getCurrentUserAccount() {
        Optional<String> usernameOptional = SecurityUtil.getCurrentUserLogin();
        if (usernameOptional.isPresent()) {
            String username = usernameOptional.get();
            Optional<Account> account = accountService.findTraineeByAccount(username);
            if (account.isPresent()) {

                return account.get().getAccount();
            }
        }
        return null;
    }

}
