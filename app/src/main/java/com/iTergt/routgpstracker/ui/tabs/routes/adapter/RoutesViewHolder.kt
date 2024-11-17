package com.iTergt.routgpstracker.ui.tabs.routes.adapter

import androidx.recyclerview.widget.RecyclerView
import com.iTergt.routgpstracker.R
import com.iTergt.routgpstracker.databinding.RouteItemBinding
import com.iTergt.routgpstracker.models.RouteModel

class RoutesViewHolder(private val binding: RouteItemBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(
        route: RouteModel,
        onClick: (id: Long) -> Unit,
        deleteItem: (routeModel: RouteModel) -> Unit
    ) {
        binding.run {
            tvData.text = route.date.toString()
            tvSpeed.text =
                binding.root.context.resources.getString(
                    R.string.tv_averageSpeed,
                    route.averageSpeed
                )
            tvTime.text = binding.root.context.resources.getString(R.string.tv_timer, route.time)
            tvDistance.text =
                binding.root.context.resources.getString(R.string.tv_distance, route.distance)
            imbDelete.setOnClickListener {
                deleteItem.invoke(route)
            }
            cardContainer.setOnClickListener {
                onClick.invoke(route.id)
            }
        }
    }
}