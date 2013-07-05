package net.ontrack.acceptance.pages;

public class ErrorPageException extends RuntimeException {
    public ErrorPageException(String text) {
        super("An error occurred: " + text);
    }
}
