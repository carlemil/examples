package com.sonymobile.sonyselect.bi;

public enum CustomDimension {
    /**
     * Index of the custom dimension which tracks model.
     */
    MODEL(1),

    /**
     * Index of the custom dimension which tracks build.
     */
    BUILD(2),

    /**
     * Index of the custom dimension which tracks network.
     */
    SAMPLE_RATE(3),

    /**
     * Index of the custom dimension which tracks network.
     */
    NETWORK(4),

    /**
     * Index of the custom dimension which tracks list name.
     */
    LIST_NAME(5),

    /**
     * Index of the custom dimension which tracks list position.
     */
    LIST_POSITION(6),

    /**
     * Index of the custom dimension which tracks item position.
     */
    ITEM_POSITION(7),

    /**
     * Index of the custom dimension which tracks number of lists.
     */
    NUMBER_OF_LISTS(8),

    /**
     * Index of the custom dimension which tracks number of items in list.
     */
    NUMBER_OF_ITEMS_IN_LIST(9);

    private final int value;

    private CustomDimension(final int newValue) {
        value = newValue;
    }

    public int getValue() {
        return value;
    }
}
