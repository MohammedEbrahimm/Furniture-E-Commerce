package com.example.e_commerce.adapters

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.e_commerce.data.Product
import com.example.e_commerce.databinding.ProductRvItemBinding
import com.example.e_commerce.helper.getProductPrice

class BestProductAdapter : RecyclerView.Adapter<BestProductAdapter.BestProductViewHolder>() {

    inner class BestProductViewHolder(private val binding: ProductRvItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(products: Product) {


            binding.apply {
                Glide.with(itemView).load(products.images[0]).into(binding.imageBestProductRv)
                    val priceAfterOffer =  products.offerPercentage.getProductPrice(products.price)
                     tvBestDealsNewPrice.text = "$ ${String.format("%.2f", priceAfterOffer)}"
                     tvPrice.paintFlags =
                        Paint.STRIKE_THRU_TEXT_FLAG // this will make sure to draw a line over this text view

                if (products.offerPercentage == null)
                    tvBestDealsNewPrice.visibility = View.INVISIBLE
                tvPrice.text = "$ ${products.price}"
                tvBestProductName.text = products.name
            }

        }
    }

    private val differCallback = object : DiffUtil.ItemCallback<Product>() {
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem == newItem
        }

    }

    val differ = AsyncListDiffer(this, differCallback)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BestProductViewHolder {
        return BestProductViewHolder(
            ProductRvItemBinding.inflate(
                LayoutInflater.from(parent.context)
            )
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: BestProductViewHolder, position: Int) {
        val products = differ.currentList[position]
        holder.bind(products)
        holder.itemView.setOnClickListener {
            onClick?.invoke(products)
        }
    }
    var onClick:((Product) -> Unit)?=null
}
