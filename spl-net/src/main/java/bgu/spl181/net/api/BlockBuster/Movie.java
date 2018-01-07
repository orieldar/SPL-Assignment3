package bgu.spl181.net.api.BlockBuster;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Movie {

    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("price")
    @Expose
    private int price;
    @SerializedName("bannedCountries")
    @Expose
    private List<String> bannedCountries = null;
    @SerializedName("availableAmount")
    @Expose
    private int availableAmount;
    @SerializedName("totalAmount")
    @Expose
    private int totalAmount;

    public Movie(String name,String amount,String price,String[] bannedCountries,int id ){
        this.id = id;
        this.name = name;
        this.price = Integer.parseInt(price);
        this.availableAmount = Integer.parseInt(amount);
        this.totalAmount = Integer.parseInt(amount);
        for(String country : bannedCountries)
            this.bannedCountries.add(country);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public int getPrice(){ return price;}

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getBannedCountries() {
        return bannedCountries;
    }

    public void setBannedCountries(List<String> bannedCountries) {
        this.bannedCountries = bannedCountries;
    }

    public boolean checkBannedIn(String Country){
        for(String country : bannedCountries)
            if(country.equals(Country))
                return true;
        return false;
    }

    public int getAvailableAmount() {
        return availableAmount;
    }

    public void decAvailableAmount(){
        availableAmount--;
    }

    public void incAvailableAmount(){
        availableAmount ++;
    }

    public void setPrice(String price){
        this.price=Integer.parseInt(price);
    }

    public void setAvailableAmount(String availableAmountString) {
        int availableAmount = Integer.parseInt(availableAmountString);
        this.availableAmount = availableAmount;
    }

    public int getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(String totalAmountString) {
        int totalAmount = Integer.parseInt(totalAmountString);
        this.totalAmount = totalAmount;
    }

    @Override
    public String toString() {
        String bannedCountriesString = "" ;
                for(String country: bannedCountries)
                    bannedCountriesString= bannedCountriesString + country + " ";
                   // bannedCountriesString= bannedCountriesString + "\"" + country + "\"";
       // return '"' + name + "\" " + availableAmount + "\" " +  price + "\" " + bannedCountriesString;
        return name + " " + availableAmount + " " + price + " " + bannedCountriesString;
    }
}