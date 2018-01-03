package bgu.spl181.net.api.ustp;

import bgu.spl181.net.api.bidi.BidiMessagingProtocol;
import bgu.spl181.net.api.bidi.Connections;
import bgu.spl181.net.api.bidi.ServerUsers;

public class USTProtocol implements BidiMessagingProtocol<String> {

    private ServerUsers users;
    private Connections<String> connections;
    private int connectionId;



    public USTProtocol(ServerUsers args){
        if(args != null)
            this.users = args;
        else
            this.users = new ServerUsers();
        }
    }
    @Override
    public void start(int connectionId, Connections<String> connections) {
        this.connectionId = connectionId;
        this.connections = connections;

    }

    @Override
    public void process(String message) {
        String commandName = message.substring(0, message.indexOf(" "));
        String commandDet = message.substring(message.indexOf(" ") + 1);
        if (commandName == "REGISTER")
            register(commandDet, users);





         /*
            String userName = commandDet.substring(0, commandDet.indexOf(" "));
            String userPass = commandDet.substring(commandDet.indexOf(" ") + 1);

            users.addUser(userName, userPass);
            connections.send(connectionId, message);
        }
    }

    public void acknowledge(String message){
        connections.send(connectionId, "ACK " + message);
    }

    @Override
    public boolean shouldTerminate() {
        return false;
    }
    */
