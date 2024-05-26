package com.example.e_commerce.adapters

import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.e_commerce.databinding.ColorRvItemBinding

class ColorsAdapter : RecyclerView.Adapter<ColorsAdapter.ColorsViewHolder>() {
    private var selectedPosition = -1

    inner class ColorsViewHolder(val binding: ColorRvItemBinding) : ViewHolder(binding.root) {
        fun bind(color: Int, position: Int) {
            val imageDrawable = ColorDrawable(color)
            binding.imageColor.setImageDrawable(imageDrawable)

            if (position == selectedPosition)// color is selected
            {
                binding.apply {
                    imgShadow.visibility = View.VISIBLE
                    imagePicked.visibility = View.VISIBLE
                }
            } else // color is selected
            {
                binding.apply {
                    imgShadow.visibility = View.INVISIBLE
                    imagePicked.visibility = View.INVISIBLE
                }

            }

        }


    }

    val diffCallback = object : DiffUtil.ItemCallback<Int>() {
        override fun areItemsTheSame(oldItem: Int, newItem: Int): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Int, newItem: Int): Boolean {
            return oldItem == newItem

        }
    }
    val differ = AsyncListDiffer(this, diffCallback)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ColorsViewHolder {
        return ColorsViewHolder(
            ColorRvItemBinding.inflate(
                LayoutInflater.from(parent.context)
            )
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: ColorsViewHolder, position: Int) {
        val color = differ.currentList[position]
        holder.bind(color, position)

        holder.itemView.setOnClickListener {
            if (selectedPosition >= 0)
                notifyItemChanged(selectedPosition) // used to unselect the selected item so when we clicked on the item we basically check if the selected position is greater or equal to zero which will rebuild the view of the Rv so it will go above the bind function and execute the same function for the position which will execute the else body and select the item

            selectedPosition =
                holder.adapterPosition // after we unselect that item we will select the new item by this to lines 1
            notifyItemChanged(selectedPosition)                                                                  // 2
            onItemClick?.invoke(color)
        }
    }

    var onItemClick: ((Int) -> Unit)? = null


}