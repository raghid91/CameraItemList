package suchitra.example.com.pluralsightdatabaselist;

import android.graphics.Bitmap;


public class TodoItems {
    private int task_id;
    String description;
    private String uriPath;
    private boolean is_done;

    public TodoItems()
    {

    }

    public TodoItems(String desc, String uriPath)
    {
        this.description = desc;
        this.setUriPath(uriPath);
        is_done = false;
        //this.setIs_done(is_done);
    }

    public int getTask_id() {
        return task_id;
    }

    public void setTask_id(int task_id) {
        this.task_id = task_id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUriPath() {
        return uriPath;
    }

    public void setUriPath(String uriPath) {
        this.uriPath = uriPath;
    }

    public boolean getIs_done() {
        return is_done;
    }

    public void setIs_done(boolean is_done) {
        this.is_done = is_done;
    }

    public String toString()
    {
        return description;
    }

}
