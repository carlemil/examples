package com.sonymobile.sonyselect.adapter;

import java.util.HashSet;
import java.util.Set;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.sonymobile.sonyselect.api.content.Item;

abstract class AbstractItemPagerAdapter<T1 extends View, T2 extends Item> extends PagerAdapter {
    protected final Set<T1> activeViews = new HashSet<T1>();
    protected T2[] items;

    /**
     * Lock down constructor visibility.
     */
    AbstractItemPagerAdapter() {
    }

    /**
     * Binds content to the given view. For the end adapter to implement.
     * 
     * @param view
     *            The view that shows the data.
     * @param item
     *            The data to show.
     */
    abstract public void bindView(T1 view, T2 item);

    @Override
    public void destroyItem(ViewGroup collection, int position, Object object) {
        collection.removeView((View) object);
        activeViews.remove(object);
    }

    @Override
    public int getCount() {
        return items != null ? items.length : 0;
    }

    /**
     * Gets the item at the given position.
     * 
     * @param position
     *            The position of the item to get within the current item set.
     * @return The requested item.
     */
    public T2 getItemAt(int position) {
        return position >= 0 && position < getCount() ? items[position] : null;
    }

    /**
     * Gets a new, clean, unbound view. For the end adapter to implement.
     * 
     * @return A new view.
     */
    abstract public T1 newView(int position);

    @Override
    public View instantiateItem(ViewGroup collection, int position) {
        T1 view = newView(position);
        T2 item = getItemAt(position);

        if (view != null && item != null) {
            collection.addView(view);
            bindView(view, item);
            activeViews.add(view);
        }

        return view;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    public void reset() {
        setItems(null);
    }

    /**
     * Sets the underlying data set for this adapter.
     * 
     * @param items
     *            The new items to visualize.
     */
    public void setItems(T2[] items) {
        this.items = items;
        notifyDataSetChanged();
    }

    /**
     * Sets the primary item ("selected-ish" item). This method enables the end
     * adapter to further decorate a primary item. For the end adapter to
     * implement.
     * 
     * @param view
     *            The view that shows the primary item.
     */
    abstract public void setPrimaryItem(T1 view);

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        super.setPrimaryItem(container, position, object);

        @SuppressWarnings("unchecked")
        T1 view = (T1) object;
        setPrimaryItem(view);
    }
}
