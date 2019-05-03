package bgu.spl.net.impl.bidi.MessageTypes;

import java.util.LinkedList;
import java.util.List;

public class PostMsg implements Msg {
    private final MsgType type = MsgType.POST;
    private String content;
    private String postingUser;

    public PostMsg(String content){
        this.content = content;

    }

    public MsgType getMsgType() {
        return type;
    }

    public String getContent() {
        return content;
    }

    /**
     * Create a List of users that are tagged in the message (@user).
     * @return
     */
    public List<String> getTaggedUsersList(){
        List<String> taggedUsers = new LinkedList<>();
        String msg = content;
        String user = "";
        int i = 0;
        while((i = msg.indexOf('@'))>=0) {
            if(msg.charAt(i+1) != '@') {
                msg = msg.substring(msg.indexOf('@'));

                if (msg.indexOf(' ') >= 0)
                    taggedUsers.add(msg.substring(1, msg.indexOf(' ')));

                else taggedUsers.add(msg.substring(1));
            }
            if(msg.length()>i)
                msg = msg.substring(i+1);

        }
        return taggedUsers;
    }

    public void updatePostingUser(String postingUser){
        this.postingUser = postingUser;
    }

    public String getPostingUser() {
        return postingUser;
    }
}
