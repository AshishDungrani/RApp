package com.rawalinfocom.rcontact.notifications;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.rawalinfocom.rcontact.R;
import com.rawalinfocom.rcontact.timeline.TimelineActivity;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by maulik on 14/03/17.
 */

public class NotificationsMainAdapter extends RecyclerView.Adapter<NotificationsMainAdapter.MyViewHolder> {

    private List<NotificationItem> list;
    private Context context;
    private final int TYPE_TIMELINE = 1;
    private final int TYPE_REQUESTS = 2;
    private final int TYPE_RATING = 3;
    private final int TYPE_COMMENTS = 4;
    private final int TYPE_RCONTACTS = 5;

    public NotificationsMainAdapter(List<NotificationItem> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_notifications_main, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final NotificationItem item = list.get(position);
        holder.textNotificationHeader.setText(item.getNotificationItemTitle());
        holder.textNotificationsCount.setText("" + item.getNotificationItemCount());
        String[] detailList = item.getNotificationDetail();
        holder.textNotificationDetail.setText(detailList[0]);
        holder.buttonViewMore.setText("View More");
        holder.buttonViewMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                int tabIndex = -1;
                int notificationType = item.getNotificationItemType();
                if (notificationType == TYPE_TIMELINE) {
                    intent = new Intent(context, TimelineActivity.class);
                } else {
                    intent = new Intent(context, NotificationsDetailActivity.class);
                    switch (notificationType) {
                        case TYPE_REQUESTS:
                            tabIndex = NotificationsDetailActivity.TAB_REQUEST;
                            break;
                        case TYPE_RATING:
                            tabIndex = NotificationsDetailActivity.TAB_RATING;
                            break;
                        case TYPE_COMMENTS:
                            tabIndex = NotificationsDetailActivity.TAB_COMMENTS;
                            break;
                        case TYPE_RCONTACTS:
                            tabIndex = NotificationsDetailActivity.TAB_RCONTACTS;
                            break;
                        default:
                            break;
                    }
                    intent.putExtra("TAB_INDEX", tabIndex);
                }
                context.startActivity(intent);
            }
        });
        //  holder.buttonViewMore.setBackgroundColor(Color.parseColor("#003933"));
        Log.i("MAULIK", "position" + position);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.text_notification_header)
        TextView textNotificationHeader;
        @BindView(R.id.text_notifications_count)
        TextView textNotificationsCount;
        @BindView(R.id.text_notification_detail)
        TextView textNotificationDetail;
        @BindView(R.id.button_view_more)
        Button buttonViewMore;

        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
