package com.example.newpipeapp

import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.newpipeapp.databinding.ActivityMainBinding
import kotlinx.coroutines.*
import org.schabi.newpipe.extractor.NewPipe
import org.schabi.newpipe.extractor.ServiceList
import org.schabi.newpipe.extractor.search.SearchInfo
import org.schabi.newpipe.extractor.search.SearchInfoItem

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val videoAdapter = VideoAdapter { videoId -> openVideo(videoId) }
    private val scope = CoroutineScope(Dispatchers.Main + Job())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = videoAdapter

        binding.searchEditText.setOnEditorActionListener { v, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val query = binding.searchEditText.text.toString()
                if (query.isNotBlank()) {
                    searchYouTube(query)
                }
                true
            } else {
                false
            }
        }
    }

    private fun searchYouTube(query: String) {
        binding.progressBar.show()
        scope.launch {
            try {
                val service = ServiceList.YouTube // Youtube come sorgente
                val searchResult: SearchInfo = withContext(Dispatchers.IO) {
                    NewPipe.getSearchResult(service, query, 0)
                }
                val videos = searchResult.items.filterIsInstance<SearchInfoItem.VideoSearchResult>()
                videoAdapter.submitList(videos)
            } catch (e: Exception) {
                Toast.makeText(this@MainActivity, "Errore ricerca: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
            } finally {
                binding.progressBar.hide()
            }
        }
    }

    private fun openVideo(videoId: String) {
        VideoPlayerActivity.start(this, videoId)
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }
}

