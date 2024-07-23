package campus.tech.kakao.map.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import campus.tech.kakao.map.model.Place
import campus.tech.kakao.map.databinding.PlaceModuleBinding

class PlacesAdapter(
    private val onClick: (Place) -> Unit
) : RecyclerView.Adapter<PlacesAdapter.ViewHolder>() {

    private var placesList: List<Place> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = PlaceModuleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(placesList[position])
    }

    override fun getItemCount(): Int {
        return placesList.size
    }

    fun updateList(newList: List<Place>) {
        placesList = newList
    }

    fun getItem(position: Int): Place {
        return placesList[position]
    }

    fun getItemName(position: Int): String {
        return placesList[position].name
    }

    inner class ViewHolder(private val binding: PlaceModuleBinding): RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                onClick(placesList[bindingAdapterPosition])

            }
        }

        fun bind(place: Place) {
            binding.data = place
        }

    }

}