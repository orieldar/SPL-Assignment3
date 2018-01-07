package bgu.spl181.net.api.USTP;

import com.google.gson.*;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;


public abstract class ServerUsers {

    protected  ConcurrentHashMap<String,User> RegisteredUsers;
    protected final Object WriteLock = new Object();
    protected  JsonArray users;
    protected JsonObject ServerBase;
    protected Gson gson;

    public ServerUsers(){
    }

    public ServerUsers(String FileName){
        Gson gson = new Gson();
        JsonParser parser = new JsonParser();
        ServerBase = new JsonObject();
        try {
            ServerBase = (JsonObject) parser.parse(new FileReader(FileName));
        } catch (Exception e) {
            e.printStackTrace();
        }

        users = ServerBase.getAsJsonArray("users");
        RegisteredUsers = new ConcurrentHashMap<String,User>();

        for(int i=0; i<users.size();i++){
            User user = gson.fromJson(users.get(i), User.class);
            RegisteredUsers.put(user.getUsername(),user);
        }
    }

    public boolean checkIfTaken(String username){
        User user = RegisteredUsers.get(username);
        if(user!=null)
            return false;
        return true;
    }

    public boolean isLoggedIn(String username){
        User user = RegisteredUsers.get(username);
        if(user!=null)
            return user.isLoggedIn();
        return false;
    }



    public User logIn(String username, String password) {
        User user = RegisteredUsers.get(username);
        synchronized (user) {
            if (user != null) {
                if (user.getPassword().equals(password))
                    return user;
            }
            return null;
        }
    }



    protected void updateJson(){
        try (FileWriter file = new FileWriter("Users.json")) {
            file.write(ServerBase.toString());

        } catch (IOException e) { e.printStackTrace();  }

    }

   /*public static List<User> getUsers(){ /////delete
        List <User> users = new ArrayList<User> ();
        for (User user: RegisteredUsers.values())
            users.add(user);
        return users;

    }*/

    public  boolean  Register(String username, String password, String ObjectData) {
        User newUser = new User(username, password);
        if (RegisteredUsers.get(username) != null)
            return false;
        RegisteredUsers.put(username, newUser);

        synchronized (WriteLock) {
            JsonObject jsonUser = new JsonObject();
            jsonUser.addProperty("username", username);
            jsonUser.addProperty("type", "normal");
            jsonUser.addProperty("password", password);
            users.add(jsonUser);
            updateJson();
        }
        return true;
    }
}
