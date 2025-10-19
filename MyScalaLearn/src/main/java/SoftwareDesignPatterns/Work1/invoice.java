package SoftwareDesignPatterns.Work1;

import java.util.ArrayList;

public class invoice {
    public Header header = new Header();
    public Subject subject = new Subject();
    public Footer footer = new Footer();

    // 添加商品项到主体中
    public void addItem(Item item) {
        if (subject.getItemList() == null) {
            subject.setItemList(new ArrayList<>());
        }
        subject.getItemList().add(item);
    }

    // 计算总金额
    public double js() {
        double allMoney = 0.0;
        if (subject.getItemList() != null) {
            for (Item item : subject.getItemList()) {
                allMoney += item.getPartSum();
            }
        }
        footer.setAllMoney(allMoney);
        return allMoney;
    }

    public invoice(Header header, Subject subject, Footer footer) {
        this.header = header;
        this.subject = subject;
        this.footer = footer;
    }

    public invoice() {
    }

    public Header getHeader() {
        return header;
    }

    public void setHeader(Header header) {
        this.header = header;
    }

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    public Footer getFooter() {
        return footer;
    }

    public void setFooter(Footer footer) {
        this.footer = footer;
    }
}












