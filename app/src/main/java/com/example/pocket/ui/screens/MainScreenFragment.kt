package com.example.pocket.ui.screens

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pocket.adapters.PocketAdapter
import com.example.pocket.databinding.MainFragmentBinding
import com.example.pocket.utils.MainScreenViewModelFactory
import com.example.pocket.utils.doOnTextChanged
import com.example.pocket.viewmodels.MainScreenViewModel
import kotlinx.coroutines.launch

class MainScreenFragment : Fragment() {
    private val mBinding get() = _mBinding!!
    private var _mBinding: MainFragmentBinding? = null
    private lateinit var mViewModel: MainScreenViewModel
    private lateinit var mAdapter: PocketAdapter
    private val TAG = "MainScreenFragment"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        //initializing binding and viewModel
        _mBinding = MainFragmentBinding.inflate(inflater)
        mViewModel = ViewModelProvider(
            this,
            MainScreenViewModelFactory(requireActivity().application)
        ).get(MainScreenViewModel::class.java)

        //initializing recycler view adapter with onClick action
        mAdapter = PocketAdapter { openUrl(it) }

        //init recycler view
        mBinding.recyclerView.apply {
            adapter = mAdapter
            layoutManager = LinearLayoutManager(context)
        }

        //observing the main list of urls stored in the database
        mViewModel.savedUrls.observe(viewLifecycleOwner) { mAdapter.submitList(it) }

        //Setting up the search view for filtering
        mBinding.searchView.doOnTextChanged {
            lifecycleScope.launch {
                val filteredList = mViewModel.filter(it)
                mAdapter.submitList(filteredList)
            }
        }

        /*
        * Intercept the back button for closing the search view when the back button is
        * pressed twice.
        * First press  : closes keyboard
        * Second press : Makes the search view iconified
        * */
        activity?.let {
            it.onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
                if (!mBinding.searchView.isIconified) {
                    mBinding.searchView.isIconified = true
                } else {
                    isEnabled = false //disable the callback
                    it.onBackPressed()
                }
            }
        }

        //enabling the search view on tap
        mBinding.searchView.setOnClickListener { mBinding.searchView.isIconified = false }

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