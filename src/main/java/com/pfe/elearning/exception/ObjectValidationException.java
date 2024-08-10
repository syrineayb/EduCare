package com.pfe.elearning.exception;

import java.util.Set;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ObjectValidationException extends RuntimeException {

    // Getter method for violations
    @Getter
    private final Set<String> violations; /* validation error messages */
    private final String violationSource; /* source of the violation */
    public Set<String> getValidationErrors() {
        return violations;
    }
}
