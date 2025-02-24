package com.example.videoappvk.network


import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.videoappvk.database.AppDatabase
import com.example.videoappvk.database.VideoDao
import com.example.videoappvk.dataclasses.Video

class VideoRepository(
    private val apiService: ApiService,
    private val database: AppDatabase,
    private val connectivityChecker: ConnectivityChecker
) {
    fun getVideosPagingSource(query: String? = null): PagingSource<Int, Video> {
        return if (connectivityChecker.hasInternetConnection()) {
            NetworkVideoPagingSource(apiService, database.videoDao(), query)
        } else {
            DatabaseVideoPagingSource(database.videoDao(), query)
        }
    }

    private inner class NetworkVideoPagingSource(
        private val apiService: ApiService,
        private val videoDao: VideoDao,
        private val query: String?
    ) : PagingSource<Int, Video>() {
        override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Video> {
            val page = params.key ?: 1
            return try {
                val response = when {
                    query != null -> apiService.searchVideos(query, page)
                    else -> apiService.getVideos(page)
                }

                if (response.isSuccessful) {
                    val videos = response.body()?.videos ?: emptyList()
                    if (query == null && videos.isNotEmpty()) {
                        val videoEntities = videos.map { it.toVideoEntity() }
                        videoDao.insertReplace(videoEntities)
                    }

                    val nextKey = if (videos.isEmpty()) null else page + 1

                    LoadResult.Page(
                        data = videos,
                        prevKey = if (page == 1) null else page - 1,
                        nextKey = nextKey
                    )
                } else {
                    LoadResult.Error(Exception("API error"))
                }
            } catch (e: Exception) {
                LoadResult.Error(e)
            }
        }

        override fun getRefreshKey(state: PagingState<Int, Video>): Int? {
            return state.anchorPosition
        }
    }

    private inner class DatabaseVideoPagingSource(
        private val videoDao: VideoDao,
        private val query: String?
    ) : PagingSource<Int, Video>() {

        override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Video> {
            return try {
                val page = params.key ?: 0
                val pageSize = 15
                val videos = if (!query.isNullOrEmpty()) {
                    videoDao.searchVideos(query, page, pageSize)
                } else {
                    videoDao.getVideosPage(page, pageSize)
                }.map { it.toVideo() }

                LoadResult.Page(
                    data = videos,
                    prevKey = if (page == 0) null else page - 1,
                    nextKey = if (videos.size < pageSize) null else page + 1
                )
            } catch (e: Exception) {
                LoadResult.Error(e)
            }
        }

        override fun getRefreshKey(state: PagingState<Int, Video>): Int? {
            return state.anchorPosition?.let { anchorPosition ->
                val anchorPage = state.closestPageToPosition(anchorPosition)
                anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
            }
        }
    }
}