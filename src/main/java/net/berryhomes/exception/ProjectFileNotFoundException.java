package net.berryhomes.exception;

public class ProjectFileNotFoundException extends RuntimeException {
    public ProjectFileNotFoundException(String message) {
        super(message);
    }
}
