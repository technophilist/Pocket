package com.example.pocket.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.pocket.data.database.UrlEntity
import com.example.pocket.databinding.UrlItemBinding
import java.io.File


class PocketAdapter(private val onClick: (position: Int) -> Unit) :
    RecyclerView.Adapter<PocketAdapter.PocketViewHolder>() {

    private var mCurrentList = arrayListOf<UrlEntity>()

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
        val item = mCurrentList[position]
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

    /**
     * We are checking only insertion case because delete
     * case is handled by [removeItemAtPosition]
     * to prevent the recycler view getting informed
     * twice
     */
    fun submitList(newList: List<UrlEntity>) {
        if (newList.size>=mCurrentList.size){
            mCurrentList = newList as ArrayList<UrlEntity>
            notifyItemInserted(mCurrentList.size - 1)
        }
    }

    fun addItemAtPos(item: UrlEntity, position: Int) {
        mCurrentList.add(position, item)
        notifyItemInserted(position)
    }

    override fun getItemCount() = mCurrentList.size

    class PocketViewHolder(
        val binding: UrlItemBinding,
        private val onClick: (position: Int) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener { onClick(adapterPosition) }
        }
    }

}