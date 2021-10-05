package tn.app.grocerystore.models;

public class OrderModel {
    String email, name, address, currency, amount, currentDate, currentTime;

    public OrderModel() {
    }

    public OrderModel(String email, String name, String address, String currency, String amount, String currentDate, String currentTime) {
        this.email = email;
        this.name = name;
        this.address = address;
        this.currency = currency;
        this.amount = amount;
        this.currentDate = currentDate;
        this.currentTime = currentTime;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getCurrentDate() {
        return currentDate;
    }

    public void setCurrentDate(String currentDate) {
        this.currentDate = currentDate;
    }

    public String getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(String currentTime) {
        this.currentTime = currentTime;
    }
}
