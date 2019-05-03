package bgu.spl.net.impl.BGSServer;

import bgu.spl.net.impl.bidi.BGSDB;
import bgu.spl.net.impl.bidi.BidiMessagingProtocolImpl;
import bgu.spl.net.impl.bidi.MessageEncoderDecoderImpl;
import bgu.spl.net.srv.Server;

public class TPCMain {
    public static void main(String[] args){
        BGSDB DB = new BGSDB();

        Server.threadPerClient(
                7777,
                 ()-> new BidiMessagingProtocolImpl<>(DB),
                 MessageEncoderDecoderImpl::new
        ).serve();
    }

}
