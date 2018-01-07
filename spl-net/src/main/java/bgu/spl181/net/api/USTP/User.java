package bgu.spl181.net.api.USTP;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class User {

    @SerializedName("username")
    @Expose
    protected String username;
    @SerializedName("type")
    @Expose
    protected String type;
    @SerializedName("password")
    @Expose
    protected String password;

    private boolean isLoggedIn;

    public User(){
    }

    public User(String username,String password){
        this.username = username;
        this.password = password;
        type = "normal";
    }

    public boolean isLoggedIn(){
        return isLoggedIn;
    }


    public void logOut(){
        isLoggedIn=false;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}