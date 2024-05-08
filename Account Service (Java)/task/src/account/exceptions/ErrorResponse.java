package account.exceptions;

public record ErrorResponse (
    int status,
    String error,
    String message,
    String path
) { }
