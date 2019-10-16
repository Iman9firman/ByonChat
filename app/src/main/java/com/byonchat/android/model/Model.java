package com.byonchat.android.model;

/**
 * Created by Iman Firmansyah on 12/1/2015.
 */
public class Model {
    String name;
    String id_self;
    String id_parent;

    public Model(String name, String id_self, String id_parent){
        this.name = name;
        this.id_self = id_self;
        this.id_parent = id_parent;
    }

    public String getId_parent() {
        return id_parent;
    }

    public String getId_self() {
        return id_self;
    }

    public String getName() {
        return name;
    }

    public void setId_parent(String id_parent) {
        this.id_parent = id_parent;
    }

    public void setId_self(String id_self) {
        this.id_self = id_self;
    }

    public void setName(String name) {
        this.name = name;
    }
}
