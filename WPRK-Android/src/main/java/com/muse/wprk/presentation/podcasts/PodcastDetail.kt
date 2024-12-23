package com.muse.wprk.presentation.podcasts

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import coil.request.ImageRequest
import com.muse.wprk.core.exts.parse
import com.mwaibanda.wprksdk.main.model.Episode
import com.muse.wprk.presentation.components.EpisodeRow
import com.muse.wprk.presentation.components.ExpandableText
import com.muse.wprk.presentation.swapList

@Composable
fun PodcastDetail(
    thumbnailURL: String?,
    showID: String?,
    title: String?,
    description: String?,
    gradient: Color,
    podcastViewModel: PodcastViewModel,
    onEpisodeClick: (String) -> Unit
) {
    val episodes = remember { mutableStateListOf<Episode>() }
    val canLoadMore by podcastViewModel.canLoadMore.collectAsState()
    val lazyListState = rememberLazyListState()
    podcastViewModel.episodes.observe(LocalLifecycleOwner.current) { newEpisodes ->
        episodes.swapList(newEpisodes)
    }
    LaunchedEffect(key1 = true) {
        podcastViewModel.getEpisodes(showID ?: "")
    }

    LazyColumn(Modifier.fillMaxHeight(), state = lazyListState) {
        item {
            Box(modifier = Modifier.height(350.dp)) {
                Image(painter = rememberAsyncImagePainter(
                    ImageRequest.Builder(LocalContext.current).data(data = thumbnailURL ?: "")
                        .apply(block = fun ImageRequest.Builder.() {
                            crossfade(true)
                        }).build()
                ),
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    contentDescription = null
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colorStops = arrayOf(
                                    Pair(0.4f, Color.Transparent),
                                    Pair(1f, Color.Black)
                                )
                            )
                        )
                ) {
                    Column(
                        modifier = Modifier.fillMaxHeight(),
                        verticalArrangement = Arrangement.Bottom
                    ) {
                        Box(
                            Modifier
                                .padding(start = 10.dp, bottom = 10.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color.White)
                                .padding(horizontal = 15.dp, vertical = 5.dp)
                        ) {
                            Text(text = "Talk", color = Color.Black)
                        }
                    }
                }
            }
        }
        item {
            Column(
                modifier = Modifier
                    .background(gradient)
                    .padding(horizontal = 10.dp)

            ) {
                Text(
                    text = title ?: "",
                    color = Color.White,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold
                )
                ExpandableText(text = description ?: "", minimizedMaxLines = 4)
                Spacer(modifier = Modifier.height(10.dp))
            }
        }
        items(episodes) { episode ->
            if (episodes.first() == episode) {
                Divider(color = Color.Gray.copy(0.3f), thickness = 1.dp)
            }
            EpisodeRow(Modifier.padding(horizontal = 10.dp),episode = episode){ onEpisodeClick(it) }
            Divider(color = Color.Gray.copy(0.3f), thickness = 1.dp)

        }
        item {
            Column(Modifier.fillMaxWidth()) {
                AnimatedVisibility(visible = canLoadMore && episodes.count() > 9 && episodes.last().number > 1, enter = fadeIn(), exit = fadeOut()) {
                    Column(
                        Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(modifier = Modifier.height(15.dp))
                        TextButton(onClick = { podcastViewModel.getEpisodes(showID ?: "") }) {
                            Text(
                                text = "Load More...",
                                color = Color.parse("#ffafcc"),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }
                }
            }
        }
    }
}

