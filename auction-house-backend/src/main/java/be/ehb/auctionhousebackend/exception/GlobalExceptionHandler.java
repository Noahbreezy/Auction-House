package be.ehb.auctionhousebackend.exception;


import be.ehb.auctionhousebackend.dto.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;


@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(AuctionClosedException.class)
    public ResponseEntity<ErrorResponse> handleAuctionClosed(AuctionClosedException ex) {
        return buildResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InsufficientBidException.class)
    public ResponseEntity<ErrorResponse> handleInsufficientBid(InsufficientBidException ex) {
        return buildResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(FraudException.class)
    public ResponseEntity<ErrorResponse> handleFraud(FraudException ex) {
        // SECURITY: Log specific security threats as WARN
        logger.warn("SECURITY ALERT: Fraud attempt detected. Reason: {}", ex.getMessage());

        return buildResponse(ex.getMessage(), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(ResourceException.class)
    public ResponseEntity<ErrorResponse> handleResourceException(ResourceException ex) {
        return buildResponse(ex.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleOtherExceptions(Exception ex) {
        // SECURITY: Log the full error internally for debugging
        logger.error("CRITICAL SYSTEM ERROR: ", ex);

        // SECURITY: Return a sanitized message to the user to prevent Information Leakage
        return buildResponse("An unexpected internal error occurred. Please contact support.", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<ErrorResponse> buildResponse(String message, HttpStatus status) {
        ErrorResponse error = new ErrorResponse(message, status.value());
        return new ResponseEntity<>(error, status);
    }
}