package com.honda.android.provider;

public class UpdateList {

    String id;
    String type_name;
    String username;
    String id_tab;
    String parent_id;
    String id_list_push;
    String link_tembak;
    String fromlist;
    String version;
    String company;
    String status;

    String update_req;
    String date_received;
    String date_expired;

    public UpdateList(String ntype_name, String nusername, String nid_tab, String nparent_id, String nid_list_push, String nlink_tembak, String fromlistT, String nstatus) {
        this.type_name = ntype_name;
        this.username = nusername;
        this.id_tab = nid_tab;
        this.parent_id = nparent_id;
        this.id_list_push = nid_list_push;
        this.link_tembak = nlink_tembak;
        this.status = nstatus;
        this.fromlist = fromlistT;

    }

    public UpdateList(String ntype_name, String nusername, String nstatus) {
        this.type_name = ntype_name;
        this.username = nusername;
        this.status = nstatus;
    }

    public UpdateList(String ntype_name, String link_tembak, String version, String company, String nstatus, String date_received, String date_expired) {
        this.type_name = ntype_name;
        this.link_tembak = link_tembak;
        this.version = version;
        this.company = company;
        this.status = nstatus;
        this.date_received = date_received;
        this.date_expired = date_expired;
    }



    public String getfromlist() {
        return fromlist;
    }

    public void setfromlist(String fromlist) {
        this.fromlist = fromlist;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String gettype_name() {
        return type_name;
    }

    public void settype_name(String type_name) {
        this.type_name = type_name;
    }

    public String getusername() {
        return username;
    }

    public void setusername(String username) {
        this.username = username;
    }

    public String getid_tab() {
        return id_tab;
    }

    public void setid_tab(String id_tab) {
        this.id_tab = id_tab;
    }

    public String getparent_id() {
        return parent_id;
    }

    public void setparent_id(String parent_id) {
        this.parent_id = parent_id;
    }

    public String getid_list_push() {
        return id_list_push;
    }

    public void setid_list_push(String id_list_push) {
        this.id_list_push = id_list_push;
    }

    public String getlink_tembak() {
        return link_tembak;
    }

    public void setlink_tembak(String link_tembak) {
        this.link_tembak = link_tembak;
    }

    public String getstatus() {
        return status;
    }

    public void setstatus(String status) {
        this.status = status;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getUpdate_req() {
        return update_req;
    }

    public void setUpdate_req(String update_req) {
        this.update_req = update_req;
    }

    public String getDate_received() {
        return date_received;
    }

    public void setDate_received(String date_received) {
        this.date_received = date_received;
    }

    public String getDate_expired() {
        return date_expired;
    }

    public void setDate_expired(String date_expired) {
        this.date_expired = date_expired;
    }
}
