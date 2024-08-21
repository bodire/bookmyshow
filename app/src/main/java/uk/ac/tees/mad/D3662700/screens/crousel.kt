package uk.ac.tees.mad.D3662700.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.delay

@OptIn(ExperimentalPagerApi::class)
@Composable
fun AutoScrollingImageCarousel(images: List<String>) {
    if (images.isEmpty()) {
        // Display a placeholder or return early if there are no images
        Box(
            modifier = Modifier
                .height(200.dp)
                .fillMaxWidth()
                .background(Color.LightGray)
        ) {
            Text(
                "No images available",
                modifier = Modifier.align(Alignment.Center)
            )
        }
        return
    }

    val pagerState = rememberPagerState()

    LaunchedEffect(pagerState, images) {
        while (true) {
            delay(1000)
            if (images.isNotEmpty()) {
                val nextPage = (pagerState.currentPage + 1) % images.size
                pagerState.animateScrollToPage(nextPage)
            }
        }
    }

    HorizontalPager(
        count = images.size,
        state = pagerState,
        modifier = Modifier
            .height(200.dp)
            .fillMaxWidth()
    ) { page ->
        Image(
            painter = rememberAsyncImagePainter(model = images[page]),
            contentDescription = "Carousel Image",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    }
}
