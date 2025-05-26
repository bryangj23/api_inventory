package com.nexos.api_inventory.exception;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.nexos.api_inventory.util.error.*;
import com.nexos.api_inventory.util.message.MessageService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.method.HandlerMethod;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@ControllerAdvice(annotations = Controller.class)
@Component
public class GlobalExceptionHandler {
    public static final String STATUS = "status";
    public static final String CODE = "code";
    public static final String MESSAGE = "message";
    public static final String ENTITY_NAME = "entityTypeName";
    public static final String VALIDATION = "validation";
    public static final String HEADER_ENTITY_NAME = "Headers";
    public static final String DEFAULT_ENTITY_ERROR_MESSAGE = "Error in Dto";
    public static final String ERROR_DEFAULT_MESSAGE_REFERENCE = "error.handler.invalid.format";
    public static final String ERROR_HANDLER_REQUEST_BODY_ERROR = "error.handler.request.body.error";
    public static final String ERROR_HANDLER_MISSING_HEADER = "error.handler.missing.header";
    public static final String ERROR_HANDLER_ERROR_INTERNAL = "error.handler.error.internal";
    public static final String COMMON_HANDLER_FILTER_FORMAT_ERROR = "error.handler.filter_format";

    public static final String JPA_FILTER_PARSER_EXCEPTION = "cz.jirutka.rsql.parser.RSQLParserException";
    public static final String JPA_FILTER_UNKNOWN_PROPERTY_EXCEPTION = "io.github.perplexhub.rsql.UnknownPropertyException";

    private final MessageService messageService;
    public final Map<Class<? extends BaseError>, Function<Exception, Map<String, Object>>> knownExceptions = Map.of(
            EntityError.class, this::mapToEntityError,
            ServiceInternalError.class, this::mapToInternalError,
            UnCodedError.class, this::mapToUnCodeError,
            ValidationError.class, this::mapToValidationError
    );

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> resolveException(Exception exception) {
        if(JPA_FILTER_PARSER_EXCEPTION.equals(exception.getClass().getName())){
            return resolveRSQLParserException();
        }
        return  internalServerError(exception);
    }

    private ResponseEntity<Object> internalServerError(Exception exception) {
        log.error("Unexpected error : {0}", exception);
        return ResponseEntity.internalServerError().body(getInternalError());
    }

    @ExceptionHandler(EntityError.class)
    public ResponseEntity<Object> resolveApiError(Exception exception) {
        Map<String, Object> result = manageKnowErrors(EntityError.class, exception);
        return ResponseEntity.status((int) result.get(STATUS)).body(result);
    }

    @ExceptionHandler(ServiceInternalError.class)
    public ResponseEntity<Object> resolveInternalError(Exception exception) {
        Map<String, Object> result = manageKnowErrors(ServiceInternalError.class, exception);
        return ResponseEntity.status((int) result.get(STATUS)).body(result);
    }

    @ExceptionHandler(ValidationError.class)
    public ResponseEntity<Object> resolveValidationError(Exception exception) {
        Map<String, Object> result = manageKnowErrors(ValidationError.class, exception);
        return ResponseEntity.status((int) result.get(STATUS)).body(result);
    }

