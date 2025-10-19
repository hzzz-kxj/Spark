package SoftwareDesignPatterns.Work1;

public class Test {
    public static void main(String[] args) {
        invoice invoice = new invoice();

        invoice.getHeader().setUsername("张三");
        invoice.getHeader().setDate("2025-10-14");

        Item item11 = new  Item("棒棒糖", 10, 1.0);
        Item item12 = new  Item("巧克力", 5, 2.5);
        Item item13 = new  Item("饼干", 3, 3.0);

        invoice.addItem(item11);
        invoice.addItem(item12);
        invoice.addItem(item13);

        double allMoney = invoice.js();

        System.out.println("顾客: " + invoice.getHeader().getUsername());
        System.out.println("日期: " + invoice.getHeader().getDate());
        System.out.println("商品列表:");
        for (Item item : invoice.getSubject().getItemList()) {
            System.out.println("商品名称:" + item.getItemName() + " 数量:" + item.getNum() +
                    " 单价:" + item.getPrice() + " 小计:" + item.getPartSum());
        }
        System.out.println("总金额: " + allMoney);
    }
}
