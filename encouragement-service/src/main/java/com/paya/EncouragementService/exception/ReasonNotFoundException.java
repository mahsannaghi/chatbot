package com.paya.EncouragementService.exception;

public class ReasonNotFoundException extends RuntimeException {

    private final String description;

    public ReasonNotFoundException(String description) {
        super("Reason not found with description: " + description);
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
