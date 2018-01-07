package bgu.spl181.net.api.BlockBuster;

import bgu.spl181.net.api.USTP.User;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class UsersToJson {
    @SerializedName("users")
    @Expose
    private List<User> UsersList = new ArrayList<User>();

    public UsersToJson( List<User> userlist){
        this.UsersList=  userlist;
    }
}
