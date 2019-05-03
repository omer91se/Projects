package bgu.spl.net.impl.bidi;

import bgu.spl.net.api.bidi.BidiMessagingProtocol;
import bgu.spl.net.api.bidi.Connections;
import bgu.spl.net.impl.bidi.MessageTypes.*;

import java.util.LinkedList;
import java.util.List;


public class BidiMessagingProtocolImpl<T> implements BidiMessagingProtocol<T> {

    //-----------Fields--------------------
    private Connections<T> connections;
    private int connectionId;
    private UserInfo userInfo;
    private BGSDB DB;
    private boolean shouldTerminate;
    private boolean isLoggedIn;


    //-----------Constructors--------------
    public BidiMessagingProtocolImpl(BGSDB DB){
        this.DB = DB;
        shouldTerminate = false;
        isLoggedIn = false;
    }


    //-----------Methods-------------------
    @Override
    public void start(int connectionId, Connections<T> connections) {
        this.connections = connections;
        this.connectionId = connectionId;
        this.userInfo = null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void process(T message) {

        boolean isError = false;
        short op = -1;
        switch (((Msg) message).getMsgType()) {
            case REGISTER:
                op = 1;
                isError = !(DB.register(((RegisterMsg) message).getUsername(), ((RegisterMsg) message).getPassword()));
                if (!isError) {
                    connections.send(connectionId, (T) new AckMsg(op));
                }
                break;

            case LOGIN:
                op = 2;
                String username = ((LoginMsg) message).getUsername();
                String password = ((LoginMsg) message).getPassword();

                isError = (!(DB.logIn(username, password, connectionId))) | (isLoggedIn);
                if (!isError) {
                    this.userInfo = DB.getUserInfo(username);
                    connections.send(connectionId, (T) new AckMsg(op));

                    //Sends all the PM the user missed when he was logged-out.
                    for (PMrequestMsg PM : userInfo.getMailBox()) {
                        NotificationMsg notif = new NotificationMsg(true, PM.getUsername(), PM.getContent());
                        connections.send(connectionId, (T)notif);
                    }


                    //Sends all the Posts the user missed when he was logged-out.
                    for (PostMsg post : userInfo.getFeed()) {
                        NotificationMsg notif = new NotificationMsg(false, post.getPostingUser(), post.getContent());
                        connections.send(connectionId, (T) notif);
                    }
                    DB.cleanBoxes(username);
                    isLoggedIn = true;
                }


                break;

            case LOGOUT:
                op = 3;
                if (userInfo != null) {
                    DB.logout(userInfo.getUsername());
                    userInfo = null;
                    shouldTerminate = true;

                } else
                    isError = true;

                if (!isError) {
                    connections.send(connectionId, (T) new AckMsg(op));
                }
                break;

            case FOLLOW:
                op = 4;
                List<String> usersList = ((FollowMsg) message).getUsers();
                List<String> success = new LinkedList<>();
                if (((FollowMsg) message).isFollow()) {
                    if (userInfo != null) {
                        success = DB.follow(userInfo.getUsername(), usersList);
                        if (success.isEmpty()) {
                            isError = true;
                        }
                    }
                } else {
                    if (userInfo != null) {
                        success = DB.unFollow(userInfo.getUsername(), usersList);
                        if (success.isEmpty()) {
                            isError = true;
                        }
                    }
                }
                if (!isError) {
                    connections.send(connectionId, (T) new AckMsg(op, (short)success.size(), success));
                }

                break;

            case POST:
                op = 5;

                if (userInfo == null) {
                    isError = true;

                } else {
                    ((PostMsg)message).updatePostingUser(userInfo.getUsername());
                    DB.postShared(userInfo.getUsername());

                    //list of users to send the post to.
                    List<String> users = uniteUserlists(userInfo.getFollowingList(), ((PostMsg) message).getTaggedUsersList());

                    //get the connection ids of all the connected recipient, and save the post for all the disconnected recipient.
                    List<Integer> connectionIds = DB.postOffice(users, (Msg) message);

                    for (Integer id : connectionIds) {
                        NotificationMsg notif = new NotificationMsg(false, userInfo.getUsername(), ((PostMsg) message).getContent());
                        connections.send(id, (T) notif);
                    }
                }

                if (!isError) {
                    connections.send(connectionId, (T) new AckMsg(op));
                }

                break;

            case PM:
                op = 6;
                if (userInfo == null) {
                    isError = true;
                } else {
                    if (DB.isRegistered(((PMrequestMsg) message).getUsername())) {
                        List<String> user = new LinkedList<>();
                        user.add(((PMrequestMsg) message).getUsername());

                        //get the connection id of the user and save the post if he is not connected.
                        List<Integer> connectionIds = DB.postOffice(user, (Msg) message);

                        if (!connectionIds.isEmpty()) {
                            NotificationMsg notif = new NotificationMsg(true, userInfo.getUsername(), ((PMrequestMsg) message).getContent());
                            connections.send(connectionIds.get(0), ((T) notif));
                        }

                    }
                    else
                        isError = true;
                }
                if (!isError) {
                    connections.send(connectionId, (T) new AckMsg(op));
                }
                break;

            case USERLIST:
                op = 7;
                if (userInfo == null)
                    isError = true;
                else {
                    List<String> userList = DB.getUserList();
                    AckMsg ack = new AckMsg(op, (short)userList.size(), userList);
                    connections.send(connectionId, (T) ack);
                }

                break;

            case STAT:
                op = 8;
                if (userInfo == null | !DB.isRegistered(((StatMsg)message).getUsername()))
                    isError = true;
                else {
                    int numOfPosts = DB.numOfPosts(((StatMsg)message).getUsername());
                    int numOfUsersUserIsFollowing = DB.numOfUsersUserIsFollowing(((StatMsg)message).getUsername());
                    int numOfFollowers = DB.numOfFollowers(((StatMsg)message).getUsername());
                    connections.send(connectionId, (T)new AckMsg(op, (short)numOfPosts, (short)numOfFollowers , (short)numOfUsersUserIsFollowing));
                }
                break;
        }
        if (isError) {
            connections.send(connectionId, (T) new ErrorMsg(op));
        }
    }


    /**
     * Unites 2 lists of Strings to one list with no returns.
     *
     * @param followingList
     * @param taggedUsersList
     * @return United list
     */
    private List<String> uniteUserlists(List<String> followingList, List<String> taggedUsersList) {
        List<String> list = new LinkedList<>(followingList);
        for(String user : taggedUsersList){
            boolean toAdd = true;
            for(String followUser: list){
                if(user.equals(followUser))
                    toAdd = false;
            }
            if(toAdd)
                list.add(user);
        }
        return list;
    }


    @Override
    public boolean shouldTerminate() {
        return shouldTerminate;
    }

}
