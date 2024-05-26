package com.example.e_commerce.adapters

import android.graphics.Paint
import android.health.connect.datatypes.units.Percentage
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.e_commerce.data.Product
import com.example.e_commerce.databinding.BestDealsRvItemBinding

class BestDealsAdapter : RecyclerView.Adapter<BestDealsAdapter.BestDealsViewHolder>() {

    inner class BestDealsViewHolder(private val binding: BestDealsRvItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(products: Product) {
            Glide.with(itemView).load(products.images[0]).into(binding.imageBestDealsRV)
            binding.tvBestDealsName.text = products.name
            products.offerPercentage?.let {
                val remainingPricePercentage = 1f - it
                val priceAfterOffer = remainingPricePercentage * products.price
                binding.tvBestDealsNewPrice.text = "$ ${String.format("%.2f", priceAfterOffer)}"
                binding.tvBestDealsOldPrice.paintFlags= Paint.STRIKE_THRU_TEXT_FLAG // this will make sure to draw a line over this text view
            }
            if (products.offerPercentage == null)
                binding.tvBestDealsNewPrice.visibility = View.INVISIBLE
            binding.tvBestDealsOldPrice.text = "$ ${products.price}"

            /*   val offerPercentage = products.offerPercentage*/
            /*  binding.tvBestDealsNewPrice.text =
                  String.format("%.2f",newPrice(products.price, offerPercentage))*/

        }
    }

    /*   private fun newPrice(oldPrice: Float, offerPercentage: Float?): Float {

           return (oldPrice - oldPrice * offerPercentage!!)
           this is another way to operate the newPrice

       }*/

    private val differCallback = object : DiffUtil.ItemCallback<Product>() {
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem == newItem
        }

    }
    val differ = AsyncListDiffer(this, differCallback)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BestDealsViewHolder {
        return BestDealsViewHolder(
            BestDealsRvItemBinding.inflate(
                LayoutInflater.from(parent.context)
            )
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: BestDealsViewHolder, position: Int) {
        val products = differ.currentList[position]
        holder.bind(products)
        holder.itemView.setOnClickListener {
            onClick?.invoke(products)
        }
    }
    var onClick:((Product) -> Unit)?=null
}

