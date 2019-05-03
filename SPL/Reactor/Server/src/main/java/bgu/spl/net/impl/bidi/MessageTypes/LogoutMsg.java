package bgu.spl.net.impl.bidi.MessageTypes;

public class LogoutMsg implements Msg {
    @Override
    public MsgType getMsgType() {
        return MsgType.LOGOUT;
    }
}
