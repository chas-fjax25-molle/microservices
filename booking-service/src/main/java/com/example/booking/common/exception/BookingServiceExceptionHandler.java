package com.example.booking.common.exception;

import java.nio.file.AccessDeniedException;
import java.time.Instant;
import java.util.NoSuchElementException;

import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

@RestControllerAdvice
public class BookingServiceExceptionHandler {

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleException(Exception e) {
        return "Internal Server Error";
    }

    // 400 Bad Request Exceptions

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ProblemDetail handleMethodArgumentNotValid(MethodArgumentNotValidException e) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST,
                "Validation failed for request body");
        problemDetail.setTitle("Invalid Request Body");
        problemDetail.setProperty("errorCategory", "Validation Exception");
        problemDetail.setProperty("timestamp", Instant.now());
        return problemDetail;
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ProblemDetail handleHttpMessageNotReadable(HttpMessageNotReadableException e) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST,
                "Request body is malformed");
        problemDetail.setTitle("Bad Request Body");
        problemDetail.setProperty("errorCategory", "Request Format Exception");
        problemDetail.setProperty("timestamp", Instant.now());
        return problemDetail;
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ProblemDetail handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException e) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST,
                "Parameter '" + e.getName() + "' should be " + e.getRequiredType().getSimpleName());
        problemDetail.setTitle("Type Mismatch");
        problemDetail.setProperty("errorCategory", "Type Conversion Exception");
        problemDetail.setProperty("timestamp", Instant.now());
        return problemDetail;
    }

    @ExceptionHandler(ConversionNotSupportedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ProblemDetail handleConversionNotSupported(ConversionNotSupportedException e) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST,
                "Type conversion not supported");
        problemDetail.setTitle("Conversion Not Supported");
        problemDetail.setProperty("errorCategory", "Conversion Exception");
        problemDetail.setProperty("timestamp", Instant.now());
        return problemDetail;
    }

    // 403 Forbidden

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ProblemDetail handleAccessDenied(AccessDeniedException e) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN,
                "You do not have permission to access this resource");
        problemDetail.setTitle("Access Denied");
        problemDetail.setProperty("errorCategory", "Authorization Exception");
        problemDetail.setProperty("timestamp", Instant.now());
        return problemDetail;
    }

    // 404 Not Found

    @ExceptionHandler(NoSuchElementException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ProblemDetail handleNoSuchElement(NoSuchElementException e) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND,
                "The requested resource was not found");
        problemDetail.setTitle("Not Found");
        problemDetail.setProperty("errorCategory", "Resource Not Found");
        problemDetail.setProperty("timestamp", Instant.now());
        return problemDetail;
    }

    // 406 Not Acceptable

    @ExceptionHandler(HttpMediaTypeNotAcceptableException.class)
    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    public ProblemDetail handleHttpMediaTypeNotAcceptable(HttpMediaTypeNotAcceptableException e) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_ACCEPTABLE,
                "Media type not acceptable");
        problemDetail.setTitle("Not Acceptable");
        problemDetail.setProperty("errorCategory", "Media Type Exception");
        problemDetail.setProperty("timestamp", Instant.now());
        return problemDetail;
    }

    // 415 Unsupported Media Type

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    @ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
    public ProblemDetail handleHttpMediaTypeNotSupported(HttpMediaTypeNotSupportedException e) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.UNSUPPORTED_MEDIA_TYPE,
                "Content-Type not supported");
        problemDetail.setTitle("Unsupported Media Type");
        problemDetail.setProperty("errorCategory", "Media Type Exception");
        problemDetail.setProperty("timestamp", Instant.now());
        return problemDetail;
    }
}
