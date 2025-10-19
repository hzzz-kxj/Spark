package SoftwareDesignPatterns.Work1;

import java.util.ArrayList;

class Subject {
    private ArrayList<Item> itemList;

    public Subject(ArrayList<Item> itemList) {
        this.itemList = itemList;
    }

    public Subject() {
    }

    public ArrayList<Item> getItemList() {
        return itemList;
    }

    public void setItemList(ArrayList<Item> itemList) {
        this.itemList = itemList;
    }

}