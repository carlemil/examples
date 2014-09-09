
package com.sonymobile.sonyselect.adapter;

import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.sonymobile.sonyselect.R;
import com.sonymobile.sonyselect.components.RelativeHeightImageView;
import com.sonymobile.sonyselect.domain.GooglePlayItem;
import com.sonymobile.sonyselect.domain.ItemUtil;
import com.sonymobile.sonyselect.net.VolleySingelton;
import com.sonymobile.sonyselect.util.ColorUtil;

public class DetailPagerAdapter extends AbstractItemPagerAdapter<View, GooglePlayItem> {

    protected static final String LOG_TAG = DetailPagerAdapter.class.getCanonicalName();

    private final LayoutInflater layoutInflater;

    private final Resources resources;

    public DetailPagerAdapter(Context context) {
        layoutInflater = LayoutInflater.from(context);
        resources = context.getResources();
    }

    public long getIdForPosition(int position) {
        GooglePlayItem item = getItemAt(position);
        return item != null ? item.id : -1L;
    }

    @Override
    public void setPrimaryItem(View view) {
        if (view != null) {
            View body = view.findViewById(R.id.body);
            body.setAlpha(1.0f);
            View image = view.findViewById(R.id.image);
            image.setAlpha(1.0f);
        }
    }

    @Override
    public void bindView(View view, GooglePlayItem item) {
        if (item != null) {

            FullItemViewHolder viewHolder = (FullItemViewHolder) view.getTag();
            if (viewHolder != null) {
                List<ImageLink> imageLinks = ItemUtil.getImageLinks(item, resources.getStringArray(R.array.image_detail_link_rel));
                viewHolder.setItemImageLinks(imageLinks);
                if (imageLinks == null || imageLinks.get(0) == null) {
                    Log.d(LOG_TAG, "Binding view. item id: " + item.id + " item title:" + item.title +
                            " url: null(item image links missing or doesn't match?)");
                } else {
                    Log.d(LOG_TAG, "Binding view. item id: " + item.id + " item title:" + item.title + " NrLinks:" +
                            imageLinks.size() + " url:" + imageLinks.get(0).getImageUrl());
                }
                viewHolder.getTitle().setText(item.title);
                viewHolder.getGenre().setText(item.genre);
                viewHolder.getColor().setBackgroundColor(ColorUtil.getBarColor(item));
                viewHolder.getDescription().setText(item.description);
                viewHolder.getImage().setImageUrl(imageLinks.get(0).getImageUrl(), VolleySingelton.getInstance().getImageLoader());
            }
        }
    }

    @Override
    public View newView(int position) {
        View view = layoutInflater.inflate(R.layout.detail_item, null, false);
        RelativeHeightImageView imageView = (RelativeHeightImageView) view.findViewById(R.id.image);

        view.setTag(new FullItemViewHolder(
                imageView,
                (TextView) view.findViewById(R.id.title),
                view.findViewById(R.id.color),
                (TextView) view.findViewById(R.id.genre),
                (TextView) view.findViewById(R.id.description)));

        return view;
    }

}
