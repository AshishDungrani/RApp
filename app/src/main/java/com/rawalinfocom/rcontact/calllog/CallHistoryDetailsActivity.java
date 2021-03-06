package com.rawalinfocom.rcontact.calllog;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.provider.Telephony;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.rawalinfocom.rcontact.BaseActivity;
import com.rawalinfocom.rcontact.R;
import com.rawalinfocom.rcontact.RContactApplication;
import com.rawalinfocom.rcontact.adapters.CallHistoryListAdapter;
import com.rawalinfocom.rcontact.adapters.OrganizationListAdapter;
import com.rawalinfocom.rcontact.adapters.ProfileDetailAdapter;
import com.rawalinfocom.rcontact.asynctasks.AsyncWebServiceCall;
import com.rawalinfocom.rcontact.constants.AppConstants;
import com.rawalinfocom.rcontact.constants.IntegerConstants;
import com.rawalinfocom.rcontact.constants.WsConstants;
import com.rawalinfocom.rcontact.database.PhoneBookContacts;
import com.rawalinfocom.rcontact.database.QueryManager;
import com.rawalinfocom.rcontact.database.TableCommentMaster;
import com.rawalinfocom.rcontact.database.TableProfileMaster;
import com.rawalinfocom.rcontact.enumerations.WSRequestType;
import com.rawalinfocom.rcontact.helper.CallConfirmationListDialog;
import com.rawalinfocom.rcontact.helper.MaterialDialog;
import com.rawalinfocom.rcontact.helper.ProfileMenuOptionDialog;
import com.rawalinfocom.rcontact.helper.RippleView;
import com.rawalinfocom.rcontact.helper.Utils;
import com.rawalinfocom.rcontact.helper.imagetransformation.CropCircleTransformation;
import com.rawalinfocom.rcontact.interfaces.WsResponseListener;
import com.rawalinfocom.rcontact.model.CallLogHistoryType;
import com.rawalinfocom.rcontact.model.CallLogType;
import com.rawalinfocom.rcontact.model.Comment;
import com.rawalinfocom.rcontact.model.ProfileData;
import com.rawalinfocom.rcontact.model.ProfileDataOperation;
import com.rawalinfocom.rcontact.model.ProfileDataOperationAddress;
import com.rawalinfocom.rcontact.model.ProfileDataOperationEmail;
import com.rawalinfocom.rcontact.model.ProfileDataOperationEvent;
import com.rawalinfocom.rcontact.model.ProfileDataOperationImAccount;
import com.rawalinfocom.rcontact.model.ProfileDataOperationOrganization;
import com.rawalinfocom.rcontact.model.ProfileDataOperationPhoneNumber;
import com.rawalinfocom.rcontact.model.ProfileDataOperationWebAddress;
import com.rawalinfocom.rcontact.model.ProfileVisit;
import com.rawalinfocom.rcontact.model.Rating;
import com.rawalinfocom.rcontact.model.WsRequestObject;
import com.rawalinfocom.rcontact.model.WsResponseObject;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CallHistoryDetailsActivity extends BaseActivity implements RippleView
        .OnRippleCompleteListener, WsResponseListener {

    @BindView(R.id.image_action_back)
    ImageView imageActionBack;
    @BindView(R.id.linear_action_right)
    LinearLayout linearActionRight;
    @BindView(R.id.relative_action_back)
    RelativeLayout relativeActionBack;
    @BindView(R.id.text_full_screen_text)
    TextView textFullScreenText;
    @BindView(R.id.image_profile)
    ImageView imageProfile;
    @BindView(R.id.text_user_rating)
    TextView textUserRating;
    @BindView(R.id.rating_user)
    RatingBar ratingUser;
    @BindView(R.id.linear_basic_detail_rating)
    LinearLayout linearBasicDetailRating;
    @BindView(R.id.text_name)
    TextView textName;
    @BindView(R.id.text_designation)
    TextView textDesignation;
    @BindView(R.id.text_organization)
    TextView textOrganization;
    @BindView(R.id.text_view_all_organization)
    TextView textViewAllOrganization;
    @BindView(R.id.linear_organization_detail)
    LinearLayout linearOrganizationDetail;
    @BindView(R.id.linear_basic_detail)
    LinearLayout linearBasicDetail;
    @BindView(R.id.relative_basic_detail)
    RelativeLayout relativeBasicDetail;
    @BindView(R.id.button_call_log)
    Button buttonCallLog;
    @BindView(R.id.ripple_call_log)
    RippleView rippleCallLog;
    @BindView(R.id.button_sms)
    Button buttonSms;
    @BindView(R.id.ripple_sms)
    RippleView rippleSms;
    @BindView(R.id.linear_call_sms)
    LinearLayout linearCallSms;
    @BindView(R.id.text_text_call_history)
    TextView textTextCallHistory;
    @BindView(R.id.text_no_history_to_show)
    TextView textNoHistoryToShow;
    @BindView(R.id.recycler_call_history)
    RecyclerView recyclerCallHistory;
    @BindView(R.id.relative_call_history)
    RelativeLayout relativeCallHistory;
    @BindView(R.id.relative_root_profile_detail)
    RelativeLayout relativeRootProfileDetail;

    @BindView(R.id.include_toolbar)
    Toolbar includeToolbar;
    TextView textToolbarTitle;
    RippleView rippleActionBack;
    RippleView rippleActionRightLeft;
    RippleView rippleActionRightCenter;
    RippleView rippleActionRightRight;
    ImageView imageRightLeft;
    ImageView imageRightCenter;
    String profileThumbnail;

    private final String TAG_IMAGE_FAVOURITE = "tag_favourite";
    private final String TAG_IMAGE_UN_FAVOURITE = "tag_un_favourite";
    private final String TAG_IMAGE_CALL = "tag_call";

    RelativeLayout relativeRootRatingDialog;

    ProfileDataOperation profileDataOperationVcard;

    String pmId, phoneBookId, contactName = "", cloudContactName = null, checkNumberFavourite =
            null;
    boolean displayOwnProfile = false, isHideFavourite = false;

    PhoneBookContacts phoneBookContacts;
    int listClickedPosition = -1;

    MaterialDialog callConfirmationDialog;

    ArrayList<String> arrayListFavouriteContacts;
    RContactApplication rContactApplication;

    String profileContactNumber;
    ArrayList<CallLogType> arrayListHistory;
    String historyNumber = "";
    String historyName = "";
    long historyDate;
    CallHistoryListAdapter callHistoryListAdapter;
    ArrayList<Object> tempPhoneNumber;
    String hashMapKey = "";
    String uniqueContactId = "";
    @BindView(R.id.button_view_old_records)
    Button buttonViewOldRecords;
    @BindView(R.id.ripple_view_old_records)
    RippleView rippleViewOldRecords;
    @BindView(R.id.progressBarLoadCallLogs)
    ProgressBar progressBarLoadCallLogs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_history_details);
        rContactApplication = (RContactApplication) getApplicationContext();
        ButterKnife.bind(this);

        phoneBookContacts = new PhoneBookContacts(this);
        Intent intent = getIntent();

        if (intent != null) {

            if (intent.hasExtra(AppConstants.EXTRA_CALL_HISTORY_NUMBER)) {
                historyNumber = intent.getStringExtra(AppConstants.EXTRA_CALL_HISTORY_NUMBER);
            }

            if (intent.hasExtra(AppConstants.EXTRA_CALL_HISTORY_NAME)) {
                historyName = intent.getStringExtra(AppConstants.EXTRA_CALL_HISTORY_NAME);
            }

            if (intent.hasExtra(AppConstants.EXTRA_CALL_HISTORY_DATE)) {
                historyDate = intent.getLongExtra(AppConstants.EXTRA_CALL_HISTORY_DATE, 0);
            }

            if (intent.hasExtra(AppConstants.EXTRA_CALL_UNIQUE_ID)) {
                hashMapKey = intent.getStringExtra(AppConstants.EXTRA_CALL_UNIQUE_ID);
            }

            if (intent.hasExtra(AppConstants.EXTRA_UNIQUE_CONTACT_ID)) {
                uniqueContactId = intent.getStringExtra(AppConstants.EXTRA_UNIQUE_CONTACT_ID);
            }

            if (intent.hasExtra(AppConstants.EXTRA_CONTACT_PROFILE_IMAGE)) {
                profileThumbnail = intent.getStringExtra(AppConstants.EXTRA_CONTACT_PROFILE_IMAGE);
            }

            if (intent.hasExtra(AppConstants.EXTRA_PM_ID)) {
                pmId = intent.getStringExtra(AppConstants.EXTRA_PM_ID);
                if (!pmId.equalsIgnoreCase("-1") && !pmId.equalsIgnoreCase(getUserPmId())) {
                    if (Utils.isNetworkAvailable(this)) {
                        ArrayList<ProfileVisit> profileVisits = new ArrayList<>();
                        ProfileVisit profileVisit = new ProfileVisit();
                        profileVisit.setVisitorPmId(Integer.parseInt(pmId));
                        profileVisit.setVisitCount(1);
                        profileVisits.add(profileVisit);
                        profileVisit(profileVisits);
                    } else {
                        HashMap<String, String> mapProfileViews = new HashMap<>();
                        if (Utils.getHashMapPreference(this, AppConstants
                                .PREF_PROFILE_VIEWS) != null) {
                            mapProfileViews.putAll(Utils.getHashMapPreference(this, AppConstants
                                    .PREF_PROFILE_VIEWS));
                        }
                        if (mapProfileViews.containsKey(pmId)) {
                            int count = Integer.parseInt(mapProfileViews.get(pmId));
                            mapProfileViews.put(pmId, String.valueOf(++count));
                        } else {
                            mapProfileViews.put(pmId, "1");
                        }
                        Utils.setHashMapPreference(this, AppConstants.PREF_PROFILE_VIEWS,
                                mapProfileViews);
                    }
                }
            } else {
                pmId = "-1";
            }

            if (intent.hasExtra(AppConstants.EXTRA_PHONE_BOOK_ID)) {
                phoneBookId = intent.getStringExtra(AppConstants.EXTRA_PHONE_BOOK_ID);
            } else {
                phoneBookId = "-1";
            }

            if (intent.hasExtra(AppConstants.EXTRA_CONTACT_NAME)) {
                contactName = intent.getStringExtra(AppConstants.EXTRA_CONTACT_NAME);
            } else {
                contactName = "";
            }

            if (intent.hasExtra(AppConstants.EXTRA_CLOUD_CONTACT_NAME)) {
                cloudContactName = intent.getStringExtra(AppConstants.EXTRA_CLOUD_CONTACT_NAME);
               /* if(!TextUtils.isEmpty(cloudContactName)){
                    cloudContactName = StringUtils.substring(cloudContactName, 2, cloudContactName
                            .length() - 1);
                }*/
            }

            if (intent.hasExtra(AppConstants.EXTRA_CHECK_NUMBER_FAVOURITE)) {
//                isHideFavourite = true;
                checkNumberFavourite = intent.getStringExtra(AppConstants
                        .EXTRA_CHECK_NUMBER_FAVOURITE);
            }

            if (intent.hasExtra(AppConstants.EXTRA_CONTACT_POSITION)) {
                listClickedPosition = intent.getIntExtra(AppConstants.EXTRA_CONTACT_POSITION, -1);
            }
        }

        if (pmId.equalsIgnoreCase(getUserPmId())) {
            displayOwnProfile = true;
        }

        arrayListFavouriteContacts = new ArrayList<>();
        if (!Utils.isArraylistNullOrEmpty(Utils.getArrayListPreference(this, AppConstants
                .PREF_FAVOURITE_CONTACT_NUMBER_EMAIL))) {
            arrayListFavouriteContacts.addAll(Utils.getArrayListPreference(this, AppConstants
                    .PREF_FAVOURITE_CONTACT_NUMBER_EMAIL));
        }

        init();

        if (!TextUtils.isEmpty(contactName) && !contactName.equalsIgnoreCase("[Unknown]")) {
            fetchAllCallLogHistory(contactName);
        } else {
            if (!TextUtils.isEmpty(profileContactNumber)) {
                fetchAllCallLogHistory(profileContactNumber);
            }
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);
        IntentFilter intentFilter = new IntentFilter(AppConstants
                .ACTION_LOCAL_BROADCAST_CALL_HISTORY_ACTIVITY);
        localBroadcastManager.registerReceiver(localBroadcastReceiver, intentFilter);

        if (!TextUtils.isEmpty(contactName) && !contactName.equalsIgnoreCase(getString(R.string
                .unknown))) {
            fetchAllCallLogHistory(contactName);
        } else {
            if (!TextUtils.isEmpty(profileContactNumber)) {
                fetchAllCallLogHistory(profileContactNumber);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);
        localBroadcastManager.unregisterReceiver(localBroadcastReceiver);
    }

    private BroadcastReceiver localBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i("Profile Activity ", "onReceive() of LocalBroadcast");

            arrayListHistory.clear();
            recyclerCallHistory.setVisibility(View.GONE);
            setHistoryAdapter();

        }
    };

    @Override
    public void onDeliveryResponse(String serviceType, Object data, Exception error) {

        if (error == null) {

            // <editor-fold desc="REQ_MARK_AS_FAVOURITE">
            if (serviceType.equalsIgnoreCase(WsConstants.REQ_MARK_AS_FAVOURITE)) {
                WsResponseObject favouriteStatusResponse = (WsResponseObject) data;
                Utils.hideProgressDialog();
                if (favouriteStatusResponse == null || !StringUtils.equalsIgnoreCase
                        (favouriteStatusResponse.getStatus(), WsConstants.RESPONSE_STATUS_TRUE)) {
                    if (favouriteStatusResponse != null) {
                        Log.e("error response", favouriteStatusResponse.getMessage());
                    } else {
                        Log.e("onDeliveryResponse: ", "otpDetailResponse null");
                        Utils.showErrorSnackBar(this, relativeRootProfileDetail, getString(R
                                .string.msg_try_later));
                    }
                }
            }
            //</editor-fold>

            // <editor-fold desc="REQ_PROFILE_RATING">
            if (serviceType.equalsIgnoreCase(WsConstants.REQ_PROFILE_RATING)) {
                WsResponseObject profileRatingResponse = (WsResponseObject) data;
                if (profileRatingResponse != null && StringUtils.equalsIgnoreCase
                        (profileRatingResponse.getStatus(), WsConstants.RESPONSE_STATUS_TRUE)) {

                    Utils.showSuccessSnackBar(this, relativeRootProfileDetail, getString(R.string
                            .rating_submit));

                    if (profileRatingResponse.getProfileRating() != null) {

                        Rating responseRating = profileRatingResponse.getProfileRating();

                        /*DbRating dbRating = new DbRating();

                        dbRating.setRcProfileMasterPmId(String.valueOf(responseRating.getPrToPmId
                                ()));
                        dbRating.setCrmStatus(String.valueOf(responseRating.getPrStatus()));
                        dbRating.setCrmRating(responseRating.getPrRatingStars());
                        dbRating.setCrmCloudPrId(String.valueOf(responseRating.getPrId()));
                        dbRating.setCrmComment(responseRating.getPrComment());
                        dbRating.setCrmCreatedAt(responseRating.getCreatedAt());

                        TableContactRatingMaster tableContactRatingMaster = new
                                TableContactRatingMaster(databaseHandler);
                        tableContactRatingMaster.addRating(dbRating);*/

                        Comment comment = new Comment();
                        comment.setRcProfileMasterPmId(responseRating.getPrToPmId());
                        comment.setCrmStatus(responseRating.getPrStatus());
                        comment.setCrmRating(responseRating.getPrRatingStars());
                        comment.setCrmType(TableCommentMaster.COMMENT_TYPE_RATING);
                        comment.setCrmCloudPrId(String.valueOf(responseRating.getPrId()));
                        comment.setCrmComment(responseRating.getPrComment());
                        comment.setCrmCreatedAt(responseRating.getCreatedAt());
                        comment.setCrmUpdatedAt(responseRating.getCreatedAt());

                        TableCommentMaster tableCommentMaster = new TableCommentMaster
                                (databaseHandler);
                        tableCommentMaster.addComment(comment);

                        TableProfileMaster tableProfileMaster = new TableProfileMaster
                                (databaseHandler);
                        tableProfileMaster.updateUserProfileRating(pmId, responseRating
                                .getProfileRating(), responseRating.getTotalProfileRateUser());

                        ratingUser.setRating(Float.parseFloat(responseRating.getProfileRating()));
                        textUserRating.setText(responseRating.getTotalProfileRateUser());
                    }
                } else {
                    if (profileRatingResponse != null) {
                        Log.e("error response", profileRatingResponse.getMessage());
                    } else {
                        Log.e("onDeliveryResponse: ", "otpDetailResponse null");
                        Utils.showErrorSnackBar(this, relativeRootProfileDetail, getString(R
                                .string.msg_try_later));
                    }
                }
            }
            //</editor-fold>

            if (serviceType.equalsIgnoreCase(WsConstants.REQ_GET_CALL_LOG_HISTORY_REQUEST)) {
                WsResponseObject callHistoryResponse = (WsResponseObject) data;
                progressBarLoadCallLogs.setVisibility(View.GONE);
                if (callHistoryResponse != null && StringUtils.equalsIgnoreCase
                        (callHistoryResponse.getStatus(), WsConstants.RESPONSE_STATUS_TRUE)) {

                    ArrayList<CallLogType> oldHistoryList = callHistoryResponse
                            .getArrayListCallLogHistory();
                    if (oldHistoryList != null && oldHistoryList.size() > 0) {
                        rippleViewOldRecords.setVisibility(View.VISIBLE);
                        arrayListHistory.addAll(oldHistoryList);
                        if (callHistoryListAdapter != null) {
                            callHistoryListAdapter.notifyDataSetChanged();
                        }
                    } else {
                        rippleViewOldRecords.setVisibility(View.GONE);
                        Utils.showSuccessSnackBar(this, relativeRootProfileDetail,
                                callHistoryResponse.getMessage());
                    }

                } else {
                    progressBarLoadCallLogs.setVisibility(View.GONE);
                    if (callHistoryResponse != null) {
                        Log.e("error response", callHistoryResponse.getMessage());
                    } else {
                        Log.e("onDeliveryResponse: ", "otpDetailResponse null");
                        Utils.showErrorSnackBar(this, relativeRootProfileDetail, getString(R
                                .string.msg_try_later));
                    }
                }
            }


        } else {
            Utils.showErrorSnackBar(this, relativeRootProfileDetail, "" + error
                    .getLocalizedMessage());
        }

    }

    @Override
    public void onComplete(RippleView rippleView) {

        switch (rippleView.getId()) {

            case R.id.ripple_action_back:
                onBackPressed();
                break;

            case R.id.ripple_view_old_records:
                progressBarLoadCallLogs.setVisibility(View.VISIBLE);
                rippleViewOldRecords.setVisibility(View.GONE);
                getOldCallHistory();
                break;

            case R.id.ripple_sms:

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission
                            .READ_SMS) != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(new String[]{Manifest.permission.READ_SMS},
                                AppConstants.MY_PERMISSIONS_REQUEST_READ_CONTACTS);
                    } else {

                        if (tempPhoneNumber != null && tempPhoneNumber.size() > 1) {
                            if (tempPhoneNumber != null && tempPhoneNumber.size() > 0) {
                                int count = tempPhoneNumber.size();
                                ArrayList<String> listPhoneNumber = new ArrayList<>();
                                if (count > 1) {
                                    for (int i = 0; i < tempPhoneNumber.size(); i++) {
                                        ProfileDataOperationPhoneNumber phoneNumber =
                                                (ProfileDataOperationPhoneNumber) tempPhoneNumber
                                                        .get(i);
                                        String number = phoneNumber.getPhoneNumber();
                                        listPhoneNumber.add(number);
                                    }

                                    CallConfirmationListDialog callConfirmationListDialog = new
                                            CallConfirmationListDialog(this, listPhoneNumber,
                                            false);
                                    callConfirmationListDialog.setDialogTitle(getString(R.string
                                            .please_select_number_view_sms_log));
                                    callConfirmationListDialog.showDialog();

                                } else {
                                    showCallConfirmationDialog(profileContactNumber);
                                }
                            }
                        } else {
                            if (tempPhoneNumber != null && tempPhoneNumber.size() == 1) {
                                ProfileDataOperationPhoneNumber phoneNumber =
                                        (ProfileDataOperationPhoneNumber) tempPhoneNumber.get(0);
                                if (phoneNumber != null) {
                                    historyNumber = phoneNumber.getPhoneNumber();
                                }
                            }
                            if (!TextUtils.isEmpty(historyNumber)) {
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.fromParts
                                        ("sms", historyNumber, null));
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                    intent.setPackage(Telephony.Sms.getDefaultSmsPackage(this));
                                }
                                intent.putExtra("finishActivityOnSaveCompleted", true);
                                startActivity(intent);
                            }
                        }

                    }
                } else {
                    if (tempPhoneNumber != null && tempPhoneNumber.size() > 1) {
                        if (tempPhoneNumber != null && tempPhoneNumber.size() > 0) {
                            int count = tempPhoneNumber.size();
                            ArrayList<String> listPhoneNumber = new ArrayList<>();
                            if (count > 1) {
                                for (int i = 0; i < tempPhoneNumber.size(); i++) {
                                    ProfileDataOperationPhoneNumber phoneNumber =
                                            (ProfileDataOperationPhoneNumber) tempPhoneNumber.get
                                                    (i);
                                    String number = phoneNumber.getPhoneNumber();
                                    listPhoneNumber.add(number);
                                }

                                CallConfirmationListDialog callConfirmationListDialog = new
                                        CallConfirmationListDialog(this, listPhoneNumber, false);
                                callConfirmationListDialog.setDialogTitle(getString(R.string
                                        .please_select_number_view_sms_log));
                                callConfirmationListDialog.showDialog();

                            } else {
                                showCallConfirmationDialog(profileContactNumber);
                            }
                        }
                    } else {
                        if (tempPhoneNumber != null && tempPhoneNumber.size() == 1) {
                            ProfileDataOperationPhoneNumber phoneNumber =
                                    (ProfileDataOperationPhoneNumber) tempPhoneNumber.get(0);
                            if (phoneNumber != null) {
                                historyNumber = phoneNumber.getPhoneNumber();
                            }
                        }
                        if (!TextUtils.isEmpty(historyNumber)) {
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms",
                                    historyNumber, null));
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                intent.setPackage(Telephony.Sms.getDefaultSmsPackage(this));
                            }
                            intent.putExtra("finishActivityOnSaveCompleted", true);
                            startActivity(intent);
                        }

                    }
                }
                break;

            case R.id.ripple_action_right_center:

                if (StringUtils.equals(imageRightCenter.getTag().toString(), TAG_IMAGE_CALL)) {
                    if (tempPhoneNumber != null && tempPhoneNumber.size() > 0) {
                        int count = tempPhoneNumber.size();
                        ArrayList<String> listPhoneNumber = new ArrayList<>();
                        if (count > 1) {
                            for (int i = 0; i < tempPhoneNumber.size(); i++) {
                                ProfileDataOperationPhoneNumber phoneNumber =
                                        (ProfileDataOperationPhoneNumber) tempPhoneNumber.get(i);
                                String number = phoneNumber.getPhoneNumber();
                                listPhoneNumber.add(number);
                            }

                            CallConfirmationListDialog callConfirmationListDialog = new
                                    CallConfirmationListDialog(this, listPhoneNumber, true);
                            callConfirmationListDialog.setDialogTitle(getString(R.string
                                    .please_select_number_call));
                            callConfirmationListDialog.showDialog();

                        } else {
                            showCallConfirmationDialog(profileContactNumber);
                        }
                    }
                }
                break;

            case R.id.ripple_action_right_left:

                if (StringUtils.equals(imageRightLeft.getTag().toString(), TAG_IMAGE_FAVOURITE)
                        || StringUtils.equals(imageRightLeft.getTag().toString(),
                        TAG_IMAGE_UN_FAVOURITE)) {
                    int favStatus;
                    if (StringUtils.equals(imageRightLeft.getTag().toString(),
                            TAG_IMAGE_FAVOURITE)) {
                        favStatus = PhoneBookContacts.STATUS_UN_FAVOURITE;
                        imageRightLeft.setImageResource(R.drawable.ic_action_favorite_border);
                        imageRightLeft.setTag(TAG_IMAGE_UN_FAVOURITE);
                    } else {
                        favStatus = PhoneBookContacts.STATUS_FAVOURITE;
                        imageRightLeft.setImageResource(R.drawable.ic_action_favorite_fill);
                        imageRightLeft.setTag(TAG_IMAGE_FAVOURITE);
                    }

                    int updateStatus = phoneBookContacts.setFavouriteStatus(phoneBookId, favStatus);
                    if (updateStatus != 1) {
                        Utils.showErrorSnackBar(this, relativeRootProfileDetail, getString(R
                                .string.error_update_favorite_status));
                    }

                    ArrayList<ProfileData> arrayListFavourites = new ArrayList<>();
                    ProfileData favouriteStatus = new ProfileData();
                    favouriteStatus.setLocalPhoneBookId(phoneBookId);
                    favouriteStatus.setIsFavourite(String.valueOf(favStatus));
                    arrayListFavourites.add(favouriteStatus);
                    setFavouriteStatus(arrayListFavourites);
//                    rContactApplication.setFavouriteModified(true);
                    if (favStatus == PhoneBookContacts.STATUS_FAVOURITE) {
                        rContactApplication.setFavouriteStatus(RContactApplication.FAVOURITE_ADDED);
                    } else {
                        rContactApplication.setFavouriteStatus(RContactApplication
                                .FAVOURITE_REMOVED);
                    }
                }
                break;

            case R.id.ripple_action_right_right:

                ProfileMenuOptionDialog profileMenuOptionDialog;
                boolean isFromCallLogTab = false;
                String blockedNumber = "";
                ArrayList<CallLogType> callLogTypeList = new ArrayList<>();
                HashMap<String, ArrayList<CallLogType>> blockProfileHashMapList =
                        Utils.getHashMapPreferenceForBlock(this, AppConstants
                                .PREF_BLOCK_CONTACT_LIST);

                if (blockProfileHashMapList != null && blockProfileHashMapList.size() > 0) {
                    if (blockProfileHashMapList.containsKey(hashMapKey))
                        callLogTypeList.addAll(blockProfileHashMapList.get(hashMapKey));

                }
                String name = "";
                if (!TextUtils.isEmpty(contactName)) {
                    ArrayList<CallLogType> callLogTypes = getNumbersFromName(contactName);
                    if (callLogTypes != null && callLogTypes.size() > 0) {
                        for (int i = 0; i < callLogTypes.size(); i++) {
                            CallLogType callLogType = callLogTypes.get(i);
                            name = callLogType.getName();
                        }
                    }

                }

                if (callLogTypeList != null) {
                    for (int j = 0; j < callLogTypeList.size(); j++) {
                        Log.i("value", callLogTypeList.get(j) + "");
                        String tempNumber = callLogTypeList.get(j).getName();
                        if (tempNumber.equalsIgnoreCase(name)) {
                            blockedNumber = tempNumber;
                        }
                    }
                }

                if (!TextUtils.isEmpty(blockedNumber)) {
                    if (!TextUtils.isEmpty(contactName)) {
                        ArrayList<String> arrayListName = new ArrayList<>(Arrays.asList(this
                                        .getString(R.string.edit),
                                /*this.getString(R.string.view_in_ac), this.getString(R.string
                                .view_in_rc),
                                this.getString(R.string.call_reminder),
                                this.getString(R.string.unblock),*/ this.getString(R.string.delete),
                                this.getString(R.string.clear_call_log)));
                        profileMenuOptionDialog = new ProfileMenuOptionDialog(this,
                                arrayListName, contactName, 0, isFromCallLogTab,
                                arrayListHistory, contactName, "", hashMapKey, profileThumbnail);
                        profileMenuOptionDialog.showDialog();

                    } else {
                        if (!TextUtils.isEmpty(profileContactNumber)) {
                            ArrayList<String> arrayListNumber = new ArrayList<>(Arrays.asList
                                    (this.getString(R.string.add_to_contact),
                                            this.getString(R.string.add_to_existing_contact), this
                                                    .getString(R.string.view_profile),
                                            this.getString(R.string.copy_phone_number),
                                    /*this.getString(R.string.call_reminder), this.getString(R
                                    .string.unblock),*/
                                            this.getString(R.string.delete), this.getString(R.string
                                                    .clear_call_log)));
                            profileMenuOptionDialog = new ProfileMenuOptionDialog(this,
                                    arrayListNumber, profileContactNumber, 0, isFromCallLogTab,
                                    arrayListHistory, "", uniqueContactId, hashMapKey,
                                    profileThumbnail);
                            profileMenuOptionDialog.showDialog();
                        }
                    }
                } else {
                    if (!TextUtils.isEmpty(contactName) /*&& !contactName.equalsIgnoreCase
                    ("[Unknown]")*/) {
                        ArrayList<String> arrayListName = new ArrayList<>(Arrays.asList(this
                                        .getString(R.string.edit), this.getString(R.string
                                        .view_in_ac),
                                /*this.getString(R.string.view_in_ac), this.getString(R.string
                                .view_in_rc),
                                this.getString(R.string.call_reminder),
                                this.getString(R.string.block),*/ this.getString(R.string.delete),
                                this.getString(R.string.clear_call_log)));
                        profileMenuOptionDialog = new ProfileMenuOptionDialog(this,
                                arrayListName, contactName, 0, isFromCallLogTab,
                                arrayListHistory, contactName, "", phoneBookId, profileThumbnail);
                        profileMenuOptionDialog.showDialog();

                    } else {
                        if (!TextUtils.isEmpty(profileContactNumber)) {
                            ArrayList<String> arrayListNumber = new ArrayList<>(Arrays.asList
                                    (this.getString(R.string.add_to_contact),
                                            this.getString(R.string.add_to_existing_contact), this
                                                    .getString(R.string.view_profile),
                                            this.getString(R.string.copy_phone_number),
                                    /*this.getString(R.string.call_reminder), this.getString(R
                                    .string.block),*/
                                            this.getString(R.string.delete), this.getString(R.string
                                                    .clear_call_log)));
                            profileMenuOptionDialog = new ProfileMenuOptionDialog(this,
                                    arrayListNumber, profileContactNumber, 0, isFromCallLogTab,
                                    arrayListHistory, "", uniqueContactId, "", profileThumbnail);
                            profileMenuOptionDialog.showDialog();
                        }
                    }

                }
                break;

        }

    }


    private void getOldCallHistory() {
        ArrayList<CallLogHistoryType> arrayListToSend = new ArrayList<>();
//        ArrayList<CallLogHistoryType> temp = new ArrayList<>();
        if (arrayListHistory != null && arrayListHistory.size() > 0) {
            for (int i = arrayListHistory.size() - 1; i >= 0; i--) {
                CallLogType callLogType = arrayListHistory.get(i);
                String number = callLogType.getHistoryNumber();
                String formattedNumber = Utils.getFormattedNumber(this, number);
                long date = callLogType.getHistoryDate();
                CallLogHistoryType callLogHistoryType = new CallLogHistoryType();
                callLogHistoryType.setHistoryNumber(formattedNumber);
                callLogHistoryType.setHistoryDate(date);
                if (arrayListToSend.size() == 0) {
                    arrayListToSend.add(callLogHistoryType);
                } else {
                    boolean isNumberExists = false;
                    for (int j = 0; j < arrayListToSend.size(); j++) {
                        if ((arrayListToSend.get(j)).getHistoryNumber().equalsIgnoreCase
                                (callLogHistoryType.getHistoryNumber())) {
                            isNumberExists = true;
                            break;
                        } else {
                            isNumberExists = false;
                        }
                    }
                    if (!isNumberExists) {
                        arrayListToSend.add(callLogHistoryType);
                    }
                }
            }
            fetchOldRecordsServiceCall(arrayListToSend);

        } else {
            rippleViewOldRecords.setVisibility(View.GONE);
        }
    }

    private void fetchOldRecordsServiceCall(ArrayList<CallLogHistoryType> callLogTypeArrayList) {


        WsRequestObject deviceDetailObject = new WsRequestObject();
        deviceDetailObject.setHistoryTypeArrayList(callLogTypeArrayList);
        if (Utils.isNetworkAvailable(this)) {
            new AsyncWebServiceCall(this, WSRequestType.REQUEST_TYPE_JSON.getValue(),
                    deviceDetailObject, null, WsResponseObject.class, WsConstants
                    .REQ_GET_CALL_LOG_HISTORY_REQUEST, null, true).execute
                    (WsConstants.WS_ROOT + WsConstants.REQ_GET_CALL_LOG_HISTORY_REQUEST);
        } else {
            Utils.showErrorSnackBar(this, relativeRootProfileDetail, getResources()
                    .getString(R.string.msg_no_network));
        }

    }

    //    @TargetApi(Build.VERSION_CODES.M)
    private ArrayList<CallLogType> getNumbersFromName(String number) {
        Cursor cursor = null;
        ArrayList<CallLogType> listNumber = new ArrayList<>();
        try {
            final Uri Person = Uri.withAppendedPath(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_FILTER_URI,
                    Uri.encode(number));

            cursor = this.getContentResolver().query(Person, null,
                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " =?", new
                            String[]{number}, null);

            if (cursor != null && cursor.getCount() > 0) {
                int number1 = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                while (cursor.moveToNext()) {
                    CallLogType callLogType = new CallLogType();
                    String profileNumber = cursor.getString(number1);
                    String formattedNumber = Utils.getFormattedNumber(this, profileNumber);
                    String uniqueContactId = getStarredStatusFromNumber(profileNumber);
                    callLogType.setUniqueContactId(uniqueContactId);
                    callLogType.setName(number);
                    callLogType.setNumber(formattedNumber);
                    listNumber.add(callLogType);
                }
            }
            cursor.close();


        } catch (SecurityException e) {
            e.printStackTrace();
        }
        return listNumber;
    }

    private void init() {
        rippleActionBack = ButterKnife.findById(includeToolbar, R.id.ripple_action_back);
        textToolbarTitle = ButterKnife.findById(includeToolbar, R.id.text_toolbar_title);
        imageRightLeft = ButterKnife.findById(includeToolbar, R.id.image_right_left);
        imageRightCenter = ButterKnife.findById(includeToolbar, R.id.image_right_center);
        rippleActionRightLeft = ButterKnife.findById(includeToolbar,
                R.id.ripple_action_right_left);
        rippleActionRightCenter = ButterKnife.findById(includeToolbar, R.id
                .ripple_action_right_center);
        rippleActionRightRight = ButterKnife.findById(includeToolbar, R.id
                .ripple_action_right_right);

        textToolbarTitle.setTypeface(Utils.typefaceSemiBold(this));
        textFullScreenText.setTypeface(Utils.typefaceSemiBold(this));
        textName.setTypeface(Utils.typefaceSemiBold(this));
        textDesignation.setTypeface(Utils.typefaceRegular(this));
        textOrganization.setTypeface(Utils.typefaceRegular(this));
        textViewAllOrganization.setTypeface(Utils.typefaceRegular(this));
        textUserRating.setTypeface(Utils.typefaceRegular(this));
        textFullScreenText.setSelected(true);
        rippleActionBack.setOnRippleCompleteListener(this);
        rippleActionRightLeft.setOnRippleCompleteListener(this);
        rippleActionRightCenter.setOnRippleCompleteListener(this);
        rippleActionRightRight.setOnRippleCompleteListener(this);

        buttonViewOldRecords.setTypeface(Utils.typefaceRegular(this));
        rippleViewOldRecords.setVisibility(View.VISIBLE);
        rippleViewOldRecords.setOnRippleCompleteListener(this);
        rippleSms.setOnRippleCompleteListener(this);


        LayerDrawable stars = (LayerDrawable) ratingUser.getProgressDrawable();
        // Filled stars
        Utils.setRatingStarColor(stars.getDrawable(2), ContextCompat.getColor(this, R.color
                .vivid_yellow));
        // half stars
        Utils.setRatingStarColor(stars.getDrawable(1), ContextCompat.getColor(this, android.R
                .color.darker_gray));
        // Empty stars
        Utils.setRatingStarColor(stars.getDrawable(0), ContextCompat.getColor(this, android.R
                .color.darker_gray));

        if (!StringUtils.equalsIgnoreCase(pmId, "-1")) {
            // RC Profile
//            getProfileDetail();
            if (displayOwnProfile) {
                ProfileDataOperation profileDataOperation = (ProfileDataOperation) Utils
                        .getObjectPreference(this, AppConstants.PREF_REGS_USER_OBJECT,
                                ProfileDataOperation.class);
                setUpView(profileDataOperation);
            } else {
//                TableProfileMaster tableProfileMaster = new TableProfileMaster(databaseHandler);
                QueryManager queryManager = new QueryManager(databaseHandler);
                ProfileDataOperation profileDataOperation = queryManager.getRcProfileDetail
                        (this, pmId);
                setUpView(profileDataOperation);
            }
        } else {
            // Non-RC Profile
//            textJoiningDate.setVisibility(View.GONE);
            setUpView(null);
        }

        layoutVisibility();
    }

    @OnClick(R.id.linear_basic_detail_rating)
    public void onRatingClick() {

        if (!StringUtils.equalsIgnoreCase(pmId, "-1") && !displayOwnProfile) {

            final Dialog dialog = new Dialog(this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.dialog_submit_rating);
            dialog.setCancelable(false);

            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.copyFrom(dialog.getWindow().getAttributes());
            layoutParams.width = (int) (getResources().getDisplayMetrics().widthPixels * 0.90);
            layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

            dialog.getWindow().setLayout(layoutParams.width, layoutParams.height);

            TextView textDialogTitle = (TextView) dialog.findViewById(R.id.text_dialog_title);
            relativeRootRatingDialog = (RelativeLayout) dialog.findViewById(R.id
                    .relative_root_rating_dialog);
            final RatingBar ratingUser = (RatingBar) dialog.findViewById(R.id.rating_user);
            TextView textComment = (TextView) dialog.findViewById(R.id.text_comment);
            final TextView textRemainingCharacters = (TextView) dialog.findViewById(R.id
                    .text_remaining_characters);
            final EditText inputComment = (EditText) dialog.findViewById(R.id.input_comment);
            RippleView rippleLeft = (RippleView) dialog.findViewById(R.id.ripple_left);
            Button buttonLeft = (Button) dialog.findViewById(R.id.button_left);
            RippleView rippleRight = (RippleView) dialog.findViewById(R.id.ripple_right);
            Button buttonRight = (Button) dialog.findViewById(R.id.button_right);

            textDialogTitle.setTypeface(Utils.typefaceSemiBold(this));
            textComment.setTypeface(Utils.typefaceRegular(this));
            textRemainingCharacters.setTypeface(Utils.typefaceLight(this));
            inputComment.setTypeface(Utils.typefaceRegular(this));
            buttonLeft.setTypeface(Utils.typefaceSemiBold(this));
            buttonRight.setTypeface(Utils.typefaceSemiBold(this));

            textDialogTitle.setText(String.format("%s%s", this.getString(R.string.text_rate),
                    contactName));
            textRemainingCharacters.setText(String.format(Locale.getDefault(), "%d%s",
                    getResources().getInteger(R.integer
                            .max_comment_length), this.getString(R.string.characters_left)));

            buttonRight.setText(R.string.action_submit);
            buttonLeft.setText(R.string.action_cancel);

            rippleLeft.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
                @Override
                public void onComplete(RippleView rippleView) {
                    dialog.dismiss();
                }
            });

            rippleRight.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
                @Override
                public void onComplete(RippleView rippleView) {
                    if (ratingUser.getRating() > 0) {
                        submitRating(String.valueOf(ratingUser.getRating()), inputComment.getText
                                ().toString());
                        dialog.dismiss();
                    } else {
                        Utils.showErrorSnackBar(CallHistoryDetailsActivity.this,
                                relativeRootRatingDialog, getString(R.string.please_fill_stars));
                    }
                }
            });

            inputComment.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    int characters = getResources().getInteger(R.integer.max_comment_length) -
                            charSequence.toString().length();
                    textRemainingCharacters.setText(String.format(Locale.getDefault(), "%d%s",
                            characters, characters == 1
                                    ? getString(R.string.text_character) : getString(R.string
                                    .characters_left)));
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });

            dialog.show();

        }
    }

    private void layoutVisibility() {

        textFullScreenText.setText(contactName);
        if (StringUtils.length(cloudContactName) > 0) {
            textFullScreenText.setTextColor(ContextCompat.getColor(this, R.color.colorBlack));
            textName.setText(cloudContactName);

        } else {
            if (StringUtils.equalsIgnoreCase(pmId, "-1")) {
                textFullScreenText.setTextColor(ContextCompat.getColor(this, R.color
                        .colorBlack));
            } else {
                textFullScreenText.setTextColor(ContextCompat.getColor(this, R.color
                        .colorAccent));
            }
            textName.setVisibility(View.GONE);
        }

        profileThumbnail = getPhotoUrlFromNumber();
        if (!TextUtils.isEmpty(profileThumbnail)) {
            Glide.with(this)
                    .load(profileThumbnail)
                    .placeholder(R.drawable.home_screen_profile)
                    .error(R.drawable.home_screen_profile)
                    .bitmapTransform(new CropCircleTransformation(CallHistoryDetailsActivity.this))
                    .override(200, 200)
                    .into(imageProfile);
        } else {
            imageProfile.setImageResource(R.drawable.home_screen_profile);
        }

       /* if (displayOwnProfile) {
            textToolbarTitle.setText(getString(R.string.title_my_profile));
            linearCallSms.setVisibility(View.GONE);
            imageRightLeft.setImageResource(R.drawable.ic_action_edit);
        } else {
            textToolbarTitle.setText("Profile Detail");
            linearCallSms.setVisibility(View.VISIBLE);
        }*/

        setCallLogHistoryDetails();

        if (isHideFavourite) {
//            rippleActionRightLeft.setEnabled(false);
            if (checkNumberFavourite != null && arrayListFavouriteContacts.contains
                    (checkNumberFavourite)) {
                imageRightLeft.setImageResource(R.drawable.ic_action_favorite_fill);
            } else {
                imageRightLeft.setImageResource(R.drawable.ic_action_favorite_border);
            }
        }

    }


    private String getPhotoUrlFromNumber() {
        String phoneNumber = "";
        if (!TextUtils.isEmpty(contactName) && !contactName.equalsIgnoreCase(getString(R.string
                .unknown))) {
            ArrayList<CallLogType> listOfNumbers = getNumbersFromName(contactName);
            if (listOfNumbers != null && listOfNumbers.size() > 0) {
                for (int i = 0; i < listOfNumbers.size(); i++) {
                    CallLogType callLogType = listOfNumbers.get(i);
                    phoneNumber = callLogType.getNumber();
                    break;
                }
            }
        }

        String photoThumbUrl = "";
        try {

            photoThumbUrl = "";
            ContentResolver contentResolver = getContentResolver();

            Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri
                    .encode(phoneNumber));

            String[] projection = new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME,
                    ContactsContract.PhoneLookup.PHOTO_THUMBNAIL_URI};
            Cursor cursor =
                    contentResolver.query(uri, projection, null, null, null);

            if (cursor != null) {
                while (cursor.moveToNext()) {
                    String contactName = cursor.getString(cursor.getColumnIndexOrThrow
                            (ContactsContract.PhoneLookup.DISPLAY_NAME));
                    photoThumbUrl = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract
                            .PhoneLookup.PHOTO_THUMBNAIL_URI));
