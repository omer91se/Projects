package bgu.spl.net.impl.BGSServer;

import bgu.spl.net.impl.bidi.BGSDB;
import bgu.spl.net.impl.bidi.BidiMessagingProtocolImpl;
import bgu.spl.net.impl.bidi.MessageEncoderDecoderImpl;
import bgu.spl.net.srv.Server;

public class ReactorMain {
    public static void main(String[] args) {
        BGSDB DB = new BGSDB();
        Server.reactor(
                Runtime.getRuntime().availableProcessors(),
                7777,
                () -> new BidiMessagingProtocolImpl<>(DB),
                MessageEncoderDecoderImpl::new
        ).serve();
    }
}