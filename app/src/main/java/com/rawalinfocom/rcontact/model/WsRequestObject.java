package com.rawalinfocom.rcontact.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Monal on 20/10/16.
 * <p>
 * Class containing all the Response Objects
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class WsRequestObject {

    @JsonProperty("car_id")
    private Integer carId;

    @JsonProperty("car_pm_id_to")
    private Integer carPmIdTo;

    @JsonProperty("car_filed_type")
    private String carFiledType;

    @JsonProperty("car_mongodb_record_index")
    private String carMongoDbRecordIndex;

    @JsonProperty("car_status")
    private Integer carStatus;

    @JsonProperty("date")
    private String date;

    @JsonProperty("comment")
    private String comment;

    @JsonProperty("to_pm_id")
    private Integer toPmId;

    @JsonProperty("event_record_index_id")
    private String eventRecordIndexId;

    @JsonProperty("id")
    private String commentId;

    @JsonProperty("reply")
    private String reply;

    @JsonProperty("flag")
    private Integer flag;

    @JsonProperty("privacy_data")
    private List<PrivacyDataItem> privacyData;

    private String countryCode;
    private String mobileNumber;

    @JsonProperty("pm_id")
    private Integer pmId;

    private String cmId;
    private String otp;
    private String otpGenerationTime;

    private String status;
    private String ldOtpDeliveredTimeFromCloudToDevice;
    private String accessToken;
    private String pbSocialId;

    private String firstName;
    private String lastName;
    private String emailId;
    private String type;
    private String deviceId;
    private String socialMediaTokenId;
    private String createdBy;

    private String profileImage;

    private String prComment;
    private String prRatingStars;
    private String prStatus;
    private Integer prToPmId;

    private Integer sendProfileType;

    private Integer pmIdWhose;
    private ContactReceiver receiver;

    private String dmModel;
    private String dmVersion;
    private String dmBrand;
    private String dmDevice;
    private String dmUniqueid;
    private String dmLocation;

    private ArrayList<ProfileData> profileData;
    private ArrayList<ProfileData> favourites;
    private ProfileDataOperation contactData;

    private ArrayList<String> arrayListContactNumber;
    private ArrayList<String> arrayListEmailAddress;
    private ArrayList<CallLogType> arrayListCallLogType;

    private ArrayList<ProfileVisit> arrayListProfileVisit;

    private ProfileDataOperation profileEdit;

    private ArrayList<CallLogHistoryType> historyTypeArrayList;

    private ArrayList<SmsDataType> arrayListSmsDataType;

    private ArrayList<GlobalSearchType> globalSearchTypeArrayList;

    private String searchQuery;

    @JsonProperty("startAt")
    private Integer searchStartAt;

    @JsonProperty("maxRecords")
    private Integer searchMaxRecord;

    @JsonProperty("pr_reply")
    private String prReply;

    @JsonProperty("pr_id")
    private String prId;

    public String getPrReply() {
        return prReply;
    }

    public void setPrReply(String prReply) {
        this.prReply = prReply;
    }

    public String getPrId() {
        return prId;
    }

    public void setPrId(String prId) {
        this.prId = prId;
    }

    @JsonProperty("submit_rating")
    private ArrayList<Rating> ratings;


    @JsonProperty("country_code")
    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    @JsonProperty("mobile_number")
    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public Integer getPmId() {
        return pmId;
    }

    public void setPmId(Integer pmId) {
        this.pmId = pmId;
    }

    @JsonProperty("otp")
    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    @JsonProperty("otp_generation_time")
    public String getOtpGenerationTime() {
        return otpGenerationTime;
    }

    public void setOtpGenerationTime(String otpGenerationTime) {
        this.otpGenerationTime = otpGenerationTime;
    }

    @JsonProperty("status")
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @JsonProperty("ld_otp_delivered_time_from_cloud_to_device")
    public String getLdOtpDeliveredTimeFromCloudToDevice() {
        return ldOtpDeliveredTimeFromCloudToDevice;
    }

    public void setLdOtpDeliveredTimeFromCloudToDevice(String ldOtpDeliveredTimeFromCloudToDevice) {
        this.ldOtpDeliveredTimeFromCloudToDevice = ldOtpDeliveredTimeFromCloudToDevice;
    }

    @JsonProperty("access_token")
    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    @JsonProperty("first_name")
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @JsonProperty("last_name")
    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @JsonProperty("email_id")
    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public Integer getSearchMaxRecord() {
        return searchMaxRecord;
    }

    @JsonProperty("device_id")
    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    @JsonProperty("social_media_token_id")
    public String getSocialMediaTokenId() {
        return socialMediaTokenId;
    }

    public void setSocialMediaTokenId(String socialMediaTokenId) {
        this.socialMediaTokenId = socialMediaTokenId;
    }

    @JsonProperty("pb_social_id")
    public String getPbSocialId() {
        return pbSocialId;
    }

    public void setPbSocialId(String pbSocialId) {
        this.pbSocialId = pbSocialId;
    }

    @JsonProperty("profile_image")
    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    @JsonProperty("data")
    public ArrayList<ProfileData> getProfileData() {
        return profileData;
    }

    public void setProfileData(ArrayList<ProfileData> profileData) {
        this.profileData = profileData;
    }

    @JsonProperty("cm_id")
    public String getCmId() {
        return cmId;
    }

    public void setCmId(String cmId) {
        this.cmId = cmId;
    }

    @JsonProperty("favourites")
    public ArrayList<ProfileData> getFavourites() {
        return favourites;
    }

    public void setFavourites(ArrayList<ProfileData> favourites) {
        this.favourites = favourites;
    }

    @JsonProperty("pr_comment")
    public String getPrComment() {
        return prComment;
    }

    public void setPrComment(String prComment) {
        this.prComment = prComment;
    }

    @JsonProperty("pr_rating_stars")
    public String getPrRatingStars() {
        return prRatingStars;
    }

    public void setPrRatingStars(String prRatingStars) {
        this.prRatingStars = prRatingStars;
    }

    @JsonProperty("pr_status")
    public String getPrStatus() {
        return prStatus;
    }

    public void setPrStatus(String prStatus) {
        this.prStatus = prStatus;
    }

    @JsonProperty("pr_to_pm_id")
    public Integer getPrToPmId() {
        return prToPmId;
    }

    public void setPrToPmId(Integer prToPmId) {
        this.prToPmId = prToPmId;
    }

    @JsonProperty("send_profile_type")
    public Integer getSendProfileType() {
        return sendProfileType;
    }

    public void setSendProfileType(Integer sendProfileType) {
        this.sendProfileType = sendProfileType;
    }

    @JsonProperty("contact_data")
    public ProfileDataOperation getContactData() {
        return contactData;
    }

    public void setContactData(ProfileDataOperation contactData) {
        this.contactData = contactData;
    }

    @JsonProperty("pm_id_whose")
    public Integer getPmIdWhose() {
        return pmIdWhose;
    }

    public void setPmIdWhose(Integer pmIdWhose) {
        this.pmIdWhose = pmIdWhose;
    }

    @JsonProperty("receiver")
    public ContactReceiver getReceiver() {
        return receiver;
    }

    public void setReceiver(ContactReceiver receiver) {
        this.receiver = receiver;
    }

    @JsonProperty("contact_number")
    public ArrayList<String> getArrayListContactNumber() {
        return arrayListContactNumber;
    }

    public void setArrayListContactNumber(ArrayList<String> arrayListContactNumber) {
        this.arrayListContactNumber = arrayListContactNumber;
    }

    @JsonProperty("email_address")
    public ArrayList<String> getArrayListEmailAddress() {
        return arrayListEmailAddress;
    }

    public void setArrayListEmailAddress(ArrayList<String> arrayListEmailAddress) {
        this.arrayListEmailAddress = arrayListEmailAddress;
    }

    @JsonProperty("dm_model")
    public String getDmModel() {
        return dmModel;
    }

    public void setDmModel(String dmModel) {
        this.dmModel = dmModel;
    }

    @JsonProperty("dm_version")
    public String getDmVersion() {
        return dmVersion;
    }

    public void setDmVersion(String dmVersion) {
        this.dmVersion = dmVersion;
    }

    @JsonProperty("dm_brand")
    public String getDmBrand() {
        return dmBrand;
    }

    public void setDmBrand(String dmBrand) {
        this.dmBrand = dmBrand;
    }

    @JsonProperty("dm_device")
    public String getDmDevice() {
        return dmDevice;
    }

    public void setDmDevice(String dmDevice) {
        this.dmDevice = dmDevice;
    }

    @JsonProperty("dm_uniqueid")
    public String getDmUniqueid() {
        return dmUniqueid;
    }

    public void setDmUniqueid(String dmUniqueid) {
        this.dmUniqueid = dmUniqueid;
    }

    @JsonProperty("dm_location")
    public String getDmLocation() {
        return dmLocation;
    }

    public void setDmLocation(String dmLocation) {
        this.dmLocation = dmLocation;
    }

    @JsonProperty("profile_visit")
    public ArrayList<ProfileVisit> getArrayListProfileVisit() {
        return arrayListProfileVisit;
    }

    public void setArrayListProfileVisit(ArrayList<ProfileVisit> arrayListProfileVisit) {
        this.arrayListProfileVisit = arrayListProfileVisit;
    }

    public Integer getFlag() {
        return flag;
    }

    public void setFlag(Integer flag) {
        this.flag = flag;
    }

    @JsonProperty("call_log")
    public ArrayList<CallLogType> getArrayListCallLogType() {
        return arrayListCallLogType;
    }

    public void setArrayListCallLogType(ArrayList<CallLogType> arrayListCallLogType) {
        this.arrayListCallLogType = arrayListCallLogType;
    }

   /* @JsonProperty("profile_edit")
    public ArrayList<ProfileDataOperation> getProfileEdit() {
        return profileEdit;
    }

    public void setProfileEdit(ArrayList<ProfileDataOperation> profileEdit) {
        this.profileEdit = profileEdit;
    }*/

    @JsonProperty("profile_edit")
    public ProfileDataOperation getProfileEdit() {
        return profileEdit;
    }

    public void setProfileEdit(ProfileDataOperation profileEdit) {
        this.profileEdit = profileEdit;
    }

    @JsonProperty("created_by")
    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    @JsonProperty("call_history")
    public ArrayList<CallLogHistoryType> getHistoryTypeArrayList() {
        return historyTypeArrayList;
    }

    public void setHistoryTypeArrayList(ArrayList<CallLogHistoryType> historyTypeArrayList) {
        this.historyTypeArrayList = historyTypeArrayList;
    }

    @JsonProperty("sms_log")
    public ArrayList<SmsDataType> getArrayListSmsDataType() {
        return arrayListSmsDataType;
    }

    public void setArrayListSmsDataType(ArrayList<SmsDataType> arrayListSmsDataType) {
        this.arrayListSmsDataType = arrayListSmsDataType;
    }

    @JsonProperty("results")
    public ArrayList<GlobalSearchType> getGlobalSearchTypeArrayList() {
        return globalSearchTypeArrayList;
    }

    public void setGlobalSearchTypeArrayList(ArrayList<GlobalSearchType> globalSearchTypeArrayList) {
        this.globalSearchTypeArrayList = globalSearchTypeArrayList;
    }


    @JsonProperty("search")
    public String getSearchQuery() {
        return searchQuery;
    }

    public void setSearchQuery(String searchQuery) {
        this.searchQuery = searchQuery;
    }

    public Integer getSearchStartAt() {
        return searchStartAt;
    }

    public void setSearchStartAt(Integer searchStartAt) {
        this.searchStartAt = searchStartAt;
    }

    public void setSearchMaxRecord(Integer searchMaxRecord) {
        this.searchMaxRecord = searchMaxRecord;
    }

    public List<PrivacyDataItem> getPrivacyData() {
        return privacyData;
    }

    public void setPrivacyData(List<PrivacyDataItem> privacyData) {
        this.privacyData = privacyData;
    }

    public Integer getCarPmIdTo() {
        return carPmIdTo;
    }

    public void setCarPmIdTo(Integer carPmIdTo) {
        this.carPmIdTo = carPmIdTo;
    }

    public String getCarFiledType() {
        return carFiledType;
    }

    public void setCarFiledType(String carFiledType) {
        this.carFiledType = carFiledType;
    }

    public String getCarMongoDbRecordIndex() {
        return carMongoDbRecordIndex;
    }

    public void setCarMongoDbRecordIndex(String carMongoDbRecordIndex) {
        this.carMongoDbRecordIndex = carMongoDbRecordIndex;
    }

    public Integer getCarId() {
        return carId;
    }

    public void setCarId(Integer carId) {
        this.carId = carId;
    }

    public Integer getCarStatus() {
        return carStatus;
    }

    public void setCarStatus(Integer carStatus) {
        this.carStatus = carStatus;
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public String getReply() {
        return reply;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }

    public String getEvmRecordIndexId() {
        return eventRecordIndexId;
    }

    public void setEvmRecordIndexId(String eventRecordIndexId) {
        this.eventRecordIndexId = eventRecordIndexId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getToPmId() {
        return toPmId;
    }

    public void setToPmId(Integer toPmId) {
        this.toPmId = toPmId;
    }

    public ArrayList<Rating> getRatings() {
        return ratings;
    }

    public void setRatings(ArrayList<Rating> ratings) {
        this.ratings = ratings;
    }
}
