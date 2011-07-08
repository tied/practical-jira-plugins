package com.mycompany.jira.plugins.aoexample;

import net.java.ao.Entity;

public interface Address extends Entity {
    // This could equally have used an Integer 
    String getHousenumber();
    void  setHousenumber(String housenumber);
    
    String getStreet();
    void  setStreet(String street);
    
    String getCity();
    void  setCity(String city);
}
