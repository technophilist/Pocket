package com.example.pocket.ui.screens

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pocket.adapters.PocketAdapter
import com.example.pocket.databinding.MainFragmentBinding
import com.example.pocket.utils.MainScreenViewModelFactory
import com.example.pocket.viewmodels.MainScreenViewModel

class MainScreenFragment : Fragment() {
    private val mBinding get() = _mBinding!!
    private var _mBinding: MainFragmentBinding? = null
    private lateinit var mViewModel: MainScreenViewModel
    private lateinit var mAdapter: PocketAdapter

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