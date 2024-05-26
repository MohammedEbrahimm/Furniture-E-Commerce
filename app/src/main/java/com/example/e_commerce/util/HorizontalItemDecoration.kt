package com.example.e_commerce.util

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

// this class job to add some extra spaces between our recyclerViewItems
class HorizontalItemDecoration(private val amount: Int = 15 ):RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        // which you want to add to this amount
        outRect.right = amount
    }

}