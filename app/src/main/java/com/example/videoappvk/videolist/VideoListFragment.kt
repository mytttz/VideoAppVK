package com.example.videoappvk.videolist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.videoappvk.R
import com.example.videoappvk.databinding.FragmentVideoListBinding
import com.example.videoappvk.network.ConnectivityChecker
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class VideoListFragment : Fragment() {

    private val viewModel: VideoListViewModel by viewModel()
    private val connectivityChecker: ConnectivityChecker by inject()

    private var _binding: FragmentVideoListBinding? = null
    private val binding get() = _binding!!

    private lateinit var videoAdapter: VideoAdapter
    private lateinit var searchAdapter: VideoSearchAdapter
    private var searchJob: Job? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVideoListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        checkInternetConnection()
        setupRecyclerView()
        setupSearchBar()
        setupObserver()
    }

    private fun checkInternetConnection() {
        if (!connectivityChecker.hasInternetConnection()) {
            Toast.makeText(
                requireContext(),
                getString(R.string.checkConnection),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun setupRecyclerView() {
        videoAdapter = VideoAdapter { videoID ->
            viewModel.selectedVideo(binding.root, videoID)
        }.apply {
            addLoadStateListener {
                binding.swipeRefreshLayout.isRefreshing = it.refresh is LoadState.Loading
            }
        }

        binding.videoList.apply {
            adapter = videoAdapter.withLoadStateFooter(LoadStateAdapter { videoAdapter.retry() })
            layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        }

        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.fetchVideos()
            binding.swipeRefreshLayout.isRefreshing = false
        }

    }

    private fun setupSearchBar() {
        searchAdapter = VideoSearchAdapter { videoID ->
            viewModel.selectedVideo(binding.root, videoID)
        }.apply {
            addLoadStateListener {
                binding.searchSwipeRefreshLayout.isRefreshing = it.refresh is LoadState.Loading
            }
        }

        binding.videoSearchList.apply {
            adapter = searchAdapter.withLoadStateFooter(LoadStateAdapter { searchAdapter.retry() })
            layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        }

        binding.searchBarInputText.editText.addTextChangedListener { query ->
            searchJob?.cancel()
            searchJob = lifecycleScope.launch {
                delay(1000)
                val searchQuery = query.toString()

                if (searchQuery.isNotBlank()) {
                    if (connectivityChecker.hasInternetConnection()) {
                        viewModel.fetchVideos(searchQuery)
                    } else {
                        viewModel.fetchVideos(searchQuery)
                    }
                } else {
                    viewModel.selectedVideo(PagingData.empty()) // Обновляем LiveData напрямую
                }
            }
        }

        binding.searchSwipeRefreshLayout.setOnRefreshListener {
            val query = binding.searchBarInputText.editText.text.toString()
            viewModel.fetchVideos(query)
            binding.searchSwipeRefreshLayout.isRefreshing = false
        }
    }

    private fun setupObserver() {
        viewModel.videos.observe(viewLifecycleOwner) {
            videoAdapter.submitData(lifecycle, it)
        }

        viewModel.searchedVideos.observe(viewLifecycleOwner) { pagingData ->
            searchAdapter.submitData(lifecycle, pagingData)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
