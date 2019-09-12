package com.honda.android.list;

public class ListData {
	
	private static ListMenu listdata_groupchat = new ListMenu();
	private static ListMenu sticky = new ListMenu();
	private static ListMenu list_voting = new ListMenu();
	
	public static ListMenu listGroupChat(){
		
		return listdata_groupchat;
	}
	
	public static ListMenu listVoting(){
		
		return list_voting;
	}
	
	public static ListMenu listMenuSticky(){
		//ListMenu sticky = new ListMenu();
		//sticky.add(new ListMenu("test sample sticky 1", "groupid1", "time_stamp"))
        
        return sticky;
	}
	
}
