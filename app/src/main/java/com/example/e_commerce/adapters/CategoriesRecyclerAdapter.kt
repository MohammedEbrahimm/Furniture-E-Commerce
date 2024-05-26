package com.example.e_commerce.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.e_commerce.data.Category1
import com.example.e_commerce.databinding.RecyclerViewCategoryItemBinding

class CategoriesRecyclerAdapter: RecyclerView.Adapter<CategoriesRecyclerAdapter.CategoriesRecyclerAdapterViewHolder>() {
    inner class CategoriesRecyclerAdapterViewHolder(val binding: RecyclerViewCategoryItemBinding):RecyclerView.ViewHolder(binding.root)

    private val diffCallback = object : DiffUtil.ItemCallback<Category1>(){
        override fun areItemsTheSame(oldItem: Category1, newItem: Category1): Boolean {
            return oldItem.rank == newItem.rank
        }

        override fun areContentsTheSame(oldItem: Category1, newItem: Category1): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this,diffCallback)
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CategoriesRecyclerAdapterViewHolder {
        return CategoriesRecyclerAdapterViewHolder(
            RecyclerViewCategoryItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: CategoriesRecyclerAdapterViewHolder, position: Int) {
        val category = differ.currentList[position]
        holder.binding.apply {
            Glide.with(holder.itemView).load(category.image).into(imgCategory)
            tvCategoryName.text = category.name
        }
        holder.itemView.setOnClickListener {
            onItemClick?.invoke(category)
        }
    }
    var onItemClick :((Category1)->Unit)?=null



}