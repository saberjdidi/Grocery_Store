package tn.app.grocerystore.models;

import java.io.Serializable;

public class Offer implements Serializable {
    String name, description, discount, Date_debut, Date_fin, img_url;
    int price;

    public Offer() {
    }

    public Offer(String name, String description, String discount, String date_debut, String date_fin, String img_url, int price) {
        this.name = name;
        this.description = description;
        this.discount = discount;
        Date_debut = date_debut;
        Date_fin = date_fin;
        this.img_url = img_url;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getDate_debut() {
        return Date_debut;
    }

    public void setDate_debut(String date_debut) {
        Date_debut = date_debut;
    }

    public String getDate_fin() {
        return Date_fin;
    }

    public void setDate_fin(String date_fin) {
        Date_fin = date_fin;
    }

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }
}
