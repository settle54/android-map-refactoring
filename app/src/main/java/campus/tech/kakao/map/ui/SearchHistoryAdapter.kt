package campus.tech.kakao.map.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import campus.tech.kakao.map.model.RecentSearchWord
import campus.tech.kakao.map.databinding.SearchHistoryModuleBinding

class SearchHistoryAdapter(
    private val onDeleteClick: (Int) -> Unit,
    private val onTextClick: (Int) -> Unit
) : ListAdapter<RecentSearchWord, SearchHistoryAdapter.ViewHolder>(diffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            SearchHistoryModuleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    fun getItemName(position: Int): String {
        return getItem(position).word
    }

    inner class ViewHolder(private val binding: SearchHistoryModuleBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.deleteHistory.setOnClickListener {
                onDeleteClick(bindingAdapterPosition)
            }
            binding.seachRecord.setOnClickListener {
                onTextClick(bindingAdapterPosition)
            }
        }

        fun bind(recentSearchWord: RecentSearchWord) {
            binding.data = recentSearchWord
        }
    }

    companion object {

        val diffUtil = object : DiffUtil.ItemCallback<RecentSearchWord>() {
            override fun areItemsTheSame(
                oldItem: RecentSearchWord,
                newItem: RecentSearchWord
            ): Boolean {
                return (oldItem.word == newItem.word)
            }

            override fun areContentsTheSame(
                oldItem: RecentSearchWord,
                newItem: RecentSearchWord
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}