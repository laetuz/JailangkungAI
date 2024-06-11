package id.neotica.asclepius.presentation.history.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import id.neotica.asclepius.data.room.AscEntity
import id.neotica.asclepius.databinding.ItemHistoryBinding

class HistoryAdapter(
    private val onItemClick: (entity: AscEntity) -> Unit
) :
    ListAdapter<AscEntity, HistoryAdapter.FavViewHolder>(DIFF_CALLBACK) {

    class FavViewHolder(val binding: ItemHistoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(entity: AscEntity) {
            binding.apply {
                tvId.text = entity.id.toString()
                tvDate.text = entity.dateAdded
                Glide.with(root)
                    .load(entity.imageUri)
                    .circleCrop()
                    .into(ivThumbnail)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavViewHolder {
        val binding =
            ItemHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FavViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FavViewHolder, position: Int) {
        val user = getItem(position)

        holder.bind(user)
        holder.binding.root.setOnClickListener {
            if (user.id != null) {
                onItemClick(
                    AscEntity(
                        id = user.id,
                        imageUri = user.imageUri,
                        dateAdded = user.dateAdded,
                        category = user.category,
                        percentage = user.percentage
                    )
                )
            } else {
                Toast.makeText(it.context, "Username is null", Toast.LENGTH_SHORT).show()
            }
        }

    }

    companion object {
        val DIFF_CALLBACK: DiffUtil.ItemCallback<AscEntity> =
            object : DiffUtil.ItemCallback<AscEntity>() {
                override fun areItemsTheSame(oldUser: AscEntity, newUser: AscEntity): Boolean {
                    return oldUser.id == newUser.id
                }

                @SuppressLint("DiffUtilEquals")
                override fun areContentsTheSame(oldUser: AscEntity, newUser: AscEntity): Boolean {
                    return oldUser == newUser
                }
            }
    }
}