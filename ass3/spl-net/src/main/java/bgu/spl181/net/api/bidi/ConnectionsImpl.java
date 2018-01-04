package bgu.spl181.net.api.bidi;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class ConnectionsImpl<T> implements Connections<T> {

    Map<Integer,ConnectionHandler<T>> connections;

    public ConnectionsImpl(){
        connections = new HashMap<Integer,ConnectionHandler<T>>(); //need to be concurrent!?
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
        try {
            connections.get(connectionId).close();
        } catch (IOException e) {
            e.printStackTrace(); // ?????
        }
        connections.remove(connectionId);

    }
}
