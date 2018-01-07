package bgu.spl181.net.api.ustp;

import bgu.spl181.net.api.bidi.BidiMessagingProtocol;
import bgu.spl181.net.api.bidi.Connections;
import bgu.spl181.net.api.bidi.ServerUsers;

import java.util.concurrent.atomic.AtomicBoolean;

public abstract class USTProtocol implements BidiMessagingProtocol<String>{

    protected ServerUsers users;
    protected User user;
    protected Connections<String> connections;
    protected int connectionId;

    public USTProtocol(ServerUsers args){this.users = args;}

    @Override
    public void start(int connectionId, Connections<String> connections) {
        this.connectionId = connectionId;
        this.connections = connections;

    }

    @Override
    public void process(String message) {
        String commandName = message.substring(0, message.indexOf(" "));
        String commandDet = message.substring(message.indexOf(" ") + 1);
        if (commandName.equals("REGISTER"))
            register(commandDet);
        else if (commandName.equals("LOGIN"))
            login(commandDet);
            else if (commandName.equals("SIGNOUT"))
                signOut();
                else if (commandName.equals("REQUEST"))
                    request(commandDet);
    }

    @Override
    public boolean shouldTerminate() {
        return false;
    }


    private void login(String detail){
        String[] details = detail.split("\\s+");
        if((details.length != 2)||(user != null)) {
            connections.send(connectionId, "ERROR login failed");
            return;
        }
        user = users.logIn(details[0], details[1]); // need to check with sync if the user is already logged in!
        if(user == null) {
            connections.send(connectionId, "ERROR login failed");
            return;
        }
        connections.send(connectionId, "ACK login succeeded");
    }

    private void signOut(){
        if(user == null){
            connections.send(connectionId, "ERROR signout failed");
            return;
        }
        user.logOut();
        user = null;
        connections.send(connectionId, "ACK login succeeded"); //need to check if there is a problem with the pool (try to disconnect while a pending actions.. maybe we throw it in the pool
        connections.disconnect(connectionId);
    }

    protected abstract void register(String detail);

    protected abstract void request(String detail);


}