
package com.sonymobile.sonyselect.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.util.LongSparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseExpandableListAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.sonymobile.sonyselect.R;
import com.sonymobile.sonyselect.api.content.ItemListInfo;
import com.sonymobile.sonyselect.application.SonySelectApplication;
import com.sonymobile.sonyselect.components.WrappableGridView;
import com.sonymobile.sonyselect.domain.GooglePlayItem;

public final class ListsAdapter extends BaseExpandableListAdapter {

    private static final class ChildViewHolder {
        private final GridView gridView;

        private ChildViewHolder(GridView gridView) {
            this.gridView = gridView;
        }
    }

    private static final class GroupViewHolder {
        private final TextView title;

        private final TextView expandText;

        private final GridView preview;

        private GroupViewHolder(TextView textView, TextView expandTextView, GridView gridView) {
            title = textView;
            if (title != null) {
                // force selection to force scrolling.
                title.setSelected(true);
            }
            expandText = expandTextView;
            preview = gridView;
        }
    }

    private final Context context;

    private final LayoutInflater layoutInflater;

    private final LongSparseArray<GooglePlayItem[]> previewItems;

    private final LongSparseArray<GooglePlayItem[]> expandItems;

    private ItemListInfo[] lists;

    private OnListClickListener onListClickListener;

    public ListsAdapter(Context context) {
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
        this.previewItems = new LongSparseArray<GooglePlayItem[]>();
        this.expandItems = new LongSparseArray< GooglePlayItem[]>();
    }

