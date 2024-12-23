package com.mwaibanda.virtualgroceries.Domain.Presentation.Navigation

import android.view.animation.OvershootInterpolator
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import coil.request.ImageRequest
import com.muse.wprk.core.utilities.NavigationRoutes
import com.mwaibanda.wprksdk.util.Constants
import kotlinx.coroutines.delay

@OptIn(ExperimentalCoilApi::class)
@Composable
fun SplashScreen(navController: NavController){
    val scale = remember {
         Animatable(0.5f)
    }
    LaunchedEffect(key1 = true) {
        scale.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                500, easing = {
                    OvershootInterpolator(2f).getInterpolation(it)
                }
            )
        )
        delay(2200L)
        navController.navigate(NavigationRoutes.PodcastHome.route)


    }
    Column(
        verticalArrangement = Arrangement.Center ,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier =
        Modifier
            .fillMaxHeight()
            .fillMaxWidth()
            .background(Color.Black)
    ) {
        Image(painter = rememberAsyncImagePainter(
            ImageRequest.Builder(LocalContext.current).data(data = Constants.LOGO_URL).apply(block = fun ImageRequest.Builder.() {
                crossfade(true)
            }).build()
        ),
            modifier = Modifier.offset(y = -20.dp).size(150.dp),
            contentScale = ContentScale.Crop,
            contentDescription = null
        )
    }
}

@Preview
@Composable
fun SplashScreenPreview(){
    SplashScreen(rememberNavController())
}