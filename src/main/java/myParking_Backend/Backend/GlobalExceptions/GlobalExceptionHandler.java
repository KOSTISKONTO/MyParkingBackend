package myParking_Backend.Backend.GlobalExceptions;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;



@ControllerAdvice
public class GlobalExceptionHandler {



    // Not Found
    @ExceptionHandler(ChangeSetPersister.NotFoundException.class)
    public ResponseEntity<?> handleNotFound(ChangeSetPersister.NotFoundException ex) {
        return buildResponse(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    // @Valid field errors
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, Object> errors = new HashMap<>();
        errors.put("timestamp", LocalDateTime.now());
        errors.put("status", HttpStatus.BAD_REQUEST.value());
        errors.put("error", "Validation Failed");

        List<String> messages = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .collect(Collectors.toList());

        errors.put("message", messages);
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    // ConstraintViolationException (π.χ. @PathVariable validation)
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<?> handleConstraintViolation(ConstraintViolationException ex) {
        String msg = ex.getConstraintViolations().stream()
                .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                .collect(Collectors.joining(", "));
        return buildResponse(msg, HttpStatus.BAD_REQUEST);
    }


    // Για Hibernate Constraint Violation (π.χ. duplicate email)
    @ExceptionHandler(org.hibernate.exception.ConstraintViolationException.class)
    public ResponseEntity<?> handleHibernateConstraintViolation(org.hibernate.exception.ConstraintViolationException ex) {
        String message = ex.getSQLException().getMessage();
        return buildResponse("Database error: " + message, HttpStatus.CONFLICT);
    }


    // IllegalArgumentException
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleIllegalArgument(IllegalArgumentException ex) {
        return buildResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    // Access Denied (π.χ. roles)
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<?> handleAccessDenied(AccessDeniedException ex) {
        return buildResponse("Access Denied", HttpStatus.FORBIDDEN);
    }

    //  Catch-All
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGeneric(Exception ex) {
        return buildResponse("Unexpected error: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // Helper
    private ResponseEntity<?> buildResponse(String message, HttpStatus status) {
        Map<String, Object> error = new HashMap<>();
        error.put("timestamp", LocalDateTime.now());
        error.put("status", status.value());
        error.put("error", status.getReasonPhrase());
        error.put("message", message);
        return new ResponseEntity<>(error, status);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<?> handleDataIntegrity(DataIntegrityViolationException ex) {
        String rootMsg = ex.getRootCause() != null ? ex.getRootCause().getMessage() : "Database integrity error";
        String message;

        if (rootMsg.contains("email")) {
            message = "Το email υπάρχει ήδη!";
        } else if (rootMsg.contains("username")) {
            message = "Το username υπάρχει ήδη!";
        } else if (rootMsg.contains("afm")) {
            message = "Το ΑΦΜ υπάρχει ήδη!";
        } else {
            message = "Database error: " + rootMsg;
        }

        return buildResponse(message, HttpStatus.CONFLICT);
    }

}
