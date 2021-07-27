package edu.buet.messages;

public class LogoutResponse extends MessageBase<Void> {
    private LogoutResponse(boolean success, String message) {
        super(success, message, null);
    }
    public static LogoutResponse errorMessage(String reason) {
        return new LogoutResponse(false, reason);
    }
    public static LogoutResponse successMessage(){
        return new LogoutResponse(true, null);
    }
}