//                Log.d("LocalPBId", "contactMatch id: " + numberId + " of " + contactName);
                }
                cursor.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


        return photoThumbUrl;
    }

    private void setCallLogHistoryDetails() {
        if (!TextUtils.isEmpty(contactName) /*&& !contactName.equalsIgnoreCase("[Unknown]")*/) {
//            textToolbarTitle.setText(contactName);
            //17/06/2017 : toolBarTitle text is changed for Call-logs as per Avijit Sir's suggestion
            textToolbarTitle.setText(getString(R.string.str_profile_deails));
        } else {
            if (!TextUtils.isEmpty(profileContactNumber)) {
//                textToolbarTitle.setText(profileContactNumber);
                //17/06/2017 : toolBarTitle text is changed for Call-logs as per Avijit Sir's
                // suggestion
                textToolbarTitle.setText(getString(R.string.str_profile_deails));
            }

        }
        imageRightCenter.setImageResource(R.drawable.ic_phone);
        imageRightCenter.setTag(TAG_IMAGE_CALL);

    }

    private void setUpView(final ProfileDataOperation profileDetail) {

        profileDataOperationVcard = new ProfileDataOperation();

        profileDataOperationVcard.setPbNameFirst(contactName);

        //<editor-fold desc="Favourite">

        if (!displayOwnProfile && !isHideFavourite) {

            int isFavourite = 0;
            Cursor contactFavouriteCursor = phoneBookContacts.getStarredStatus(phoneBookId);

            if (contactFavouriteCursor != null && contactFavouriteCursor.getCount() > 0) {
                while (contactFavouriteCursor.moveToNext()) {
                    isFavourite = contactFavouriteCursor.getInt(contactFavouriteCursor
                            .getColumnIndex(ContactsContract.Contacts.STARRED));
                }
                contactFavouriteCursor.close();
            }

            if (isFavourite == 0) {
                imageRightLeft.setImageResource(R.drawable.ic_action_favorite_border);
                imageRightLeft.setTag(TAG_IMAGE_UN_FAVOURITE);
            } else {
                imageRightLeft.setImageResource(R.drawable.ic_action_favorite_fill);
                imageRightLeft.setTag(TAG_IMAGE_FAVOURITE);
            }
        }

        //</editor-fold>

        //<editor-fold desc="Joining Date">
       /* if (profileDetail != null) {
            String joiningDate = StringUtils.defaultString(Utils.convertDateFormat(profileDetail
                    .getJoiningDate(), "yyyy-MM-dd HH:mm:ss", "dd'th' MMM, yyyy"), "-");
            textJoiningDate.setText("Joining Date:- " + joiningDate);
        }*/
        //</editor-fold>

        //<editor-fold desc="User Name">
        /*if (profileDetail != null) {
            textName.setText(profileDetail.getPbNameFirst() + " " + profileDetail.getPbNameLast());
        }*/
        //</editor-fold>

        //<editor-fold desc="Organization Detail">

        // From Cloud
        ArrayList<ProfileDataOperationOrganization> arrayListOrganization = new ArrayList<>();

        if (profileDetail != null && !Utils.isArraylistNullOrEmpty(profileDetail
                .getPbOrganization())) {
            arrayListOrganization.addAll(profileDetail.getPbOrganization());
        }

        // From PhoneBook
        Cursor contactOrganizationCursor = phoneBookContacts.getContactOrganization(phoneBookId);
        ArrayList<ProfileDataOperationOrganization> arrayListPhoneBookOrganization = new
                ArrayList<>();
        ArrayList<ProfileDataOperationOrganization> arrayListPhoneBookOrganizationOperation = new
                ArrayList<>();

        if (contactOrganizationCursor != null && contactOrganizationCursor.getCount() > 0) {
            while (contactOrganizationCursor.moveToNext()) {

                ProfileDataOperationOrganization organization = new
                        ProfileDataOperationOrganization();
                ProfileDataOperationOrganization organizationOperation = new
                        ProfileDataOperationOrganization();

                organization.setOrgName(contactOrganizationCursor.getString
                        (contactOrganizationCursor.getColumnIndex(ContactsContract
                                .CommonDataKinds.Organization.COMPANY)));
                organization.setOrgJobTitle(contactOrganizationCursor.getString
                        (contactOrganizationCursor.getColumnIndex(ContactsContract
                                .CommonDataKinds.Organization.TITLE)));
                organization.setOrgDepartment(contactOrganizationCursor.getString
                        (contactOrganizationCursor.getColumnIndex(ContactsContract
                                .CommonDataKinds.Organization.DEPARTMENT)));
                organization.setOrgType(phoneBookContacts.getOrganizationType
                        (contactOrganizationCursor,
                                contactOrganizationCursor.getInt((contactOrganizationCursor
                                        .getColumnIndex(ContactsContract.CommonDataKinds
                                                .Organization.TYPE)))));
                organization.setOrgJobDescription(contactOrganizationCursor.getString
                        (contactOrganizationCursor.getColumnIndex(ContactsContract
                                .CommonDataKinds.Organization.JOB_DESCRIPTION)));
                organization.setOrgOfficeLocation(contactOrganizationCursor.getString
                        (contactOrganizationCursor.getColumnIndex(ContactsContract
                                .CommonDataKinds.Organization.OFFICE_LOCATION)));
                organization.setOrgRcpType(String.valueOf(IntegerConstants
                        .RCP_TYPE_LOCAL_PHONE_BOOK));

                organizationOperation.setOrgName(contactOrganizationCursor.getString
                        (contactOrganizationCursor.getColumnIndex(ContactsContract
                                .CommonDataKinds.Organization.COMPANY)));
                organizationOperation.setOrgJobTitle(contactOrganizationCursor.getString
                        (contactOrganizationCursor.getColumnIndex(ContactsContract
                                .CommonDataKinds.Organization.TITLE)));
                organizationOperation.setOrgDepartment(contactOrganizationCursor.getString
                        (contactOrganizationCursor.getColumnIndex(ContactsContract
                                .CommonDataKinds.Organization.DEPARTMENT)));
                organizationOperation.setOrgType(phoneBookContacts.getOrganizationType
                        (contactOrganizationCursor,
                                contactOrganizationCursor.getInt((contactOrganizationCursor
                                        .getColumnIndex(ContactsContract.CommonDataKinds
                                                .Organization.TYPE)))));
                organizationOperation.setOrgJobDescription(contactOrganizationCursor.getString
                        (contactOrganizationCursor.getColumnIndex(ContactsContract
                                .CommonDataKinds.Organization.JOB_DESCRIPTION)));
                organizationOperation.setOrgOfficeLocation(contactOrganizationCursor.getString
                        (contactOrganizationCursor.getColumnIndex(ContactsContract
                                .CommonDataKinds.Organization.OFFICE_LOCATION)));


                if (!arrayListOrganization.contains(organization)) {
                    arrayListPhoneBookOrganization.add(organization);
                }
                arrayListPhoneBookOrganizationOperation.add(organizationOperation);
            }
            contactOrganizationCursor.close();
            profileDataOperationVcard.setPbOrganization(arrayListPhoneBookOrganizationOperation);
        }

        if (!Utils.isArraylistNullOrEmpty(arrayListOrganization) || !Utils.isArraylistNullOrEmpty
                (arrayListPhoneBookOrganization)) {

            final ArrayList<ProfileDataOperationOrganization> tempOrganization = new ArrayList<>();
            tempOrganization.addAll(arrayListOrganization);
            tempOrganization.addAll(arrayListPhoneBookOrganization);

            if (tempOrganization.size() == 1) {
                textViewAllOrganization.setVisibility(View.GONE);
            } else {
                textViewAllOrganization.setVisibility(View.VISIBLE);
            }
            textDesignation.setText(tempOrganization.get(0).getOrgJobTitle());
            textOrganization.setText(tempOrganization.get(0).getOrgName());

            textViewAllOrganization.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showAllOrganizations(tempOrganization);
                }
            });

        } else {
            linearOrganizationDetail.setVisibility(View.INVISIBLE);
        }
        //</editor-fold>

        //<editor-fold desc="User Rating">
        if (profileDetail != null) {
            textUserRating.setText(profileDetail.getTotalProfileRateUser());
            ratingUser.setRating(Float.parseFloat(profileDetail.getProfileRating()));
        } else {
            textUserRating.setText("0");
            ratingUser.setRating(0);
            ratingUser.setEnabled(false);
        }
        //</editor-fold>

        //<editor-fold desc="Phone Number">

        // From Cloud
        ArrayList<ProfileDataOperationPhoneNumber> arrayListPhoneNumber = new ArrayList<>();
        ArrayList<String> arrayListCloudNumber = new ArrayList<>();

        if (profileDetail != null && !Utils.isArraylistNullOrEmpty(profileDetail.getPbPhoneNumber
                ())) {
            arrayListPhoneNumber.addAll(profileDetail.getPbPhoneNumber());
            for (int i = 0; i < arrayListPhoneNumber.size(); i++) {
                String number = Utils.getFormattedNumber(this, arrayListPhoneNumber.get(i)
                        .getPhoneNumber());
                arrayListCloudNumber.add(number);
            }
        }

        // From PhoneBook
        Cursor contactNumberCursor = phoneBookContacts.getContactNumbers(phoneBookId);
        ArrayList<ProfileDataOperationPhoneNumber> arrayListPhoneBookNumber = new ArrayList<>();
        ArrayList<ProfileDataOperationPhoneNumber> arrayListPhoneBookNumberOperation = new
                ArrayList<>();

        if (contactNumberCursor != null && contactNumberCursor.getCount() > 0) {
            while (contactNumberCursor.moveToNext()) {

                ProfileDataOperationPhoneNumber phoneNumber = new
                        ProfileDataOperationPhoneNumber();
                ProfileDataOperationPhoneNumber phoneNumberOperation = new
                        ProfileDataOperationPhoneNumber();

                phoneNumber.setPhoneNumber(Utils.getFormattedNumber(this, contactNumberCursor
                        .getString(contactNumberCursor.getColumnIndex(ContactsContract
                                .CommonDataKinds.Phone.NUMBER))));
                phoneNumber.setPhoneType(phoneBookContacts.getPhoneNumberType
                        (contactNumberCursor.getInt(contactNumberCursor.getColumnIndex
                                (ContactsContract.CommonDataKinds.Phone.TYPE))));
                phoneNumberOperation.setPhoneNumber(Utils.getFormattedNumber(this,
                        contactNumberCursor.getString(contactNumberCursor.getColumnIndex
                                (ContactsContract.CommonDataKinds.Phone.NUMBER))));
                phoneNumberOperation.setPhoneType(phoneBookContacts.getPhoneNumberType
                        (contactNumberCursor.getInt(contactNumberCursor.getColumnIndex
                                (ContactsContract.CommonDataKinds.Phone.TYPE))));
                phoneNumber.setPbRcpType(String.valueOf(IntegerConstants
                        .RCP_TYPE_LOCAL_PHONE_BOOK));

                if (!arrayListCloudNumber.contains(phoneNumber.getPhoneNumber())) {
                    arrayListPhoneBookNumber.add(phoneNumber);
                }
                arrayListPhoneBookNumberOperation.add(phoneNumberOperation);
                profileContactNumber = phoneNumber.getPhoneNumber();

            }
            contactNumberCursor.close();
            profileDataOperationVcard.setPbPhoneNumber(arrayListPhoneBookNumberOperation);
        }

        if (!Utils.isArraylistNullOrEmpty(arrayListPhoneNumber) || !Utils.isArraylistNullOrEmpty
                (arrayListPhoneBookNumber)) {
            tempPhoneNumber = new ArrayList<>();
            tempPhoneNumber.addAll(arrayListPhoneNumber);
            tempPhoneNumber.addAll(arrayListPhoneBookNumber);

        } else {
        }
        //</editor-fold>

        // <editor-fold desc="Email Id">

        // From Cloud
        ArrayList<ProfileDataOperationEmail> arrayListEmail = new ArrayList<>();
        ArrayList<String> arrayListCloudEmail = new ArrayList<>();
        if (profileDetail != null && !Utils.isArraylistNullOrEmpty(profileDetail.getPbEmailId())) {
            arrayListEmail.addAll(profileDetail.getPbEmailId());
            for (int i = 0; i < arrayListEmail.size(); i++) {
                String email = arrayListEmail.get(i).getEmEmailId();
                arrayListCloudEmail.add(email);
            }
        }

        // From PhoneBook
        Cursor contactEmailCursor = phoneBookContacts.getContactEmail(phoneBookId);
        ArrayList<ProfileDataOperationEmail> arrayListPhoneBookEmail = new ArrayList<>();
        ArrayList<ProfileDataOperationEmail> arrayListPhoneBookEmailOperation = new ArrayList<>();

        if (contactEmailCursor != null && contactEmailCursor.getCount() > 0) {
            while (contactEmailCursor.moveToNext()) {

                ProfileDataOperationEmail emailId = new ProfileDataOperationEmail();
                ProfileDataOperationEmail emailIdOperation = new ProfileDataOperationEmail();

                emailId.setEmEmailId(contactEmailCursor.getString(contactEmailCursor
                        .getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS)));
                emailId.setEmType(phoneBookContacts.getEmailType(contactEmailCursor,
                        contactEmailCursor.getInt
                                (contactEmailCursor.getColumnIndex(ContactsContract
                                        .CommonDataKinds.Email.TYPE))));
                emailId.setEmRcpType(String.valueOf(IntegerConstants.RCP_TYPE_LOCAL_PHONE_BOOK));

                emailIdOperation.setEmEmailId(contactEmailCursor.getString(contactEmailCursor
                        .getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS)));
                emailIdOperation.setEmType(phoneBookContacts.getEmailType(contactEmailCursor,
                        contactEmailCursor.getInt
                                (contactEmailCursor.getColumnIndex(ContactsContract
                                        .CommonDataKinds.Email.TYPE))));

                if (!arrayListCloudEmail.contains(emailId.getEmEmailId())) {
                    arrayListPhoneBookEmail.add(emailId);
                }
                arrayListPhoneBookEmailOperation.add(emailIdOperation);
            }
            contactEmailCursor.close();
            profileDataOperationVcard.setPbEmailId(arrayListPhoneBookEmailOperation);
        }

        if (!Utils.isArraylistNullOrEmpty(arrayListEmail) || !Utils.isArraylistNullOrEmpty
                (arrayListPhoneBookEmail)) {
            ArrayList<Object> tempEmail = new ArrayList<>();
            tempEmail.addAll(arrayListEmail);
            tempEmail.addAll(arrayListPhoneBookEmail);
            ProfileDetailAdapter emailDetailAdapter = new ProfileDetailAdapter(this, tempEmail,
                    AppConstants.EMAIL, displayOwnProfile, pmId);
        } else {
        }
        //</editor-fold>

        // <editor-fold desc="Website">

        // From Cloud
        ArrayList<ProfileDataOperationWebAddress> arrayListWebsite = new ArrayList<>();
        ArrayList<String> arrayListCloudWebsite = new ArrayList<>();
        if (profileDetail != null && !Utils.isArraylistNullOrEmpty(profileDetail.getPbWebAddress
                ())) {
            arrayListWebsite.addAll(profileDetail.getPbWebAddress());
            for (int i = 0; i < arrayListWebsite.size(); i++) {
                String website = arrayListWebsite.get(i).getWebAddress();
                arrayListCloudWebsite.add(website);
            }
        }
       /* ArrayList<String> arrayListWebsite = new ArrayList<>();
        if (profileDetail != null && !Utils.isArraylistNullOrEmpty(profileDetail.getPbWebAddress
                ())) {
            arrayListWebsite.addAll(profileDetail.getPbWebAddress());
        }*/

        // From PhoneBook
        Cursor contactWebsiteCursor = phoneBookContacts.getContactWebsite(phoneBookId);
        ArrayList<ProfileDataOperationWebAddress> arrayListPhoneBookWebsite = new ArrayList<>();
        ArrayList<ProfileDataOperationWebAddress> arrayListPhoneBookWebsiteOperation = new
                ArrayList<>();

        if (contactWebsiteCursor != null && contactWebsiteCursor.getCount() > 0) {
            while (contactWebsiteCursor.moveToNext()) {

                ProfileDataOperationWebAddress webAddress = new ProfileDataOperationWebAddress();
                ProfileDataOperationWebAddress webAddressOperation = new
                        ProfileDataOperationWebAddress();

                webAddress.setWebAddress(contactWebsiteCursor.getString(contactWebsiteCursor
                        .getColumnIndex(ContactsContract.CommonDataKinds.Website.URL)));
                webAddress.setWebRcpType(String.valueOf(IntegerConstants
                        .RCP_TYPE_LOCAL_PHONE_BOOK));

                webAddressOperation.setWebAddress(contactWebsiteCursor.getString
                        (contactWebsiteCursor
                                .getColumnIndex(ContactsContract.CommonDataKinds.Website.URL)));


                if (!arrayListCloudWebsite.contains(webAddress.getWebAddress())) {
                    arrayListPhoneBookWebsite.add(webAddress);
                }
                arrayListPhoneBookWebsiteOperation.add(webAddressOperation);
            }
            contactWebsiteCursor.close();
//            profileDataOperationVcard.setPbWebAddress(arrayListPhoneBookWebsiteOperation);
        }

        if (!Utils.isArraylistNullOrEmpty(arrayListWebsite) || !Utils.isArraylistNullOrEmpty
                (arrayListPhoneBookWebsite)) {
            ArrayList<Object> tempWebsite = new ArrayList<>();
            tempWebsite.addAll(arrayListWebsite);
            tempWebsite.addAll(arrayListPhoneBookWebsite);
        } else {
        }
        //</editor-fold>

        // <editor-fold desc="Address">

        // From Cloud
        ArrayList<ProfileDataOperationAddress> arrayListAddress = new ArrayList<>();
        ArrayList<String> arrayListCloudAddress = new ArrayList<>();
        if (profileDetail != null && !Utils.isArraylistNullOrEmpty(profileDetail.getPbAddress())) {
            arrayListAddress.addAll(profileDetail.getPbAddress());
            for (int i = 0; i < arrayListAddress.size(); i++) {
                String address = arrayListAddress.get(i).getFormattedAddress();
                arrayListCloudAddress.add(address);
            }
        }

        // From PhoneBook
        Cursor contactAddressCursor = phoneBookContacts.getContactAddress(phoneBookId);
        ArrayList<ProfileDataOperationAddress> arrayListPhoneBookAddress = new ArrayList<>();
        ArrayList<ProfileDataOperationAddress> arrayListPhoneBookAddressOperation = new
                ArrayList<>();

        if (contactAddressCursor != null && contactAddressCursor.getCount() > 0) {
            while (contactAddressCursor.moveToNext()) {

                ProfileDataOperationAddress address = new ProfileDataOperationAddress();
                ProfileDataOperationAddress addressOperation = new ProfileDataOperationAddress();

                address.setFormattedAddress(contactAddressCursor.getString
                        (contactAddressCursor.getColumnIndex(ContactsContract
                                .CommonDataKinds.StructuredPostal.FORMATTED_ADDRESS)));
                address.setCity(contactAddressCursor.getString(contactAddressCursor
                        .getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal
                                .CITY)));
                address.setCountry(contactAddressCursor.getString(contactAddressCursor
                        .getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal
                                .COUNTRY)));
                address.setNeighborhood(contactAddressCursor.getString(contactAddressCursor
                        .getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal
                                .NEIGHBORHOOD)));
                address.setPostCode(contactAddressCursor.getString(contactAddressCursor
                        .getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal
                                .POSTCODE)));
                address.setPoBox(contactAddressCursor.getString(contactAddressCursor
                        .getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal
                                .POBOX)));
                address.setStreet(contactAddressCursor.getString(contactAddressCursor
                        .getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal
                                .STREET)));
                address.setAddressType(phoneBookContacts.getAddressType(contactAddressCursor,
                        contactAddressCursor.getInt(contactAddressCursor.getColumnIndex
                                (ContactsContract.CommonDataKinds.StructuredPostal.TYPE))));
                address.setRcpType(String.valueOf(IntegerConstants.RCP_TYPE_LOCAL_PHONE_BOOK));

                addressOperation.setFormattedAddress(contactAddressCursor.getString
                        (contactAddressCursor.getColumnIndex(ContactsContract
                                .CommonDataKinds.StructuredPostal.FORMATTED_ADDRESS)));
                addressOperation.setCity(contactAddressCursor.getString(contactAddressCursor
                        .getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal
                                .CITY)));
                addressOperation.setCountry(contactAddressCursor.getString(contactAddressCursor
                        .getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal
                                .COUNTRY)));
                addressOperation.setNeighborhood(contactAddressCursor.getString(contactAddressCursor
                        .getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal
                                .NEIGHBORHOOD)));
                addressOperation.setPostCode(contactAddressCursor.getString(contactAddressCursor
                        .getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal
                                .POSTCODE)));
                addressOperation.setPoBox(contactAddressCursor.getString(contactAddressCursor
                        .getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal
                                .POBOX)));
                addressOperation.setStreet(contactAddressCursor.getString(contactAddressCursor
                        .getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal
                                .STREET)));
                addressOperation.setAddressType(phoneBookContacts.getAddressType
                        (contactAddressCursor,
                                contactAddressCursor.getInt(contactAddressCursor.getColumnIndex
                                        (ContactsContract.CommonDataKinds.StructuredPostal.TYPE))));

                if (!arrayListCloudAddress.contains(address.getFormattedAddress())) {
                    arrayListPhoneBookAddress.add(address);
                }
                arrayListPhoneBookAddressOperation.add(addressOperation);
            }
            contactAddressCursor.close();
            profileDataOperationVcard.setPbAddress(arrayListPhoneBookAddressOperation);
        }

        if (!Utils.isArraylistNullOrEmpty(arrayListAddress) || !Utils.isArraylistNullOrEmpty
                (arrayListPhoneBookAddress)) {
            ArrayList<Object> tempAddress = new ArrayList<>();
            tempAddress.addAll(arrayListAddress);
            tempAddress.addAll(arrayListPhoneBookAddress);

        } else {

        }
        //</editor-fold>

        // <editor-fold desc="Im Account">

        // From Cloud
        ArrayList<ProfileDataOperationImAccount> arrayListImAccount = new ArrayList<>();
        ArrayList<String> arrayListCloudImAccount = new ArrayList<>();
        if (profileDetail != null && !Utils.isArraylistNullOrEmpty(profileDetail.getPbIMAccounts
                ())) {
            arrayListImAccount.addAll(profileDetail.getPbIMAccounts());
            for (int i = 0; i < arrayListImAccount.size(); i++) {
                String imAccount = arrayListImAccount.get(i).getIMAccountProtocol();
                arrayListCloudImAccount.add(imAccount);
            }
        }

        // From PhoneBook
        Cursor contactImAccountCursor = phoneBookContacts.getContactIm(phoneBookId);
        ArrayList<ProfileDataOperationImAccount> arrayListPhoneBookImAccount = new ArrayList<>();

        if (contactImAccountCursor != null && contactImAccountCursor.getCount() > 0) {
            while (contactImAccountCursor.moveToNext()) {

                ProfileDataOperationImAccount imAccount = new ProfileDataOperationImAccount();

                imAccount.setIMAccountDetails(contactImAccountCursor.getString
                        (contactImAccountCursor
                                .getColumnIndex(ContactsContract.CommonDataKinds.Im.DATA1)));

                imAccount.setIMAccountType(phoneBookContacts.getImAccountType
                        (contactImAccountCursor,
                                contactImAccountCursor.getInt(contactImAccountCursor.getColumnIndex
                                        (ContactsContract.CommonDataKinds.Im.TYPE))));

                imAccount.setIMAccountProtocol(phoneBookContacts.getImProtocol
                        (contactImAccountCursor.getInt((contactImAccountCursor.getColumnIndex
                                (ContactsContract.CommonDataKinds.Im.PROTOCOL)))));

                imAccount.setIMRcpType(String.valueOf(IntegerConstants.RCP_TYPE_LOCAL_PHONE_BOOK));

                if (!arrayListCloudImAccount.contains(imAccount.getIMAccountProtocol())) {
                    arrayListPhoneBookImAccount.add(imAccount);
                }

            }
            contactImAccountCursor.close();
        }

        if (!Utils.isArraylistNullOrEmpty(arrayListImAccount) || !Utils.isArraylistNullOrEmpty
                (arrayListPhoneBookImAccount)) {
            ArrayList<Object> tempImAccount = new ArrayList<>();
            tempImAccount.addAll(arrayListImAccount);
            tempImAccount.addAll(arrayListPhoneBookImAccount);
            ProfileDetailAdapter imAccountDetailAdapter = new ProfileDetailAdapter(this,
                    tempImAccount, AppConstants.IM_ACCOUNT, displayOwnProfile, pmId);
        } else {
        }
        //</editor-fold>

        if ((!Utils.isArraylistNullOrEmpty(arrayListWebsite) || !Utils.isArraylistNullOrEmpty
                (arrayListPhoneBookWebsite))
                ||
                (!Utils.isArraylistNullOrEmpty(arrayListAddress) || !Utils
                        .isArraylistNullOrEmpty(arrayListPhoneBookAddress))
                ||
                (!Utils.isArraylistNullOrEmpty(arrayListImAccount) || !Utils
                        .isArraylistNullOrEmpty(arrayListPhoneBookImAccount))
                ) {
        } else {
        }

        // <editor-fold desc="Event">

        // From Cloud
        ArrayList<ProfileDataOperationEvent> arrayListEvent = new ArrayList<>();
        if (profileDetail != null && !Utils.isArraylistNullOrEmpty(profileDetail.getPbEvent())) {
            arrayListEvent.addAll(profileDetail.getPbEvent());
        }

        // From PhoneBook
        Cursor contactEventCursor = phoneBookContacts.getContactEvent(phoneBookId);
        ArrayList<ProfileDataOperationEvent> arrayListPhoneBookEvent = new ArrayList<>();
        ArrayList<ProfileDataOperationEvent> arrayListPhoneBookEventOperation = new ArrayList<>();

        if (contactEventCursor != null && contactEventCursor.getCount() > 0) {
            while (contactEventCursor.moveToNext()) {

                ProfileDataOperationEvent event = new ProfileDataOperationEvent();
                ProfileDataOperationEvent eventOperation = new ProfileDataOperationEvent();

                event.setEventType(phoneBookContacts.getEventType(contactEventCursor,
                        contactEventCursor
                                .getInt(contactEventCursor.getColumnIndex(ContactsContract
                                        .CommonDataKinds.Event.TYPE))));

                event.setEventDateTime(contactEventCursor.getString(contactEventCursor
                        .getColumnIndex(ContactsContract.CommonDataKinds.Event
                                .START_DATE)));

                event.setEventRcType(String.valueOf(IntegerConstants.RCP_TYPE_LOCAL_PHONE_BOOK));

                eventOperation.setEventType(phoneBookContacts.getEventType(contactEventCursor,
                        contactEventCursor
                                .getInt(contactEventCursor.getColumnIndex(ContactsContract
                                        .CommonDataKinds.Event.TYPE))));

                eventOperation.setEventDateTime(contactEventCursor.getString(contactEventCursor
                        .getColumnIndex(ContactsContract.CommonDataKinds.Event
                                .START_DATE)));

                if (!arrayListEvent.contains(event)) {
                    arrayListPhoneBookEvent.add(event);
                }
                arrayListPhoneBookEventOperation.add(eventOperation);
            }
            contactEventCursor.close();
            profileDataOperationVcard.setPbEvent(arrayListPhoneBookEventOperation);
        }

        if (!Utils.isArraylistNullOrEmpty(arrayListEvent) || !Utils.isArraylistNullOrEmpty
                (arrayListPhoneBookEvent)) {
            ArrayList<Object> tempEvent = new ArrayList<>();
            tempEvent.addAll(arrayListEvent);
            tempEvent.addAll(arrayListPhoneBookEvent);

        } else {
        }
        //</editor-fold>

        if (Utils.isArraylistNullOrEmpty(arrayListEvent) && Utils.isArraylistNullOrEmpty
                (arrayListPhoneBookEvent)
//                && Utils.isArraylistNullOrEmpty(arrayListAddress)
                ) {
        } else {
        }

    }

    private void showAllOrganizations(ArrayList<ProfileDataOperationOrganization>
                                              arrayListOrganization) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_all_organization);
        dialog.setCancelable(false);

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(dialog.getWindow().getAttributes());
        layoutParams.width = (int) (getResources().getDisplayMetrics().widthPixels * 0.90);
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

        dialog.getWindow().setLayout(layoutParams.width, layoutParams.height);

        TextView textDialogTitle = (TextView) dialog.findViewById(R.id.text_dialog_title);
        textDialogTitle.setText(getString(R.string.title_all_organizations));
        textDialogTitle.setTypeface(Utils.typefaceSemiBold(this));

        Button buttonRight = (Button) dialog.findViewById(R.id.button_right);
        RippleView rippleRight = (RippleView) dialog.findViewById(R.id.ripple_right);
        RippleView rippleLeft = (RippleView) dialog.findViewById(R.id.ripple_left);

        rippleLeft.setVisibility(View.GONE);

        buttonRight.setTypeface(Utils.typefaceRegular(this));
        buttonRight.setText(R.string.action_close);

        rippleRight.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                dialog.dismiss();
            }
        });

        RecyclerView recyclerViewDialogList = (RecyclerView) dialog.findViewById(R.id
                .recycler_view_dialog_list);
        recyclerViewDialogList.setLayoutManager(new LinearLayoutManager(this));

        /*OrganizationListAdapter adapter = new OrganizationListAdapter(this, profileDetail
                .getPbOrganization());*/
        OrganizationListAdapter adapter = new OrganizationListAdapter(this, arrayListOrganization);
        recyclerViewDialogList.setAdapter(adapter);

        dialog.show();
    }


    private void showCallConfirmationDialog(final String number) {
        final String formattedNumber = Utils.getFormattedNumber(this, number);
        RippleView.OnRippleCompleteListener cancelListener = new RippleView
                .OnRippleCompleteListener() {

            @Override
            public void onComplete(RippleView rippleView) {
                switch (rippleView.getId()) {
                    case R.id.rippleLeft:
                        callConfirmationDialog.dismissDialog();
                        break;

                    case R.id.rippleRight:
                        callConfirmationDialog.dismissDialog();
                       /* Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" +
                                formattedNumber));
                        startActivity(intent);*/
                        Utils.callIntent(CallHistoryDetailsActivity.this, formattedNumber);
                        break;
                }
            }
        };

        callConfirmationDialog = new MaterialDialog(this, cancelListener);
        callConfirmationDialog.setTitleVisibility(View.GONE);
        callConfirmationDialog.setLeftButtonText(getString(R.string.action_cancel));
        callConfirmationDialog.setRightButtonText(getString(R.string.action_call));
        callConfirmationDialog.setDialogBody(getString(R.string.action_call) + " " +
                formattedNumber + "?");
        callConfirmationDialog.showDialog();
    }

    private void submitRating(String ratingStar, String comment) {

        WsRequestObject ratingObject = new WsRequestObject();
        ratingObject.setPmId(Integer.parseInt(getUserPmId()));
        ratingObject.setPrComment(comment);
        ratingObject.setPrRatingStars(ratingStar);
        ratingObject.setPrStatus(String.valueOf(IntegerConstants.RATING_DONE));
        ratingObject.setPrToPmId(Integer.parseInt(pmId));

        if (Utils.isNetworkAvailable(this)) {
            new AsyncWebServiceCall(this, WSRequestType.REQUEST_TYPE_JSON.getValue(),
                    ratingObject, null, WsResponseObject.class, WsConstants.REQ_PROFILE_RATING,
                    null, true).execute(WsConstants.WS_ROOT + WsConstants.REQ_PROFILE_RATING);
        } else {
            Utils.showErrorSnackBar(this, relativeRootProfileDetail, getResources().getString(R
                    .string.msg_no_network));
        }
    }

    private void profileVisit(ArrayList<ProfileVisit> arrayListProfileVisit) {

        WsRequestObject profileVisitObject = new WsRequestObject();
        profileVisitObject.setArrayListProfileVisit(arrayListProfileVisit);

        if (Utils.isNetworkAvailable(this)) {
            new AsyncWebServiceCall(this, WSRequestType.REQUEST_TYPE_JSON.getValue(),
                    profileVisitObject, null, WsResponseObject.class, WsConstants
                    .REQ_ADD_PROFILE_VISIT, null, true).execute(WsConstants.WS_ROOT + WsConstants
                    .REQ_ADD_PROFILE_VISIT);
        } else {
            Utils.showErrorSnackBar(this, relativeRootProfileDetail, getResources()
                    .getString(R.string.msg_no_network));
        }
    }

    private void setFavouriteStatus(ArrayList<ProfileData> favourites) {

        WsRequestObject favouriteStatusObject = new WsRequestObject();
        favouriteStatusObject.setPmId(Integer.parseInt(getUserPmId()));
        favouriteStatusObject.setFavourites(favourites);

        if (Utils.isNetworkAvailable(this)) {
            new AsyncWebServiceCall(this, WSRequestType.REQUEST_TYPE_JSON.getValue(),
                    favouriteStatusObject, null, WsResponseObject.class, WsConstants
                    .REQ_MARK_AS_FAVOURITE, null, true).execute(WsConstants.WS_ROOT + WsConstants
                    .REQ_MARK_AS_FAVOURITE);
        } else {
            Utils.showErrorSnackBar(this, relativeRootProfileDetail, getResources().getString(R
                    .string.msg_no_network));
        }
    }


    private void fetchAllCallLogHistory(String value) {
        if (!TextUtils.isEmpty(value)) {
            arrayListHistory = callLogHistory(value);
            Log.i("History size  ", arrayListHistory.size() + "" + " of  " + value);
        }
        setHistoryAdapter();
    }

    private void setHistoryAdapter() {
        if (arrayListHistory != null && arrayListHistory.size() > 0) {
            textNoHistoryToShow.setVisibility(View.GONE);
            recyclerCallHistory.setVisibility(View.VISIBLE);
            rippleViewOldRecords.setVisibility(View.VISIBLE);
            callHistoryListAdapter = new CallHistoryListAdapter(getApplicationContext(),
                    arrayListHistory);
            recyclerCallHistory.setAdapter(callHistoryListAdapter);
            recyclerCallHistory.setFocusable(false);
            setRecyclerViewLayoutManager(recyclerCallHistory);
        } else {
            recyclerCallHistory.setVisibility(View.GONE);
            rippleViewOldRecords.setVisibility(View.GONE);
            textNoHistoryToShow.setVisibility(View.VISIBLE);
            textNoHistoryToShow.setText(getResources().getString(R.string.text_no_history));
        }
    }

    /**
     * Set RecyclerView's LayoutManager
     */
    public void setRecyclerViewLayoutManager(RecyclerView recyclerView) {
        int scrollPosition = 0;
        // If a layout manager has already been set, get current scroll position.
        if (recyclerView.getLayoutManager() != null) {
            scrollPosition =
                    ((LinearLayoutManager) recyclerView.getLayoutManager())
                            .findFirstCompletelyVisibleItemPosition();
        }
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.scrollToPosition(scrollPosition);
    }

    @TargetApi(Build.VERSION_CODES.M)
    private Cursor getCallHistoryDataByNumber(String number) {
        Cursor cursor = null;
        String order = CallLog.Calls.DATE + " DESC";
        try {
            cursor = this.getContentResolver().query(CallLog.Calls.CONTENT_URI, null, CallLog
                    .Calls.NUMBER + " =?", new String[]{number}, order);

        } catch (SecurityException e) {
            e.printStackTrace();
        }
        return cursor;
    }


    @TargetApi(Build.VERSION_CODES.M)
    private Cursor getCallHistoryDataByName(String name) {
        Cursor cursor = null;
        String order = CallLog.Calls.DATE + " DESC";
        try {
            cursor = this.getContentResolver().query(CallLog.Calls.CONTENT_URI, null, CallLog
                    .Calls.CACHED_NAME + " =?", new String[]{name}, order);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return cursor;
    }


    private ArrayList callLogHistory(String number) {
        ArrayList<CallLogType> callDetails = new ArrayList<>();
        Cursor cursor = null;
        Pattern numberPat = Pattern.compile("\\d+");
        Matcher matcher1 = numberPat.matcher(number);
        if (matcher1.find()) {
            cursor = getCallHistoryDataByNumber(number);
        } else {
            cursor = getCallHistoryDataByName(number);
        }

        try {
            if (cursor != null && cursor.getCount() > 0) {
                int number1 = cursor.getColumnIndex(CallLog.Calls.NUMBER);
                int type = cursor.getColumnIndex(CallLog.Calls.TYPE);
                int date = cursor.getColumnIndex(CallLog.Calls.DATE);
                int duration = cursor.getColumnIndex(CallLog.Calls.DURATION);
                int callLogId = cursor.getColumnIndex(CallLog.Calls._ID);
                int numberType = cursor.getColumnIndex(CallLog.Calls.CACHED_NUMBER_TYPE);
                int name = cursor.getColumnIndexOrThrow(CallLog.Calls.CACHED_NAME);
                int account = -1;
                int account_id = -1;
                int profileImage = -1;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    account = cursor.getColumnIndex(CallLog.Calls.PHONE_ACCOUNT_COMPONENT_NAME);
                    //for versions above lollipop
                    account_id = cursor.getColumnIndex(CallLog.Calls.PHONE_ACCOUNT_ID);
                    profileImage = cursor.getColumnIndex(CallLog.Calls.CACHED_PHOTO_URI);
                } else {
//                        account_id = cursor.getColumnIndex(CallLog.Calls.PHONE_ACCOUNT_ID);
                }

                while (cursor.moveToNext()) {
                    String phNum = cursor.getString(number1);
                    int callType = Integer.parseInt(cursor.getString(type));
                    String callDate = cursor.getString(date);
                    long dateOfCall = Long.parseLong(callDate);
                    String callDuration = cursor.getString(duration);
                    String numberTypeLog = getPhoneNumberType(cursor.getInt(numberType));
                    String accountId = "";
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        accountId = cursor.getString(account_id);
                        if (!TextUtils.isEmpty(accountId) && account_id > 0)
                            Log.e("Sim Type", accountId);
                        String accountName = cursor.getString(account);
                        if (!TextUtils.isEmpty(accountName))
                            Log.e("Sim Name", accountName);
//                        String userImage = cursor.getString(profileImage);
//                        if (userImage != null)
//                            Log.e("User Image", userImage);
                    }
                    int histroyId = Integer.parseInt(cursor.getString(callLogId));
                    String uniquePhoneBookId = getStarredStatusFromNumber(phNum);
                    if (!TextUtils.isEmpty(uniquePhoneBookId)) {
                        hashMapKey = uniquePhoneBookId;
                    } else {
                        hashMapKey = cursor.getString(callLogId);
                        uniqueContactId = cursor.getString(callLogId);
                    }

                    CallLogType logObject = new CallLogType();
                    logObject.setHistoryNumber(phNum);
                    logObject.setHistoryType(callType);
                    logObject.setHistoryDate(dateOfCall);
                    logObject.setHistoryDuration(Integer.parseInt(callDuration));
                    logObject.setHistoryCallSimNumber(accountId);
                    logObject.setHistoryId(histroyId);
                    logObject.setHistoryNumberType(numberTypeLog);
                    callDetails.add(logObject);


                   /* ArrayList<CallLogType> callLogTypeArrayList =  new ArrayList<>();
                    logObject.setNumber(phNum);
                    String userName =  cursor.getString(name);
                    if(!TextUtils.isEmpty(userName)){
                        logObject.setName(userName);
                    }else{
                        logObject.setName("");
                    }
                    logObject.setUniqueContactId(cursor.getString(callLogId));
                    logObject.setDate(dateOfCall);
                    logObject.setDuration(Integer.parseInt(callDuration));
                    logObject.setType(callType);
                    String photoThumbNail = getPhotoUrlFromNumber(phNum);
                    if (!TextUtils.isEmpty(photoThumbNail)) {
                        logObject.setProfileImage(photoThumbNail);
                    } else {
                        logObject.setProfileImage("");
                    }

                    callLogTypeArrayList.add(logObject);
                    rContactApplication.setArrayListCallLogType(callLogTypeArrayList);*/
                }
            }
            cursor.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return callDetails;
    }


    private String getPhotoUrlFromNumber(String phoneNumber) {
        String photoThumbUrl = "";
        try {

            photoThumbUrl = "";
            ContentResolver contentResolver = getContentResolver();

            Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri
                    .encode(phoneNumber));

            String[] projection = new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME,
                    ContactsContract.PhoneLookup.PHOTO_THUMBNAIL_URI};
            Cursor cursor =
                    contentResolver.query(uri, projection, null, null, null);

            if (cursor != null) {
                while (cursor.moveToNext()) {
                    /*String contactName = cursor.getString(cursor.getColumnIndexOrThrow
                            (ContactsContract.PhoneLookup.DISPLAY_NAME));*/
                    photoThumbUrl = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract
                            .PhoneLookup.PHOTO_THUMBNAIL_URI));
