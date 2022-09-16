package br.com.social.core.exceptions;

import lombok.Data;

import javax.validation.ConstraintViolation;
import javax.ws.rs.core.Response;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Data
public class ResponseError {

    private String message;
    private Collection<FieldError> errors;

    public ResponseError(String message, Collection<FieldError> errors) {
        this.message = message;
        this.errors = errors;
    }

    public static <T> ResponseError createFromValidation(Set<ConstraintViolation<T>> violations) {
        List<FieldError> errors = violations
                .stream()
                .map(x -> new FieldError(x.getPropertyPath().toString(), x.getMessage()))
                .collect(Collectors.toList());

        return new ResponseError("Validation error", errors);
    }

    public Response withStatusCode(int code) {
        return Response.status(code).entity(this).build();
    }
}
