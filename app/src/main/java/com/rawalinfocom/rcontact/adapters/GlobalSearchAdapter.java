package com.rawalinfocom.rcontact.adapters;

import android.content.Context;
import android.graphics.drawable.LayerDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.rawalinfocom.rcontact.R;
import com.rawalinfocom.rcontact.helper.Utils;
import com.rawalinfocom.rcontact.helper.imagetransformation.CropCircleTransformation;
import com.rawalinfocom.rcontact.model.GlobalSearchType;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Aniruddh on 14/06/17.
 */

public class GlobalSearchAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private Context context;
    ArrayList<GlobalSearchType> globalSearchTypeArrayList;

    public GlobalSearchAdapter(Context context, ArrayList<GlobalSearchType> globalSearchTypeList) {
        this.context = context;
        this.globalSearchTypeArrayList = globalSearchTypeList;
    }

    @Override
    public GlobalSearchViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_global_search,
                parent, false);
        return new GlobalSearchViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder contactViewHolder, int position) {
        GlobalSearchViewHolder holder = (GlobalSearchViewHolder) contactViewHolder;
        final GlobalSearchType globalSearchType = (GlobalSearchType) globalSearchTypeArrayList.get(position);
        String firstName =  globalSearchType.getFirstName();
        int isRcpVerified = globalSearchType.getIsRcpVerified();
        if(isRcpVerified == 1){
            holder.textContactFirstname.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
            holder.textContactLastname.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
            holder.textContactNumber.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
            holder.textRatingUserCount.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
            holder.linearRating.setVisibility(View.VISIBLE);
        }else{
            holder.textContactFirstname.setTextColor(ContextCompat.getColor(context, R.color.textColorBlue));
            holder.textContactLastname.setTextColor(ContextCompat.getColor(context, R.color.textColorBlue));
            holder.textContactNumber.setTextColor(ContextCompat.getColor(context, R.color.textColorBlue));
            holder.linearRating.setVisibility(View.GONE);
        }

        if(!StringUtils.isEmpty(firstName))
        {
            holder.textContactFirstname.setText(firstName);
        }else{
            holder.textContactFirstname.setText("");
        }

        String lastName = globalSearchType.getLastName();
        if(!StringUtils.isEmpty(lastName))
            holder.textContactLastname.setText(lastName);
        else
            holder.textContactLastname.setText("");

        String mobileNumber =  globalSearchType.getMobileNumber();
        if(!StringUtils.isEmpty(mobileNumber))
            holder.textContactNumber.setText(mobileNumber);
        else
            holder.textContactNumber.setText("");

        String profileImage =  globalSearchType.getProfileImageUrl();
        if (!StringUtils.isEmpty(profileImage)) {
            Glide.with(context)
                    .load(profileImage)
                    .placeholder(R.drawable.home_screen_profile)
                    .error(R.drawable.home_screen_profile)
                    .bitmapTransform(new CropCircleTransformation(context))
                    .override(500, 500)
                    .into(holder.imageProfile);

        } else {
            holder.imageProfile.setImageResource(R.drawable.home_screen_profile);
        }

        String userRatingCount =  globalSearchType.getProfileRatedCount();
        if(!StringUtils.isEmpty(userRatingCount)){
            holder.textRatingUserCount.setText(userRatingCount);
        }else{
            holder.textRatingUserCount.setText("");
        }

        String averageRating =  globalSearchType.getAverageRating();
        if(!StringUtils.isEmpty(averageRating)){
            holder.ratingUser.setRating(Float.parseFloat(averageRating));
        }else{
            holder.ratingUser.setRating(Float.parseFloat("0"));
        }

        String publicUrl = globalSearchType.getPublicProfileUrl();
        if(!StringUtils.isEmpty(publicUrl)){
            if(isRcpVerified == 1){
                holder.relativeRowMain.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
            }else {
                holder.relativeRowMain.setClickable(false);
            }
        }


    }

    @Override
    public int getItemCount() {
        return globalSearchTypeArrayList.size();
    }


    public class GlobalSearchViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.image_profile)
        ImageView imageProfile;
        @BindView(R.id.image_3dots_call_log)
        ImageView image3dotsCallLog;
        @BindView(R.id.text_temp_number)
        TextView textTempNumber;
        @BindView(R.id.image_social_media)
        ImageView imageSocialMedia;
        @BindView(R.id.button_invite)
        Button buttonInvite;
        @BindView(R.id.text_contact_firstname)
        TextView textContactFirstname;
        @BindView(R.id.text_contact_lastname)
        TextView textContactLastname;
        @BindView(R.id.image_call_type)
        ImageView imageCallType;
        @BindView(R.id.textCount)
        TextView textCount;
        @BindView(R.id.text_contact_number)
        TextView textContactNumber;
        @BindView(R.id.text_contact_date)
        TextView textContactDate;
        @BindView(R.id.text_sim_type)
        TextView textSimType;
        @BindView(R.id.text_rating_user_count)
        TextView textRatingUserCount;
        @BindView(R.id.rating_user)
        RatingBar ratingUser;
        @BindView(R.id.linear_rating)
        LinearLayout linearRating;
        @BindView(R.id.linear_content_main)
        LinearLayout linearContentMain;
        @BindView(R.id.relative_row_main)
        RelativeLayout relativeRowMain;

        GlobalSearchViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            LayerDrawable stars = (LayerDrawable) ratingUser.getProgressDrawable();
            // Filled stars
            Utils.setRatingStarColor(stars.getDrawable(2), ContextCompat.getColor(context, R
                    .color.vivid_yellow));
            // half stars
            Utils.setRatingStarColor(stars.getDrawable(1), ContextCompat.getColor(context,
                    android.R.color.darker_gray));
            // Empty stars
            Utils.setRatingStarColor(stars.getDrawable(0), ContextCompat.getColor(context,
                    android.R.color.darker_gray));

        }
    }
}