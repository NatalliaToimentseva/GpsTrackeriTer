package com.iTergt.routgpstracker.ui.routes.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import com.iTergt.routgpstracker.databinding.RouteItemBinding
import com.iTergt.routgpstracker.models.RouteModel

class RoutesAdapter(
    private val onClick: (id: Long) -> Unit,
    private val deleteItem: (routeModel: RouteModel) -> Unit
) : PagingDataAdapter<RouteModel, RoutesViewHolder>(RoutesDiffUtils()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoutesViewHolder {
        return RoutesViewHolder(
            RouteItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RoutesViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it, onClick, deleteItem) }
    }
}