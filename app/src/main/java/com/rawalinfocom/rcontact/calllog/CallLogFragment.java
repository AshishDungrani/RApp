package com.rawalinfocom.rcontact.calllog;


import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.rawalinfocom.rcontact.BaseFragment;
import com.rawalinfocom.rcontact.R;
import com.rawalinfocom.rcontact.adapters.CallLogListAdapter;
import com.rawalinfocom.rcontact.asynctasks.AsyncWebServiceCall;
import com.rawalinfocom.rcontact.constants.AppConstants;
import com.rawalinfocom.rcontact.constants.WsConstants;
import com.rawalinfocom.rcontact.enumerations.WSRequestType;
import com.rawalinfocom.rcontact.helper.MaterialDialog;
import com.rawalinfocom.rcontact.helper.RippleView;
import com.rawalinfocom.rcontact.helper.Utils;
import com.rawalinfocom.rcontact.interfaces.WsResponseListener;
import com.rawalinfocom.rcontact.model.CallLogType;
import com.rawalinfocom.rcontact.model.WsRequestObject;
import com.rawalinfocom.rcontact.model.WsResponseObject;

import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CallLogFragment extends BaseFragment implements WsResponseListener {


    private final String ALL_CALLS = "All Calls";
    private final String INCOMING_CALLS = "Incoming Calls";
    private final String OUTGOING_CALLS = "Outgoing Calls";
    private final String MISSED_CALLS = "Missed Calls";

    @BindView(R.id.progressBarCallLog)
    ProgressBar progressBarCallLog;
    @BindView(R.id.linearProgressBar)
    LinearLayout linearProgressBar;
    @BindView(R.id.linearMainContent)
    LinearLayout linearMainContent;
    @BindView(R.id.linearCallLogMain)
    LinearLayout linearCallLogMain;
    private Spinner spinnerCallFilter;
    private RecyclerView recyclerCallLogs;
    private CallLogListAdapter callLogListAdapter;
    ArrayList<Object> arrayListObjectCallLogs;
    ArrayList<String> arrayListCallLogHeader;
    ArrayList<CallLogType> arrayListCallLogs;
    ArrayList<CallLogType> arrayListCallLogsHistory;
    ArrayList<CallLogType> tempList;
    MaterialDialog callConfirmationDialog;
    String selectedCallType = "";
    View mainView;
    private Runnable logsRunnable;
    private String[] requiredPermissions = {Manifest.permission.READ_CALL_LOG,
            Manifest.permission.READ_CONTACTS, Manifest.permission.ACCESS_COARSE_LOCATION};
    public static CallLogType callLogTypeReceiver;
    private int LIST_PARTITION_COUNT = 50;


    //<editor-fold desc="Constructors">

    public CallLogFragment() {
        // Required empty public constructor
    }

    public static CallLogFragment newInstance() {
        return new CallLogFragment();
    }

    //</editor-fold>

    //<editor-fold desc="Override Methods">

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        callLogTypeReceiver = new CallLogType();
    }

    @Override
    public void getFragmentArguments() {

    }

    private void showProgressBar() {
        linearProgressBar.setVisibility(View.VISIBLE);
        progressBarCallLog.setVisibility(View.VISIBLE);
        linearMainContent.setVisibility(View.GONE);
    }


    private void hideProgressBar() {
        linearProgressBar.setVisibility(View.GONE);
        progressBarCallLog.setVisibility(View.GONE);
        linearMainContent.setVisibility(View.VISIBLE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mainView = inflater.inflate(R.layout.fragment_call_log, container, false);
        ButterKnife.bind(this, mainView);
        return mainView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
        telephonyInit();
    }

    @Override
    public void onDeliveryResponse(String serviceType, Object data, Exception error) {

        if (error == null) {
            if (serviceType.equalsIgnoreCase(WsConstants.REQ_UPLOAD_CALL_LOGS)) {
                WsResponseObject callLogInsertionResponse = (WsResponseObject) data;
                if (callLogInsertionResponse != null && StringUtils.equalsIgnoreCase(callLogInsertionResponse
                        .getStatus(), WsConstants.RESPONSE_STATUS_TRUE)) {

                } else {
                    if (callLogInsertionResponse != null) {
                        Log.e("error response", callLogInsertionResponse.getMessage());
                    } else {
                        Log.e("onDeliveryResponse: ", "userProfileResponse null");
                        Utils.showErrorSnackBar(getActivity(), linearMainContent, getString(R
                                .string.msg_try_later));
                    }
                }

            } else {
//            AppUtils.hideProgressDialog();
                Utils.showErrorSnackBar(getActivity(), linearMainContent, "" + error
                        .getLocalizedMessage());
            }
        }
    }

    private void init() {
        spinnerCallFilter = (Spinner) mainView.findViewById(R.id.spinner_call_filter);
        recyclerCallLogs = (RecyclerView) mainView.findViewById(R.id.recycler_call_logs);
        arrayListCallLogs = new ArrayList<>();
        arrayListCallLogHeader = new ArrayList<>();
        arrayListObjectCallLogs = new ArrayList<>();

        initSpinner();
        logsRunnable = new Runnable() {
            @Override
            public void run() {
            }
        };

        // Checking for permissions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkPermissionToExecute(requiredPermissions, AppConstants.READ_LOGS, logsRunnable);
        } else {
            logsRunnable.run();
        }

    }

    private void initSpinner() {
        final List<String> spinnerArray = new ArrayList<>();
        spinnerArray.add(ALL_CALLS);
        spinnerArray.add(INCOMING_CALLS);
        spinnerArray.add(OUTGOING_CALLS);
        spinnerArray.add(MISSED_CALLS);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout
                .simple_spinner_item, spinnerArray);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCallFilter.setAdapter(adapter);

        spinnerCallFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String value = parent.getSelectedItem().toString();
                if (!TextUtils.isEmpty(value)) {
                    Log.i("callType", value);
                    selectedCallType = value;
                    loadLogs(value);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


    // This is to be run only when READ_CONTACTS and READ_CALL_LOG permission are granted
    @SuppressLint("SimpleDateFormat")
    private void loadLogs(String callType) {

        List<CallLogType> callLogs;
        arrayListCallLogs = new ArrayList<>();
        arrayListCallLogHeader = new ArrayList<>();
        arrayListObjectCallLogs = new ArrayList<>();
        arrayListCallLogsHistory = new ArrayList<>();
        tempList = new ArrayList<>();

        if (callType.equalsIgnoreCase(MISSED_CALLS)) {
            callLogs = getLogsByCallType(AppConstants.MISSED_CALLS);
        } else if (callType.equalsIgnoreCase(INCOMING_CALLS)) {
            callLogs = getLogsByCallType(AppConstants.INCOMING_CALLS);
        } else if (callType.equalsIgnoreCase(OUTGOING_CALLS)) {
            callLogs = getLogsByCallType(AppConstants.OUTGOING_CALLS);
        } else {
            callLogs = getLogsByCallType(AppConstants.ALL_CALLS);
        }

        tempList.addAll(callLogs);
        insertDataByChunck();
        // To show recent call on top
//        Collections.reverse(callLogs);
        List<String> listOfDates = new ArrayList<>();
        if (callLogs != null && callLogs.size() > 0)
            for (int i = 0; i < callLogs.size(); i++) {
                CallLogType callLogType = callLogs.get(i);
                long logDate1 = callLogType.getDate();
                Date date1 = new Date(logDate1);
                String logDate = new SimpleDateFormat("yyyy-MM-dd").format(date1);
                Log.i("Call Log date", logDate);

                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.DATE, -1);
                Date yesDate;
                yesDate = cal.getTime();
                String yesterdayDate = new SimpleDateFormat("yyyy-MM-dd").format(yesDate);
                Log.i("Call yesterday date", yesterdayDate);

                Calendar c = Calendar.getInstance();
                Date cDate = c.getTime();
                String currentDate = new SimpleDateFormat("yyyy-MM-dd").format(cDate);
                Log.i("Call Current date", currentDate);

                String finalDate;
                if (logDate.equalsIgnoreCase(currentDate)) {
                    finalDate = "Today";
                    listOfDates.add(finalDate);
                    if (!arrayListObjectCallLogs.contains(finalDate)) {
                        arrayListCallLogHeader.add(finalDate);
                        arrayListObjectCallLogs.add(finalDate);
                    }
                    arrayListObjectCallLogs.add(callLogType);

                } else if (logDate.equalsIgnoreCase(yesterdayDate)) {
                    finalDate = "Yesterday";
                    listOfDates.add(finalDate);
                    if (!arrayListObjectCallLogs.contains(finalDate)) {
                        arrayListCallLogHeader.add(finalDate);
                        arrayListObjectCallLogs.add(finalDate);
                    }
                    arrayListObjectCallLogs.add(callLogType);

                } else {
                    finalDate = new SimpleDateFormat("dd/MM,EEE").format(date1);
                    listOfDates.add(finalDate);
                    if (!arrayListObjectCallLogs.contains(finalDate)) {
                        arrayListCallLogHeader.add(finalDate);
                        arrayListObjectCallLogs.add(finalDate);
                    }
                    arrayListObjectCallLogs.add(callLogType);
                }
            }
        setAdapter();
        initSwipe();
    }


    private void setAdapter() {
        if (arrayListCallLogHeader != null && arrayListObjectCallLogs != null
                && arrayListCallLogHeader.size() > 0 && arrayListObjectCallLogs.size() > 0) {
            callLogListAdapter = new CallLogListAdapter(getActivity(), arrayListObjectCallLogs,
                    arrayListCallLogHeader);
            recyclerCallLogs.setAdapter(callLogListAdapter);
            setRecyclerViewLayoutManager(recyclerCallLogs);
        }
    }

    private void insertDataByChunck() {
        int size = tempList.size();
        for (ArrayList<CallLogType> partition : chopped(tempList, LIST_PARTITION_COUNT)) {
            // do something with partition
            Log.i("Partition of Call Logs", partition.size() + " from " + size + "");
            insertServiceCall(partition);
        }
    }


    private void insertServiceCall(ArrayList<CallLogType> callLogTypeArrayList) {

        WsRequestObject deviceDetailObject = new WsRequestObject();
        deviceDetailObject.setArrayListCallLogType(callLogTypeArrayList);

        if (Utils.isNetworkAvailable(getActivity())) {
            new AsyncWebServiceCall(this, WSRequestType.REQUEST_TYPE_JSON.getValue(),
                    deviceDetailObject, null, WsResponseObject.class, WsConstants
                    .REQ_UPLOAD_CALL_LOGS, null, true).execute
                    (WsConstants.WS_ROOT + WsConstants.REQ_UPLOAD_CALL_LOGS);
        } else {
            Utils.showErrorSnackBar(getActivity(), linearCallLogMain, getResources()
                    .getString(R.string.msg_no_network));
        }
    }

    private ArrayList<ArrayList<CallLogType>> chopped(ArrayList<CallLogType> list, final int L) {
        ArrayList<ArrayList<CallLogType>> parts = new ArrayList<ArrayList<CallLogType>>();
        final int N = list.size();
        for (int i = 0; i < N; i += L) {
            parts.add(new ArrayList<CallLogType>(
                    list.subList(i, Math.min(N, i + L)))
            );
        }
        return parts;


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
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.scrollToPosition(scrollPosition);
    }


    private List<CallLogType> getLogsByCallType(int callType) {
        ArrayList logs = new ArrayList();
        String order = CallLog.Calls.DATE + " DESC";
        try {
            String selection;
            switch (callType) {
                case AppConstants.OUTGOING_CALLS:
                    selection = "type = 2";
                    break;
                case AppConstants.INCOMING_CALLS:
                    selection = "type = 1";
                    break;
                case AppConstants.ALL_CALLS:
                    selection = null;
                    break;
                case AppConstants.MISSED_CALLS:
                    selection = "type = 3";
                    break;
                default:
                    selection = null;
                    break;
            }

            Cursor cursor = getActivity().getContentResolver().query(CallLog.Calls.CONTENT_URI,
                    null, selection, null, order);
            if (cursor != null) {
                int number = cursor.getColumnIndex(CallLog.Calls.NUMBER);
                int name = cursor.getColumnIndex(CallLog.Calls.CACHED_NAME);
                int type = cursor.getColumnIndex(CallLog.Calls.TYPE);
                int date = cursor.getColumnIndex(CallLog.Calls.DATE);
                int duration = cursor.getColumnIndex(CallLog.Calls.DURATION);
                int rowId = cursor.getColumnIndex(CallLog.Calls._ID);
                int numberType = cursor.getColumnIndex(CallLog.Calls.CACHED_NUMBER_TYPE);

                while (cursor.moveToNext()) {
                    CallLogType log = new CallLogType(getActivity());
                    log.setNumber(cursor.getString(number));
                    log.setName(cursor.getString(name));
                    log.setType(cursor.getInt(type));
                    log.setDuration(cursor.getInt(duration));
                    log.setDate(cursor.getLong(date));
                    log.setUniqueContactId(cursor.getString(rowId));
                    String numberTypeLog = getPhoneNumberType(cursor.getInt(numberType));
                    Log.i("Number Type", numberTypeLog + " of number " + cursor.getString(number));
                    Log.i("Number Log Type", getLogType(cursor.getInt(type)) + " of number " +
                            cursor.getString(number));
                    log.setNumberType(numberTypeLog);
                    ArrayList<CallLogType> arrayListHistory;
                    String userName = cursor.getString(name);
                    String userNumber = cursor.getString(number);
                    String uniquePhoneBookId = getStarredStatusFromNumber(userNumber);
                    Log.i("Unique PhoneBook Id", uniquePhoneBookId + " of no.:" + userNumber);
                    if (!TextUtils.isEmpty(uniquePhoneBookId))
                        log.setLocalPbRowId(uniquePhoneBookId);
                    else
                        log.setLocalPbRowId(null);

                    log.setFlag(1);

                   /* if (!TextUtils.isEmpty(userName)) {
                        arrayListHistory = callLogHistory(userName);
                    } else {*/
                    arrayListHistory = callLogHistory(userNumber);
//                    }


                    log.setArrayListCallHistory(arrayListHistory);
                    int logCount = arrayListHistory.size();
                    log.setHistoryLogCount(logCount);
                    Log.i("History size ", logCount + "" + " of " + cursor.getString(number));
                    Log.i("History", "----------------------------------");

                    for (int i = 0; i < arrayListHistory.size(); i++) {
                        String simNumber = arrayListHistory.get(i).getHistoryCallSimNumber();
                        log.setCallSimNumber(simNumber);
                    }
                    logs.add(log);
                }
                cursor.close();
            }

        } catch (SecurityException e) {
            e.printStackTrace();
        }

        return logs;
    }


    private String getStarredStatusFromNumber(String phoneNumber) {

        String numberId = "";
        ContentResolver contentResolver = getActivity().getContentResolver();

        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));

        String[] projection = new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME, ContactsContract.PhoneLookup.LOOKUP_KEY};
        Cursor cursor =
                contentResolver.query(
                        uri,
                        projection,
                        null,
                        null,
                        null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                String contactName = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.PhoneLookup.DISPLAY_NAME));
                numberId = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.PhoneLookup.LOOKUP_KEY));
                Log.d("contactMatch id: ", numberId);
            }
            cursor.close();
        }
        return numberId;
    }


    public String getLogType(int type) {
        switch (type) {
            case CallLog.Calls.INCOMING_TYPE:
                return "Incoming";
            case CallLog.Calls.OUTGOING_TYPE:
                return "Outgoing";
            case CallLog.Calls.MISSED_TYPE:
                return "Missed call";
            case CallLog.Calls.REJECTED_TYPE:
                return "Rejected";
            case CallLog.Calls.BLOCKED_TYPE:
                return "Blocked";
            case CallLog.Calls.VOICEMAIL_TYPE:
                return "Voicemail";

        }
        return "OTHERS";
    }


    public String getPhoneNumberType(int type) {
        switch (type) {
            case ContactsContract.CommonDataKinds.Phone.TYPE_HOME:
                return "Home";

            case ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE:
                return "Mobile";

            case ContactsContract.CommonDataKinds.Phone.TYPE_WORK:
                return "Work";

            case ContactsContract.CommonDataKinds.Phone.TYPE_FAX_WORK:
                return "Fax Work";

            case ContactsContract.CommonDataKinds.Phone.TYPE_FAX_HOME:
                return "Fax Home";

            case ContactsContract.CommonDataKinds.Phone.TYPE_PAGER:
                return "Pager";

            case ContactsContract.CommonDataKinds.Phone.TYPE_OTHER:
                return "Other";

            case ContactsContract.CommonDataKinds.Phone.TYPE_CALLBACK:
                return "Callback";

            case ContactsContract.CommonDataKinds.Phone.TYPE_CAR:
                return "Car";

            case ContactsContract.CommonDataKinds.Phone.TYPE_COMPANY_MAIN:
                return "Company Main";

            case ContactsContract.CommonDataKinds.Phone.TYPE_ISDN:
                return "ISDN";

            case ContactsContract.CommonDataKinds.Phone.TYPE_MAIN:
                return "Main";

            case ContactsContract.CommonDataKinds.Phone.TYPE_OTHER_FAX:
                return "Other Fax";

            case ContactsContract.CommonDataKinds.Phone.TYPE_RADIO:
                return "Radio";

            case ContactsContract.CommonDataKinds.Phone.TYPE_TELEX:
                return "Telex";

            case ContactsContract.CommonDataKinds.Phone.TYPE_TTY_TDD:
                return "Tty Tdd";

            case ContactsContract.CommonDataKinds.Phone.TYPE_WORK_MOBILE:
                return "Work Mobile";

            case ContactsContract.CommonDataKinds.Phone.TYPE_WORK_PAGER:
                return "Work Pager";

            case ContactsContract.CommonDataKinds.Phone.TYPE_ASSISTANT:
                return "Assistant";

            case ContactsContract.CommonDataKinds.Phone.TYPE_MMS:
                return "MMS";

        }
        return "Other";
    }


    private void initSwipe() {
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper
                .SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                                  RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                String actionNumber = StringUtils.defaultString(((CallLogListAdapter
                        .AllCallLogViewHolder) viewHolder).textTempNumber.getText()
                        .toString());
                if (direction == ItemTouchHelper.LEFT) {
                    /* SMS */
                    Intent smsIntent = new Intent(Intent.ACTION_SENDTO);
                    smsIntent.addCategory(Intent.CATEGORY_DEFAULT);
                    smsIntent.setType("vnd.android-dir/mms-sms");
                    smsIntent.setData(Uri.parse("sms:" + actionNumber));
                    startActivity(smsIntent);

                } else {
                    showCallConfirmationDialog(actionNumber);
                }
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        callLogListAdapter.notifyDataSetChanged();
                    }
                }, 1500);
            }

            @Override
            public int getSwipeDirs(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                /* Disable swiping in headers */
                if (viewHolder instanceof CallLogListAdapter.CallLogHeaderViewHolder) {
                    return 0;
                }
                return super.getSwipeDirs(recyclerView, viewHolder);
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder
                    viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

                Bitmap icon;
                Paint p = new Paint();
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {

                    View itemView = viewHolder.itemView;
                    float height = (float) itemView.getBottom() - (float) itemView.getTop();
                    float width = height / 3;

                    if (dX > 0) {
                        p.setColor(ContextCompat.getColor(getActivity(), R.color
                                .darkModerateLimeGreen));
                        RectF background = new RectF((float) itemView.getLeft(), (float) itemView
                                .getTop(), dX, (float) itemView.getBottom());
                        c.drawRect(background, p);
                        icon = BitmapFactory.decodeResource(getResources(), R.drawable
                                .ic_action_call);
                        RectF icon_dest = new RectF((float) itemView.getLeft() + width, (float)
                                itemView.getTop() + width, (float) itemView.getLeft() + 2 *
                                width, (float) itemView.getBottom() - width);
                        c.drawBitmap(icon, null, icon_dest, p);
                    } else {
                        p.setColor(ContextCompat.getColor(getActivity(), R.color.brightOrange));
                        RectF background = new RectF((float) itemView.getRight() + dX, (float)
                                itemView.getTop(), (float) itemView.getRight(), (float) itemView
                                .getBottom());
                        c.drawRect(background, p);
                        icon = BitmapFactory.decodeResource(getResources(), R.drawable
                                .ic_action_sms);
                        RectF icon_dest = new RectF((float) itemView.getRight() - 2 * width,
                                (float) itemView.getTop() + width, (float) itemView.getRight() -
                                width, (float) itemView.getBottom() - width);
                        c.drawBitmap(icon, null, icon_dest, p);
                    }
                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState,
                        isCurrentlyActive);
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerCallLogs);
    }


    private void showCallConfirmationDialog(final String number) {

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
                        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" +
                                number));
                        startActivity(intent);
                        break;
                }
            }
        };

        callConfirmationDialog = new MaterialDialog(getActivity(), cancelListener);
        callConfirmationDialog.setTitleVisibility(View.GONE);
        callConfirmationDialog.setLeftButtonText("Cancel");
        callConfirmationDialog.setRightButtonText("Call");
        callConfirmationDialog.setDialogBody("Call " + number + "?");
        callConfirmationDialog.showDialog();

    }

    boolean isDualSIM;

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void telephonyInit() {
        TelephonyInfo telephonyInfo = TelephonyInfo.getInstance(getActivity());

        String imsiSIM1 = telephonyInfo.getImsiSIM1();
        String imsiSIM2 = telephonyInfo.getImsiSIM2();
        boolean isSIM1Ready = telephonyInfo.isSIM1Ready();
        boolean isSIM2Ready = telephonyInfo.isSIM2Ready();

        isDualSIM = telephonyInfo.isDualSIM();
        AppConstants.setIsDualSimPhone(isDualSIM);
        Log.i("IsDual", isDualSIM + "");
        Log.i("SIM1 ready", isSIM1Ready + "");
        Log.i("SIM2 ready", isSIM2Ready + "");


    }


    // A method to check if a permission is granted then execute tasks depending on that
    // particular permission
    @TargetApi(Build.VERSION_CODES.M)
    private void checkPermissionToExecute(String permissions[], int requestCode, Runnable
            runnable) {

        boolean logs = ContextCompat.checkSelfPermission(getActivity(), permissions[0]) !=
                PackageManager.PERMISSION_GRANTED;
        boolean contacts = ContextCompat.checkSelfPermission(getActivity(), permissions[1]) !=
                PackageManager.PERMISSION_GRANTED;
        boolean location = ContextCompat.checkSelfPermission(getActivity(), permissions[2]) !=
                PackageManager.PERMISSION_GRANTED;
        if (logs || contacts || location) {
            requestPermissions(permissions, requestCode);
        } else {
            runnable.run();
        }
    }

    @Override
    @TargetApi(Build.VERSION_CODES.M)
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == AppConstants.READ_LOGS && permissions[0].equals(Manifest.permission
                .READ_CALL_LOG) && permissions[1].equals(Manifest.permission.READ_CONTACTS)
                && permissions[2].equals(Manifest.permission.ACCESS_COARSE_LOCATION)) {
            if (grantResults[0] == PermissionChecker.PERMISSION_GRANTED && grantResults[1] ==
                    PermissionChecker.PERMISSION_GRANTED &&
                    grantResults[2] == PermissionChecker.PERMISSION_GRANTED) {
                logsRunnable.run();
            } else {
                new AlertDialog.Builder(getActivity())
                        .setMessage("The app needs these permissions to work, Exit?")
                        .setTitle("Permission Denied")
                        .setCancelable(false)
                        .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                checkPermissionToExecute(requiredPermissions, AppConstants
                                        .READ_LOGS, logsRunnable);
                            }
                        })
                        .setNegativeButton("Exit App", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                getActivity().finish();
                            }
                        }).show();
            }
        }
    }


    private void simSlotDetection(String number) {
        Uri allCalls = Uri.parse("content://call_log/calls");
        Cursor c = getActivity().getContentResolver().query(allCalls, null, CallLog.Calls.NUMBER
                + " =?", new String[]{number}, null);
        if (c != null && c.getCount() > 0) {
            String id = c.getString(c.getColumnIndex(CallLog.Calls.PHONE_ACCOUNT_ID));
            Log.i("Sim slot id", id + " of number " + number);
        }
        c.close();

    }

    @TargetApi(Build.VERSION_CODES.M)
    private Cursor getCallHistoryDataByNumber(String number) {
        Cursor cursor = null;
        String order = CallLog.Calls.DATE + " DESC";
        try {
            cursor = getActivity().getContentResolver().query(CallLog.Calls.CONTENT_URI, null,
                    CallLog.Calls.NUMBER + " =?", new String[]{number}, order);

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
            cursor = getActivity().getContentResolver().query(CallLog.Calls.CONTENT_URI, null,
                    CallLog
                            .Calls.CACHED_NAME + " =?", new String[]{name}, order);

        } catch (SecurityException e) {
            e.printStackTrace();
        }
        return cursor;
    }


    private ArrayList callLogHistory(String number) {
        String numberToSearch = number;
        ArrayList<CallLogType> callDetails = new ArrayList<>();
        Cursor cursor;

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
                    String accountId = "";
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        accountId = cursor.getString(account_id);
                        if (!TextUtils.isEmpty(accountId) && account_id > 0)
                            Log.e("Sim Type", accountId);
                        String accountName = cursor.getString(account);
                        if (!TextUtils.isEmpty(accountName))
                            Log.e("Sim Name", accountName);
                        String userImage = cursor.getString(profileImage);
                        if (userImage != null)
                            Log.e("User Image", userImage);
                    }
                    int histroyId = Integer.parseInt(cursor.getString(callLogId));
                    CallLogType logObject = new CallLogType();
                    logObject.setHistoryNumber(phNum);
                    logObject.setHistoryType(callType);
                    logObject.setHistoryDate(dateOfCall);
                    logObject.setHistoryDuration(Integer.parseInt(callDuration));
                    logObject.setHistoryCallSimNumber(accountId);
                    logObject.setHistoryId(histroyId);

                    Date date1 = new Date(dateOfCall);
                    String callDataAndTime = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a").format(date1);
                    logObject.setCallDateAndTime(callDataAndTime);

                    String typeOfCall = getLogType(callType);
                    logObject.setTypeOfCall(typeOfCall);

                    String durationtoPass = logObject.getCoolDuration(Float.parseFloat(callDuration));
                    logObject.setDurationToPass(durationtoPass);

                    callDetails.add(logObject);
                }
            }
            cursor.close();


        } catch (SecurityException e) {
            e.printStackTrace();
        }

        return callDetails;
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            if (AppConstants.isFromReceiver) {
                CallLogType callLogType = new CallLogType(getActivity());
                String name = callLogType.findNameByNumber(callLogTypeReceiver.getNumber());
                if (!TextUtils.isEmpty(name))
                    callLogTypeReceiver.setName(name);
                ArrayList<CallLogType> arrayListHistroy = callLogHistory(callLogTypeReceiver.getNumber());
                int count = arrayListHistroy.size();
                callLogTypeReceiver.setHistoryLogCount(count);
                String receiverDate = "Today";
                if (!arrayListObjectCallLogs.contains(receiverDate)) {
                    arrayListCallLogHeader.add(0, receiverDate);
                    arrayListObjectCallLogs.add(0, receiverDate);
                    callLogListAdapter.notifyItemInserted(0);
                    recyclerCallLogs.scrollToPosition(0);
                }

                arrayListObjectCallLogs.add(1, callLogTypeReceiver);
                callLogListAdapter.notifyItemInserted(1);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Override
    public void onPause() {
        super.onPause();
        AppConstants.isFromReceiver = false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }


}
