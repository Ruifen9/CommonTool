package com.ruifen9.uicomponents

import android.os.Bundle
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

interface AutoUpdatableAdapter {

    fun <T> RecyclerView.Adapter<*>.autoNotifyWithDiffUtil(
        old: List<T>,
        new: List<T>,
        isSameItem: (T, T) -> Boolean,
        compare: (T, T) -> Boolean,
        getPayload:( T,T)-> Bundle?
    ): List<T> {
        val diff = DiffUtil.calculateDiff(object : DiffUtil.Callback() {

            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return isSameItem(old[oldItemPosition], new[newItemPosition])
            }

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return compare(old[oldItemPosition], new[newItemPosition])
            }

            override fun getOldListSize() = old.size

            override fun getNewListSize() = new.size

            override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
                val newItem=new[newItemPosition]
                val oldItem=new[oldItemPosition]
                return getPayload(oldItem,newItem)
            }
        })

        diff.dispatchUpdatesTo(this)
        return new
    }
}