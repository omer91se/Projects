package bgu.spl.net.impl.bidi.MessageTypes;

public class ErrorMsg implements Msg {
    private final MsgType type = MsgType.ERROR;
    private short messageOp;

    public ErrorMsg(short messageOp){
        this.messageOp = messageOp;
    }

    public MsgType getMsgType() {
        return type;
    }

    public short getMessageOp() {
        return messageOp;
    }
}
