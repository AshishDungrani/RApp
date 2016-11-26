package com.rawalinfocom.rcontact.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProfileDataOperationOrganization {

    private String orgJobTitle;
    private String orgDepartment;
    private String orgName;
    private String orgType;
    private String orgJobDescription;
    private String orgOfficeLocation;

    @JsonProperty("org_job_title")
    public String getOrgJobTitle() {return this.orgJobTitle;}

    public void setOrgJobTitle(String orgJobTitle) {this.orgJobTitle = orgJobTitle;}

    @JsonProperty("org_department")
    public String getOrgDepartment() {return this.orgDepartment;}

    public void setOrgDepartment(String orgDepartment) {this.orgDepartment = orgDepartment;}

    @JsonProperty("org_name")
    public String getOrgName() {return this.orgName;}

    public void setOrgName(String orgName) {this.orgName = orgName;}

    @JsonProperty("org_type")
    public String getOrgType() {
        return orgType;
    }

    public void setOrgType(String orgType) {
        this.orgType = orgType;
    }

    @JsonProperty("org_job_description")
    public String getOrgJobDescription() {
        return orgJobDescription;
    }

    public void setOrgJobDescription(String orgJobDescription) {
        this.orgJobDescription = orgJobDescription;
    }

    @JsonProperty("org_office_location")
    public String getOrgOfficeLocation() {
        return orgOfficeLocation;
    }

    public void setOrgOfficeLocation(String orgOfficeLocation) {
        this.orgOfficeLocation = orgOfficeLocation;
    }
}
