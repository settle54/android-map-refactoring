package campus.tech.kakao.map

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import campus.tech.kakao.map.model.Place
import campus.tech.kakao.map.databinding.PlaceModuleBinding

class PlacesAdapter(
    private val onClick: (Int) -> Unit
) : RecyclerView.Adapter<PlacesAdapter.ViewHolder>() {

    private var localList: List<Place> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = PlaceModuleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(localList[position])
    }

    override fun getItemCount(): Int {
        return localList.size
    }

    fun updateList(newList: List<Place>) {
        localList = newList
    }

    fun getItemName(position: Int): String {
        return localList[position].name
    }

    inner class ViewHolder(private val binding: PlaceModuleBinding): RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                onClick(bindingAdapterPosition)
            }
        }

        fun bind(place: Place) {
            binding.data = place
        }

    }

}