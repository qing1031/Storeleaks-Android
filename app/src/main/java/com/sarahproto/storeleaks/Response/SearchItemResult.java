package com.sarahproto.storeleaks.Response;

public class SearchItemResult {
    private int id;
    private String name;
    private String country;
    private String city;
    private String location_more;
    private String desc;
    private String user;
    private String shop;
    private String own;
    private String datet;
    private String images;
    private String likes;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCountry() {
        return country;
    }

    public String getCity() {
        return city;
    }

    public String getLocation_more() {
        return location_more;
    }

    public String getDesc() {
        return desc;
    }

    public String getUser() {
        return user;
    }

    public String getShop() {
        return shop;
    }

    public String getOwn() {
        return own;
    }

    public String getDatet() {
        return datet;
    }

    public String getImages() {
        return images;
    }

    public String getLikes() {
        return likes;
    }

    @Override
    public String toString() {
        return "SearchItemResult{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", country='" + country + '\'' +
                ", city='" + city + '\'' +
                ", location_more='" + location_more + '\'' +
                ", desc='" + desc + '\'' +
                ", user='" + user + '\'' +
                ", shop='" + shop + '\'' +
                ", own='" + own + '\'' +
                ", datet='" + datet + '\'' +
                ", images='" + images + '\'' +
                ", likes='" + likes + '\'' +
                '}';
    }
}
