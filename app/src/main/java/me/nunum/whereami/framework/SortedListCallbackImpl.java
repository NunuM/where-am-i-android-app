package me.nunum.whereami.framework;

import android.support.v7.util.SortedList;
import android.support.v7.widget.RecyclerView;

import java.util.Comparator;

@SuppressWarnings("JavadocReference")
public class SortedListCallbackImpl<T extends Comparable<T>> extends SortedList.Callback<T> {

    private final RecyclerView.Adapter<?> adapter;

    public SortedListCallbackImpl(RecyclerView.Adapter<?> adapter) {
        this.adapter = adapter;
    }

    /**
     * Similar to {@link Comparator#compare(Object, Object)}, should compare two and
     * return how they should be ordered.
     *
     * @param o1 The first object to compare.
     * @param o2 The second object to compare.
     * @return a negative integer, zero, or a positive integer as the
     * first argument is less than, equal to, or greater than the
     * second.
     */
    @Override
    public int compare(T o1, T o2) {
        return o1.compareTo(o2);
    }

    /**
     * Called by the SortedList when the item at the given position is updated.
     *
     * @param position The position of the item which has been updated.
     * @param count    The number of items which has changed.
     */
    @Override
    public void onChanged(int position, int count) {
        this.adapter.notifyItemRangeChanged(position, count);
    }

    /**
     * Called by the SortedList when it wants to check whether two items have the same data
     * or not. SortedList uses this information to decide whether it should call
     * {@link #onChanged(int, int)} or not.
     * <p>
     * SortedList uses this method to check equality instead of {@link Object#equals(Object)}
     * so
     * that you can change its behavior depending on your UI.
     * <p>
     * For example, if you are using SortedList with a
     * {@link RecyclerView.Adapter RecyclerView.Adapter}, you should
     * return whether the items' visual representations are the same or not.
     *
     * @param oldItem The previous representation of the object.
     * @param newItem The new object that replaces the previous one.
     * @return True if the contents of the items are the same or false if they are different.
     */
    @Override
    public boolean areContentsTheSame(T oldItem, T newItem) {
        return oldItem.equals(newItem);
    }

    /**
     * Called by the SortedList to decide whether two objects represent the same Item or not.
     * <p>
     * For example, if your items have unique ids, this method should check their equality.
     *
     * @param item1 The first item to check.
     * @param item2 The second item to check.
     * @return True if the two items represent the same object or false if they are different.
     */
    @Override
    public boolean areItemsTheSame(T item1, T item2) {
        return item1.equals(item2);
    }

    /**
     * Called when {@code count} number of items are inserted at the given position.
     *
     * @param position The position of the new item.
     * @param count    The number of items that have been added.
     */
    @Override
    public void onInserted(int position, int count) {
        this.adapter.notifyItemRangeInserted(position, count);
    }

    /**
     * Called when {@code count} number of items are removed from the given position.
     *
     * @param position The position of the item which has been removed.
     * @param count    The number of items which have been removed.
     */
    @Override
    public void onRemoved(int position, int count) {
        this.adapter.notifyItemRangeRemoved(position, count);
    }

    /**
     * Called when an item changes its position in the list.
     *
     * @param fromPosition The previous position of the item before the move.
     * @param toPosition   The new position of the item.
     */
    @Override
    public void onMoved(int fromPosition, int toPosition) {
        this.adapter.notifyItemMoved(fromPosition, toPosition);
    }
}


