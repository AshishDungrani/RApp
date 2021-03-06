package com.rawalinfocom.rcontact.helper;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.rawalinfocom.rcontact.R;
import com.rawalinfocom.rcontact.RContactApplication;
import com.rawalinfocom.rcontact.constants.AppConstants;
import com.rawalinfocom.rcontact.database.DatabaseHandler;
import com.rawalinfocom.rcontact.model.CallLogType;
import com.rawalinfocom.rcontact.model.Country;

import org.apache.commons.lang3.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Monal on 10/10/16.
 * <p>
 * Class containing some static utility methods.
 */

public class Utils {

    private static String LOG_TAG = Utils.class.getSimpleName();

    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private final static AtomicInteger c = new AtomicInteger(0);

    public static int getID() {
        return c.incrementAndGet();
    }

    //<editor-fold desc="Network Info">

    /**
     * Check Network Availability
     */
    public static boolean isNetworkAvailable(Context context) {
        if (context != null) {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context
                    .CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            return netInfo != null && netInfo.isConnectedOrConnecting();
        }
        return false;
    }
    //</editor-fold>

    //<editor-fold desc="SnackBar">

    public static void showErrorSnackBar(@NonNull Context context, View view, String message) {
        Snackbar snackbar = Snackbar
                .make(view, message, Snackbar.LENGTH_LONG);
        View snackBarView = snackbar.getView();
        snackBarView.setBackgroundColor(ContextCompat.getColor(context, R.color
                .colorSnackBarNegative));
        TextView textView = (TextView) snackBarView.findViewById(android.support.design.R.id
                .snackbar_text);
        textView.setTextColor(Color.WHITE);
        snackbar.show();
    }

    public static void showSuccessSnackBar(@NonNull Context context, View view, String message) {
        Snackbar snackbar = Snackbar
                .make(view, message, Snackbar.LENGTH_LONG);
        View snackBarView = snackbar.getView();
        snackBarView.setBackgroundColor(ContextCompat.getColor(context, R.color
                .colorSnackBarPositive));
        TextView textView = (TextView) snackBarView.findViewById(android.support.design.R.id
                .snackbar_text);
        textView.setTextColor(Color.WHITE);
        snackbar.show();
    }

    //</editor-fold>

    //<editor-fold desc="Device Dimensions">
    public static int getDeviceHeight(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return displayMetrics.heightPixels;
    }

    public static int getDeviceWidth(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return displayMetrics.widthPixels;
    }
    //</editor-fold>

    //<editor-fold desc="Data Type Operations">
    public static boolean isArraylistNullOrEmpty(ArrayList arrayList) {
        return !(arrayList != null && arrayList.size() > 0);
    }
    //</editor-fold>

    //<editor-fold desc="Date Time Functions">

    /**
     * Returns UTC time in Date Format
     */
    public static Date getUtcDateTimeAsDate() {
        return getStringDateToDate(getUtcDateTimeAsString());
    }

    /**
     * Returns UTC time in String Format
     */
    public static String getUtcDateTimeAsString() {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        TimeZone timeZone = TimeZone.getTimeZone("UTC");
        Calendar calendar = Calendar.getInstance(timeZone);
        sdf.setTimeZone(timeZone);
        return sdf.format(calendar.getTime());


    }

