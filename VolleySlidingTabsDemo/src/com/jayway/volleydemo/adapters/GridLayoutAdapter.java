
package com.jayway.volleydemo.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.jayway.volleydemo.R;
import com.jayway.volleydemo.data.Repository;
import com.jayway.volleydemo.data.RepositoryChangeListener;
import com.jayway.volleydemo.domain.server.ServerModel.JsonItem;
import com.jayway.volleydemo.domain.server.ServerModel.JsonLink;
import com.jayway.volleydemo.domain.server.ServerModel.JsonList;
import com.jayway.volleydemo.net.VolleySingelton;

public class GridLayoutAdapter implements RepositoryChangeListener {

    private static final String LOG_TAG = GridLayoutAdapter.class.getCanonicalName();

    protected final LayoutInflater inflater;

    public JsonList jsonList = null;

    private Context context;

    private int listID = -1;

    private int selectedItemID = -1;

    public GridLayoutAdapter(Context context) {
        this.inflater = LayoutInflater.from(context);
        this.context = context;
    }

    public GridLayoutAdapter(Context context, int listID) {
        this(context);
        this.listID = listID;
        jsonList = Repository.getList(listID);
        this.context = context;
    }

    public int getCount() {
        if (jsonList != null) {
            return jsonList.items.size();
        } else {
            return 0;
        }
    }

    public Object getItem(int position) {
        if (jsonList != null) {
            return jsonList.items.get(position);
        } else {
            return null;
        }
    }

    public View getSmallView(final int position) {

        final View view = inflater.inflate(R.layout.grid_item, null);

        TextView title = (TextView) view.findViewById(R.id.title);
        NetworkImageView icon = (NetworkImageView) view.findViewById(R.id.image);

        JsonItem item = (JsonItem) getItem(position);
        if (item != null) {
            title.setText(item.title);

            String iconUrl = null;
            for (JsonLink link : item.links) {
                if ("icon-medium".equals(link.rel)) {
                    iconUrl = link.href;
                }
            }
            ImageLoader imageLoader = VolleySingelton.getInstance().getImageLoader();
            icon.setDefaultImageResId(R.drawable.ic_launcher);
            icon.setErrorImageResId(R.drawable.image_load_error);
            icon.setImageUrl(iconUrl, imageLoader);
            setDownloadClickListener(view, item);
        } else {
            title.setText("item == null");
        }

        return view;
    }

    @Override
    public void notifyRepositoryChanged() {
        jsonList = Repository.getList(listID);
    }

    private void setDownloadClickListener(View view, final JsonItem items) {
        view.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                String url = null;
                for (JsonLink link : items.links) {
                    if (link.rel.equals("download")) {
                        url = link.href;
                    }
                }
                if (!TextUtils.isEmpty(url)) {
                    Uri uri = Uri.parse(url);
                    if (TextUtils.isEmpty(uri.getScheme())) {
                        uri = Uri.parse("http://" + url);
                    }
                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    } catch (Exception e) {
                        Log.e(LOG_TAG, "Couldn't get item: " + uri.toString(), e);
                    }
                }
            }
        });
    }

    public int getSelectedItemID() {
        return selectedItemID;
    }

}
