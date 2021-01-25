package com.example.pocket.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.pocket.data.UrlEntity
import com.example.pocket.databinding.UrlItemBinding
import java.io.File


class PocketAdapter(private val onClick: (position: Int) -> Unit) :
    ListAdapter<UrlEntity, PocketAdapter.PocketViewHolder>(DiffUtilCallback),
//    RecyclerView.Adapter<PocketAdapter.PocketViewHolder>(),
    Filterable {

//    private var currentList: List<UrlEntity> = emptyList()

    private val TAG = "PocketAdapter"

    private val mFilter = object : Filter() {
        override fun performFiltering(constraint: CharSequence) =
            FilterResults().apply {
                values = if (constraint.isEmpty()) currentList
                else currentList.filter { it.contentTitle.contains(constraint, true) }
            }

        @Suppress("UNCHECKED_CAST")
        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            results?.values?.let {
                submitList(it as List<UrlEntity>)
            }
        }
    }

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

    override fun getFilter(): Filter = mFilter
//    override fun getItemCount() = currentList.size

//    fun submitList(list:List<UrlEntity>){
//        val insertedPosition = currentList.size+1
//        currentList = list
//        notifyItemInserted(insertedPosition)
//    }

}


