package io.github.emersondll.auth.exception;


public class SchedulingNotFoundException extends RuntimeException {
    public SchedulingNotFoundException(String id) {
        super("Scheduling not found with id: " + id);
    }
}