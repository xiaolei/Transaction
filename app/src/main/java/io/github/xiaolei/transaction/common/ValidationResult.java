package io.github.xiaolei.transaction.common;

/**
 * The validation result.
 */
public class ValidationResult {
    private boolean success;
    private String message;

    public ValidationResult(boolean success, String message) {
        setSuccess(success);
        setMessage(message);
    }

    public ValidationResult(boolean success) {
        setSuccess(success);
    }

    public static ValidationResult successResult(){
        return new ValidationResult(true);
    }

    public static ValidationResult failureResult(String message){
        return new ValidationResult(false, message);
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
