package com.byonchat.android.provider;

public class AvailableReward {
	 private long id;
	 private String name;
	 private String desc;

    public AvailableReward(int id,String name, String desc) {
        this.id = id;
        this.name = name;
        this.desc = desc;
    }

    public AvailableReward( String name,String desc) {
        this.name = name;
        this.desc = desc;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
