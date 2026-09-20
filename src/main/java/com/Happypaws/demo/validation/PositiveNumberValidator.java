package com.Happypaws.demo.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validador para números positivos
 */
public class PositiveNumberValidator implements ConstraintValidator<PositiveNumber, Number> {
    
    private boolean allowZero;

    @Override
    public void initialize(PositiveNumber annotation) {
        this.allowZero = annotation.allowZero();
    }

    @Override
    public boolean isValid(Number value, ConstraintValidatorContext context) {
        if (value == null) {
            return true; // Dejar que @NotNull maneje valores nulos
        }

        double doubleValue = value.doubleValue();
        
        if (allowZero) {
            return doubleValue >= 0;
        } else {
            return doubleValue > 0;
        }
    }
}
