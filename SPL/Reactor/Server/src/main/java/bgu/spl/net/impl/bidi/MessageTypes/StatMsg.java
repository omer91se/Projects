package bgu.spl.net.impl.bidi.MessageTypes;

public class StatMsg implements Msg {
    private final MsgType type = MsgType.STAT;
    private String username;

    public StatMsg(String username){
        this.username = username;
    }

    public MsgType getMsgType() {
        return type;
    }

    public String getUsername() {
        return username;
    }
}
