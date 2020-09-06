package com.qltech.bws.Utility;

public interface ItemTouchHelperViewHolder {
    void onItemSelected();


    /**
     * Called when the {@link ItemTouchHelper} has completed the
     * move or swipe, and the active item state should be cleared.
     */

    void onItemClear();
}
