package bgu.spl181.net.api.BlockBuster;

import bgu.spl181.net.api.USTP.User;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class BBUser extends User {

        @SerializedName("country")
        @Expose
        private String country;
        @SerializedName("movies")
        @Expose
        private List<Movie> movies = null;
        @SerializedName("balance")
        @Expose
        private int balance;

        public BBUser(String username,String password,String country){
            this.username = username;
            this.password = password;
            this.country = country;
            type = "normal";
        }

        public String getCountry() {
            return country;
        }

        public void setCountry(String country) {
            this.country = country;
        }

        public List<Movie> getMovies() {
            return movies;
        }

        public void setMovies(List<Movie> movies) {
            this.movies = movies;
        }

        public int getBalance() {
            return balance;
        }

        public void setBalance(int balance) {
            this.balance = balance;
        }

        public boolean checkIfMovieRented(Movie movie){
            return movies.contains(movie);
        }

        public void addMovie(Movie movie){
            movies.add(movie);
        }

        public void returnMovie(Movie movie){
            movies.remove(movie);
        }

        public boolean isAdmin(){
            return type.equals("admin");
        }

}

