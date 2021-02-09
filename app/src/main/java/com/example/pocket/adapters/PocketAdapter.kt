package com.example.pocket.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.pocket.data.database.UrlEntity
import com.example.pocket.databinding.UrlItemBinding
import java.io.File


class PocketAdapter(private val onClick: (position: Int) -> Unit) :
    ListAdapter<UrlEntity, PocketAdapter.PocketViewHolder>(DiffUtilCallback) {
    private val TAG = "PocketAdapter"

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) = PocketViewHolder(
        UrlItemBinding
            .inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
        onClick
    )

    override fun onBindViewHolder(holder: PocketViewHolder, position: Int) {
        val item = currentList[position]
        holder.binding.apply {

            //setting the title text of the url item
            contentTextView.text = item.contentTitle

            //setting the host text of the url item.
            urlTextView.text = item.host

            /*
            If the imageAbsolutePath is
            not null,we load the image else the image
            space is left empty
             */
            item.imageAbsolutePath?.let {
                Glide.with(thumbnailImageView)
                    .load(File(item.imageAbsolutePath))
                    .into(thumbnailImageView)
            }

        }
    }

    private object DiffUtilCallback : DiffUtil.ItemCallback<UrlEntity>() {
        override fun areItemsTheSame(
            oldItem: UrlEntity,
            newItem: UrlEntity
        ) = oldItem.id == newItem.id

        override fun areContentsTheSame(
            oldItem: UrlEntity,
            newItem: UrlEntity
        ) = oldItem == newItem
    }

    class PocketViewHolder(
        val binding: UrlItemBinding,
        private val onClick: (position: Int) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener { onClick(adapterPosition) }
        }
    }


}


