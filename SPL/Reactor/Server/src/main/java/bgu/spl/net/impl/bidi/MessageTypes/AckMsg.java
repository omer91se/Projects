package bgu.spl.net.impl.bidi.MessageTypes;

import java.util.List;

public class AckMsg implements Msg {
    private short op;
    private final MsgType type = MsgType.ACK;
    private short numOfUsers;
    private List<String> usernameList;
    private short numOfPosts;
    private short numOfFollowers;
    private short numOfFollowing;


    public AckMsg(short op){
        this.op = op;
    }

    public AckMsg(short op, short numOfUsers, List<String> usernameList){
        this.op = op;
        this.numOfUsers = numOfUsers;
        this.usernameList = usernameList;
    }

    public AckMsg(short op, short numOfPosts, short numOfFollowers, short numOfFollowing){
        this.op = op;
        this.numOfPosts = numOfPosts;
        this.numOfFollowers = numOfFollowers;
        this.numOfFollowing = numOfFollowing;
    }

    public short getOp() {
        return op;
    }


    @Override
    public MsgType getMsgType() {
        return type;
    }

    public short getNumOfUsers() {
        return numOfUsers;
    }

    public List<String> getUsernameList() {
        return usernameList;
    }

    public short getNumOfPosts() {
        return numOfPosts;
    }

    public short getNumOfFollowers() {
        return numOfFollowers;
    }

    public short getNumOfFollowing() {
        return numOfFollowing;
    }
}
