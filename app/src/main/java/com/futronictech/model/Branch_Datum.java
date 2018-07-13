
package com.futronictech.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Branch_Datum {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("location")
    @Expose
    private String location;
    @SerializedName("dateCreated")
    @Expose
    private String dateCreated;
    @SerializedName("dateUpdated")
    @Expose
    private String dateUpdated;
    @SerializedName("organisationId")
    @Expose
    private Integer organisationId;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("headquarter")
    @Expose
    private Boolean headquarter;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getDateUpdated() {
        return dateUpdated;
    }

    public void setDateUpdated(String dateUpdated) {
        this.dateUpdated = dateUpdated;
    }

    public Integer getOrganisationId() {
        return organisationId;
    }

    public void setOrganisationId(Integer organisationId) {
        this.organisationId = organisationId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Boolean getHeadquarter() {
        return headquarter;
    }

    public void setHeadquarter(Boolean headquarter) {
        this.headquarter = headquarter;
    }

}
