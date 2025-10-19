package SoftwareDesignPatterns.Work1;

public class Header{
    private String username;
    private String date;

    public Header(String username, String date) {
        this.username = username;
        this.date = date;
    }

    public Header(){

    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}