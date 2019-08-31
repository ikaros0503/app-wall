package com.library.android.appwall.library.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.transition.DrawableCrossFadeTransition
import com.library.android.appwall.library.R
import com.library.android.appwall.library.goStore
import com.library.android.appwall.library.model.App
import kotlinx.android.synthetic.main.item_app.view.*

class AppWallItemAdapter :
    ListAdapter<App, AppWallItemAdapter.ItemViewHolder>(DiffItemCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_app,
                parent,
                false
            )
        )
    }

    private var onItemClickListener: ((App, Int) -> Unit)? = null

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        getItem(position)?.also { item ->
            Glide.with(holder.itemView)
                .load(item.icon)
                .transform(RoundedCorners(12))
                .centerCrop()
                .into(holder.itemView.image_icon)

            holder.itemView.rating_bar.isEnabled = false
            holder.itemView.image_new.visibility = if (item.isNew) View.VISIBLE else View.GONE
            holder.itemView.text_title.text = item.getTitle()
            holder.itemView.text_description.text = item.getDescription()
            holder.itemView.rating_bar.rating = item.stars
            holder.itemView.setOnClickListener { onItemClickListener(it, item, position) }
            holder.itemView.button_install.setOnClickListener { onItemClickListener(it, item, position) }
        }
    }


    private fun onItemClickListener(view: View, item: App, position: Int) {
        if (item.packageId.isEmpty()) return
        view.context.goStore(item.packageId)
        onItemClickListener?.invoke(item, position)
    }

    fun registerOnItemClickListener(action: (App, Int) -> Unit) {
        onItemClickListener = action
    }

    class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view)

    class DiffItemCallback : DiffUtil.ItemCallback<App>() {
        override fun areItemsTheSame(oldItem: App, newItem: App): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: App, newItem: App): Boolean {
            return oldItem.packageId == newItem.packageId
        }

    }
}