package com.iTergt.routgpstracker.ui.tabs.routes.adapter

import androidx.recyclerview.widget.DiffUtil
import com.iTergt.routgpstracker.models.RouteModel

class RoutesDiffUtils : DiffUtil.ItemCallback<RouteModel>() {

    override fun areItemsTheSame(oldItem: RouteModel, newItem: RouteModel): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: RouteModel, newItem: RouteModel): Boolean {
        return oldItem == newItem
    }
}