package es.source.code.model;

import es.source.code.activity.R;

/**
 * 用来存放所有的菜品信息
 */
public class FoodItems {

    public static final int HOT_FOOD = 1;
    public static final int COLD_FOOD = 2;
    public static final int SEE_FOOD = 3;
    public static final int DRINK = 4;

    public static String[] hot_food_name = {"香辣牛肉西兰花","蚂蚁上树","家常干锅排骨","菠萝咕咾肉","家常口水鸡","地三鲜","干贝边炖豆腐","鱼香肉丝","水煮肉片","铁锅芋头鸡"};
    public static int[] hot_food_image = {R.drawable.hot_food_1,R.drawable.hot_food_2,R.drawable.hot_food_3,R.drawable.hot_food_4,R.drawable.hot_food_5,
                                   R.drawable.hot_food_6,R.drawable.hot_food_7,R.drawable.hot_food_8,R.drawable.hot_food_9,R.drawable.hot_food_10};
    public static int[] hot_food_price = {35,67,89,58,66,52,32,55,76,73};
    public static int[] hot_food_storage = {18,23,22,31,25,12,23,21,7,12};



    public static String[] cold_food_name = {"蜜汁苦瓜","皮蛋生菜卷","墨鱼汁凉粉","捞拌鲍鱼","龙须菜拌花生","香辣手撕鸡","红油金针菇"};
    public static int[] cold_food_image = {R.drawable.cold_food_1,R.drawable.cold_food_2,R.drawable.cold_food_3,R.drawable.cold_food_4,R.drawable.cold_food_5,
                                    R.drawable.cold_food_6,R.drawable.cold_food_7};
    public static int[] cold_food_price = {15,23,17,47,25,36,14};
    public static int[] cold_food_storage = {23,12,11,5,23,12,22};


    public static String[] see_food_name = {"香辣肉蟹煲","美味蒜蓉烤生蚝","啤酒烧鲍鱼","椒盐皮皮虾","海参芹菜","烤花甲"};
    public static int[] see_food_image = {R.drawable.see_food_1,R.drawable.see_food_2,R.drawable.see_food_3,R.drawable.see_food_4,R.drawable.see_food_5,R.drawable.see_food_6};
    public static int[] see_food_price ={99,73,102,90,87,40};
    public static int[] see_food_storage = {12,5,3,8,23,31};


    public static String[] drink_name = {"可乐","啤酒","雪碧","椰汁","果粒橙"};
    public static int[] drink_image = {R.drawable.drink_1,R.drawable.drink_2,R.drawable.drink_3,R.drawable.drink_4,R.drawable.drink_5};
    public static int[] drink_price = {4,3,4,8,5};
    public static int[] drink_storage = {41,51,23,34,37};

    /**
     * 返回指定的名字字符串
     * @param number 1==hot_food,2==cold_food,3==see_food_name,4=drink_name
     * @return
     */
    public static String[] getName(int number){

        switch (number){
            case HOT_FOOD: return hot_food_name;
            case COLD_FOOD: return cold_food_name;
            case SEE_FOOD: return see_food_name;
            default: return drink_name;
        }
    }

    /**
     * 返回指定的图片信息
     * @param number 1==hot_food,2==cold_food,3==see_food_name,4=drink_name
     * @return
     */
    public static int[] getImage(int number){
        switch (number){
            case HOT_FOOD: return hot_food_image;
            case COLD_FOOD: return cold_food_image;
            case SEE_FOOD: return see_food_image;
            default: return drink_image;
        }
    }

    /**
     * 返回指定的价格信息
     * @param number 1==hot_food,2==cold_food,3==see_food_name,4=drink_name
     * @return
     */
    public static int[] getPrice(int number){
        switch (number){
            case HOT_FOOD: return hot_food_price;
            case COLD_FOOD: return cold_food_price;
            case SEE_FOOD: return see_food_price;
            default: return drink_price;
        }
    }

    /**
     * 返回指定的库存信息
     * @param numebr 1==hot_food,2==cold_food,3==see_food_name,4=drink_name
     * @return
     */
    public static int[] getStorage(int numebr){
        switch (numebr){
            case HOT_FOOD: return hot_food_storage;
            case COLD_FOOD: return cold_food_storage;
            case SEE_FOOD: return see_food_storage;
            default: return drink_storage;
        }
    }


}
