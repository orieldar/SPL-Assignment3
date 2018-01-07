package bgu.spl181.net.api.BlockBuster;

import bgu.spl181.net.api.USTP.ServerUsers;
import bgu.spl181.net.api.USTP.User;
import com.google.gson.*;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

public class BBServerUsers extends ServerUsers {


    public BBServerUsers(String FileName){
        gson = new Gson();
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
            User user = gson.fromJson(users.get(i), BBUser.class);
            RegisteredUsers.put(user.getUsername(),user);
        }
    }

    @Override
    public boolean Register(String username, String password, String country) {
        User newUser = new BBUser(username,password,country);
        if(RegisteredUsers.get(username)!=null)
            return false;
        RegisteredUsers.put(username,newUser);

        synchronized (WriteLock) {
            JsonObject jsonUser = new JsonObject();
            jsonUser.addProperty("username", username);
            jsonUser.addProperty("type", "normal");
            jsonUser.addProperty("password", password);
            jsonUser.addProperty("country", country);
            jsonUser.addProperty("movies", String.valueOf(((JsonElement)new JsonArray())));
            jsonUser.addProperty("balance", "0");

            users.add(jsonUser);
            updateJson();
        }
        return true;
    }

    public void addToBalance(User user , String balance) {
        int balanceToAdd = Integer.parseInt(balance);
        int newBalance = ((BBUser) user).getBalance() + balanceToAdd;
        ((BBUser) user).setBalance(newBalance);

        synchronized (WriteLock) {
            for (int i = 0; i < users.size(); i++) {
                JsonObject jsonUser = users.get(i).getAsJsonObject();
                if (jsonUser.get("username").getAsString().equals(user.getUsername())) {
                    jsonUser.remove("balance");
                    jsonUser.addProperty("balance", newBalance);
                    break;
                }
            }
            updateJson();

        }
    }

    public void addMovie(User user, Movie movie){
        ((BBUser)user).addMovie(movie);

        synchronized (WriteLock) {
            for (int i = 0; i < users.size(); i++) {
                JsonObject jsonUser = users.get(i).getAsJsonObject();
                if (jsonUser.get("username").getAsString().equals(user.getUsername())) {
                    JsonArray movieArray = (JsonArray) jsonUser.get("movies");
                    JsonObject movieAdded = new JsonObject();
                    movieAdded.addProperty("id", movie.getId());
                    movieAdded.addProperty("name", movie.getName());
                    movieArray.add(movieAdded);
                    break;
                }
            }
            updateJson();

        }


    }

    public boolean returnMovie(User user, Movie movie) {
        ((BBUser) user).returnMovie(movie);

        synchronized (WriteLock) {
            for (int i = 0; i < users.size(); i++) {
                JsonObject jsonUser = users.get(i).getAsJsonObject();
                if (jsonUser.get("username").getAsString().equals(user.getUsername())) {
                    JsonArray movieArray = (JsonArray) jsonUser.get("movies");
                    for (int j = 0; j < movieArray.size(); j++) {
                        JsonObject jMovie = movieArray.get(i).getAsJsonObject();
                        if (jMovie.get("name").getAsString().equals(movie.getName())) {
                            movieArray.remove(jMovie);
                            updateJson();
                            return true;
                        }
                    }
                }

            }
            return false;
        }
    }

    public boolean checkIfMovieRented(Movie movie){
        for(User user : RegisteredUsers.values())
            if(((BBUser)user).checkIfMovieRented(movie))
                return true;
        return false;

    }

    public static void main(String[] args) throws IOException {
        BBServerUsers BBS = new BBServerUsers("Users.json");

       // BBS.addToBalance("lisa","3");
        System.out.println("updated");
        BBS.Register("ORI", "k8cy4ioe", "Israel");
        Gson gson = new Gson();




    }
}