    private void bindChildView(final View view, final Context context, final ItemListInfo list) {
        ChildViewHolder viewHolder = view != null ? (ChildViewHolder) view.getTag() : null;

        if (viewHolder != null && list != null) {
            final GooglePlayItem[] children = expandItems.get(list.id);
            GridItemAdapter gridItemAdapter = (GridItemAdapter) viewHolder.gridView.getAdapter();
            final GooglePlayItem[] items = previewItems.get(list.id);
            if (gridItemAdapter == null) {
                gridItemAdapter = new GridItemAdapter(context, R.layout.lists_grid_item);
                gridItemAdapter.setItems(children);
                viewHolder.gridView.setAdapter(gridItemAdapter);
            } else {
                gridItemAdapter.setItems(children);
            }

            viewHolder.gridView.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapter, View view, int position, long itemId) {
                    if (onListClickListener != null) {
                        onListClickListener.onItemClick(list.id, itemId, position,
                                getNumberOfItemsInList(children, items));
                    }
                }
            });
        }
    }

    private void bindGroupView(final View view, final Context context, final ItemListInfo list) {
        GroupViewHolder viewHolder = (GroupViewHolder) view.getTag();

        if (viewHolder != null) {
            String listTitle = list.title;
            viewHolder.title.setText(listTitle != null ? listTitle : "");

            final GooglePlayItem[] children = expandItems.get(list.id);
            int visibility = children != null && children.length > 0 ? View.VISIBLE : View.GONE;
            viewHolder.expandText.setVisibility(visibility);

            final GooglePlayItem[] items = previewItems.get(list.id);
            GridItemAdapter gridItemAdapter = (GridItemAdapter) viewHolder.preview.getAdapter();

            if (gridItemAdapter == null) {
                gridItemAdapter = new GridItemAdapter(context, R.layout.lists_grid_item);
                gridItemAdapter.setItems(items);
                viewHolder.preview.setAdapter(gridItemAdapter);
            } else {
                gridItemAdapter.setItems(items);
            }

            viewHolder.preview.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapter, View view, int position, long itemId) {
                    if (onListClickListener != null) {
                        onListClickListener.onItemClick(list.id, itemId, position,
                                getNumberOfItemsInList(children, items));
                    }
                }
            });
        }
    }

    private int getNumberOfItemsInList(GooglePlayItem[] children, GooglePlayItem[] items) {
        int childCount = children != null ? children.length : 0;
        int itemCount = items != null ? items.length : 0;
        final int numberOfItemsInList = childCount + itemCount;
        return numberOfItemsInList;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        ItemListInfo list = (ItemListInfo) getGroup(groupPosition);
        long listId = list != null ? list.id : -1L;
        return listId != -1L ? expandItems.get(listId) : null;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return -1L;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild,
            View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = newChildView(parent);
        }

        ItemListInfo list = (ItemListInfo) getGroup(groupPosition);
        bindChildView(convertView, context, list);

        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        ItemListInfo list = (ItemListInfo) getGroup(groupPosition);
        if (list != null) {
            return expandItems.indexOfKey(list.id) >= 0 ? 1 : 0;
        } else {
            return 0;
        }
    }

    @Override
    public Object getGroup(int groupPosition) {
        return groupPosition >= 0 && groupPosition < getGroupCount() ? lists[groupPosition] : null;
    }

    @Override
    public int getGroupCount() {
        return lists != null ? lists.length : 0;
    }

    @Override
    public long getGroupId(int groupPosition) {
        ItemListInfo list = (ItemListInfo) getGroup(groupPosition);
        return list != null ? list.id : -1L;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView,
            ViewGroup parent) {
        if (convertView == null) {
            convertView = newGroupView(parent);
        }

        ItemListInfo list = (ItemListInfo) getGroup(groupPosition);
        if (list != null) {
            bindGroupView(convertView, context, list);
        }
        GroupViewHolder viewHolder = (GroupViewHolder) convertView.getTag();

        if (viewHolder != null) {
            Resources res = SonySelectApplication.get().getResources();
            if (isExpanded) {
                viewHolder.expandText.setText(res.getString(R.string.Less));
                viewHolder.expandText.setCompoundDrawablesWithIntrinsicBounds(0, 0,
                        R.drawable.arrow_up, 0);
            } else {
                viewHolder.expandText.setText(res.getString(R.string.More));
                viewHolder.expandText.setCompoundDrawablesWithIntrinsicBounds(0, 0,
                        R.drawable.arrow_down, 0);
            }
        }

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    private View newChildView(ViewGroup parent) {
        View view = layoutInflater.inflate(R.layout.lists_expand, null, false);
        WrappableGridView gridView = (WrappableGridView) view.findViewById(R.id.gridview);
        ChildViewHolder viewHolder = new ChildViewHolder(gridView);
        view.setTag(viewHolder);

        return view;
    }

    private View newGroupView(ViewGroup parent) {
        View view = layoutInflater.inflate(R.layout.lists_preview, null, false);
        TextView title = (TextView) view.findViewById(R.id.preview_title);
        if (title != null) {
            title.setSelected(true);
        }
        TextView expandText = (TextView) view.findViewById(R.id.preview_more);
        GridView grid = (GridView) view.findViewById(R.id.preview_items);
        grid.setFocusable(false);

        GroupViewHolder viewHolder = new GroupViewHolder(title, expandText, grid);
        view.setTag(viewHolder);

        return view;
    }

    public void setLists(ItemListInfo[] lists) {
        this.lists = null;
        this.lists = lists.clone();
        previewItems.clear();
        expandItems.clear();
        notifyDataSetChanged();
    }

    public void setPreviewItems(long listId, GooglePlayItem[] items) {
        previewItems.put(listId, items);
        notifyDataSetChanged();
    }

    public void setExpandItems(long listId, GooglePlayItem[] items) {
        expandItems.put(listId, items);
        notifyDataSetChanged();
    }

    public void setOnListClickListener(OnListClickListener listener) {
        onListClickListener = listener;
    }

    public GooglePlayItem getItem(long listId, long itemId) {
        GooglePlayItem[] items = null;
        items = previewItems.get(listId);
        if (items != null) {
            for (GooglePlayItem item : items) {
                if (item.id == itemId) {
                    return item;
                }
            }
        }
        items = expandItems.get(listId);
        if (items != null) {
            for (GooglePlayItem item : items) {
                if (item.id == itemId) {
                    return item;
                }
            }
        }
        return null;
    }
}
