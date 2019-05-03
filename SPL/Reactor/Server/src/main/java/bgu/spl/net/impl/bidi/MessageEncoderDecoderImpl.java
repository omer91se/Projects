package bgu.spl.net.impl.bidi;

import bgu.spl.net.api.MessageEncoderDecoder;
import bgu.spl.net.impl.bidi.MessageTypes.*;

import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;

public class MessageEncoderDecoderImpl<T> implements MessageEncoderDecoder<T> {
    private byte[] bytes = new byte[1 << 10];
    byte[] encodeBytes;
    int encodeBytesInd;
    int currByte = 0;


    @Override
    @SuppressWarnings("unchecked")
    public T decodeNextByte(byte nextByte) {
        Msg message = null;
        if(nextByte == '\n') {
            byte[] opBytes =  new byte[2];
            opBytes[0] = bytes[0];
            opBytes[1] = bytes[1];
            short op = bytesToShort(opBytes);
            switch (op){
                case(1):
                    byte[] usernameByte1 = subArrayToZero(2);
                    String username1 = new String(usernameByte1, StandardCharsets.UTF_8);
                    if(username1.indexOf('\0') >= 0){
                        username1 = username1.substring(0 , username1.length()-1);
                    }
                    byte[] passwordByte1 = subArrayToZero(2 + usernameByte1.length);
                    String password1 = new String(passwordByte1, StandardCharsets.UTF_8);

                    message = new RegisterMsg(username1, password1);
                    break;
                case(2):
                    byte[] usernameByte2 = subArrayToZero(2);
                    String username2 = new String(usernameByte2, StandardCharsets.UTF_8);
                    if(username2.indexOf('\0') >= 0){
                        username2 = username2.substring(0 , username2.length()-1);
                    }
                    byte[] passwordByte2 = subArrayToZero(2 + usernameByte2.length);
                    String password2 = new String(passwordByte2, StandardCharsets.UTF_8);

                    message = new LoginMsg(username2,password2);
                    break;

                case(3):
                    message = new LogoutMsg();
                    break;

                case(4):
                    boolean isFollow = false;
                    if (bytes[2] == 48)
                        isFollow = true;

                    char a = (char)bytes[3];
                    char b = (char)bytes[4];
                    StringBuilder sb = new StringBuilder();
                    sb.append(a);
                    sb.append(b);
                    String numOfUsers = sb.toString();
                    int nOfUsers = Integer.parseInt(numOfUsers);
                    int from = 5;
                    List<String> users = new LinkedList<>();
                    for (int i = 0; i < nOfUsers; i++) {
                        byte[] usernameByte4 = subArrayToZero(from);
                        from = from + usernameByte4.length ;
                        String username4 = new String(usernameByte4,StandardCharsets.UTF_8);
                        if(username4.indexOf('\0') >= 0){
                            username4 = username4.substring(0 , username4.length()-1);
                        }
                        users.add(username4);
                    }

                    message = new FollowMsg(isFollow, users);
                    break;

                case(5):
                    byte[] contentByte5 = subArrayToZero(2);
                    String cont = new String(contentByte5, StandardCharsets.UTF_8);
                    message = new PostMsg(cont.substring(0, cont.length()-1));
                    break;

                case(6):
                    byte[] usernameByte6 = subArrayToZero(2);
                    String username6 = new String(usernameByte6, StandardCharsets.UTF_8);
                    if(username6.indexOf('\0') >= 0){
                        username6 = username6.substring(0 , username6.length()-1);
                    }
                    byte[] contentByte6 = subArrayToZero(2 + usernameByte6.length);
                    String content6 = new String(contentByte6, StandardCharsets.UTF_8);

                    message = new PMrequestMsg(username6, content6);
                    break;

                case(7):
                    message = new UserListReqMsg();
                    break;

                case(8):
                    byte[] usernameByte8 = subArrayToZero(2);
                    String username8 = new String(usernameByte8, StandardCharsets.UTF_8);
                    if(username8.indexOf('\0') >= 0){
                        username8 = username8.substring(0 , username8.length()-1);
                    }
                    message = new StatMsg(username8);
                    break;

            }

            bytes = new byte[1 << 10];
            currByte = 0;
            return (T)message;
        }

        else{
            bytes[currByte] = nextByte;
            currByte++;
            return null;}
    }

