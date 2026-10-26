package com.example.blogapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.blogapp.Model.BlogItemModel
import com.example.blogapp.databinding.ArticleItemBinding

class ArticleAdapter(
    private val context: Context,
    private var blogList: List<BlogItemModel>,
    private val itemClickListener: OnItemClickListener
) : RecyclerView.Adapter<ArticleAdapter.BlogViewHolder>() {

    interface OnItemClickListener {
        fun onEditClick(blogItem: BlogItemModel)
        fun onReadMoreClick(blogItem: BlogItemModel)
        fun onDeleteClick(blogItem: BlogItemModel)
    }

    inner class BlogViewHolder(private val binding: ArticleItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(blogItem: BlogItemModel) {
            binding.heading.text = blogItem.heading
            binding.userName.text = blogItem.userName
            binding.date.text = blogItem.date
            binding.post.text = blogItem.post

            Glide.with(binding.profile.context)
                .load(blogItem.profileImage)
                .into(binding.profile)

            binding.readmorebutton.setOnClickListener { itemClickListener.onReadMoreClick(blogItem) }
            binding.editButton.setOnClickListener { itemClickListener.onEditClick(blogItem) }
            binding.deleteButton.setOnClickListener { itemClickListener.onDeleteClick(blogItem) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BlogViewHolder {
        val binding = ArticleItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BlogViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BlogViewHolder, position: Int) {
        holder.bind(blogList[position])
    }

    override fun getItemCount(): Int = blogList.size

    fun setData(blogSavedList: List<BlogItemModel>) {
        blogList = blogSavedList
        notifyDataSetChanged()
    }
}
