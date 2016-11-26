package com.rawalinfocom.rcontact.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProfileData {

    private String localPhoneBookId;
    //    private String givenName;
    private ArrayList<ProfileDataOperation> operation;

    @JsonProperty("pb_local_phonebook_id")
    public String getLocalPhoneBookId() {
        return this.localPhoneBookId;
    }

    public void setLocalPhoneBookId(String localPhoneBookId) {
        this.localPhoneBookId = localPhoneBookId;
    }

    @JsonProperty("operation")
    public ArrayList<ProfileDataOperation> getOperation() {
        return operation;
    }

    public void setOperation(ArrayList<ProfileDataOperation> operation) {
        this.operation = operation;
    }

   /* public String getGivenName() {
        return givenName;
    }

    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }*/
}
