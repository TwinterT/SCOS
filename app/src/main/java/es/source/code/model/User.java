package es.source.code.model;

import android.content.Intent;

import java.io.Serializable;

public class User implements Serializable{

    private String userName;
    private String password;
    private boolean oldUser;

    private String example = "000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000";

    private StringBuilder tagHotFood;
    private StringBuilder tagColdFood;
    private StringBuilder tagSeeFood;
    private StringBuilder tagDrink;

    private StringBuilder tagHotFoodCommited;
    private StringBuilder tagColdFoodCommited;
    private StringBuilder tagSeeFoodCommited;
    private StringBuilder tagDrinkCommited;

    private StringBuilder positioinOrder;
    private int numberHotFoodNotOrder = 0, numberColdFoodNotOrder = 0, numberSeeFoodNotOrder = 0, numberDrinkNotOrder = 0;
    private int totalPrice = 0;

    public User(){
        tagHotFood = new StringBuilder(example.substring(0,FoodItems.hot_food_name.length));
        tagColdFood = new StringBuilder(example.substring(0,FoodItems.cold_food_name.length));
        tagSeeFood = new StringBuilder(example.substring(0,FoodItems.see_food_name.length));
        tagDrink = new StringBuilder(example.substring(0,FoodItems.drink_name.length));

        tagHotFoodCommited = new StringBuilder(example.substring(0,FoodItems.hot_food_name.length));
        tagColdFoodCommited = new StringBuilder(example.substring(0,FoodItems.cold_food_name.length));
        tagSeeFoodCommited = new StringBuilder(example.substring(0,FoodItems.see_food_name.length));
        tagDrinkCommited = new StringBuilder(example.substring(0,FoodItems.drink_name.length));

        positioinOrder = new StringBuilder();
    }

    public void initTag(){
        tagHotFoodCommited = new StringBuilder(example.substring(0,FoodItems.hot_food_name.length));
        tagColdFoodCommited = new StringBuilder(example.substring(0,FoodItems.cold_food_name.length));
        tagSeeFoodCommited = new StringBuilder(example.substring(0,FoodItems.see_food_name.length));
        tagDrinkCommited = new StringBuilder(example.substring(0,FoodItems.drink_name.length));
    }

    public int getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(int totalPrice) {
        this.totalPrice = totalPrice;
    }

    public int getNumberHotFoodNotOrder() {
        return numberHotFoodNotOrder;
    }

    public void setNumberHotFoodNotOrder(int numberHotFoodNotOrder) {
        this.numberHotFoodNotOrder = numberHotFoodNotOrder;
    }

    public int getNumberColdFoodNotOrder() {
        return numberColdFoodNotOrder;
    }

    public void setNumberColdFoodNotOrder(int numberColdFoodNotOrder) {
        this.numberColdFoodNotOrder = numberColdFoodNotOrder;
    }

    public int getNumberSeeFoodNotOrder() {
        return numberSeeFoodNotOrder;
    }

    public void setNumberSeeFoodNotOrder(int numberSeeFoodNotOrder) {
        this.numberSeeFoodNotOrder = numberSeeFoodNotOrder;
    }

    public int getNumberDrinkNotOrder() {
        return numberDrinkNotOrder;
    }

    public void setNumberDrinkNotOrder(int numberDrinkNotOrder) {
        this.numberDrinkNotOrder = numberDrinkNotOrder;
    }

    public StringBuilder getPositioinOrder() {
        return positioinOrder;
    }

    public void setPositioinOrder(StringBuilder positioinOrder) {
        this.positioinOrder = new StringBuilder(positioinOrder.toString());
    }

    public void setTagHotFood(int pos , char data){
        tagHotFood.setCharAt(pos,data);
    }

    public void setTagColdFood(int pos , char data){
        tagColdFood.setCharAt(pos,data);
    }

    public void setTagSeeFood(int pos , char data){
        tagSeeFood.setCharAt(pos,data);
    }

    public void setTagDrink(int pos , char data){
        tagDrink.setCharAt(pos,data);
    }

    public int getTagPositionHotFood(int pos){
        return Integer.parseInt(""+tagHotFood.charAt(pos));
    }
    public int getTagPositionColdFood(int pos){
        return Integer.parseInt(""+tagHotFood.charAt(pos));
    }
    public int getTagPositionSeeFood(int pos){
        return Integer.parseInt(""+tagHotFood.charAt(pos));
    }
    public int getTagPositionDrink(int pos){
        return Integer.parseInt(""+tagHotFood.charAt(pos));
    }

    public StringBuilder getTagHotFood() {
        return tagHotFood;
    }

    public StringBuilder getTagColdFood() {
        return tagColdFood;
    }

    public StringBuilder getTagSeeFood() {
        return tagSeeFood;
    }

    public StringBuilder getTagDrink() {
        return tagDrink;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isOldUser() {
        return oldUser;
    }

    public void setOldUser(boolean oldUser) {
        this.oldUser = oldUser;
    }
}
