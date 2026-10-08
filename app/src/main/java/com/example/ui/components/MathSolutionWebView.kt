package com.example.ui.components

import android.annotation.SuppressLint
import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.model.MathSolution
import kotlinx.serialization.json.Json

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun MathSolutionWebView(
    solution: MathSolution,
    modifier: Modifier = Modifier,
    isDarkTheme: Boolean = isSystemInDarkTheme()
) {
    val jsonString = remember(solution) {
        try {
            Json { ignoreUnknownKeys = true }.encodeToString(MathSolution.serializer(), solution)
        } catch (e: Exception) {
            "{}"
        }
    }

    var webViewRef by remember { mutableStateOf<WebView?>(null) }

    LaunchedEffect(jsonString, isDarkTheme, webViewRef) {
        webViewRef?.let { wv ->
            // Escape json for js call
            val safeJson = jsonString
                .replace("\\", "\\\\")
                .replace("'", "\\'")
                .replace("\n", "\\n")
                .replace("\r", "")
            wv.evaluateJavascript("javascript:if(window.setMathContent){ setMathContent('$safeJson', $isDarkTheme); }", null)
        }
    }

    AndroidView(
        factory = { ctx ->
            WebView(ctx).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                // Strict security settings as specified
                settings.apply {
                    javaScriptEnabled = true
                    allowFileAccess = false
                    allowContentAccess = false
                    domStorageEnabled = false
                    cacheMode = WebSettings.LOAD_NO_CACHE
                }
                setBackgroundColor(0) // Transparent background

                webViewClient = object : WebViewClient() {
                    override fun onPageFinished(view: WebView?, url: String?) {
                        super.onPageFinished(view, url)
                        val safeJson = jsonString
                            .replace("\\", "\\\\")
                            .replace("'", "\\'")
                            .replace("\n", "\\n")
                            .replace("\r", "")
                        view?.evaluateJavascript("javascript:if(window.setMathContent){ setMathContent('$safeJson', $isDarkTheme); }", null)
                    }
                }

                loadUrl("file:///android_asset/math_template.html")
                webViewRef = this
            }
        },
        update = { wv ->
            webViewRef = wv
            val safeJson = jsonString
                .replace("\\", "\\\\")
                .replace("'", "\\'")
                .replace("\n", "\\n")
                .replace("\r", "")
            wv.evaluateJavascript("javascript:if(window.setMathContent){ setMathContent('$safeJson', $isDarkTheme); }", null)
        },
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 200.dp, max = 2000.dp)
            .testTag("math_solution_webview")
    )
}
