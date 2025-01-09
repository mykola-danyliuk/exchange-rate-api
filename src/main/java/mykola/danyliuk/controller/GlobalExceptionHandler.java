package mykola.danyliuk.controller;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponseDTO> handleIllegalArgumentException(IllegalArgumentException ex) {
        return new ResponseEntity<>(ErrorResponseDTO.of(ex), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ErrorResponseDTO> handleException(Exception ex) {
        return new ResponseEntity<>(ErrorResponseDTO.of(ex), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Schema(description = "Error response DTO")
    public record ErrorResponseDTO(
        @Schema(description = "Error message", example = "Currency not found: XXX") String error) {
        public static ErrorResponseDTO of(Throwable throwable) {
            return new ErrorResponseDTO(throwable.getMessage());
        }
    }

}
