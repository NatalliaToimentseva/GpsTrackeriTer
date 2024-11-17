package com.iTergt.routgpstracker.ui.tabs.routes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.iTergt.routgpstracker.R
import com.iTergt.routgpstracker.databinding.FragmentRoutesBinding
import com.iTergt.routgpstracker.models.RouteModel
import com.iTergt.routgpstracker.repository.SharedPreferencesRepository
import com.iTergt.routgpstracker.ui.tabs.routes.adapter.RoutesAdapter
import com.iTergt.routgpstracker.utils.findTopNavController
import com.iTergt.routgpstracker.utils.snackbar
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

const val ROUTE_DETAILS_ARGS_KEY = "id"

class RoutesFragment : Fragment() {

    private val viewModel: RoutesViewModel by viewModel()
    private val sharedPreference: SharedPreferencesRepository by inject()
    private val disposable = CompositeDisposable()
    private var binding: FragmentRoutesBinding? = null
    private var rwAdapter: RoutesAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRoutesBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedPreference.getUserUid()?.let { uid ->
            disposable.add(
                viewModel.getListRoutes(uid)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                        { result ->
                            initAdapter(result)
                        },
                        { error ->
                            error.message?.let { binding?.rwContainer?.snackbar(it) }
                        }
                    )
            )
        }
        viewModel.operationResult = { message ->
            if (message != null) {
                binding?.rwContainer?.snackbar(message)
            } else {
                binding?.rwContainer?.snackbar(resources.getString(R.string.delete_route_success))
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable.clear()
    }

    private fun initAdapter(routePagingData: PagingData<RouteModel>) {
        binding?.rwContainer?.run {
            layoutManager = LinearLayoutManager(requireContext())
            rwAdapter = RoutesAdapter(
                { id ->
                    val bundle = Bundle().apply {
                        putLong(ROUTE_DETAILS_ARGS_KEY, id)
                    }
                    findTopNavController().navigate(R.id.routeDetailsFragment, bundle)
                },
                { route ->
                    showDeleteRouteDialog(route)

                }).also {
                if (adapter == null) {
                    adapter = it
                }
                (adapter as? RoutesAdapter)?.submitData(lifecycle, routePagingData)
            }
        }
    }

    private fun showDeleteRouteDialog(routeModel: RouteModel) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(resources.getString(R.string.delete_route_dialog_title))
            .setMessage(
                resources.getString(R.string.delete_route_dialog_message)
            )
            .setPositiveButton(getString(R.string.positive_button)) { _, _ ->
                viewModel.deleteRoute(routeModel)
            }
            .setNegativeButton(getString(R.string.negative_button)) { _, _ ->
            }
            .create()
            .show()
    }
}