    /**
     * Converts Date to String
     */
    public static Date getStringDateToDate(String StrDate) {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        Date dateToReturn = null;
        try {
            dateToReturn = sdf.parse(StrDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dateToReturn;
    }

    /**
     * Returns OTP expiration Time
     */
    public static String getOtpExpirationTime(String startTime) {
        String endTime = null;
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        try {
            Date d = sdf.parse(startTime);
            Calendar cal = Calendar.getInstance();
            cal.setTime(d);
            cal.add(Calendar.MINUTE, AppConstants.OTP_VALIDITY_DURATION);
            endTime = sdf.format(cal.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return endTime;
    }

    @SuppressLint("SimpleDateFormat")
    public static String convertDateFormat(String oldFormattedDate, String oldFormat, String
            newFormat) {
        SimpleDateFormat oldDateFormatter = new SimpleDateFormat(oldFormat);
        try {
            Date tempDate = oldDateFormatter.parse(oldFormattedDate);
            SimpleDateFormat newDateFormatter = new SimpleDateFormat(newFormat);
            return newDateFormatter.format(tempDate);
        } catch (Exception ex) {
            ex.printStackTrace();
            return "";
        }
    }

    //</editor-fold>

    //<editor-fold desc="Shared Preferences">

    public static void setStringPreference(Context context, String key, @Nullable String value) {
        SharedPreferences sharedpreferences = context.getSharedPreferences(AppConstants
                .KEY_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static String getStringPreference(Context context, String key, String defaultValue) {
        SharedPreferences sharedpreferences = context.getSharedPreferences(AppConstants
                .KEY_PREFERENCES, Context.MODE_PRIVATE);
        return sharedpreferences.getString(key, defaultValue);
    }

    public static void setArrayListPreference(Context context, String key, @Nullable ArrayList
            arrayList) {
        SharedPreferences sharedpreferences = RContactApplication.getInstance().getSharedPreferences(AppConstants
                .KEY_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(arrayList);
        editor.putString(key, json);
        editor.apply();
    }

    public static ArrayList<String> getArrayListPreference(Context context, String key) {
        SharedPreferences sharedpreferences = context.getSharedPreferences(AppConstants
                .KEY_PREFERENCES, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedpreferences.getString(key, null);
        Type type = new TypeToken<ArrayList>() {
        }.getType();
        return gson.fromJson(json, type);
    }

    public static void setIntegerPreference(Context context, String key, int value) {
        SharedPreferences sharedpreferences = context.getSharedPreferences(AppConstants
                .KEY_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public static int getIntegerPreference(Context context, String key, int defaultValue) {
        SharedPreferences sharedpreferences = context.getSharedPreferences(AppConstants
                .KEY_PREFERENCES, Context.MODE_PRIVATE);
        return sharedpreferences.getInt(key, defaultValue);
    }

    public static void setBooleanPreference(Context context, String key, boolean value) {
        SharedPreferences sharedpreferences = context.getSharedPreferences(AppConstants
                .KEY_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public static boolean getBooleanPreference(Context context, String key, boolean defaultValue) {
        SharedPreferences sharedpreferences = context.getSharedPreferences(AppConstants
                .KEY_PREFERENCES, Context.MODE_PRIVATE);
        return sharedpreferences.getBoolean(key, defaultValue);
    }

    public static void setObjectPreference(Context context, String key, @Nullable Object object) {
        SharedPreferences sharedpreferences = context.getSharedPreferences(AppConstants
                .KEY_PREFERENCES, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        SharedPreferences.Editor editor = sharedpreferences.edit();
        String json = gson.toJson(object);
        editor.putString(key, json);
        editor.apply();
    }

    public static Object getObjectPreference(Context context, String key, Class classObject) {
        try {
            SharedPreferences sharedpreferences = context.getSharedPreferences(AppConstants
                    .KEY_PREFERENCES, Context.MODE_PRIVATE);
            Gson gson = new Gson();
            String json = sharedpreferences.getString(key, "");
            return gson.fromJson(json, classObject);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(LOG_TAG, "method : getObjectPreference()");
            return null;
        }
    }

    public static void setHashMapPreference(Context context, String key, @Nullable HashMap
            hashMap) {
        SharedPreferences sharedpreferences = context.getSharedPreferences(AppConstants
                .KEY_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(hashMap);
        editor.putString(key, json);
        editor.apply();
    }

    public static HashMap<String, String> getHashMapPreference(Context context, String key) {
        SharedPreferences sharedpreferences = context.getSharedPreferences(AppConstants
                .KEY_PREFERENCES, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedpreferences.getString(key, null);
        Type type = new TypeToken<HashMap>() {
        }.getType();
        return gson.fromJson(json, type);
    }

    public static HashMap<String, ArrayList<CallLogType>> getHashMapPreferenceForBlock(Context context, String key) {
        SharedPreferences sharedpreferences = context.getSharedPreferences(AppConstants
                .KEY_PREFERENCES, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedpreferences.getString(key, null);
        /*Type type = new TypeToken<HashMap>() {
        }.getType();*/
        Type type = new TypeToken<HashMap<String, ArrayList<CallLogType>>>() {
        }.getType();
        return gson.fromJson(json, type);
    }


    public static ArrayList<Object> getArrayListCallLogPreference(Context context, String key) {
        SharedPreferences sharedpreferences = context.getSharedPreferences(AppConstants
                .KEY_PREFERENCES, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedpreferences.getString(key, null);
        Type type = new TypeToken<ArrayList<Object>>() {
        }.getType();
        return gson.fromJson(json, type);
    }


    public static void removePreference(Context context, String key) {
        SharedPreferences sharedpreferences = context.getSharedPreferences(AppConstants
                .KEY_PREFERENCES, Context.MODE_PRIVATE);
        sharedpreferences.edit().remove(key).apply();
    }

    //</editor-fold>

    //<editor-fold desc="Progress Dialog">
    private static MaterialProgressDialog progressDialog;

    public static void showProgressDialog(Context context, String msg, boolean isCancelable) {
        if (context != null) {

            progressDialog = (MaterialProgressDialog) MaterialProgressDialog.ctor(context, msg);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setCancelable(isCancelable);
            progressDialog.show();

        }
    }

    public static void hideProgressDialog() {
        try {
            progressDialog.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(LOG_TAG, "method : hideProgressDialog()");
        }
    }
    //</editor-fold>

    //<editor-fold desc="Typeface">

    public static Typeface typefaceRegular(Context context) {
        if (context != null) {
            return Typeface.createFromAsset(context.getAssets(), "fonts/OpenSans-Regular.ttf");
        } else {
            Log.e(LOG_TAG, "method : typefaceRegular() , Null context");
            return null;
        }
    }

    public static Typeface typefaceBold(Context context) {
        if (context != null) {
            return Typeface.createFromAsset(context.getAssets(), "fonts/OpenSans-Bold.ttf");
        } else {
            Log.e(LOG_TAG, "method : typefaceBold() , Null context");
            return null;
        }
    }

    public static Typeface typefaceLight(Context context) {
        if (context != null) {
            return Typeface.createFromAsset(context.getAssets(), "fonts/OpenSans-Light.ttf");
        } else {
            Log.e(LOG_TAG, "method : typefaceLight() , Null context");
            return null;
        }
    }

    public static Typeface typefaceItalic(Context context) {
        if (context != null) {
            return Typeface.createFromAsset(context.getAssets(), "fonts/OpenSans-Italic.ttf");
        } else {
            Log.e(LOG_TAG, "method : typefaceItalic() , Null context");
            return null;
        }
    }

    public static Typeface typefaceSemiBold(Context context) {
        if (context != null) {
            return Typeface.createFromAsset(context.getAssets(), "fonts/OpenSans-Semibold.ttf");
        } else {
            Log.e(LOG_TAG, "method : typefaceSemiBold() , Null context");
            return null;
        }
    }

    public static Typeface typefaceIcons(Context context) {
        if (context != null) {
            return Typeface.createFromAsset(context.getAssets(), "fonts/icon_fonts.ttf");
//            return Typeface.createFromAsset(context.getAssets(), "fonts/icomoon_latest.ttf");
        } else {
            Log.e(LOG_TAG, "method : typefaceSemiBold() , Null context");
            return null;
        }
    }

    //</editor-fold>

    //<editor-fold desc="Image Conversion">

    public static String convertBitmapToBase64(Bitmap imgBitmap) {
//        imgBitmap = Bitmap.createScaledBitmap(imgBitmap, 100, 100, false);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        imgBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    public static String convertBitmapToBase64(Bitmap imgBitmap, String imagePath) {
//        imgBitmap = Bitmap.createScaledBitmap(imgBitmap, 100, 100, false);
        imgBitmap = Bitmap.createScaledBitmap(imgBitmap, 512, 512, false);
        try {
            imgBitmap = modifyOrientation(imgBitmap, imagePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        imgBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    private static Bitmap modifyOrientation(Bitmap bitmap, String image_absolute_path) throws
            IOException {
        ExifInterface ei = new ExifInterface(image_absolute_path);
        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface
                .ORIENTATION_NORMAL);

        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                return rotate(bitmap, 90);

            case ExifInterface.ORIENTATION_ROTATE_180:
                return rotate(bitmap, 180);

            case ExifInterface.ORIENTATION_ROTATE_270:
                return rotate(bitmap, 270);

            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                return flip(bitmap, true, false);

            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                return flip(bitmap, false, true);

            default:
                return bitmap;
        }
    }

    private static Bitmap rotate(Bitmap bitmap, float degrees) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degrees);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix,
                true);
    }

    private static Bitmap flip(Bitmap bitmap, boolean horizontal, boolean vertical) {
        Matrix matrix = new Matrix();
        matrix.preScale(horizontal ? -1 : 1, vertical ? -1 : 1);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix,
                true);
    }

    private Bitmap decodeSampledBitmapFromResource(Resources res, int resId, int reqWidth, int
            reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    //</editor-fold>

    //<editor-fold desc="Keyboard Visibility">
    public static void hideSoftKeyboard(Context context, EditText editText) {
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService
                (Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }


    public static void showSoftKeyboard(Context context, EditText editText) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context
                .INPUT_METHOD_SERVICE);
        imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
    }
    //</editor-fold>

    //<editor-fold desc="Check Location Enability">
    public static boolean isLocationEnabled(Context context) {
        return Settings.Secure.isLocationProviderEnabled(context.getContentResolver(),
                LocationManager.GPS_PROVIDER);
    }
    //</editor-fold>

    public static void callIntent(Context context, String number) {
        String unicodeNumber = number.replace("*", Uri.encode("*")).replace("#", Uri.encode("#"));
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + unicodeNumber));
        context.startActivity(intent);
    }

    public static String setFormattedAddress(String streetName, String neighborhoodName, String
            cityName, String stateName, String countryName, String pinCodeName) {

        String[] addressStrings = {streetName, neighborhoodName, cityName, stateName,
                countryName, pinCodeName};

        String formattedAddress = "";

        for (int j = 0; j < addressStrings.length; j++) {
            if (j != addressStrings.length - 1) {
                if (StringUtils.length(addressStrings[j]) > 0) {
                    formattedAddress = formattedAddress + StringUtils.appendIfMissing
                            (addressStrings[j], ", ");
                }
            } else {
                formattedAddress = formattedAddress + pinCodeName;
            }
        }
        return StringUtils.removeEnd(formattedAddress, ", ");
    }

    public static void setRoundedCornerBackground(View view, int backgroundColor, int radius, int
            strokeWidth, int strokeColor) {
        GradientDrawable gd = (GradientDrawable) view.getBackground().getCurrent();
        gd.setColor(backgroundColor);
        gd.setCornerRadius(radius);
        gd.setStroke(strokeWidth, strokeColor);
    }

    public static String exportDB(Context context) {
        String inFileName = context.getDatabasePath(DatabaseHandler.DATABASE_NAME).getPath();
        try {
            File dbFile = new File(inFileName);
            FileInputStream fis = new FileInputStream(dbFile);

            String outFileName = Environment.getExternalStorageDirectory() + "/" +
                    DatabaseHandler.DATABASE_NAME;

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
            return DatabaseHandler.DATABASE_NAME;
        } catch (Exception e) {
            return null;
        }
    }

    public static void copyToClipboard(Context context, String label, String text) {
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context
                .CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(label, text);
        clipboard.setPrimaryClip(clip);
    }

    public static void changeTabsFont(Context context, TabLayout tabLayout) {
        ViewGroup vg = (ViewGroup) tabLayout.getChildAt(0);
        int tabsCount = vg.getChildCount();
        for (int j = 0; j < tabsCount; j++) {
            ViewGroup vgTab = (ViewGroup) vg.getChildAt(j);
            int tabChildCount = vgTab.getChildCount();
            for (int i = 0; i < tabChildCount; i++) {
                View tabViewChild = vgTab.getChildAt(i);
                if (tabViewChild instanceof TextView) {
                    ((TextView) tabViewChild).setTypeface(Utils.typefaceSemiBold(context));
                }
            }
        }
    }

    public static String getFormattedNumber(Context context, String phoneNumber) {
        if (StringUtils.length(phoneNumber) > 0) {
            if (StringUtils.contains(phoneNumber, "#") || StringUtils.contains(phoneNumber, "*")) {
//                return phoneNumber.replace("*", Uri.encode("*")).replace("#", Uri.encode("#"));
                return phoneNumber;
            } else {
                Country country = (Country) Utils.getObjectPreference(context, AppConstants
                        .PREF_SELECTED_COUNTRY_OBJECT, Country.class);
                String defaultCountryCode = "+91";
                if (country != null) {
                    defaultCountryCode = country.getCountryCodeNumber();
                }
                if (!StringUtils.startsWith(phoneNumber, "+")) {
                    if (StringUtils.startsWith(phoneNumber, "0")) {
                        phoneNumber = defaultCountryCode + StringUtils.substring(phoneNumber, 1);
                    } else {
                        phoneNumber = defaultCountryCode + phoneNumber;
                    }
                }

        /* remove special characters from number */
                return "+" + StringUtils.replaceAll(StringUtils.substring(phoneNumber, 1),
                        "[\\D]", "");
            }
        } else {
            return "";
        }
    }

    public static boolean isLastItemDisplaying(RecyclerView recyclerView) {
        if (recyclerView.getAdapter().getItemCount() != 0) {
            int lastVisibleItemPosition = ((LinearLayoutManager) recyclerView.getLayoutManager())
                    .findLastVisibleItemPosition();
            if (lastVisibleItemPosition != RecyclerView.NO_POSITION && lastVisibleItemPosition ==
                    recyclerView.getAdapter().getItemCount() - 1)
                return true;
        }
        return false;
    }

    public static boolean isFirstItemDisplaying(RecyclerView recyclerView) {
        if (recyclerView.getAdapter().getItemCount() != 0) {
            int firstVisibleItemPosition = ((LinearLayoutManager) recyclerView.getLayoutManager())
                    .findFirstVisibleItemPosition();
            if (firstVisibleItemPosition != RecyclerView.NO_POSITION && firstVisibleItemPosition
                    == 0)
                return true;
        }
        return false;
    }

    public static void setRatingStarColor(Drawable drawable, @ColorInt int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            DrawableCompat.setTint(drawable, color);
        } else {
            drawable.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        }
    }

    public static void addToContact(Context context, String number) {
        Intent intent = new Intent(Intent.ACTION_INSERT,
                ContactsContract.Contacts.CONTENT_URI);
        intent.putExtra(ContactsContract.Intents.Insert.PHONE, number);
        context.startActivity(intent);
    }

    public static void addToExistingContact(Context context, String number) {
        Intent intent = new Intent(Intent.ACTION_INSERT_OR_EDIT,
                ContactsContract.Contacts.CONTENT_URI);
        intent.setType(ContactsContract.Contacts.CONTENT_ITEM_TYPE);
        intent.putExtra(ContactsContract.Intents.Insert.PHONE, number);
        context.startActivity(intent);

    }

    public static String addDateSufixes(int i) {
        String[] sufixes = new String[]{"th", "st", "nd", "rd", "th", "th", "th", "th", "th", "th"};
        switch (i % 100) {
            case 11:
            case 12:
            case 13:
                return i + "th";
            default:
                return i + sufixes[i % 10];

        }
    }

    public static String getLocalTimeFromUTCTime(String timeStamp) {
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date value = formatter.parse(timeStamp);

            SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            dateFormatter.setTimeZone(TimeZone.getDefault());
            timeStamp = dateFormatter.format(value);
        } catch (Exception e) {
            timeStamp = "";
        }
        return timeStamp;
    }

    public static String formatDateTime(String timeStamp, String format) {
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            Date value = formatter.parse(timeStamp);

            SimpleDateFormat dateFormatter = new SimpleDateFormat(format, Locale.getDefault());
            timeStamp = dateFormatter.format(value);
        } catch (Exception e) {
            timeStamp = "";
        }
        return timeStamp;
    }
}
