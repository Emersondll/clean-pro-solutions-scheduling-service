package br.com.cleanprosolutions.scheduling.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when a scheduling is not found.
 *
 * @author Emerson Lima
 * @since 1.0.0
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class SchedulingNotFoundException extends RuntimeException {

    public SchedulingNotFoundException(final String id) {
        super("Scheduling not found with ID: " + id);
    }
}
