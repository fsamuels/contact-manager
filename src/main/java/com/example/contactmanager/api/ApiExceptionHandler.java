package com.example.contactmanager.api;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.example.contactmanager.service.NoteNotFoundException;
import com.example.contactmanager.service.PersonNotFoundException;

/**
 * Maps REST API errors to RFC 9457 problem-detail responses. Scoped to the
 * {@code api} package so the server-rendered controllers keep their
 * flash-message redirect handling.
 */
@RestControllerAdvice(basePackages = "com.example.contactmanager.api")
public class ApiExceptionHandler {

    /**
     * Missing person or note: 404 with the exception message as detail.
     */
    @ExceptionHandler({ PersonNotFoundException.class, NoteNotFoundException.class })
    public ProblemDetail handleNotFound(RuntimeException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problem.setTitle("Not Found");
        return problem;
    }

    /**
     * Request-body validation failure: 400 with a field-to-message map in the
     * {@code errors} extension property.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleInvalidBody(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new LinkedHashMap<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            errors.putIfAbsent(fieldError.getField(), fieldError.getDefaultMessage());
        }
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST, "Validation failed for " + errors.size() + " field(s).");
        problem.setTitle("Validation Failed");
        problem.setProperty("errors", errors);
        return problem;
    }

    /**
     * Query-parameter constraint violation (e.g. size out of range): 400.
     */
    @ExceptionHandler(HandlerMethodValidationException.class)
    public ProblemDetail handleInvalidParameter(HandlerMethodValidationException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST, "Invalid request parameter.");
        problem.setTitle("Validation Failed");
        return problem;
    }

    /**
     * Unparseable path or query value (e.g. malformed UUID): 400.
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ProblemDetail handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST, "Invalid value for parameter '" + ex.getName() + "'.");
        problem.setTitle("Invalid Parameter");
        return problem;
    }
}
