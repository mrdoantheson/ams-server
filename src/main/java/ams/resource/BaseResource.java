package ams.resource;

import ams.model.dto.BaseResponseDto;
import ams.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class BaseResource {

    @Autowired
    private MessageService messageService;

    protected ResponseEntity<BaseResponseDto> success(String message) {
        return ResponseEntity.ok(BaseResponseDto.success(messageService.getMessage(message)));
    }

    protected ResponseEntity<BaseResponseDto> success(Object data, String message) {
        return ResponseEntity.ok(BaseResponseDto.success(data, messageService.getMessage(message)));
    }

    protected ResponseEntity<BaseResponseDto> created(Object data, String message) {
        return new ResponseEntity<>(BaseResponseDto.success(data, messageService.getMessage(message)), HttpStatus.CREATED);
    }



    protected ResponseEntity<BaseResponseDto> error(String message, String errorCode, Object data) {
        return ResponseEntity.ok(BaseResponseDto.error(messageService.getMessage(message), errorCode, data));
    }

    protected ResponseEntity<BaseResponseDto> badRequest(String message) {
        return new ResponseEntity<>(BaseResponseDto.error(message), HttpStatus.BAD_REQUEST);
    }

    protected ResponseEntity<BaseResponseDto> badRequest(String message, String errorCode, Object data) {
        return new ResponseEntity<>(BaseResponseDto.error(messageService.getMessage(message), errorCode, data), HttpStatus.BAD_REQUEST);
    }

    protected ResponseEntity<BaseResponseDto> notFound(String message) {
        return new ResponseEntity<>(BaseResponseDto.error(messageService.getMessage(message), null), HttpStatus.NOT_FOUND);
    }

    protected ResponseEntity<BaseResponseDto> notFound(String message, String errorCode) {
        return new ResponseEntity<>(BaseResponseDto.error(messageService.getMessage(message), errorCode), HttpStatus.NOT_FOUND);
    }

    protected ResponseEntity<BaseResponseDto> noContent(String message) {
        return new ResponseEntity<>(BaseResponseDto.success(messageService.getMessage(message), null), HttpStatus.NO_CONTENT);
    }

    protected ResponseEntity<BaseResponseDto> internalServerError(String message) {
        return internalServerError(messageService.getMessage(message), null);
    }

    protected ResponseEntity<BaseResponseDto> internalServerError(String message, String errorCode) {
        return new ResponseEntity<>(BaseResponseDto.error(messageService.getMessage(message), errorCode), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    protected ResponseEntity<BaseResponseDto> methodNotAllowed(String message) {
        return new ResponseEntity<>(BaseResponseDto.error(messageService.getMessage(message)), HttpStatus.METHOD_NOT_ALLOWED);
    }

    protected ResponseEntity<BaseResponseDto> forbidden(String message) {
        return new ResponseEntity<>(BaseResponseDto.error(messageService.getMessage(message)), HttpStatus.FORBIDDEN);
    }
}
