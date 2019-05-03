package bgu.spl.net.impl.bidi;

import bgu.spl.net.api.bidi.Connections;
import bgu.spl.net.srv.bidi.ConnectionHandler;

import java.util.concurrent.ConcurrentHashMap;


public class ConnectionsImpl<T> implements Connections<T> {

    private ConcurrentHashMap<Integer, ConnectionHandler> IDConnectionHandlerMap;

    public ConnectionsImpl(){
        this.IDConnectionHandlerMap = new ConcurrentHashMap<>();
    }

    /**
     * sends a message T to client represented
     * by the given {@code connectionId}
     * <p>
     *
     * @param connectionId
     * @param msg
     * @return false if {@code connectionId} is not connected, true otherwise.
     */
    @Override
    @SuppressWarnings("unchecked")
    public boolean send(int connectionId, T msg) {
        synchronized (IDConnectionHandlerMap) {
            if (IDConnectionHandlerMap.containsKey(connectionId)) {
                IDConnectionHandlerMap.get(connectionId).send(msg);
                return true;
            }
            return false;
        }
    }

    /**
     * sends a message T to all active clients. This
     * includes clients that has not yet completed log-in by the BGS protocol.
     * <p>
     *
     * @param msg
     */
    @Override
    @SuppressWarnings("unchecked")
    public void broadcast(T msg) {
        synchronized (IDConnectionHandlerMap) {
            for (ConnectionHandler handler : IDConnectionHandlerMap.values()) {
                handler.send(msg);
            }
        }
    }

    /**
     * Removes active client {@code connectionId} from map.
     * <p>
     *
     * @param connectionId of {@link ConnectionHandler} to disconnect.
     */
    @Override
    public void disconnect(int connectionId) {
        synchronized (IDConnectionHandlerMap) {
            IDConnectionHandlerMap.remove(connectionId);
        }
    }

    /**
     * Adds {@code handler} to connections and sends all the messages that occurred after {@code time}.
     * <p>
     *
     * @param handler to add.
     */
    public void add(int id, ConnectionHandler handler){
        synchronized (IDConnectionHandlerMap) {
            IDConnectionHandlerMap.put(id, handler);
        }
    }

}
