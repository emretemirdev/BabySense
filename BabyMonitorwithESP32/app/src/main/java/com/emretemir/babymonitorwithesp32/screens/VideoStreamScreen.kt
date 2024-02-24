import android.os.Handler
import android.os.Looper
import androidx.annotation.OptIn
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.emretemir.babymonitorwithesp32.User
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun VideoStreamScreen(userProfile: User?) {
    val context = LocalContext.current

    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            val mediaItem = MediaItem.fromUri("https://tv-trtturk.medya.trt.com.tr/master_720.m3u8")
            setMediaItem(mediaItem)
            prepare()
        }
    }

    DisposableEffect(context) {
        onDispose {
            exoPlayer.release()
        }
    }

    var currentTime by remember { mutableStateOf("") }

    DisposableEffect(Unit) {
        val handler = Handler(Looper.getMainLooper())

        val updateCurrentTime: Runnable = object : Runnable {
            override fun run() {
                val format = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
                currentTime = format.format(System.currentTimeMillis())
                handler.postDelayed(this, 1000)
            }
        }

        handler.post(updateCurrentTime)

        onDispose {
            handler.removeCallbacks(updateCurrentTime)
        }
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        userProfile?.let {
            Text(text = "Hoşgeldin, ${it.firstName} ${it.lastName}", modifier = Modifier.padding(bottom = 8.dp))
        }

        Text(text = "Canlı İzle", modifier = Modifier.padding(bottom = 8.dp))

        // Video oynatıcı
        VideoPlayerView(exoPlayer = exoPlayer)

        // Zaman bilgisi
        Text(text = "Mevcut Zaman: $currentTime", modifier = Modifier.padding(top = 8.dp))

        // Videoyu Yenile Butonu
        Button(onClick = {
            exoPlayer.seekTo(0)
            exoPlayer.prepare()
        }, modifier = Modifier.padding(top = 8.dp)) {
            Text(text = "Videoyu Yenile")
        }

    }
}

@OptIn(UnstableApi::class) @Composable
fun VideoPlayerView(exoPlayer: ExoPlayer) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(16f / 9f)
    ) {
        AndroidView(factory = { ctx ->
            PlayerView(ctx).apply {
                player = exoPlayer
                useController = true
                controllerAutoShow = true
            }
        }, modifier = Modifier.matchParentSize())
    }
}

