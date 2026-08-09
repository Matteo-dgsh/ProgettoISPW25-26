package logic.model;

public class CityData {
    private String city;
    private String newCity;

    public CityData(String city, String newCity){
        this.city = city;
        this.newCity = newCity;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setNewCity(String newCity) {
        this.newCity = newCity;
    }

    public String getCity() {
        return this.city;
    }

    public String getNewCity() {
        return this.newCity;
    }
}
