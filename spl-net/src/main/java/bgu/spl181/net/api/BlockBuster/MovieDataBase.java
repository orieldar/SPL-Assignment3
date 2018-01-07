package bgu.spl181.net.api.BlockBuster;

import com.google.gson.*;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;


public class MovieDataBase {

    private ConcurrentHashMap <String,Movie> Movies;
    private AtomicInteger movieIdCounter;
    private final Object WriteLock = new Object();
    private JsonArray movies;
    private JsonObject MovieBase;
    private Gson gson;

    public MovieDataBase(String FileName) {
        gson = new Gson();
        JsonParser parser = new JsonParser();
        MovieBase = new JsonObject();
        try {
            MovieBase = (JsonObject) parser.parse(new FileReader(FileName));
        } catch (Exception e) {
            e.printStackTrace();
        }

        movies = MovieBase.getAsJsonArray("movies");
        Movies = new ConcurrentHashMap<String, Movie>();
        movieIdCounter.set(movies.size()+1);

        for (int i = 0; i < movies.size(); i++) {
            Movie movie = gson.fromJson(movies.get(i), Movie.class);
            Movies.put(movie.getName(), movie);
        }

    }

    public Movie getMovie(String name) {
        Movie movie = Movies.get(name);
        return movie;
    }

    public String getMovieInfo(String name){
        Movie movie = Movies.get(name);
        if(movie!=null)
            return movie.toString();
        return null;
    }

  public String getAllNames(){
        String allMovies = "";
        for ( String name : Movies.keySet())
            allMovies = allMovies + "\"" + name + "\"" ;
        return allMovies;
  }

  public boolean removeMovieFromDataBase(String name){ // check that noone is renting
      Movie movie  = Movies.get(name);
      synchronized (movie) {
          if (Movies.remove(movie) == null)
              return false;
      }

                synchronized (WriteLock) {
                    for (int i = 0; i < movies.size(); i++) {
                        JsonObject jsonMovie = movies.get(i).getAsJsonObject();
                        if (jsonMovie.get("name").getAsString().equals(movie)) {
                            movies.remove(jsonMovie);
                            updateJson();
                            return true;
                        }
                    }

                }

            return false;

        }



  public boolean rentMovie(Movie movie, int balance){
        synchronized (movie) {
            if (Movies.contains(movie)) {
                if (balance >= movie.getPrice() && movie.getAvailableAmount() > 0)
                    movie.decAvailableAmount();
                else return false;

                    synchronized (WriteLock) {
                        for (int i = 0; i < movies.size(); i++) {
                            JsonObject jsonMovie = movies.get(i).getAsJsonObject();
                            if (jsonMovie.get("name").getAsString().equals(movie.getName())) {
                                jsonMovie.remove("availableAmount");
                                jsonMovie.addProperty("availableAmount", movie.getAvailableAmount());
                                updateJson();
                                return true;
                            }
                        }

                    }

                }
                return false;
            }
        }


  public boolean returnMovie(Movie movie){
    synchronized (movie){
        if (Movies.contains(movie))
            movie.incAvailableAmount();
        else return false;
        }
            synchronized (WriteLock) {
                for (int i = 0; i < movies.size(); i++) {
                    JsonObject jsonMovie = movies.get(i).getAsJsonObject();
                    if (jsonMovie.get("name").getAsString().equals(movie.getName())) {
                        jsonMovie.remove("availableAmount");
                        jsonMovie.addProperty("availableAmount", movie.getAvailableAmount());
                        updateJson();
                        return true;
                    }
                }
        }
        return false;
    }


  public boolean addMovieToDataBase(String name, String amount, String price, String[] bannedCountries){
    if(Integer.parseInt(price) <=0 || Movies.contains(name) )
        return false;
    Movie newMovie = new Movie(name,amount,price,bannedCountries,movieIdCounter.getAndIncrement());
    Movies.put(name,newMovie);

    List<String> trimmedCountries = new ArrayList<String>();
    for(String country: bannedCountries)
        trimmedCountries.add( country.substring(1,country.length()));

      synchronized (WriteLock) {
          JsonObject jsonMovie = new JsonObject();
          jsonMovie.addProperty("id", newMovie.getId());
          jsonMovie.addProperty("name", name.substring(1,name.length()));
          jsonMovie.addProperty("price", price);
          //String jsonBannedCountries =  gson.toJson((bannedCountries));//check
          jsonMovie.addProperty("bannedCountries", gson.toJson(trimmedCountries));
          jsonMovie.addProperty("availableAmount", amount );
          jsonMovie.addProperty("totalAmount", amount);
          movies.add(jsonMovie);
          updateJson();
      }
      return true;

  }

  public boolean changeMoviePrice(String name, String price){
        Movie movie = Movies.get(name);
        if(movie ==null)
            return false;
        synchronized (movie) {
            if(movie!=null)
            movie.setPrice(price);
        }
      synchronized (WriteLock) {
          for (int i = 0; i < movies.size(); i++) {
              JsonObject jsonMovie = movies.get(i).getAsJsonObject();
              if (jsonMovie.get("name").getAsString().equals(movie.getName())) {
                  jsonMovie.remove("price");
                  jsonMovie.addProperty("price", price);
                  updateJson();
                  return true;
              }
          }
      }
      return false;

  }

    protected void updateJson(){
        try (FileWriter file = new FileWriter("Movies.json")) {
            file.write(MovieBase.toString());

        } catch (IOException e) { e.printStackTrace();  }

    }




}
