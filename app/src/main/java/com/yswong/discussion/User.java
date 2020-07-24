package com.yswong.discussion;


//This class is for create users for firebase
public class User {
    private String name;
    private String unit1;
    private String unit2;
    private String unit3;
    private String unit4;
    private int total;

    public User(String name, String unit1, String unit2, String unit3, String unit4, int total) {
        this.name = name;
        this.unit1 = unit1;
        this.unit2 = unit2;
        this.unit3 = unit3;
        this.unit4 = unit4;
        this.total = total;
    }

    public String getName() {
        return name;
    }

    public String getUnit1() {
        return unit1;
    }

    public String getUnit2() {
        return unit2;
    }

    public String getUnit3() {
        return unit3;
    }

    public String getUnit4() {
        return unit4;
    }

    public int getTotal() {
        return total;
    }
}