//                Log.d("LocalPBId", "contactMatch id: " + numberId + " of " + contactName);
                }
                cursor.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


        return photoThumbUrl;
    }

    private String getStarredStatusFromNumber(String phoneNumber) {
        String numberId, rawId = "";
        try {

//            numberId = "";
            ContentResolver contentResolver = this.getContentResolver();

            Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri
                    .encode(phoneNumber));

            String[] projection = new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME,
                    ContactsContract.PhoneLookup.LOOKUP_KEY};
            Cursor cursor =
                    contentResolver.query(uri, projection, null, null, null);

            if (cursor != null) {
                while (cursor.moveToNext()) {
                 /*   String contactName = cursor.getString(cursor.getColumnIndexOrThrow
                            (ContactsContract.PhoneLookup.DISPLAY_NAME));
                    numberId = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract
                            .PhoneLookup.LOOKUP_KEY));*/
                    numberId = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract
                            .PhoneLookup.LOOKUP_KEY));
                    Uri uri1 = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
                    String[] projection1 = new String[]{
                            ContactsContract.CommonDataKinds.Phone.LOOKUP_KEY,
                            ContactsContract.CommonDataKinds.Phone.RAW_CONTACT_ID
                    };

                    String selection = ContactsContract.CommonDataKinds.Phone.LOOKUP_KEY + " = ?";
                    String[] selectionArgs = new String[]{numberId};

                    Cursor cursor1 = getContentResolver().query(uri1, projection1, selection,
                            selectionArgs, null);
                    if (cursor1 != null) {
                        while (cursor1.moveToNext()) {
                            rawId = cursor1.getString(cursor1.getColumnIndexOrThrow
                                    (ContactsContract.CommonDataKinds.Phone.RAW_CONTACT_ID));
                        }
                        cursor1.close();
                    }
                }
                cursor.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return rawId;
    }


    private String getPhoneNumberType(int type) {
        switch (type) {
            case ContactsContract.CommonDataKinds.Phone.TYPE_HOME:
                return getString(R.string.type_home);

            case ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE:
                return getString(R.string.type_mobile);

            case ContactsContract.CommonDataKinds.Phone.TYPE_WORK:
                return getString(R.string.type_work);

            case ContactsContract.CommonDataKinds.Phone.TYPE_FAX_WORK:
                return getString(R.string.type_fax_work);

            case ContactsContract.CommonDataKinds.Phone.TYPE_FAX_HOME:
                return getString(R.string.type_fax_home);

            case ContactsContract.CommonDataKinds.Phone.TYPE_PAGER:
                return getString(R.string.type_pager);

            case ContactsContract.CommonDataKinds.Phone.TYPE_OTHER:
                return getString(R.string.type_other);

            case ContactsContract.CommonDataKinds.Phone.TYPE_CALLBACK:
                return getString(R.string.type_callback);

            case ContactsContract.CommonDataKinds.Phone.TYPE_CAR:
                return getString(R.string.type_car);

            case ContactsContract.CommonDataKinds.Phone.TYPE_COMPANY_MAIN:
                return getString(R.string.type_company_main);

            case ContactsContract.CommonDataKinds.Phone.TYPE_ISDN:
                return getString(R.string.type_isdn);

            case ContactsContract.CommonDataKinds.Phone.TYPE_MAIN:
                return getString(R.string.type_main);

            case ContactsContract.CommonDataKinds.Phone.TYPE_OTHER_FAX:
                return getString(R.string.type_other_fax);

            case ContactsContract.CommonDataKinds.Phone.TYPE_RADIO:
                return getString(R.string.type_radio);

            case ContactsContract.CommonDataKinds.Phone.TYPE_TELEX:
                return getString(R.string.type_telex);

            case ContactsContract.CommonDataKinds.Phone.TYPE_TTY_TDD:
                return getString(R.string.type_tty_tdd);

            case ContactsContract.CommonDataKinds.Phone.TYPE_WORK_MOBILE:
                return getString(R.string.type_work_mobile);

            case ContactsContract.CommonDataKinds.Phone.TYPE_WORK_PAGER:
                return getString(R.string.type_work_pager);

            case ContactsContract.CommonDataKinds.Phone.TYPE_ASSISTANT:
                return getString(R.string.type_assistant);

            case ContactsContract.CommonDataKinds.Phone.TYPE_MMS:
                return getString(R.string.type_mms);

        }
        return getString(R.string.type_other);
    }


}
