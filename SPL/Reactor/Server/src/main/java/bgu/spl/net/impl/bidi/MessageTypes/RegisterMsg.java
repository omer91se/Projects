package bgu.spl.net.impl.bidi.MessageTypes;

public class RegisterMsg implements Msg{
    private String username;
    private String password;
    private final MsgType type = MsgType.REGISTER;

    public RegisterMsg(String userName, String password){
        this.password = password;
        this.username = userName;
    }

    public MsgType getMsgType(){
        return type;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
