package net.berryhomes.exception.business;

public class ContactAlreadyExistException extends RuntimeException {
    public ContactAlreadyExistException(String message) {
        super(message);
    }
}
