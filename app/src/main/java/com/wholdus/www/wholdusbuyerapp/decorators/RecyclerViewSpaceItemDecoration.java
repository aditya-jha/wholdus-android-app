package com.wholdus.www.wholdusbuyerapp.decorators;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by kaustubh on 11/12/16.
 */

public class RecyclerViewSpaceItemDecoration extends RecyclerView.ItemDecoration {

    private int verticalSpaceHeight;
    private int horizontalSpaceHeight;

    public RecyclerViewSpaceItemDecoration(int verticalSpaceHeight, int horizontalSpaceHeight) {
        this.verticalSpaceHeight = verticalSpaceHeight;
        this.horizontalSpaceHeight = horizontalSpaceHeight;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                               RecyclerView.State state) {
        if (parent.getChildAdapterPosition(view) != parent.getAdapter().getItemCount() - 1) {
            outRect.bottom = verticalSpaceHeight;
            outRect.right = horizontalSpaceHeight;
        }
    }
}
