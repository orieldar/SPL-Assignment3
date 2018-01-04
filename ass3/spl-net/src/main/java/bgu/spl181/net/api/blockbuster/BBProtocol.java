package bgu.spl181.net.api.blockbuster;

import bgu.spl181.net.api.ustp.ServerUsers;
import bgu.spl181.net.api.ustp.USTProtocol;

public class BBProtocol extends USTProtocol {

    MovieDataBase movieDataBase;

    public BBProtocol(ServerUsers args1, MovieDataBase args2){
        super(args1);
        this.movieDataBase = args2;
    }
    @Override
    protected void register(String detail) {
        String[] details = detail.split("\\s+");
        if((details.length != 3)||(user != null)||(users.isLoggedIn(details[0]))) { //logged from a diffrent computer
            connections.send(connectionId, "ERROR login failed");
            return;
        }
        if(users.register(detail[0], detail[1], detail[2])) // need to check if the name is taken SYNC
            connections.send(connectionId,"ACK registration succeeded");
        else
            connections.send(connectionId, "ERROR login failed");

        }

    @Override
    protected void request(String detail) {
        String requestName;
        String requestDet;
        if(detail.indexOf(" ") == -1){
            requestName = detail;
            requestDet = "";
        }
        else {
            requestName = detail.substring(0, detail.indexOf(" "));
            requestDet = detail.substring(detail.indexOf(" ") + 1);
        }
        if (user == null){
            connections.send(connectionId, "ERROR request " + requestName + " failed");
            return;
        }
        if(requestName == "balance"){
            requestBalance(requestDet);
            return;
        }
        if(requestName == "info") {
            requestInfo(requestDet);
            return;
        }
        if(requestName == "rent") {
            requestRent(requestDet);
            return;
        }
        if(requestName == "return") {
            requestReturn(requestDet);
            return;
        }
    }

    private void requestBalance(String detail){
        String[] details = detail.split("\\s+");
        if(details[0] == "info") {
            connections.send(connectionId, "ACK balance " + user.getBalance());
            return;
        }
        if(details[0] == "add") {
            user.setBalance(details[1]);
            connections.send(connectionId, "ACK balance " + user.getBalance() + " added " + details[1]);
            return;
        }
    }

    private void requestInfo(String detail){
        if(detail.length() == 0)
            connections.send(connectionId, "ACK info" + MovieDataBase.toString()); // all database to 1 string
        else
            connections.send(connectionId, "ACK info" + MovieDataBase.getMovieInfo(detail));
    }

    private void requestRent(String detail) {
        Movie movie = MovieDataBase.getMovie(detail);
        if((movie == null)||(movie.getPrice() > user.getBalance()) ||(movie.checkBannedIn(user.getCountry()))
                ||(user.checkIfMovieRented(movie.getName()))) { //check copies should be sync but only on the movie
            connections.send(connectionId, "ERROR request rent");
            return;
        }
        synchronized (movie){ //the user is sync via the pool and the fact that can be only 1 logged in. but the movie not
            if(movie.getCopies()==0){
                connections.send(connectionId, "ERROR request rent");
                return;
            }
            movie.setCopies(movie.getCopies()-1);
            user.addMovie(movie.getName());
            connections.send(connectionId,"ACK rent " + movie.getName() + " success");
            connections.broadcast("BROADCAST movie " +  movie.getName() + " " + movie.getCopies()
                    + " " + movie.getPrice()); //brodcast could be outside of sync to make it more efficient, but we need make more vals
        }
        }


}
