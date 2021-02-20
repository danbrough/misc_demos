package danbroid.util.demo.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import androidx.fragment.app.Fragment

class BrowserFragment : Fragment() {
  lateinit var webView: WebView

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
      WebView(requireContext()).also {
        webView = it
      }

  @SuppressLint("SetJavaScriptEnabled")
  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    log.debug("onViewCreated()")


    webView.settings.also {  settings->
      settings.javaScriptEnabled = false
     // log.warn("current user agent: ${settings.userAgentString}")
     settings.userAgentString = "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/88.0.4324.182 Safari/537.36"
    }

    webView.webChromeClient = object: WebChromeClient(){


    }
    webView.webViewClient = object : WebViewClient() {
      override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
        log.warn("shouldOverrideUrlLoading() $url")
        webView.loadUrl(url)
        return false
      }
      override fun onReceivedError(view: WebView, request: WebResourceRequest, error: WebResourceError) {
        log.error("onReceivedError() request:$request $error")
      }
    }

    //val url = "https://www.metlink.org.nz/service/2?stop=5510"
    val url = "https://www.metlink.org.nz/"
  // val url = "https://www.whoishostingthis.com/tools/user-agent/"
    webView.loadUrl(url)
  }
}

private val log = org.slf4j.LoggerFactory.getLogger(BrowserFragment::class.java)
