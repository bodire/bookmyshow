package uk.ac.tees.mad.D3662700

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.EaseOutBounce
import androidx.compose.animation.core.EaseOutCirc
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay
import uk.ac.tees.mad.D3662700.ui.theme.BookMyShowTheme
import uk.ac.tees.mad.D3662700.ui.theme.primary







class MainActivity : ComponentActivity() {

    private lateinit var FAuthe: FirebaseAuth


    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FAuthe = FirebaseAuth.getInstance()


        setContent {
            IntroScreen(
                done = {
                    check()
                }
            )
        }
    }






    private fun check() {
        val currentUser = FAuthe.currentUser
        val intent = if (currentUser != null) {
            Intent(this, Dashboard::class.java)
        } else {
            Intent(this, SignIn::class.java)
        }
        startActivity(intent)
        finish()
    }

}


@Composable
fun IntroScreen(done: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            FallingLogo(done)

            Spacer(modifier = Modifier.height(16.dp))

        }
    }
}

@Composable
fun FallingLogo(onAnimationFinish: () -> Unit) {
    var startAnimation by remember { mutableStateOf(false) }
    val yOffset by animateFloatAsState(
        targetValue = if (startAnimation) 0f else -1000f,
        animationSpec = tween(durationMillis = 1000, easing = EaseOutBounce)
    )
    val rotation by animateFloatAsState(
        targetValue = if (startAnimation) 0f else 360f,
        animationSpec = tween(durationMillis = 1000, easing = EaseOutCirc)
    )
    val alpha by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = 500)
    )

    LaunchedEffect(Unit) {
        startAnimation = true
        delay(1800) // 2.5 seconds delay
        onAnimationFinish()
    }

    Image(
        painter = painterResource(id = R.drawable.logonew),
        contentDescription = "Book My Show Logo",
        modifier = Modifier
            .width(250.dp)
            .offset(y = yOffset.dp)
            .rotate(rotation)
            .alpha(alpha)
                ,colorFilter = ColorFilter.tint(primary)
    )
}
