package com.example.pocket.ui.screens

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pocket.adapters.PocketAdapter
import com.example.pocket.databinding.MainFragmentBinding
import com.example.pocket.utils.*
import com.example.pocket.viewmodels.MainScreenViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class MainScreenFragment : Fragment() {
    private val mBinding get() = _mBinding!!
    private var _mBinding: MainFragmentBinding? = null
    private lateinit var mViewModel: MainScreenViewModel
    private lateinit var mAdapter: PocketAdapter

    /*
    Removed the insertion of fake urls intended for testing from repository
    Changed MainScreenViewModel.filter() to return an empty list if the live data is null
    Created a common coroutine scope for all coroutines in the PocketRepository
    Removed coroutine from saveUrl() and made saveUrl as suspend function
    Known Issues : Thumbnails not working
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _mBinding = MainFragmentBinding.inflate(inflater)
        mViewModel = ViewModelProvider(
            this,
            MainScreenViewModelFactory(requireActivity().application)
        ).get(MainScreenViewModel::class.java)

        mAdapter = PocketAdapter { openUrl(it) }
        mBinding.apply {
            recyclerView.apply {
                adapter = mAdapter
                layoutManager = LinearLayoutManager(context)
                doOnItemSwiped(RecyclerViewSwipeDirections.START) { viewHolder, _ ->
                    mViewModel.deleteUrlItem(viewHolder.adapterPosition)
                    Snackbar
                        .make(requireView(), "Url Deleted", Snackbar.LENGTH_LONG)
                        .setAction("UNDO") { mViewModel.undoDelete() }
                        .show()
                }
            }

            mViewModel.savedUrls.observe(viewLifecycleOwner) { mAdapter.submitList(it) }
            searchView.apply {
                setOnClickListener { mBinding.searchView.isIconified = false }
                doOnTextChanged {
                    lifecycleScope.launchWhenStarted { mAdapter.submitList(mViewModel.filter(it)) }
                }
            }
        }

        /*
        * Intercept the back button for closing the search view when the back button is
        * pressed twice.
        * First press  : closes keyboard
        * Second press : Makes the search view iconified
        * */
        onBackPressed {
            if (!mBinding.searchView.isIconified) mBinding.searchView.isIconified = true
            else {
                isEnabled = false //disable the callback
                activity?.onBackPressed()
            }
        }

        return mBinding.root
    }

    private fun openUrl(pos: Int) {
        val uri = Uri.parse("https://" + mViewModel.getUrlAtPos(pos))
        val openLinkIntent = Intent(Intent.ACTION_VIEW, uri)
        startActivity(openLinkIntent)
    }

    override fun onDestroyView() {
        _mBinding = null
        super.onDestroyView()
    }

}