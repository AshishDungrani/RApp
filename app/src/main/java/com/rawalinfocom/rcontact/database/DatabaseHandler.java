package com.rawalinfocom.rcontact.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;

/**
 * Created by Monal on 21/10/16.
 */

public class DatabaseHandler extends SQLiteOpenHelper {

    private final String LOG_TAG = "DatabaseHelper";

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "RContact.db";

    private static String databasePath = "";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        databasePath = context.getDatabasePath(DATABASE_NAME).getPath();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // creating required tables
        db.execSQL(TableCountryMaster.CREATE_TABLE_RC_COUNTRY_MASTER);
        db.execSQL(TableOtpLogDetails.CREATE_TABLE_OTP_LOG_DETAILS);
        db.execSQL(TableProfileMaster.CREATE_TABLE_RC_PROFILE_MASTER);
        db.execSQL(TableEmailMaster.CREATE_TABLE_RC_EMAIL_MASTER);
        db.execSQL(TableMobileMaster.CREATE_TABLE_RC_MOBILE_NUMBER_MASTER);
        db.execSQL(TableProfileMobileMapping.CREATE_TABLE_PB_PROFILE_MOBILE_MAPPING);
        db.execSQL(TableProfileEmailMapping.CREATE_TABLE_PB_PROFILE_EMAIL_MAPPING);
        db.execSQL(TableAddressMaster.CREATE_TABLE_RC_ADDRESS_MASTER);
        db.execSQL(TableEventMaster.CREATE_TABLE_RC_EVENT_MASTER);
        db.execSQL(TableFlagMaster.CREATE_TABLE_RC_FLAG_MASTER);
        db.execSQL(TableImMaster.CREATE_TABLE_RC_IM_MASTER);
        db.execSQL(TableOfflineBackupMaster.CREATE_TABLE_PB_OFFLINE_BACKUP_MASTER);
        db.execSQL(TableOrganizationMaster.CREATE_TABLE_RC_ORGANIZATION_MASTER);
        db.execSQL(TableRelationMaster.CREATE_TABLE_RC_RELATION_MASTER);
        db.execSQL(TableWebsiteMaster.CREATE_TABLE_RC_EMAIL_MASTER);
        db.execSQL(TableContactRatingMaster.CREATE_TABLE_CONTACT_RATING_MASTER);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // on upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + TableCountryMaster.CREATE_TABLE_RC_COUNTRY_MASTER);
        db.execSQL("DROP TABLE IF EXISTS " + TableOtpLogDetails.CREATE_TABLE_OTP_LOG_DETAILS);
        db.execSQL("DROP TABLE IF EXISTS " + TableProfileMaster.CREATE_TABLE_RC_PROFILE_MASTER);
        db.execSQL("DROP TABLE IF EXISTS " + TableEmailMaster.CREATE_TABLE_RC_EMAIL_MASTER);
        db.execSQL("DROP TABLE IF EXISTS " + TableMobileMaster
                .CREATE_TABLE_RC_MOBILE_NUMBER_MASTER);
        db.execSQL("DROP TABLE IF EXISTS " + TableProfileMobileMapping
                .CREATE_TABLE_PB_PROFILE_MOBILE_MAPPING);
        db.execSQL("DROP TABLE IF EXISTS " + TableProfileEmailMapping
                .CREATE_TABLE_PB_PROFILE_EMAIL_MAPPING);
        db.execSQL("DROP TABLE IF EXISTS " + TableAddressMaster.CREATE_TABLE_RC_ADDRESS_MASTER);
        db.execSQL("DROP TABLE IF EXISTS " + TableEventMaster.CREATE_TABLE_RC_EVENT_MASTER);
        db.execSQL("DROP TABLE IF EXISTS " + TableFlagMaster.CREATE_TABLE_RC_FLAG_MASTER);
        db.execSQL("DROP TABLE IF EXISTS " + TableImMaster.CREATE_TABLE_RC_IM_MASTER);
        db.execSQL("DROP TABLE IF EXISTS " + TableOfflineBackupMaster
                .CREATE_TABLE_PB_OFFLINE_BACKUP_MASTER);
        db.execSQL("DROP TABLE IF EXISTS " + TableOrganizationMaster
                .CREATE_TABLE_RC_ORGANIZATION_MASTER);
        db.execSQL("DROP TABLE IF EXISTS " + TableRelationMaster.CREATE_TABLE_RC_RELATION_MASTER);
        db.execSQL("DROP TABLE IF EXISTS " + TableWebsiteMaster.CREATE_TABLE_RC_EMAIL_MASTER);
        db.execSQL("DROP TABLE IF EXISTS " + TableContactRatingMaster
                .CREATE_TABLE_CONTACT_RATING_MASTER);

        // create new tables
        onCreate(db);
    }

    public static String exportDB() {
        String inFileName = databasePath;
        try {
            File dbFile = new File(inFileName);
            FileInputStream fis = new FileInputStream(dbFile);

            String outFileName = Environment.getExternalStorageDirectory() + "/" + DATABASE_NAME;

            OutputStream output = new FileOutputStream(outFileName);

            byte[] buffer = new byte[1024];
            int length;
            while ((length = fis.read(buffer)) > 0) {
                output.write(buffer, 0, length);
            }
            //Close the streams
            output.flush();
            output.close();
            fis.close();
            return DATABASE_NAME;
        } catch (Exception e) {
            return null;
        }
    }
}
