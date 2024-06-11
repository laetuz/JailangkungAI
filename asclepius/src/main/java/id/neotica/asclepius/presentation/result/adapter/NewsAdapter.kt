package id.neotica.asclepius.presentation.result.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import id.neotica.asclepius.data.remote.response.Articles
import id.neotica.asclepius.databinding.ItemNewsBinding

class NewsAdapter(
    private val onItemClick: (entity: Articles) -> Unit
) :
    ListAdapter<Articles, NewsAdapter.FavViewHolder>(DIFF_CALLBACK) {

    class FavViewHolder(val binding: ItemNewsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(entity: Articles) {
            binding.apply {
                tvId.text = entity.title.toString()
                tvDate.text = entity.author.toString()
                Glide.with(root)
                    .load(entity.urlToImage.toString())
                    .into(ivThumbnail)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavViewHolder {
        val binding =
            ItemNewsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FavViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FavViewHolder, position: Int) {
        val user = getItem(position)

        holder.bind(user)
        holder.binding.root.setOnClickListener {

        }

    }

    companion object {
        val DIFF_CALLBACK: DiffUtil.ItemCallback<Articles> =
            object : DiffUtil.ItemCallback<Articles>() {
                override fun areItemsTheSame(oldUser: Articles, newUser: Articles): Boolean {
                    return oldUser.title == newUser.title
                }

                @SuppressLint("DiffUtilEquals")
                override fun areContentsTheSame(oldUser: Articles, newUser: Articles): Boolean {
                    return oldUser == newUser
                }
            }
    }
}