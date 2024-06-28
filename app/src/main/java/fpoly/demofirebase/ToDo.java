package fpoly.demofirebase;

import java.util.HashMap;

public class ToDo {
    private String id;
    private String name,price,title;

    public ToDo() {
    }

    public ToDo(String name, String price, String title) {
        this.name = name;
        this.price = price;
        this.title = title;
    }

    public ToDo(String id, String name, String price, String title) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
