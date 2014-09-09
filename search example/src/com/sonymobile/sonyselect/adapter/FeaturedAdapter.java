
package com.sonymobile.sonyselect.adapter;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sonymobile.sonyselect.R;
import com.sonymobile.sonyselect.activities.LaunchActivity;
import com.sonymobile.sonyselect.components.RelativeHeightImageView;
import com.sonymobile.sonyselect.domain.GooglePlayItem;
import com.sonymobile.sonyselect.domain.ItemUtil;
import com.sonymobile.sonyselect.net.VolleySingelton;
import com.sonymobile.sonyselect.receiver.BackgroundColorUpdateReceiver;
import com.sonymobile.sonyselect.util.ColorUtil;
import com.sonymobile.sonyselect.util.StringUtil;

public class FeaturedAdapter extends AbstractItemAdapter {

    private static final String LOG_TAG = null;

    private final Resources resources;

    private final boolean showDescription;

    private final int layout;

    private Context mContext;

    private OnListClickListener onListClickListener;

    public FeaturedAdapter(Context context, int layoutResourceId) {
        this(context, layoutResourceId, true);
        this.mContext = context;
    }

    public FeaturedAdapter(Context context, int layoutResourceId, boolean showDescription) {
        super(context);
        this.mContext = context;
        this.showDescription = showDescription;
        this.layout = layoutResourceId;
        this.resources = context.getResources();
    }

    @Override
    public View newView(ViewGroup parent) {
        View view = inflater.inflate(layout, parent, false);
        view.setTag(new FullItemViewHolder(
                (RelativeHeightImageView) view.findViewById(R.id.image),
                (TextView) view.findViewById(R.id.title),
                view.findViewById(R.id.color),
                (TextView) view.findViewById(R.id.genre),
                (TextView) view.findViewById(R.id.description)
                ));

        return view;
    }

    @Override
    public void bindView(View view, final GooglePlayItem item) {

        FullItemViewHolder viewHolder = (FullItemViewHolder) view.getTag();
        if (item != null) {
            List<ImageLink> imageLinks = ItemUtil.getImageLinks(item, resources.getStringArray(R.array.promo_link_rel));

            if (imageLinks != null && imageLinks.get(0) != null && !StringUtil.isEmpty(imageLinks.get(0).getImageUrl())) {
                Log.d(LOG_TAG, "Binding view. item id: " + item.id + " item title:" + item.title + " NrLinks:" + imageLinks.size() + " url:" + imageLinks.get(0).getImageUrl());

                Intent intent = new Intent(LaunchActivity.BACKGROUND_COLOR_UPDATE_LISTENER);
                // You can also include some extra data.
                intent.putExtra(BackgroundColorUpdateReceiver.BACKGROUND_COLOR_UPDATE_URL, imageLinks.get(0).getImageUrl());
                LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
            } else {
                Log.w(LOG_TAG, "Binding view. item id: " + item.id + " item title:" + item.title + " url: null(item image links missing or doesn't match?)" );
            }

            viewHolder.getTitle().setText(item.title);
            viewHolder.getGenre().setText(item.genre);
            viewHolder.setItemImageLinks(imageLinks);
            viewHolder.getColor().setBackgroundColor(ColorUtil.getBarColor(item));
            viewHolder.getImage().setImageUrl(imageLinks.get(0).getImageUrl(), VolleySingelton.getInstance().getImageLoader());


            if (showDescription && viewHolder.getDescription() != null) {
                viewHolder.getDescription().setText(item.description);
                viewHolder.getDescription().setVisibility(View.VISIBLE);
            }

        } else {
            Log.d(LOG_TAG, "Binding view. Item not found.");
            viewHolder.getTitle().setText(null);
            viewHolder.getGenre().setText(null);
            viewHolder.getColor().setBackgroundColor(Color.BLACK);
        }
        viewHolder.getImage().setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onListClickListener != null && item != null) {
                    onListClickListener.onItemClick(item.listId, item.id, 0, getNumberOfItems());
                }
            }
        });

    }

    public void setOnItemClickListener(OnListClickListener listener) {
        onListClickListener = listener;
    }

}
