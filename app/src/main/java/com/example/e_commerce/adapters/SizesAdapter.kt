package com.example.e_commerce.adapters

import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.e_commerce.databinding.SizesRvItemBinding

class SizesAdapter : RecyclerView.Adapter<SizesAdapter.SizesViewHolder>() {

    private var selectedPosition = -1

    inner class SizesViewHolder(val binding: SizesRvItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(size: String, position: Int) {

            binding.tvSize.text = size

            if (position == selectedPosition)// size is selected
            {
                binding.apply {
                    imgShadow.visibility = View.VISIBLE

                }
            } else // size is selected
            {
                binding.apply {
                    imgShadow.visibility = View.INVISIBLE

                }

            }

        }


    }

    val diffCallback = object : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem

        }
    }
    val differ = AsyncListDiffer(this, diffCallback)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SizesViewHolder {
        return SizesViewHolder(
            SizesRvItemBinding.inflate(
                LayoutInflater.from(parent.context)
            )
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: SizesViewHolder, position: Int) {
        val size = differ.currentList[position]
        holder.bind(size, position)

        holder.itemView.setOnClickListener {
            if (selectedPosition >= 0)
                notifyItemChanged(selectedPosition) // used to unselect the selected item so when we clicked on the item we basically check if the selected position is greater or equal to zero which will rebuild the view of the Rv so it will go above the bind function and execute the same function for the position which will execute the else body and select the item

            selectedPosition =
                holder.adapterPosition // after we unselect that item we will select the new item by this to lines 1
            notifyItemChanged(selectedPosition)                                                                  // 2
            onItemClick?.invoke(size)
        }
    }

    var onItemClick: ((String) -> Unit)? = null

}