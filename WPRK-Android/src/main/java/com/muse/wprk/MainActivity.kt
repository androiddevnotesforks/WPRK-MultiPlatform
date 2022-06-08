package com.muse.wprk

import android.content.Context
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.media.audiofx.LoudnessEnhancer
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.audio.AudioAttributes
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.muse.wprk.core.NavigationRoutes.*
import com.muse.wprk.main.PodcastHome
import com.muse.wprk.presentation.components.PlayerView
import com.muse.wprk.presentation.podcasts.PodcastDetail
import com.muse.wprk.presentation.podcasts.PodcastViewModel
import com.muse.wprk_concept.presentation.MembershipHome
import com.muse.wprk_concept.presentation.ShowsHome
import com.mwaibanda.virtualgroceries.Domain.Presentation.Navigation.SplashScreen
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity(), AudioManager.OnAudioFocusChangeListener {
    private lateinit var loudnessEnhancer: LoudnessEnhancer

    private val audioManager: AudioManager by lazy {
        getSystemService(Context.AUDIO_SERVICE) as AudioManager
    }
    private var focusCallback: AudioManager.OnAudioFocusChangeListener = AudioManager.OnAudioFocusChangeListener { focusChange ->
        Log.d("Main", "Audio Focus changed to $focusChange")
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var isPlaying by rememberSaveable { mutableStateOf(false) }
            WPRKEntry(isPlaying, { enhanceLoudness(it) }, onPlayPauseClick = { isPlaying = it }) { navController, navPadding, player, context ->
                val backgroundColor = Color.Black
                NavHost(
                    navController = navController,
                    startDestination = SplashScreen.route,
                    modifier = Modifier
                        .background(Color.Black)
                        .padding(navPadding)
                ) {
                    composable(SplashScreen.route) {
                        SplashScreen(navController)
                    }
                    composable(Live.route) {
                        val context = context.current
                        ShowsHome(gradient = backgroundColor, showsViewModel = hiltViewModel()) {
                            switchToURL(it, context, player) {
                                isPlaying = true
                            }
                        }
                    }
                    composable(Podcasts.route) {
                        val context = context.current
                        PodcastHome(navController = navController, backgroundColor = backgroundColor, podcastViewModel = hiltViewModel()){
                            switchToURL(it, context, player) {
                                isPlaying = true
                            }
                        }
                    }
                    composable(Account.route) {
                        val context = context.current
                        MembershipHome(backgroundColor = backgroundColor){
                            switchToURL(it, context, player){
                                isPlaying = true
                            }
                        }
                    }
                    composable(PlayerScreen.route){ PlayerView(player = player, context = context) }
                    composable(
                            PodcastDetail.route,
                            arguments = listOf(
                                navArgument("showID"){ defaultValue = "" },
                                navArgument("imageURL"){ defaultValue = ""},
                                navArgument("title"){ defaultValue = ""},
                                navArgument("subTitle"){ defaultValue = ""}
                            )){ backStackEntry ->

                        val context = context.current
                        PodcastDetail(
                                navController = navController,
                                thumbnailURL = backStackEntry.arguments?.getString("imageURL"),
                                showID = backStackEntry.arguments?.getString("showID"),
                                title = backStackEntry.arguments?.getString("title"),
                                description = backStackEntry.arguments?.getString("subTitle"),
                                gradient = backgroundColor,
                                podcastViewModel = hiltViewModel<PodcastViewModel>()
                        ) {
                            switchToURL(it, context, player) {
                                isPlaying = true
                            }
                        }

                    }
                }
            }

        }

    }
    private fun switchToURL(
        URL: String,
        context: Context,
        player: ExoPlayer,
        onCompletion: () -> Unit
    ) {


        player.apply {
            val dataSourceFactory = DefaultHttpDataSource.Factory()


            val sourceURL = MediaItem.Builder()
                .setUri(URL)
                .setLiveConfiguration(MediaItem.LiveConfiguration.Builder().build().apply {
                    setPlaybackSpeed(1.02f)
                    setAudioAttributes(AudioAttributes.Builder()
                        .setUsage(C.USAGE_MEDIA)
                        .setContentType(C.CONTENT_TYPE_MUSIC)
                        .build(),
                        true
                    )
                    setForegroundMode(true)
                })
                .build()

            val source = ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(sourceURL)


            setMediaSource(source)
            prepare()
            playWhenReady = true

        }.also { enhanceLoudness(it) }

        onCompletion()
    }

    private fun enhanceLoudness(player: ExoPlayer) {
        try {
            val audioPct = 1.2
            val gainmB = Math.round(Math.log10(audioPct) * 10000).toInt()
            loudnessEnhancer = LoudnessEnhancer(player.getAudioSessionId())
            loudnessEnhancer.setTargetGain(gainmB)
            loudnessEnhancer.enabled = true
        } catch (e: RuntimeException) {
            e.printStackTrace()
        }
    }
    override fun onResume() {
        super.onResume()
        audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN)
    }

    override fun onAudioFocusChange(focusChange: Int) { }
}
