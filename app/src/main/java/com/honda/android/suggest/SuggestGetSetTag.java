package com.honda.android.suggest;

/**
 * Created by Lukmanpryg on 2/18/2016.
 */
public class SuggestGetSetTag {
    String id,name;
    public SuggestGetSetTag(String id, String name){
        this.setId(id);
        this.setName(name);
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
}
