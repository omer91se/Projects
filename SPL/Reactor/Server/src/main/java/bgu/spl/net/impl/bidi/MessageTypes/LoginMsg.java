package bgu.spl.net.impl.bidi.MessageTypes;

public class LoginMsg implements Msg{
    String username;
    String password;
    private final MsgType type = MsgType.LOGIN;

    public LoginMsg(String username, String password){
        this.password = password;
        this.username = username;
    }

    @Override
    public MsgType getMsgType() {
        return type;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
