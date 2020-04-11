package com.example.healthcare.Items;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Statewise implements Serializable {

    @SerializedName("active")
    @Expose
    private String active;
    @SerializedName("confirmed")
    @Expose
    private String confirmed;
    @SerializedName("deaths")
    @Expose
    private String deaths;
    @SerializedName("lastupdatedtime")
    @Expose
    private String lastupdatedtime;
    @SerializedName("recovered")
    @Expose
    private String recovered;
    @SerializedName("state")
    @Expose
    private String state;
    @SerializedName("deltaconfirmed")
    @Expose
    private String deltaconfirmed;

    public String getDeltaconfirmed() {
        return deltaconfirmed;
    }

    public void setDeltaconfirmed(String deltaconfirmed) {
        this.deltaconfirmed = deltaconfirmed;
    }

    public String getActive() {
        return active;
    }

    public void setActive(String active) {
        this.active = active;
    }

    public String getConfirmed() {
        return confirmed;
    }

    public void setConfirmed(String confirmed) {
        this.confirmed = confirmed;
    }

    public String getDeaths() {
        return deaths;
    }

    public void setDeaths(String deaths) {
        this.deaths = deaths;
    }

    public String getLastupdatedtime() {
        return lastupdatedtime;
    }

    public void setLastupdatedtime(String lastupdatedtime) {
        this.lastupdatedtime = lastupdatedtime;
    }

    public String getRecovered() {
        return recovered;
    }

    public void setRecovered(String recovered) {
        this.recovered = recovered;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

}