package com.Happypaws.demo.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

/**
 * Anotación personalizada para validar que un número sea positivo
 */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PositiveNumberValidator.class)
@Documented
public @interface PositiveNumber {
    String message() default "El número debe ser positivo";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    boolean allowZero() default false;
}
