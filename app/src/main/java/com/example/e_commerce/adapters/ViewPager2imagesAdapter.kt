package com.example.e_commerce.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.e_commerce.R
import com.example.e_commerce.data.Product
import com.example.e_commerce.databinding.ViewpagerImageItemBinding

class ViewPager2imagesAdapter :
    RecyclerView.Adapter<ViewPager2imagesAdapter.ViewPager2imagesViewHolder>() {

    inner class ViewPager2imagesViewHolder(val binding: ViewpagerImageItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(imagePath: String) {
            Glide.with(itemView).load(imagePath)/*.placeholder(R.drawable.waiting_icn)*/
                .error(R.drawable.icn_broken_image_24).into(binding.imageProductDetails)

        }
    }

    val diffCallBack = object : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }
    }

    val diffUtil = AsyncListDiffer(this, diffCallBack)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewPager2imagesViewHolder {
        return ViewPager2imagesViewHolder(
            ViewpagerImageItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun getItemCount(): Int {
        return diffUtil.currentList.size
    }

    override fun onBindViewHolder(holder: ViewPager2imagesViewHolder, position: Int) {

        val image = diffUtil.currentList[position]
        holder.bind(image)
    }


}