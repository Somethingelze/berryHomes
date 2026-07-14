package net.berryhomes.exception;

import lombok.extern.slf4j.Slf4j;
import net.berryhomes.exception.business.ProjectFileNotFoundException;
import net.berryhomes.exception.business.ProjectNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.io.IOException;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({ProjectNotFoundException.class, ProjectFileNotFoundException.class})
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public ModelAndView handleProjectNotFoundException(RuntimeException ex) {
        log.warn("Resource not found: {}", ex.getMessage());
        ModelAndView mav = new ModelAndView("error/404");
        mav.addObject("message", ex.getMessage());
        return mav;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ModelAndView handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        log.warn("Invalid arguments: {}", ex.getMessage());
        ModelAndView mav = new ModelAndView("error/400");
        mav.addObject("message", ex.getMessage());
        return mav;
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ModelAndView handleDataIntegrityViolationExceptionException(DataIntegrityViolationException ex) {
        log.warn("Integrity violation: {}", ex.getMessage());
        ModelAndView mav = new ModelAndView("error/400");
        mav.addObject("message", ex.getMessage());
        return mav;
    }

    @ExceptionHandler({NoHandlerFoundException.class, HttpRequestMethodNotSupportedException.class})
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public ModelAndView handleNoHandlerFoundExceptionException(Exception ex) {
        log.warn("Page not found: {}", ex.getMessage());
        ModelAndView mav = new ModelAndView("error/404");
        mav.addObject("message", ex.getMessage());
        return mav;
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    @ResponseStatus(HttpStatus.PAYLOAD_TOO_LARGE)
    public ModelAndView handleMaxSizeException(org.springframework.web.multipart.MaxUploadSizeExceededException ex) {
        log.warn("Size limit exceeded: {}", ex.getMessage());
        ModelAndView mav = new ModelAndView("error/400");
        mav.addObject("message", "The uploaded file is too large. Maximum size allowed is 15MB.");
        return mav;
    }

    @ExceptionHandler(value = IOException.class)
    public ModelAndView handleIOException(IOException ex) {
        log.error("IO Exception: {}", ex.getMessage());
        return new ModelAndView("error/500");
    }

    @ExceptionHandler(value = Exception.class)
    public ModelAndView handleException(Exception ex) {
        log.error("Exception: {}", ex.getMessage());
        return new ModelAndView("error/500");
    }
}
