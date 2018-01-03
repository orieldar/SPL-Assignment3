package bgu.spl181.net.api.bidi;

import java.util.HashMap;
import java.util.Map;


public class ConnectionsImpl<T> implements Connections<T> {

    Map<Integer,ConnectionHandler> connections;

    public ConnectionsImpl(){
        connections = new HashMap<Integer,ConnectionHandler>(); //need to be concurrent!?
    }
    @Override
    public boolean send(int connectionId, T msg) {
        ConnectionHandler connectionHandler = connections.get(connectionId);
        if(connectionHandler == null)
            return false;
        connectionHandler.send(msg);
        return true;
    }

    public void addConnection(int connectionId, ConnectionHandler<T> handler){
        connections.put(connectionId, handler);
    }

    @Override
    public void broadcast(T msg) {
        for(ConnectionHandler connectionHandler : connections.values())
            connectionHandler.send(msg);
    }

    @Override
    public void disconnect(int connectionId) {
        connections.remove(connectionId);

    }
}
