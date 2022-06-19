package com.istiaksaif.highlymotavated.Model;

import com.google.firebase.database.Exclude;

import java.io.Serializable;

public class HelperModel implements Serializable
{
    @Exclude
    private String key;
    public HelperModel(){}

    public String getKey()
    {
        return key;
    }

    public void setKey(String key)
    {
        this.key = key;
    }
}