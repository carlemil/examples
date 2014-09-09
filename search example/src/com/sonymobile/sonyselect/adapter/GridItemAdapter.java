
package com.sonymobile.sonyselect.adapter;

import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sonymobile.sonyselect.R;
import com.sonymobile.sonyselect.application.SonySelectApplication;
import com.sonymobile.sonyselect.components.RelativeHeightImageView;
import com.sonymobile.sonyselect.domain.GooglePlayItem;
import com.sonymobile.sonyselect.domain.ItemUtil;
import com.sonymobile.sonyselect.net.VolleySingelton;
import com.sonymobile.sonyselect.util.ColorUtil;

public class GridItemAdapter extends AbstractItemAdapter {
    private static final String LOG_TAG = GridItemAdapter.class.getName();
    private final int layoutId;

    public GridItemAdapter(Context context, int layoutId) {
        super(context);
        this.layoutId = layoutId;
    }

    @Override
    public View newView(ViewGroup parent) {
        View view = inflater.inflate(layoutId, parent, false);
        NetworkImageViewHolder viewHolder = new GridItemViewHolder((RelativeHeightImageView) view.findViewById(R.id.image),
                (TextView) view.findViewById(R.id.title), view.findViewById(R.id.color));
        view.setTag(viewHolder);

        return view;
    }

    @Override
    public void bindView(View view, GooglePlayItem item) {
        GridItemViewHolder viewHolder = (GridItemViewHolder) view.getTag();

        if (viewHolder != null) {
            if (item != null) {
                viewHolder.getTitle().setText(item.title);
                List<ImageLink> imageLinks = ItemUtil.getImageLinks(item, SonySelectApplication.get().getResources().getStringArray(R.array.icon_link_rel));
                if (imageLinks == null || imageLinks.get(0) == null) {
                    Log.d(LOG_TAG, "Binding view. item id:" + item.id + " item title:" + item.title + " url: null (item image links missing or doesn't match?)");
                } else {
                    Log.d(LOG_TAG, "Binding view. item id:" + item.id + " item title:" + item.title + " NrLinks:" + imageLinks.size() + " url:" + imageLinks.get(0).getImageUrl());
                }
                viewHolder.getImage().setImageUrl(imageLinks.get(0).getImageUrl(), VolleySingelton.getInstance()
                        .getImageLoader());

                viewHolder.setItemImageLinks(imageLinks);
                viewHolder.getColor().setBackgroundColor(ColorUtil.getBarColor(item));
            } else {
                Log.d(LOG_TAG, "Binding view. Item not found");
                viewHolder.getTitle().setText(null);
            }
        }
    }

    public void setItems(GooglePlayItem[] items) {
        super.setItems(items);

        notifyDataSetChanged();
    }

    public int getNumberOfItems() {
        return super.getNumberOfItems();
    }
}
