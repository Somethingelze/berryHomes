package net.berryhomes.exception.business;

public class ProjectFileNotFoundException extends RuntimeException {
    public ProjectFileNotFoundException(String message) {
        super(message);
    }
}
