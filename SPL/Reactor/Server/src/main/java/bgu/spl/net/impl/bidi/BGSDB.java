package bgu.spl.net.impl.bidi;

import bgu.spl.net.impl.bidi.MessageTypes.Msg;
import bgu.spl.net.impl.bidi.MessageTypes.MsgType;
import bgu.spl.net.impl.bidi.MessageTypes.PMrequestMsg;
import bgu.spl.net.impl.bidi.MessageTypes.PostMsg;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Holds the data of all registered users.
 */
public class BGSDB {

    //-----------Fields--------------------
    private ConcurrentHashMap<String, String> users;
    private ConcurrentMap<String, Integer> connectedUsers;
    private ConcurrentHashMap<String, List<String>> followingMap;
    private ConcurrentHashMap<String, List<PostMsg>> feeds;
    private ConcurrentHashMap<String, List<PMrequestMsg>> mailBoxes;
    private ConcurrentMap<String, Integer> postsCounter;
    private ReentrantReadWriteLock rwl;

    //----------Constructors---------------
    public BGSDB() {
        this.users = new ConcurrentHashMap<>();
        this.connectedUsers = new ConcurrentHashMap<>();
        this.followingMap = new ConcurrentHashMap<>();
        this.feeds = new ConcurrentHashMap<>();
        this.mailBoxes = new ConcurrentHashMap<>();
        this.postsCounter = new ConcurrentHashMap<>();
        this.rwl = new ReentrantReadWriteLock(true);
    }

    //----------Methods-------------------

    /**
     *
     * @param username
     * @param password
     * @return true if registered successfully.
     */
    public boolean register(String username, String password){
        if(users.containsKey(username))
            return false;

        users.put(username, password);
        followingMap.put(username, new LinkedList<>());
        feeds.put(username, new LinkedList<>());
        mailBoxes.put(username, new LinkedList<>());
        postsCounter.put(username, 0);

        return true;
    }


    /**
     *
     * @param username
     * @return true if logged-in successfully.
     */
    public boolean logIn(String username, String password, int connectionId){
        rwl.writeLock().lock();
        boolean didConnect = false;
        if(!users.containsKey(username))
            return false;
        synchronized (users.get(username)) {
            if (users.get(username).equals(password) && !connectedUsers.containsKey(username)) {
                didConnect = true;
                connectedUsers.put(username, connectionId);

            }
        }
        rwl.writeLock().unlock();
        return didConnect;
    }

    /**
     *
     * @param username
     */
    public void logout(String username) {
        rwl.writeLock().lock();
        connectedUsers.remove(username);
        rwl.writeLock().unlock();
    }

    /**
     *
     * @param username
     * @param toFollowList
     * @return a list of names of all successful follows.
     */
    public List<String> follow(String username, List<String> toFollowList){
        rwl.writeLock().lock();
        List<String> success = new LinkedList<>();
        for(String user : toFollowList) {
            if (users.containsKey(user)) {
                if (!followingMap.get(user).contains(username)) {
                    success.add(user);
                    followingMap.get(user).add(username);
                }
            }
        }
        rwl.writeLock().unlock();
        return success;
    }

    /**
     *
     * @param username
     * @param toFollowList
     * @return a list of names of all successful unfollows.
     */
    public List<String> unFollow(String username, List<String> toFollowList){
        rwl.writeLock().lock();
        List<String> success = new LinkedList<>();
        for(String user : toFollowList){
            if(users.containsKey(user)) {
                if (followingMap.get(user).contains(username)) {
                    success.add(user);
                    followingMap.get(user).remove(username);
                }
            }
        }
        rwl.writeLock().unlock();
        return success;
    }

    /**
     *
     * @param username
     * @return {@link UserInfo} of {@code username}.
     */
    public UserInfo getUserInfo(String username){
        rwl.readLock().lock();
        UserInfo userInfo = null;
        if(users.containsKey(username)){
            List<String> followList = followingMap.get(username);
            List<PostMsg> feed = feeds.get(username);
            List<PMrequestMsg> mailBox = mailBoxes.get(username);
            userInfo = new UserInfo(username, followList, feed, mailBox);
        }
        rwl.readLock().unlock();
        return userInfo;
    }

    /**
     *
     * @param username
     * @return true if {@code username} is registered.
     */
    public boolean isRegistered(String username){
        return (users.containsKey(username));

    }

    /**
     * put {@code message} in the mailboxes/feeds of all the unconnected users and return
     * a list of connectionIds of all connected users in {@code users}.
     * @param users
     * @param message
     * @return a list of connectionIds of all connected users in {@code users}.
     */
    public List<Integer> postOffice(List<String> users, Msg message){
        rwl.readLock().lock();
        List<Integer> connectionIds = new LinkedList<>();
        for (String user : users) {
            if (connectedUsers.containsKey(user)) {
                connectionIds.add(connectedUsers.get(user));
            } else {
                if (message.getMsgType() == MsgType.POST) {
                    if (feeds.containsKey(user))
                        feeds.get(user).add((PostMsg) message);
                } else {
                    if (mailBoxes.containsKey(user))
                        mailBoxes.get(user).add((PMrequestMsg) message);
                }
            }
        }
        rwl.readLock().unlock();
        return connectionIds;
    }


    /**
     *
     * @return a list of all registered users.
     */
    public List<String> getUserList(){
        rwl.readLock().lock();
        List<String> usersList = new LinkedList<>();
        for(String user: users.keySet())
            usersList.add(user);
        rwl.readLock().unlock();
        return usersList;
    }

    /**
     *
     * @param user
     * @return number of posts {@code user} shared.
     */
    public int numOfPosts(String user){
        return postsCounter.get(user);
    }

    /**
     *
     * @param user
     *
     * @return number of users {@code user} is following.
     */
    public int numOfUsersUserIsFollowing(String user){
        rwl.readLock().lock();
        int counter = 0;
        for(List following : followingMap.values()){
            if(following.contains(user)){
                counter++;
            }
        }
        rwl.readLock().unlock();
        return counter;
    }

    /**
     *
     * @param user
     * @return number of followers {@code user} have.
     */
    public int numOfFollowers(String user){
        rwl.readLock().lock();
        int size = followingMap.get(user).size();
        rwl.readLock().unlock();
        return size;
    }

    /**
     * Increments the number of posts {@code username} shared.
     * @param username
     */
    public void postShared(String username) {
        rwl.writeLock().lock();
        if (postsCounter.containsKey(username)) {
            postsCounter.replace(username, postsCounter.get(username)+1);
        }
        rwl.writeLock().unlock();
    }

    /**
     *
     * @param username
     */
    public void cleanBoxes(String username){
        this.mailBoxes.replace(username, new LinkedList<>());
        this.feeds.replace(username, new LinkedList<>());
    }




}
