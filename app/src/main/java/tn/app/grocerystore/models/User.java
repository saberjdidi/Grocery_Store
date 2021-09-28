package tn.app.grocerystore.models;

public class User {
    String name, email, password, number, address;
    String profileImg;

    public User() {
    }

    public User(String name, String email, String password, String number, String address, String profileImg) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.number = number;
        this.address = address;
        this.profileImg = profileImg;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getProfileImg() {
        return profileImg;
    }

    public void setProfileImg(String profileImg) {
        this.profileImg = profileImg;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
