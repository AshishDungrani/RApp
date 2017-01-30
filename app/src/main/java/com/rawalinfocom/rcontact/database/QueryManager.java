package com.rawalinfocom.rcontact.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.rawalinfocom.rcontact.R;
import com.rawalinfocom.rcontact.model.ProfileDataOperation;
import com.rawalinfocom.rcontact.model.ProfileDataOperationAddress;
import com.rawalinfocom.rcontact.model.ProfileDataOperationEmail;
import com.rawalinfocom.rcontact.model.ProfileDataOperationEvent;
import com.rawalinfocom.rcontact.model.ProfileDataOperationImAccount;
import com.rawalinfocom.rcontact.model.ProfileDataOperationOrganization;
import com.rawalinfocom.rcontact.model.ProfileDataOperationPhoneNumber;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;

/**
 * Created by user on 11/01/17.
 * <p>
 * A Class to Manage Query from multiple tables
 */

public class QueryManager {

    private DatabaseHandler databaseHandler;

    public QueryManager(DatabaseHandler databaseHandler) {
        this.databaseHandler = databaseHandler;
    }

    public ProfileDataOperation getRcProfileDetail(Context context, String rcpId) {

        SQLiteDatabase db = databaseHandler.getWritableDatabase();

        ProfileDataOperation profileDataOperation = new ProfileDataOperation();

        //<editor-fold desc="Profile Detail">
        // Select All Query
        String profileDetailQuery = "SELECT profile." + TableProfileMaster.COLUMN_PM_RAW_ID + "," +
                "profile." + TableProfileMaster.COLUMN_PM_PREFIX + ",profile." +
                TableProfileMaster.COLUMN_PM_FIRST_NAME + ",profile." + TableProfileMaster
                .COLUMN_PM_MIDDLE_NAME + ",profile." + TableProfileMaster.COLUMN_PM_LAST_NAME +
                ",profile." + TableProfileMaster.COLUMN_PM_SUFFIX + ",profile." +
                TableProfileMaster.COLUMN_PM_NICK_NAME + ",profile." + TableProfileMaster
                .COLUMN_PM_PHONETIC_FIRST_NAME + ",profile." + TableProfileMaster
                .COLUMN_PM_PHONETIC_MIDDLE_NAME + ",profile." + TableProfileMaster
                .COLUMN_PM_PHONETIC_LAST_NAME + ",profile." + TableProfileMaster
                .COLUMN_PM_PROFILE_IMAGE + ",profile." + TableProfileMaster
                .COLUMN_PM_NICK_NAME_PRIVACY + ",profile." + TableProfileMaster.COLUMN_PM_NOTES +
                ",profile." + TableProfileMaster.COLUMN_PM_NOTES_PRIVACY + ",profile." +
                TableProfileMaster.COLUMN_PM_GENDER + ",profile." + TableProfileMaster
                .COLUMN_PM_GENDER_PRIVACY + ",profile." + TableProfileMaster
                .COLUMN_PM_IS_FAVOURITE + ", profile." + TableProfileMaster
                .COLUMN_PM_PROFILE_RATING + ", profile." + TableProfileMaster
                .COLUMN_PM_PROFILE_RATE_USER + " from " + TableProfileMaster
                .TABLE_RC_PROFILE_MASTER + " profile WHERE profile." + TableProfileMaster
                .COLUMN_PM_RCP_ID + " IN (" + rcpId + ")";

        Cursor cursor = db.rawQuery(profileDetailQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {

            profileDataOperation.setRcpPmId(rcpId);
            profileDataOperation.setPbNamePrefix(StringUtils.defaultString(cursor.getString
                    (cursor.getColumnIndex(TableProfileMaster.COLUMN_PM_PREFIX))));
            profileDataOperation.setPbNameFirst(StringUtils.defaultString(cursor.getString(cursor
                    .getColumnIndex(TableProfileMaster.COLUMN_PM_FIRST_NAME))));
            profileDataOperation.setPbNameMiddle(StringUtils.defaultString(cursor.getString
                    (cursor.getColumnIndex(TableProfileMaster.COLUMN_PM_MIDDLE_NAME))));
            profileDataOperation.setPbNameLast(StringUtils.defaultString(cursor.getString(cursor
                    .getColumnIndex(TableProfileMaster.COLUMN_PM_LAST_NAME))));
            profileDataOperation.setPbNameSuffix(StringUtils.defaultString(cursor.getString
                    (cursor.getColumnIndex(TableProfileMaster.COLUMN_PM_SUFFIX))));
            profileDataOperation.setPbNickname(StringUtils.defaultString(cursor.getString(cursor
                    .getColumnIndex(TableProfileMaster.COLUMN_PM_NICK_NAME))));
            profileDataOperation.setPbPhoneticNameFirst(StringUtils.defaultString(cursor
                    .getString(cursor.getColumnIndex(TableProfileMaster
                            .COLUMN_PM_PHONETIC_FIRST_NAME))));
            profileDataOperation.setPbPhoneticNameMiddle(StringUtils.defaultString(cursor
                    .getString(cursor.getColumnIndex(TableProfileMaster
                            .COLUMN_PM_PHONETIC_MIDDLE_NAME))));
            profileDataOperation.setPbPhoneticNameLast(StringUtils.defaultString(cursor.getString
                    (cursor.getColumnIndex(TableProfileMaster.COLUMN_PM_PHONETIC_LAST_NAME))));
            profileDataOperation.setPbNote(StringUtils.defaultString(cursor.getString(cursor
                    .getColumnIndex(TableProfileMaster.COLUMN_PM_NOTES))));
            profileDataOperation.setIsFavourite(StringUtils.defaultString(cursor.getString(cursor
                    .getColumnIndex(TableProfileMaster.COLUMN_PM_IS_FAVOURITE))));
            profileDataOperation.setProfileRating(StringUtils.defaultString(cursor.getString
                    (cursor.getColumnIndex(TableProfileMaster.COLUMN_PM_PROFILE_RATING)), "0"));
            profileDataOperation.setTotalProfileRateUser(StringUtils.defaultString(cursor
                    .getString(cursor.getColumnIndex(TableProfileMaster
                            .COLUMN_PM_PROFILE_RATE_USER)), "0"));

            cursor.close();
        }
        //</editor-fold>

        //<editor-fold desc="Phone Number">
        String mobileNumberQuery = "SELECT mobile." + TableMobileMaster.COLUMN_MNM_MOBILE_NUMBER
                + ",mobile." + TableMobileMaster.COLUMN_MNM_NUMBER_TYPE + ",mobile." +
                TableMobileMaster.COLUMN_MNM_IS_PRIMARY + ",mobile." + TableMobileMaster
                .COLUMN_MNM_NUMBER_PRIVACY + " from " + TableMobileMaster
                .TABLE_RC_MOBILE_NUMBER_MASTER + " mobile WHERE mobile." + TableMobileMaster
                .COLUMN_RC_PROFILE_MASTER_PM_ID + " IN (" + rcpId + ")";

        Cursor mobileNumberCursor = db.rawQuery(mobileNumberQuery, null);

        ArrayList<ProfileDataOperationPhoneNumber> arrayListPhoneNumber = new ArrayList<>();

        // looping through all rows and adding to list
        if (mobileNumberCursor.moveToFirst()) {
            do {
                ProfileDataOperationPhoneNumber phoneNumber = new ProfileDataOperationPhoneNumber();
                phoneNumber.setPhoneNumber(StringUtils.defaultString(mobileNumberCursor.getString
                        (mobileNumberCursor.getColumnIndex(TableMobileMaster.COLUMN_MNM_MOBILE_NUMBER))));
                phoneNumber.setPhoneType(StringUtils.defaultString(mobileNumberCursor.getString
                        (mobileNumberCursor.getColumnIndex(TableMobileMaster.COLUMN_MNM_NUMBER_TYPE))));
                phoneNumber.setPbRcpType(Integer.parseInt(StringUtils.defaultString
                        (mobileNumberCursor.getString(mobileNumberCursor.getColumnIndex
                                (TableMobileMaster.COLUMN_MNM_IS_PRIMARY)), "1")));
                phoneNumber.setPhonePublic(Integer.parseInt(StringUtils.defaultString
                        (mobileNumberCursor.getString(mobileNumberCursor.getColumnIndex
                                (TableMobileMaster.COLUMN_MNM_NUMBER_PRIVACY)), "0")));
                arrayListPhoneNumber.add(phoneNumber);
            } while (mobileNumberCursor.moveToNext());
            mobileNumberCursor.close();
        }
        profileDataOperation.setPbPhoneNumber(arrayListPhoneNumber);
        //</editor-fold>

        //<editor-fold desc="EmailId">
        String emailIdQuery = "SELECT email." + TableEmailMaster.COLUMN_EM_EMAIL_ADDRESS + "," +
                "email." + TableEmailMaster.COLUMN_EM_EMAIL_TYPE + ",email." + TableEmailMaster
                .COLUMN_EM_EMAIL_PRIVACY + ",email." + TableEmailMaster.COLUMN_EM_IS_PRIMARY + " " +
                ",email." + TableEmailMaster.COLUMN_EM_IS_VERIFIED + " FROM " + TableEmailMaster
                .TABLE_RC_EMAIL_MASTER + " email where email." + TableEmailMaster
                .COLUMN_RC_PROFILE_MASTER_PM_ID + " IN (" + rcpId + ")";

        Cursor emailIdCursor = db.rawQuery(emailIdQuery, null);

        ArrayList<ProfileDataOperationEmail> arrayListEmail = new ArrayList<>();

        // looping through all rows and adding to list
        if (emailIdCursor.moveToFirst()) {
            do {
                ProfileDataOperationEmail email = new ProfileDataOperationEmail();
                email.setEmEmailId(StringUtils.defaultString(emailIdCursor.getString
                        (emailIdCursor.getColumnIndex(TableEmailMaster.COLUMN_EM_EMAIL_ADDRESS))));
                email.setEmType(StringUtils.defaultString(emailIdCursor.getString(emailIdCursor
                        .getColumnIndex(TableEmailMaster.COLUMN_EM_EMAIL_TYPE))));
                email.setEmPublic(Integer.parseInt(StringUtils.defaultString(emailIdCursor
                        .getString(emailIdCursor.getColumnIndex(TableEmailMaster
                                .COLUMN_EM_EMAIL_PRIVACY)), "0")));
                email.setEmRcpType(Integer.parseInt(StringUtils.defaultString(emailIdCursor
                        .getString(emailIdCursor.getColumnIndex(TableEmailMaster
                                .COLUMN_EM_IS_PRIMARY)), "1")));
                arrayListEmail.add(email);
            } while (emailIdCursor.moveToNext());
            emailIdCursor.close();
        }
        profileDataOperation.setPbEmailId(arrayListEmail);
        //</editor-fold>

        // <editor-fold desc="Organization">
        String organizationQuery = "SELECT org." + TableOrganizationMaster
                .COLUMN_OM_ORGANIZATION_TITLE + ", org." + TableOrganizationMaster
                .COLUMN_OM_JOB_DESCRIPTION + " from " + TableOrganizationMaster
                .TABLE_RC_ORGANIZATION_MASTER + " org WHERE org." + TableOrganizationMaster
                .COLUMN_RC_PROFILE_MASTER_PM_ID + " IN (" + rcpId + ")";

        Cursor organizationCursor = db.rawQuery(organizationQuery, null);

        ArrayList<ProfileDataOperationOrganization> arrayListOrganization = new ArrayList<>();

        // looping through all rows and adding to list
        if (organizationCursor.moveToFirst()) {
            do {
                ProfileDataOperationOrganization organization = new
                        ProfileDataOperationOrganization();
                organization.setOrgName(StringUtils.defaultString(organizationCursor.getString
                        (organizationCursor.getColumnIndex(TableOrganizationMaster
                                .COLUMN_OM_ORGANIZATION_TITLE))));
                organization.setOrgJobTitle(StringUtils.defaultString(organizationCursor
                        .getString(organizationCursor.getColumnIndex(TableOrganizationMaster
                                .COLUMN_OM_JOB_DESCRIPTION))));
                organization.setOrgRcpType(context.getResources().getInteger(R.integer
                        .rcp_type_cloud_phone_book));
                arrayListOrganization.add(organization);
            } while (organizationCursor.moveToNext());
            organizationCursor.close();
        }
        profileDataOperation.setPbOrganization(arrayListOrganization);
        //</editor-fold>

        // <editor-fold desc="Event">
        String eventQuery = "SELECT event." + TableEventMaster.COLUMN_EVM_START_DATE + ",event."
                + TableEventMaster.COLUMN_EVM_EVENT_TYPE + ",event." + TableEventMaster
                .COLUMN_EVM_EVENT_PRIVACY + " FROM " + TableEventMaster.TABLE_RC_EVENT_MASTER + "" +
                " event WHERE event." + TableEventMaster.COLUMN_RC_PROFILE_MASTER_PM_ID + " IN ("
                + rcpId + ")";

        Cursor eventCursor = db.rawQuery(eventQuery, null);

        ArrayList<ProfileDataOperationEvent> arrayListEvent = new ArrayList<>();

        // looping through all rows and adding to list
        if (eventCursor.moveToFirst()) {
            do {
                ProfileDataOperationEvent event = new ProfileDataOperationEvent();
                event.setEventDate(StringUtils.defaultString(eventCursor.getString(eventCursor
                        .getColumnIndex(TableEventMaster.COLUMN_EVM_START_DATE))));
                event.setEventType(StringUtils.defaultString(eventCursor.getString(eventCursor
                        .getColumnIndex(TableEventMaster.COLUMN_EVM_EVENT_TYPE))));
                event.setEventPublic(StringUtils.defaultString(eventCursor.getString(eventCursor
                        .getColumnIndex(TableEventMaster.COLUMN_EVM_EVENT_PRIVACY)), "0"));
                event.setEventRcType(context.getResources().getInteger(R.integer
                        .rcp_type_cloud_phone_book));
                arrayListEvent.add(event);
            } while (eventCursor.moveToNext());
            eventCursor.close();
        }
        profileDataOperation.setPbEvent(arrayListEvent);
        //</editor-fold>

        // <editor-fold desc="Im Account">
        String imAccountQuery = "SELECT im." + TableImMaster.COLUMN_IM_IM_TYPE + ", im." +
                TableImMaster.COLUMN_IM_IM_PROTOCOL + ", im." + TableImMaster
                .COLUMN_IM_IM_PRIVACY + " FROM " + TableImMaster.TABLE_RC_IM_MASTER + " im WHERE " +
                "im." + TableImMaster.COLUMN_RC_PROFILE_MASTER_PM_ID + " IN (" + rcpId + ")";

        Cursor imAccountCursor = db.rawQuery(imAccountQuery, null);

        ArrayList<ProfileDataOperationImAccount> arrayListImAccount = new ArrayList<>();

        // looping through all rows and adding to list
        if (imAccountCursor.moveToFirst()) {
            do {
                ProfileDataOperationImAccount imAccount = new ProfileDataOperationImAccount();
                imAccount.setIMAccountType(StringUtils.defaultString(imAccountCursor.getString
                        (imAccountCursor.getColumnIndex(TableImMaster.COLUMN_IM_IM_TYPE))));
                imAccount.setIMAccountProtocol(StringUtils.defaultString(imAccountCursor
                        .getString(imAccountCursor.getColumnIndex(TableImMaster
                                .COLUMN_IM_IM_PROTOCOL))));
                imAccount.setIMAccountPublic(StringUtils.defaultString(imAccountCursor.getString
                        (imAccountCursor.getColumnIndex(TableImMaster.COLUMN_IM_IM_PRIVACY)), "0"));
                imAccount.setIMRcpType(context.getResources().getInteger(R.integer
                        .rcp_type_cloud_phone_book));
                arrayListImAccount.add(imAccount);
            } while (imAccountCursor.moveToNext());
            imAccountCursor.close();
        }
        profileDataOperation.setPbIMAccounts(arrayListImAccount);
        //</editor-fold>

        // <editor-fold desc="Address">
        String addressQuery = "SELECT address." + TableAddressMaster.COLUMN_AM_FORMATTED_ADDRESS
                + ", address." + TableAddressMaster.COLUMN_AM_ADDRESS_TYPE + ", address." +
                TableAddressMaster.COLUMN_AM_ADDRESS_PRIVACY + " FROM " + TableAddressMaster
                .TABLE_RC_ADDRESS_MASTER + " address WHERE address." + TableAddressMaster
                .COLUMN_RC_PROFILE_MASTER_PM_ID + " IN (" + rcpId + ")";

        Cursor addressCursor = db.rawQuery(addressQuery, null);

        ArrayList<ProfileDataOperationAddress> arrayListAddress = new ArrayList<>();

        // looping through all rows and adding to list
        if (addressCursor.moveToFirst()) {
            do {
                ProfileDataOperationAddress address = new ProfileDataOperationAddress();
                address.setFormattedAddress(StringUtils.defaultString(addressCursor.getString
                        (addressCursor.getColumnIndex(TableAddressMaster
                                .COLUMN_AM_FORMATTED_ADDRESS))));
                address.setAddressType(StringUtils.defaultString(addressCursor.getString
                        (addressCursor.getColumnIndex(TableAddressMaster.COLUMN_AM_ADDRESS_TYPE))));
                address.setRcpType(context.getResources().getInteger(R.integer
                        .rcp_type_cloud_phone_book));
                arrayListAddress.add(address);
            } while (addressCursor.moveToNext());
            addressCursor.close();
        }
        profileDataOperation.setPbAddress(arrayListAddress);
        //</editor-fold>

        // <editor-fold desc="Website">
        String websiteQuery = "select website." + TableWebsiteMaster.COLUMN_WM_WEBSITE_URL + " " +
                "FROM " + TableWebsiteMaster.TABLE_RC_WEBSITE_MASTER + " website WHERE website."
                + TableWebsiteMaster.COLUMN_RC_PROFILE_MASTER_PM_ID + " IN (" + rcpId + ")";

        Cursor websiteCursor = db.rawQuery(websiteQuery, null);

        ArrayList<String> arrayListWebsite = new ArrayList<>();

        // looping through all rows and adding to list
        if (websiteCursor.moveToFirst()) {
            do {
                arrayListWebsite.add(StringUtils.defaultString(websiteCursor.getString
                        (websiteCursor.getColumnIndex(TableWebsiteMaster.COLUMN_WM_WEBSITE_URL))));
            } while (websiteCursor.moveToNext());
            websiteCursor.close();
        }
        profileDataOperation.setPbWebAddress(arrayListWebsite);
        //</editor-fold>

        db.close();

        // return profile data operation
        return profileDataOperation;
    }

}