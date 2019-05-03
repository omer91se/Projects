package bgu.spl.net.impl.bidi.MessageTypes;

public class PMrequestMsg implements Msg {
    private final MsgType type = MsgType.PM;
    private String content;
    private String username;

    public PMrequestMsg(String username, String content){
        this.content = content;
        this.username = username;
    }

    public MsgType getMsgType() {
        return type;
    }

    public String getContent() {
        return content;
    }

    public String getUsername() {
        return username;
    }
}
