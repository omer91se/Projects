package bgu.spl.net.impl.bidi.MessageTypes;

public class UserListReqMsg implements Msg {
    @Override
    public MsgType getMsgType() {
        return MsgType.USERLIST;
    }
}
