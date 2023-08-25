package com.kamilagcabay.kotlininstaclone.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kamilagcabay.kotlininstaclone.Model.Post
import com.kamilagcabay.kotlininstaclone.databinding.RecyclerRowBinding
import com.squareup.picasso.Picasso

class PostAdapter(val postList : ArrayList<Post>) : RecyclerView.Adapter<PostAdapter.PostHolder>() {

    class PostHolder(val recyclerRowBinding: RecyclerRowBinding) : RecyclerView.ViewHolder(recyclerRowBinding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostHolder {
        val recyclerRowBinding = RecyclerRowBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return PostHolder(recyclerRowBinding)
    }

    override fun getItemCount(): Int {
        return postList.size
    }

    override fun onBindViewHolder(holder: PostHolder, position: Int) {
        holder.recyclerRowBinding.recyclerViewUserEmailText.text = postList.get(position).email
        holder.recyclerRowBinding.recyclerViewCommentText.text = postList.get(position).comment
        Picasso.get().load(postList.get(position).downloadUrl).into(holder.recyclerRowBinding.recyclerViewImageView)

    }


}