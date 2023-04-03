package com.nzdeveloper009.webviewandroidjetpackcomposeexample

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowInsetsController
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.nzdeveloper009.webviewandroidjetpackcomposeexample.ui.theme.WebViewAndroidJetpackComposeExampleTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApp {
                MainScreen()
            }
        }
        // Set the status bar color
        setStatusBarColor(ContextCompat.getColor(this, R.color.status_bar_color))
    }

    private fun setStatusBarColor(color: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val window: Window = window
            val controller = window.insetsController
            if (controller != null) {
                controller.setSystemBarsAppearance(
                    WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
                    WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
                )
                window.statusBarColor = color
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = color
        }
    }
}

@Composable
fun MyApp(content: @Composable () -> Unit) {
    /*MaterialTheme {
        Surface(color = MaterialTheme.colors.background, modifier = Modifier.fillMaxSize()) {
            content()
        }
    }*/
    WebViewAndroidJetpackComposeExampleTheme {
        Surface(color = MaterialTheme.colors.background, modifier = Modifier.fillMaxSize()) {
            content()
        }
    }
}

@Composable
fun MainScreen() {
    var webViewState by remember { mutableStateOf(WebViewState()) }
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val url: String = "https://www.example.com/"


    // Initialize the WebView settings
    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { createWebView(context) },
        update = { webView ->
            webView.loadUrl(url)
            webViewState.webView = webView
        }
    )

    // Load the URL of the website
    BackHandler {
        if (webViewState.webView?.canGoBack() == true) {
            webViewState.webView?.goBack()
        }
    }
//    webViewState.webView?.loadUrl("https://www.example.com/")

    DisposableEffect(webViewState) {
        onDispose {
            webViewState.onDestroy()

        }
    }

    LaunchedEffect(webViewState) {
        webViewState.onResume()
        webViewState.webView?.loadUrl(url)
    }

    // Observe the lifecycle of the Composable
    lifecycleOwner.lifecycle.addObserver(object : LifecycleObserver {
        @OnLifecycleEvent(Lifecycle.Event.ON_START)
        fun onStart() {
            webViewState.onResume()

        }

        @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
        fun onStop() {
            webViewState.onPause()
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        fun onDestroy() {
            webViewState.onDestroy()
        }
    })


}


private fun createWebView(context: Context): WebView {
    val webView = WebView(context)
    // Enable hardware acceleration
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        webView.setLayerType(View.LAYER_TYPE_HARDWARE, null)
    }
    // Configure WebView settings
    val settings = webView.settings
    settings.javaScriptEnabled = true
    settings.domStorageEnabled = true
    settings.setSupportZoom(false)
    settings.displayZoomControls = false
    // more settings
    settings.javaScriptCanOpenWindowsAutomatically = false
    settings.setRenderPriority(WebSettings.RenderPriority.HIGH)
    // Enable caching
    settings.cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK
    val setAppCacheEnabledMethod =
        WebSettings::class.java.getMethod("setAppCacheEnabled", Boolean::class.javaPrimitiveType)
    setAppCacheEnabledMethod.invoke(settings, true)
    // some more
    settings.databaseEnabled = true
    settings.setSupportMultipleWindows(false)
    settings.allowFileAccess = false
    settings.allowContentAccess = false
    settings.allowFileAccessFromFileURLs = false
    settings.allowUniversalAccessFromFileURLs = false
    webView.webViewClient = CustomWebViewClient()
    return webView
}

class WebViewState(var webView: WebView? = null) {
    fun onResume() {
        webView?.onResume()
    }

    fun onPause() {
        webView?.onPause()
    }

    fun onDestroy() {
        webView?.let {
            it.stopLoading()
            it.removeAllViews()
            it.destroy()
        }
    }
}

class CustomWebViewClient : WebViewClient() {
    override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
        if (url != null && url.startsWith("whatsapp://")) {
            return try {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse(url)
                view?.context?.startActivity(intent)
                true
            } catch (e: ActivityNotFoundException) {
                // WhatsApp not installed, fallback to the WebView
                view?.loadUrl(url)
                false
            }
        }
        return false
    }
}