    @ExceptionHandler(UnCodedError.class)
    public ResponseEntity<Object> resolveUnCodedError(Exception exception) {
        Map<String, Object> result = manageKnowErrors(UnCodedError.class, exception);
        return ResponseEntity.status((int) result.get(STATUS)).body(result);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Object> resolveMissingServletRequestParameter(Exception exception) {
        MissingServletRequestParameterException error = (MissingServletRequestParameterException) exception;
        Map<String, Object> result = manageKnowErrors(UnCodedError.class, new UnCodedError(ErrorStatus.BAD_REQUEST, error.getMessage()));
        return ResponseEntity.status((int) result.get(STATUS)).body(result);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> resolveBodyValidationError(Exception exception) {
        Map<String, Object> result = manageBodyValidationErrors(exception);
        return ResponseEntity.status((int) result.get(STATUS)).body(result);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Object> resolveHttpMessageNotReadableException(Exception exception) {
        Throwable exceptionCause = exception.getCause();
        Map<String, Object> result = exceptionCause instanceof InvalidFormatException
                ? this.manageInvalidFormat(exception)
                : this.manageMessageNotReadable(exception);

        return ResponseEntity.status((int) result.get(STATUS)).body(result);
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<Object> resolveMissingRequestHeaderException(Exception exception) {
        Map<String, Object> result = manageHeaderError(exception);
        return ResponseEntity.status((int) result.get(STATUS)).body(result);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> resolveConstraintValidationError(HandlerMethod handlerMethod, Exception exception) {
        Map<String, Object> result = manageConstraintValidationErrors(exception,  handlerMethod);
        return ResponseEntity.status((int) result.get(STATUS)).body(result);
    }

    private Map<String, Object> manageConstraintValidationErrors(Exception exception, HandlerMethod handlerMethod) {
        ConstraintViolationException error = (ConstraintViolationException) exception;
        return manageKnowErrors(ValidationError.class, getValidationError(error, handlerMethod) );

    }

    private List<ValidationDto> getValidationFieldsErrors(ConstraintViolationException error) {
        return error.getConstraintViolations().stream()
                .map(this::constraintViolationToValidation)
                .toList();
    }

    private ValidationDto constraintViolationToValidation(ConstraintViolation<?> fieldError) {
        String[] classPath = fieldError.getPropertyPath().toString().split("\\.");
        String attributeName = classPath[classPath.length - 1];
        return ValidationDto.builder()
                .attribute(attributeName)
                .message(fieldError.getMessage())
                .build();
    }

    private ValidationError getValidationError(ConstraintViolationException error, HandlerMethod handlerMethod) {
        return manageHeaderConstraintValidationError(error, handlerMethod)
                .orElse(getValidationError("", getValidationFieldsErrors(error)));
    }


    private Optional<ValidationError> manageHeaderConstraintValidationError(ConstraintViolationException error, HandlerMethod handlerMethod) {
        List<String> headerNameList = getParameterNamesByAnnotation(handlerMethod, RequestHeader.class);
        List<ValidationDto> headerErrors = error.getConstraintViolations().stream()
                .filter(constraintViolation -> headerNameList.contains(getFieldNameFromConstraintViolation(constraintViolation)))
                .map(this::constraintViolationToValidation)
                .toList();

        if (headerErrors.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(getValidationError(HEADER_ENTITY_NAME, headerErrors));
    }

    private ValidationError getValidationError(String entity, List<ValidationDto> validations) {
        return new ValidationError(
                ErrorStatus.UNPROCESSABLE_CONTENT,
                DEFAULT_ENTITY_ERROR_MESSAGE,
                ErrorCode.UNPROCESSABLE_CONTENT,
                entity,
                validations
        );
    }

    private Map<String, Object> mapToEntityError(Exception exception) {
        EntityError error = (EntityError) exception;
        return Map.of(
                STATUS, error.getStatus().getCode(),
                CODE, error.getCode(),
                MESSAGE, error.getMessage(),
                ENTITY_NAME, error.getEntityTypeName()
        );
    }

    private Map<String, Object> mapToInternalError(Exception exception) {
        ServiceInternalError error = (ServiceInternalError) exception;
        return Map.of(
                STATUS, error.getStatus().getCode(),
                CODE, error.getCode(),
                MESSAGE, error.getMessage()
        );
    }

    private Map<String, Object> mapToUnCodeError(Exception exception) {
        UnCodedError error = (UnCodedError) exception;
        return Map.of(
                STATUS, error.getStatus().getCode(),
                MESSAGE, error.getMessage()
        );
    }

    private Map<String, Object> mapToValidationError(Exception exception) {
        ValidationError error = (ValidationError) exception;
        return Map.of(
                STATUS, error.getStatus().getCode(),
                CODE, error.getCode(),
                MESSAGE, error.getMessage(),
                ENTITY_NAME, error.getEntityTypeName(),
                VALIDATION, error.getValidation()
        );
    }

    public Map<String, Object> manageKnowErrors(Class<? extends BaseError> className, Exception exception) {
        log.info("Preparing to process known error {}, message :{}", className.getSimpleName(), exception.getMessage());
        return Optional.ofNullable(knownExceptions.get(className))
                .map(handler -> handler.apply(exception))
                .orElseGet(() -> internalServerErrorResponse(exception));
    }

    private Map<String, Object> manageBodyValidationErrors(Exception exception) {
        MethodArgumentNotValidException error = (MethodArgumentNotValidException) exception;
        return manageKnowErrors(ValidationError.class, getValidationError(error));
    }


    private Map<String, Object> manageMessageNotReadable(Exception exception) {
        HttpMessageNotReadableException error = (HttpMessageNotReadableException) exception;
        var name = error.getMessage().split("`");
        var entityName = name.length > 1 ? name[1].split("\\.") : new String[]{""};
        return manageKnowErrors(
                EntityError.class,
                new EntityError(
                        ErrorStatus.BAD_REQUEST,
                        messageService.getMessage(ERROR_HANDLER_REQUEST_BODY_ERROR),
                        ErrorCode.NO_SPECIFIC_PROBLEM,
                        entityName[entityName.length - 1]
                )
        );
    }

    private Map<String, Object> manageInvalidFormat(Exception exception) {
        InvalidFormatException error = (InvalidFormatException) exception.getCause();
        return manageKnowErrors(ValidationError.class, this.getValidationError(error));
    }

    private Map<String, Object> manageHeaderError(Exception exception) {
        MissingRequestHeaderException error = (MissingRequestHeaderException) exception;
        String msg = String.format(
                messageService.getMessage(ERROR_HANDLER_MISSING_HEADER),
                error.getHeaderName()
        );

        return manageKnowErrors(
                EntityError.class,
                new EntityError(
                        ErrorStatus.BAD_REQUEST,
                        msg,
                        ErrorCode.NO_SPECIFIC_PROBLEM,
                        HEADER_ENTITY_NAME
                )
        );
    }

    private Map<String, Object> getInternalError() {
        return Map.of(
                STATUS, ErrorStatus.INTERNAL_SERVER_ERROR,
                CODE, ErrorCode.API_UNEXPECTED_ERROR,
                MESSAGE, messageService.getMessage(ERROR_HANDLER_ERROR_INTERNAL)
        );
    }

    private List<ValidationDto> getValidationFieldsErrors(MethodArgumentNotValidException error) {
        return error.getBindingResult()
                .getFieldErrors().stream()
                .map(fieldError -> ValidationDto.builder()
                        .attribute(fieldError.getField())
                        .message(fieldError.getDefaultMessage())
                        .build()
                )
                .toList();
    }

    private List<ValidationDto> getValidationFieldsErrors(InvalidFormatException error) {
        String fieldName = error.getPath().stream()
                .map(this::getFieldNameFromReference)
                .collect(Collectors.joining("."));
        fieldName = fieldName.replace(".[", "[");

        return List.of(ValidationDto.builder()
                .attribute(fieldName)
                .message(messageService.getMessage(ERROR_DEFAULT_MESSAGE_REFERENCE))
                .build());
    }

    private String getFieldNameFromReference(JsonMappingException.Reference reference) {
        return reference.getFieldName() == null ? "[" + reference.getIndex() + "]" : reference.getFieldName();
    }

    private ValidationError getValidationError(MethodArgumentNotValidException error) {
        String[] classPath = Objects.requireNonNull(error.getTarget()).getClass().getName().split("\\.");
        String className = classPath[classPath.length - 1];
        return new ValidationError(
                ErrorStatus.UNPROCESSABLE_CONTENT,
                DEFAULT_ENTITY_ERROR_MESSAGE,
                ErrorCode.UNPROCESSABLE_CONTENT,
                className,
                getValidationFieldsErrors(error)
        );
    }

    private ValidationError getValidationError(InvalidFormatException error) {
        String entityName = error.getPath()
                .stream()
                .map(a -> a.getFrom().getClass().getSimpleName())
                .reduce(BinaryOperator.maxBy(Comparator.naturalOrder()))
                .orElse("");

        return new ValidationError(
                ErrorStatus.UNPROCESSABLE_CONTENT,
                DEFAULT_ENTITY_ERROR_MESSAGE,
                ErrorCode.UNPROCESSABLE_CONTENT,
                entityName,
                this.getValidationFieldsErrors(error));
    }

    private Map<String, Object> internalServerErrorResponse(Exception exception) {
        log.error("The error could not be processed correctly: {0}", exception);
        return getInternalError();
    }

    private List<String> getParameterNamesByAnnotation(HandlerMethod handlerMethod, Class<? extends Annotation> annotationClass) {
        return Arrays.stream(handlerMethod.getMethod().getParameters())
                .filter(parameter -> parameter.isAnnotationPresent(annotationClass))
                .map(Parameter::getName)
                .toList();
    }

    private String getFieldNameFromConstraintViolation(ConstraintViolation<?> violation) {
        String[] propertyPath = violation.getPropertyPath().toString().split("\\.");
        return propertyPath[propertyPath.length - 1];
    }

    public ResponseEntity<Object> resolveRSQLParserException() {
        return ResponseEntity.badRequest()
                .body(manageKnowErrors(
                        UnCodedError.class,
                        new UnCodedError(
                                ErrorStatus.BAD_REQUEST,
                                messageService.getMessage(COMMON_HANDLER_FILTER_FORMAT_ERROR)
                        )
                ));
    }
}
