package com.ruifen9.uicomponents

import android.content.Context
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration

class LinearSpacingItemDecoration(
    context: Context,
    space: Int,
    @RecyclerView.Orientation orientation: Int
) : ItemDecoration() {
    private val space: Int
    private val orientation: Int
    override fun getItemOffsets(
        outRect: Rect, view: View,
        parent: RecyclerView, state: RecyclerView.State
    ) {
        if (orientation == RecyclerView.VERTICAL) {
            outRect.left = space
            outRect.right = space
            outRect.bottom = space

            // Add top margin only for the first item to avoid double space between items
            if (parent.getChildAdapterPosition(view) == 0) outRect.top = space
        } else {
            outRect.top = space
            outRect.left = space
            outRect.bottom = space

            // Add top margin only for the first item to avoid double space between items
            if (parent.getChildAdapterPosition(view) == parent.childCount - 1) {
                outRect.right = space
            } else {
                outRect.right = 0
            }
        }
    }

    private fun dip2px(context: Context, dpValue: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (dpValue * scale + 0.5f).toInt()
    }

    init {
        this.space = dip2px(context, space.toFloat())
        this.orientation = orientation
    }
}