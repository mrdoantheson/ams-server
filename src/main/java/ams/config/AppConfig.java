package ams.config;


import ams.validation.EmailValidator;
import ams.validation.PhoneValidator;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {
    @Bean
    public ModelMapper modelMapper(){
        return new ModelMapper();
    }

    @Bean
    public EmailValidator emailValidator(){
        return new EmailValidator();
    }

    @Bean
    public PhoneValidator phoneValidator(){
        return new PhoneValidator();
    }

    @Bean
    public String string( ){
        return new String();
    }
}
