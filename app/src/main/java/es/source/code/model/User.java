package es.source.code.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import es.source.code.util.FoodItems;

public class User implements Serializable{

    public static final int HOTFOOD = 1;
    public static final int COLDFOOD = 2;
    public static final int SEEFOOD = 3;
    public static final int DRINK = 4;

    private String userName;
    private String password;
    private boolean oldUser;

    private String example = "000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000";

    private int[] tagHotFood;
    private int[] tagColdFood;
    private int[] tagSeeFood;
    private int[] tagDrink;

    private int[] tagHotFoodCommited;
    private int[] tagColdFoodCommited;
    private int[] tagSeeFoodCommited;
    private int[] tagDrinkCommited;

    private List<Integer> positioinOrder;
    private int numberHotFoodNotOrder = 0, numberColdFoodNotOrder = 0, numberSeeFoodNotOrder = 0, numberDrinkNotOrder = 0;
    private int totalPrice = 0;

    public User(){
        tagHotFood = new int[FoodItems.hot_food_name.length];
        tagColdFood = new int[FoodItems.cold_food_name.length];
        tagSeeFood = new int[FoodItems.see_food_name.length];
        tagDrink = new int[FoodItems.drink_name.length];
//        tagHotFood = new StringBuilder(example.substring(0, FoodItems.hot_food_name.length));
//        tagColdFood = new StringBuilder(example.substring(0,FoodItems.cold_food_name.length));
//        tagSeeFood = new StringBuilder(example.substring(0,FoodItems.see_food_name.length));
//        tagDrink = new StringBuilder(example.substring(0,FoodItems.drink_name.length));

        tagHotFoodCommited = new int[FoodItems.hot_food_name.length];
        tagColdFoodCommited = new int[FoodItems.cold_food_name.length];
        tagSeeFoodCommited = new int[FoodItems.see_food_name.length];
        tagDrinkCommited = new int[FoodItems.drink_name.length];

        positioinOrder = new ArrayList<>();
    }

    public void initCommitedTag(){
        for(int i = 0;i<tagHotFoodCommited.length;i++){
            tagHotFoodCommited[i]=0;
        }
        for(int i = 0;i<tagColdFoodCommited.length;i++){
            tagColdFoodCommited[i]=0;
        }
        for(int i = 0;i<tagSeeFoodCommited.length;i++){
            tagSeeFoodCommited[i]=0;
        }
        for(int i=0;i<tagDrinkCommited.length;i++){
            tagDrinkCommited[i]=0;
        }
    }

    public void CommitedTag(){
        tagHotFoodCommited = Arrays.copyOf(tagHotFood,tagHotFood.length);
        tagColdFoodCommited = Arrays.copyOf(tagColdFood,tagColdFood.length);
        tagSeeFoodCommited = Arrays.copyOf(tagSeeFood,tagSeeFood.length);
        tagDrinkCommited = Arrays.copyOf(tagDrink,tagDrink.length);
    }

    public int[] getTagHotFoodCommited() {
        return tagHotFoodCommited;
    }

    public int[] getTagColdFoodCommited() {
        return tagColdFoodCommited;
    }

    public int[] getTagSeeFoodCommited() {
        return tagSeeFoodCommited;
    }

    public int[] getTagDrinkCommited() {
        return tagDrinkCommited;
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

    public List<Integer> getPositioinOrder() {
        return positioinOrder;
    }

    public void setPositioinOrder(List<Integer> positionOrder) {
        this.positioinOrder = new ArrayList<>(positionOrder);
    }

    public void setTagPosition(int tag,int pos,char data){
        switch (tag) {
            case HOTFOOD:
                setTagPositionHotFood(pos,data);break;
            case COLDFOOD:
                setTagPositionHotFood(pos,data);break;
            case SEEFOOD:
                setTagPositionHotFood(pos,data);break;
            case DRINK:
                setTagPositionHotFood(pos,data);break;
            default:
        }
    }

    public void setTagPositionHotFood(int pos , int data){
        tagHotFood[pos]=data;
    }

    public void setTagPositionColdFood(int pos , int data){
        tagColdFood[pos]=data;
    }

    public void setTagPositionSeeFood(int pos , int data){
        tagSeeFood[pos]=data;
    }

    public void setTagPositionDrink(int pos , int data){
        tagDrink[pos]=data;
    }

    public void initTag(){

        for(int i = 0;i<tagHotFood.length;i++){
            tagHotFood[i]=0;
        }
        for(int i = 0;i<tagColdFood.length;i++){
            tagColdFood[i]=0;
        }
        for(int i = 0;i<tagSeeFood.length;i++){
            tagSeeFood[i]=0;
        }
        for(int i=0;i<tagDrink.length;i++){
            tagDrink[i]=0;
        }
    }

    public int getTagPosition(int tag,int pos){
        switch (tag) {
            case HOTFOOD:
                return getTagPositionHotFood(pos);
            case COLDFOOD:
                return getTagPositionColdFood(pos);
            case SEEFOOD:
                return getTagPositionSeeFood(pos);
            case DRINK:
                return getTagPositionDrink(pos);
            default:
                return 0;
        }
    }

    public int getTagPositionHotFood(int pos){
        return tagHotFood[pos];
    }
    public int getTagPositionColdFood(int pos){
        return tagColdFood[pos];
    }
    public int getTagPositionSeeFood(int pos){
        return tagSeeFood[pos];
    }
    public int getTagPositionDrink(int pos){
        return tagDrink[pos];
    }

    public int[] getTag(int tag){
        switch (tag) {
            case HOTFOOD:
                return getTagHotFood();
            case COLDFOOD:
                return getTagColdFood();
            case SEEFOOD:
                return getTagSeeFood();
            case DRINK:
                return getTagDrink();
            default:
                return new int[1];
        }
    }

    public int[] getTagHotFood() {
        return tagHotFood;
    }

    public int[] getTagColdFood() {
        return tagColdFood;
    }

    public int[] getTagSeeFood() {
        return tagSeeFood;
    }

    public int[] getTagDrink() {
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

    public void addTag(int tag,int pos){
        switch (tag){
            case HOTFOOD:addTagHotFood(pos);break;
            case COLDFOOD:addTagColdFood(pos);break;
            case SEEFOOD:addTagSeeFood(pos);break;
            case DRINK:addTagSeeFood(pos);break;
            default:
        }
    }

    public void addTagHotFood(int pos){
        tagHotFood[pos]++;
    }

    public void addTagColdFood(int pos){
        tagColdFood[pos]++;
    }

    public void addTagSeeFood(int pos){
        tagSeeFood[pos]++;
    }

    public void addTagDrink(int pos){
        tagDrink[pos]++;
    }

    public void minusTag(int tag,int pos){
        switch (tag){
            case HOTFOOD:minusTagHotFood(pos);break;
            case COLDFOOD:minusTagColdFood(pos);break;
            case SEEFOOD:minusTagSeeFood(pos);break;
            case DRINK:minusTagDrink(pos);break;
            default:
        }
    }

    public void minusTagHotFood(int pos){
        tagHotFood[pos]--;
    }

    public void minusTagColdFood(int pos){
        tagColdFood[pos]--;
    }

    public void minusTagSeeFood(int pos){
        tagSeeFood[pos]--;
    }

    public void minusTagDrink(int pos){
        tagDrink[pos]--;
    }
}
