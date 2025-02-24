package com.example.videoappvk.videolist

import android.view.View
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.findNavController
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.cachedIn
import com.example.videoappvk.R
import com.example.videoappvk.dataclasses.Video
import com.example.videoappvk.network.ConnectivityChecker
import com.example.videoappvk.network.VideoRepository
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class VideoListViewModel(
    private val repository: VideoRepository,
    private val connectivityChecker: ConnectivityChecker
) : ViewModel() {

    private var currentQuery: String? = null
    private var pagingSource: PagingSource<Int, Video>? = null

    private val _videos = MutableLiveData<PagingData<Video>>()
    val videos: LiveData<PagingData<Video>> get() = _videos


    private val _searchedVideos = MutableLiveData<PagingData<Video>>()
    val searchedVideos: LiveData<PagingData<Video>> get() = _searchedVideos

    val error: LiveData<String> get() = _error
    private val _error = MutableLiveData<String>()


    init {
        fetchVideos()
    }

    fun fetchVideos(query: String? = null) {
        currentQuery = query
        pagingSource = repository.getVideosPagingSource(query)
        val pager = Pager(PagingConfig(pageSize = 15)) {
            pagingSource ?: repository.getVideosPagingSource(query)
        }

        viewModelScope.launch {
            if (query != null) {
                pager.flow.cachedIn(viewModelScope).collectLatest {
                    _searchedVideos.postValue(it)
                }
            } else {
                pager.flow.cachedIn(viewModelScope).collectLatest {
                    _videos.postValue(it)
                }
            }
        }
    }

    fun selectedVideo(pagingData: PagingData<Video>) {
        _searchedVideos.postValue(pagingData)
    }

    fun selectedVideo(view: View, video: Video?) {
        if (connectivityChecker.hasInternetConnection()) {
            val videoUrl = video?.video_files?.get(0)?.link ?: ""

            val action =
                VideoListFragmentDirections.actionVideoListFragmentToVideoScreenFragment(videoUrl)
            view.findNavController().navigate(action)
        } else {
            Toast.makeText(
                view.context,
                view.context.getString(R.string.checkConnection),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

}

