package com.mycompany.jira.plugins.aoexample;

import com.atlassian.jira.web.action.JiraWebActionSupport;
import com.atlassian.activeobjects.external.ActiveObjects;
import net.java.ao.Query;
import java.util.*;

public class AOExampleAction extends JiraWebActionSupport {

    private final ActiveObjects ao;

    public AOExampleAction(ActiveObjects ao) {
        this.ao = ao;
        setInfo(-1);
    }

    protected String doExecute() throws Exception {
        log.debug("Entering doExecute");
        return SUCCESS;
    }
    
    public String doCreate() {
        Address address = ao.create(Address.class);
        address.setHousenumber("10");
        address.setStreet("Oak Avenue");
        address.setCity("San Jose");
        address.save();
        setInfo(address.getID());
        return SUCCESS;
    }

    public String doSelect() {
        Address[] addresses = ao.find(Address.class, Query.select().where("housenumber = ?", "10"));
        if (addresses.length == 0) {
            setInfo(0);
            return ERROR;
        }
        Address address = addresses[0];
        setInfo(address.getID());
        return SUCCESS;
    }

    private int info;
    public int getInfo() {
        return info;
    }
    public String getInfoStr() {
        return "Latest value is " + Integer.toString(info);
    }
    public void setInfo(int value) {
        info = value;
    }

    private String selectid;
    public void setSelectid(String value) {
        selectid = value;
    }

}
