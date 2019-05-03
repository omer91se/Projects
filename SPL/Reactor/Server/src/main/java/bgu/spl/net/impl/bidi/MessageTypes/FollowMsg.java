package bgu.spl.net.impl.bidi.MessageTypes;

import java.util.List;

public class FollowMsg implements Msg {
    boolean isFollow;
    List<String> users;
    private final MsgType type = MsgType.FOLLOW;

    public FollowMsg(boolean isFollow, List<String> users){
        this.isFollow = isFollow;
        this.users = users;
    }

    public boolean isFollow() {
        return isFollow;
    }

    public List<String> getUsers() {
        return users;
    }

    public MsgType getMsgType() {
        return type;
    }
}