    /**
     * Encodes a message T to byte[]
     * @param message the message to encode
     * @return encoded byte[]
     */
    @Override
    public byte[] encode(T message) {
        encodeBytes = new byte[1 << 10];
        encodeBytesInd = 0;

        switch(((Msg)message).getMsgType()){
            case NOTIFICATION:

                //Encodes op
                short notifOp = 9;
                byte[] notifOpBytes = shortToBytes(notifOp);
                pushBytes(notifOpBytes);


                //Encode PM ot Public
                String type;
                if(((NotificationMsg)message).isPM())
                    type = "0";
                else
                    type = "1";

                byte[] typeByte = type.getBytes();
                pushBytes(typeByte);

                //Encode posting user
                byte[] postingUserByte = (((NotificationMsg) message).getPostingUser()).getBytes();
                pushBytes(postingUserByte);

                pushZero();

                //Encode content
                byte[] contentByte = (((NotificationMsg) message).getContent()).getBytes();
                pushBytes(contentByte);

                if(type == "1")
                    pushZero();



                break;

            case ACK:

                //Encode opcode
                short opAck = 10;
                byte[] AckBytes = shortToBytes(opAck);
                pushBytes(AckBytes);

                //Encode message opcode
                short messageOp = ((AckMsg)message).getOp();
                byte[] messageOpBytes = shortToBytes(messageOp);
                pushBytes(messageOpBytes);

                switch (((AckMsg) message).getOp()){

                    case(4):
                        //Encode numOfUsers
                        short numOfUsers = ((AckMsg) message).getNumOfUsers();
                        byte[] numOfUsersBytes = shortToBytes(numOfUsers);
                        pushBytes(numOfUsersBytes);

                        //Encode usernameList
                        for(String username :((AckMsg) message).getUsernameList()){
                            byte[] usernameBytes = username.getBytes();
                            pushBytes(usernameBytes);
                            pushZero();
                        }

                        break;

                    case(7):

                        //Encode numOfUsers
                        short numOfUsers2 = ((AckMsg) message).getNumOfUsers();
                        byte[] numOfUsersBytes2 = shortToBytes(numOfUsers2);
                        pushBytes(numOfUsersBytes2);

                        //Encode usernameList
                        for(String username :((AckMsg) message).getUsernameList()){
                            byte[] usernameBytes = username.getBytes();
                            pushBytes(usernameBytes);
                            pushZero();
                        }

                        break;

                    case(8):

                        //Encode numOfPosts
                        byte[] numOfPosts = shortToBytes(((AckMsg) message).getNumOfPosts());
                        pushBytes(numOfPosts);

                        //Encode numOfFollowers
                        byte[] numOfFollowers = shortToBytes(((AckMsg) message).getNumOfFollowers());
                        pushBytes(numOfFollowers);

                        //Encode numOfFollowing
                        byte[] numOfFollowing = shortToBytes(((AckMsg) message).getNumOfFollowing());
                        pushBytes(numOfFollowing);

                        break;

                }

                break;

            case ERROR:

                //Encode opcode
                short opError = 11;
                byte[] ErrorBytes = shortToBytes(opError);
                pushBytes(ErrorBytes);

                //Encode message opcode
                short messageOp2 = ((ErrorMsg)message).getMessageOp();
                byte[] messageOpBytes2 = shortToBytes(messageOp2);
                pushBytes(messageOpBytes2);

                break;
        }
        byte[] toSend = new byte[encodeBytesInd];
        for(int i = 0; i<encodeBytesInd; i++){
            toSend[i] = encodeBytes[i];
        }
        return toSend;
    }


    private void pushBytes(byte[] push){
        for(int i = 0; i < push.length; i++){
            encodeBytes[encodeBytesInd] = push[i];
            encodeBytesInd++;
        }

    }

    /**
     * pushes a zero byte into {@code encodeByte}.
     */
    private void pushZero(){
        byte[] zeroByte = shortToBytes((short)0);
        byte[] toPush = new byte[1];
        toPush[0] = zeroByte[1];
        pushBytes(toPush);
    }


    public short bytesToShort(byte[] byteArr) {
        short result = (short)((byteArr[0] & 0xff) << 8);
        result += (short)(byteArr[1] & 0xff);
        return result;
    }

    public byte[] shortToBytes(short num) {
        byte[] bytesArr = new byte[2];
        bytesArr[0] = (byte)((num >> 8) & 0xFF);
        bytesArr[1] = (byte)(num & 0xFF);
        return bytesArr;
    }

    /**
     *
     * @param from
     * @return byte[] array from {@code from} to the next '\0' byte in the bytes array.
     */

    private byte[] subArrayToZero(int from){
        int size = 0;
        int i = 0;
        for(i = from; i < bytes.length; i++){
            size++;
            if(bytes[i] == '\0' | bytes[i] == '\n'){
                break;
            }
        }
        byte[] output = new byte[size];
        for(int j = 0; j < output.length ; j++){
            output[j] = bytes[from];
            from++;
        }

        return output;
    }

}
