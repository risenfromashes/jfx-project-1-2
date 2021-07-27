package edu.buet.messages;

public class LoginRequest extends MessageBase<LoginRequest.Data> {
    public static class Data {
        public final int clubId;
        public final String password;
        public Data(int clubId, String password) {
            this.clubId = clubId;
            this.password = password;
        }
    }

    public LoginRequest(int clubId, String password) {
        super(true, null, new Data(clubId, password));
    }
}
