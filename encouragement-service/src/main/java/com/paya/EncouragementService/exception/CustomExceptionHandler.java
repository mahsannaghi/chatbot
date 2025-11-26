package com.paya.EncouragementService.exception;

import feign.FeignException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.HibernateException;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.query.SyntaxException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.sql.SQLException;

@RestControllerAdvice
@Slf4j
public class CustomExceptionHandler {

    private void logger(HttpServletRequest req, Exception e) {
        log.error("Exception message : ", e);
        log.error("User details  : " + req.getUserPrincipal());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ResponseException> handleForbiddenException(HttpServletRequest req, AccessDeniedException e) {
        this.logger(req, e);
        return ResponseEntity.badRequest().body(new ResponseException(HttpStatus.FORBIDDEN.value(), "دسترسی غیر مجاز"));
    }

    @ExceptionHandler({SyntaxException.class, SQLException.class})
    public ResponseEntity<ResponseException> handleSqlException(HttpServletRequest req, SyntaxException e) {
        this.logger(req, e);
        return ResponseEntity.badRequest().body(new ResponseException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "خطا از سمت دیتا بیس"));
    }

//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<ResponseException> handleInternalServerException(HttpServletRequest req, Exception e) {
//        this.logger(req, e);
//        return ResponseEntity.badRequest().body(new ResponseException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "خطا از سمت سرور"));
//    }

    @ExceptionHandler(MultipartException.class)
    public ResponseEntity<ResponseException> handleMultipartException(MultipartException e) {
        Throwable cause = e.getCause();
        if (cause instanceof MaxUploadSizeExceededException) {
            return ResponseEntity.badRequest()
                    .body(new ResponseException(HttpStatus.BAD_REQUEST.value(), "حجم فایل بالاتر از حد مجاز است."));
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ResponseException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "خطای ارسال فایل"));
    }
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ResponseException> handleMethodNotSupportException(HttpServletRequest req, HttpRequestMethodNotSupportedException e) {
        this.logger(req, e);
        return ResponseEntity.badRequest().body(new ResponseException(HttpStatus.BAD_REQUEST.value(), "متود درخواست صحیح نمی باشد"));
    }

    @ExceptionHandler(FeignException.class)
    public ResponseEntity<ResponseException> handleFeignException(HttpServletRequest req, FeignException e) {
        this.logger(req, e);
        return ResponseEntity.badRequest().body(new ResponseException(HttpStatus.UNAUTHORIZED.value(), "نشست شما منقضی شده است لطفا مجددا وارد شوید ."));
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ResponseException> handleNotFound(HttpServletRequest req, FeignException e) {
        this.logger(req, e);
        return ResponseEntity.badRequest().body(new ResponseException(HttpStatus.NOT_FOUND.value(), "منبع یافت نشد ."));
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ResponseException> handleNotFound(HttpServletRequest req, EntityNotFoundException e) {
        this.logger(req, e);
        return ResponseEntity.badRequest().body(new ResponseException(HttpStatus.NOT_FOUND.value(), "این آیتم یافت نشد."));
    }

//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<ResponseException> handleException(HttpServletRequest req, Exception e) {
//        this.logger(req, e);
//        return ResponseEntity.badRequest().body(new ResponseException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "خطایی رخ داده است. با پشتیبانی تماس بگیرید."));
//    }

    @ExceptionHandler(InvalidDataAccessApiUsageException.class)
    public ResponseEntity<ResponseException> handleInvalidDataAccessApiUsageException(HttpServletRequest req, InvalidDataAccessApiUsageException e) {
        this.logger(req, e);
        return ResponseEntity.badRequest().body(new ResponseException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "خطایی رخ داده است. با پشتیبانی تماس بگیرید."));
    }

    @ExceptionHandler(HibernateException.class)
    public ResponseEntity<ResponseException> handleHibernateException(HttpServletRequest req, HibernateException e) {
        this.logger(req, e);
        return ResponseEntity.badRequest().body(new ResponseException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "خطا در دریافت اطلاعات از دیتابیس ."));
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ResponseException> handleMaximumUploadSize(HttpServletRequest req, MaxUploadSizeExceededException e) {
        this.logger(req, e);
        return ResponseEntity.badRequest().body(new ResponseException(HttpStatus.BAD_REQUEST.value(),  "حجم فایل انتخاب شده بالاتر از حد مجاز می باشد ."));
    }
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ResponseException> handleConstraintViolation(HttpServletRequest req, ConstraintViolationException e) {
        this.logger(req, e);
        return ResponseEntity.badRequest().body(new ResponseException(HttpStatus.BAD_REQUEST.value(),  "پارامتر ورودی تکراری است ."));
    }


}
