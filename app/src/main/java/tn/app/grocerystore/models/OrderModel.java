package tn.app.grocerystore.models;

public class OrderModel {
    String name, description, currency, amount, currentDate, currentTime;

    public OrderModel() {
    }

    public OrderModel(String name, String description, String currency, String amount, String currentDate, String currentTime) {
        this.name = name;
        this.description = description;
        this.currency = currency;
        this.amount = amount;
        this.currentDate = currentDate;
        this.currentTime = currentTime;
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
