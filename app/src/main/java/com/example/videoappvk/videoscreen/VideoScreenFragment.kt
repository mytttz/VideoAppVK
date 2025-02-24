package com.example.videoappvk.videoscreen

import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.videoappvk.R
import com.example.videoappvk.databinding.FragmentVideoScreenBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class VideoScreenFragment : Fragment(R.layout.fragment_video_screen) {

    private val viewModel: VideoScreenViewModel by viewModel()
    private var _binding: FragmentVideoScreenBinding? = null
    private val binding get() = _binding!!
    private lateinit var controllerView: View

    private val args: VideoScreenFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentVideoScreenBinding.bind(view)

        initializeViews()
        hideSystemBars()

        viewModel.initializePlayer(requireContext(), args.videoUrl)

        viewModel.isPlaying.observe(viewLifecycleOwner) { updatePlayPauseIcon(it) }

        binding.playerView.player = viewModel.player
    }

    private fun initializeViews() {
        controllerView = binding.playerView.findViewById(R.id.exo_controller)

        val playPauseButton = controllerView.findViewById<ImageButton>(R.id.exo_play)
        val rewindButton = controllerView.findViewById<ImageButton>(R.id.exo_rew)
        val forwardButton = controllerView.findViewById<ImageButton>(R.id.exo_ffwd)

        rewindButton.setImageResource(R.drawable.replay_10_icon)
        forwardButton.setImageResource(R.drawable.forward_10_icon)

        playPauseButton.setOnClickListener { viewModel.togglePlayback() }
        rewindButton.setOnClickListener { viewModel.seekBackward() }
        forwardButton.setOnClickListener { viewModel.seekForward() }

        binding.backButton.setOnClickListener { findNavController().navigateUp() }
    }

    private fun updatePlayPauseIcon(isPlaying: Boolean) {
        controllerView.findViewById<ImageButton>(R.id.exo_play)
            .setImageResource(if (isPlaying) R.drawable.pause_icon else R.drawable.play_icon)
    }

    private fun hideSystemBars() {
        val window = requireActivity().window
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> {
                window.insetsController?.hide(WindowInsets.Type.systemBars())
            }

            else -> {
                window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            }
        }
    }

    private fun restoreSystemBars() {
        val window = requireActivity().window
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> {
                window.insetsController?.show(WindowInsets.Type.systemBars())
            }

            else -> {
                window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
            }
        }
    }

    override fun onStart() {
        super.onStart()
        viewModel.player.playWhenReady = true
    }

    override fun onPause() {
        super.onPause()
        viewModel.savePlayerState()
        viewModel.player.playWhenReady = false
        restoreSystemBars()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
