package fpoly.demofirebase;

import java.util.HashMap;

public class ToDo {
    private String title,content;

    public HashMap<String,Object> convertToHashMap(){
        HashMap<String,Object> map = new HashMap<>();
        map.put("title",title);
        map.put("content",content);
        return map;
    }

    public ToDo(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
