package com.honda.android.list;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.util.Log;

public class ListMenu extends ArrayList<ListMenu>{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public String text;
	
	public String text2;
	
	public String text3;
	
	public int imageIcon;
	
	public boolean messageNotification;
	
	private Date dateTime;
	
	public ListMenu(){
        super();
    }
	
	public Date getDateTime() {
        return dateTime;
    }

    public void setDateTime(Date datetime) {
        this.dateTime = datetime;
    }
    
    public ListMenu (String text) {
    	super();
        this.text = text;
    }
    
    public ListMenu (String text, int imageIcon) {
    	super();
        this.text = text;
        this.imageIcon = imageIcon;
    }
    
    public ListMenu (String text, String text2) {
    	super();
        this.text = text;
        this.text2 = text2;
    }
    
    public ListMenu (String text, String text2, int imageIcon) {
    	super();
        this.text = text;
        this.text2 = text2;
        this.imageIcon = imageIcon;
    }
    
    public ListMenu (String text, String text2, boolean messageNotification) {
    	super();
        this.text = text;
        this.text2 = text2;
        this.messageNotification = messageNotification;
    }
    
    public ListMenu (int imageIcon, String text) {
    	super();
    	this.imageIcon = imageIcon;
        this.text = text;
    }
    
    public ListMenu (String text, String text2, String text3) {
    	super();
        this.text = text;
        this.text2 = text2;
        this.text3 = text3;
    }
    
}
