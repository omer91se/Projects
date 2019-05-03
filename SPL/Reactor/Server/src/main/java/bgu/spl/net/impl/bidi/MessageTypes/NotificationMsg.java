package bgu.spl.net.impl.bidi.MessageTypes;

public class NotificationMsg implements Msg {
    private final MsgType type = MsgType.NOTIFICATION;
    private String postingUser;
    private String content;
    private boolean isPM;

    public NotificationMsg(boolean isPM ,String postingUser, String content){
        this.content = content;
        this.postingUser = postingUser;
        this.isPM = isPM;
    }

    public MsgType getMsgType() {
        return type;
    }

    public String getPostingUser() {
        return postingUser;
    }

    public String getContent() {
        return content;
    }

    public boolean isPM() {
        return isPM;
    }
}
