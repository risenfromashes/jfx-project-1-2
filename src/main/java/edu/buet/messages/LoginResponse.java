package edu.buet.messages;

import edu.buet.data.Club;

public class LoginResponse extends MessageBase<Club>{
    private LoginResponse(boolean success, String message, Club body){
        super(success, message, body);
    }
    public static LoginResponse errorMessage(String reason){
        return new LoginResponse(false, reason, null);
    }
    public static LoginResponse successMessage(Club club){
        return new LoginResponse(true, null, club);
    }
}
