package com.example.newpipeapp

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.newpipeapp.databinding.ActivityVideoPlayerBinding
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import kotlinx.coroutines.*

class VideoPlayerActivity : AppCompatActivity() {

    companion object {
        private const val EXTRA_VIDEO_ID = "extra_video_id"

        fun start(context: Context, videoId: String) {
            val intent = Intent(context, VideoPlayerActivity::class.java)
            intent.putExtra(EXTRA_VIDEO_ID, videoId)
            context.startActivity(intent)
        }
    }

    private lateinit var binding: ActivityVideoPlayerBinding
    private var player: ExoPlayer? = null
    private val scope = CoroutineScope(Dispatchers.Main + Job())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVideoPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val videoId = intent.getStringExtra(EXTRA_VIDEO_ID)
        if (videoId == null) {
            finish()
            return
        }

        initializePlayer(videoId)
    }

    private fun initializePlayer(videoId: String) {
        player = ExoPlayer.Builder(this).build()
        binding.playerView.player = player

        // Otteniamo URL video con NewPipeExtractor (solo audio-video)
        scope.launch {
            try {
                val url = withContext(Dispatchers.IO) {
                    val service = org.schabi.newpipe.extractor.ServiceList.YouTube
                    val videoInfo = org.schabi.newpipe.extractor.NewPipe.getInfo(service, videoId)
                    // Prendo la migliore stream video + audio (in realtà NewPipeExtractor gestisce questo)
                    videoInfo.streams
                        .filter { it.isMuxed }
                        .maxByOrNull { it.qualityLabel ?: "" }
                        ?.url ?: throw Exception("Stream non disponibile")
                }

                val mediaItem = MediaItem.fromUri(Uri.parse(url))
                player?.setMediaItem(mediaItem)
                player?.prepare()
                player?.play()
            } catch (e: Exception) {
                finish()
            }
        }
    }

    override fun onStop() {
        super.onStop()
        player?.release()
        player = null
        scope.cancel()
    }
}

