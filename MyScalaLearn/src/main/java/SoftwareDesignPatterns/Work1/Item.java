package SoftwareDesignPatterns.Work1;

public class Item {
    private String ItemName;
    private double  num;
    private double price;
    private double partSum;

    public Item(String itemName, double num, double price) {
        this.ItemName = itemName;
        this.num = num;
        this.price = price;
        this.partSum = num * price;
    }

    public Item() {
    }

    public String getItemName() {
        return ItemName;
    }

    public void setItemName(String itemName) {
        this.ItemName = itemName;
    }

    public double getNum() {
        return num;
    }

    public void setNum(double num) {
        this.num = num;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getPartSum() {
        return partSum;
    }

    public void setPartSum(double partSum) {
        this.partSum = this.num * this.price;
    }
}
