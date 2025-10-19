package SoftwareDesignPatterns.Work2;

import java.util.Scanner;

public class PartCreateFactory {
    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        System.out.println("请输入要购买的零件类型：");
        String partType = sc.nextLine();


        Part part = PartFactory.getPart(partType);

        if (part != null) {
            part.run();
        } else {
            System.out.println("对不起，没有这种类型的零件！");
        }

        sc.close();

    }
}
