package be.technobel.bibliotheque.model.auth;

public class Address implements Cloneable {
    private long id = 0;
    private String country = "";
    private String city = "";
    private String zipCode = "";
    private String streetName = "";
    private String streetNumber = "";

    public long getId() {
        return id;
    }

    public Address setId(long id) {
        if(id < 0) throw new IllegalArgumentException();
        this.id = id;
        return this;
    }

    public String getCountry() {
        return country;
    }

    public Address setCountry(String country) {
        if(country == null) throw new IllegalArgumentException();
        this.country = country;
        return this;
    }

    public String getCity() {
        return city;
    }

    public Address setCity(String city) {
        if(city == null) throw new IllegalArgumentException();
        this.city = city;
        return this;
    }

    public String getZipCode() {
        return zipCode;
    }

    public Address setZipCode(String zipCode) {
        if(zipCode == null) throw new IllegalArgumentException();
        this.zipCode = zipCode;
        return this;
    }

    public String getStreetName() {
        return streetName;
    }

    public Address setStreetName(String streetName) {
        if(streetName == null) throw new IllegalArgumentException();
        this.streetName = streetName;
        return this;
    }

    public String getStreetNumber() {
        return streetNumber;
    }

    public Address setStreetNumber(String streetNumber) {
        if(streetNumber == null) throw new IllegalArgumentException();
        this.streetNumber = streetNumber;
        return this;
    }

    public Address(long id, String country, String city, String zipCode, String streetName, String streetNumber) {
        if(country == null || city == null || zipCode == null || streetName == null || streetNumber == null)
            throw new IllegalArgumentException();
        if(id < 0) throw new IllegalArgumentException();
        this.id = id;
        this.country = country;
        this.city = city;
        this.zipCode = zipCode;
        this.streetName = streetName;
        this.streetNumber = streetNumber;
    }

    public Address(String country, String city, String zipCode, String streetName, String streetNumber) {
        if(country == null || city == null || zipCode == null || streetName == null || streetNumber == null)
            throw new IllegalArgumentException();
        this.country = country;
        this.city = city;
        this.zipCode = zipCode;
        this.streetName = streetName;
        this.streetNumber = streetNumber;
    }

    public Address(String country, String city, String zipCode) {
        if(country == null || city == null || zipCode == null) throw new IllegalArgumentException();
        this.country = country;
        this.city = city;
        this.zipCode = zipCode;
    }

    public Address() {}

    @Override
    public Address clone() {
        try {
            return (Address)super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
