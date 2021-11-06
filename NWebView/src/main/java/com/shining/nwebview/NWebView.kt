package com.shining.nwebview

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.AttributeSet
import android.util.Base64
import android.util.Log
import android.view.ViewGroup
import android.webkit.*
import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.Fragment
import com.shining.nwebview.utils.DownloadUtils
import com.shining.nwebview.utils.WebViewUtils
import java.io.UnsupportedEncodingException
import java.lang.ref.WeakReference
import java.nio.charset.Charset
import java.util.*
import java.util.regex.Pattern
import kotlin.collections.ArrayList

/**
 * NWebView.kt
 * WebHandler
 */
class NWebView
@JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0)
    : WebView(context, attrs, defStyle) {

    private var mListener : NWebListener? = null

    private var mNWebChromeClient : NWebChromeClient? = null
    private var mNWebViewClient : NWebViewClient? = null

    private val REQUEST_CODE_FILE_PICKER = 51426
    private var mRequestCodeFilePicker: Int = REQUEST_CODE_FILE_PICKER

    private var mActivity: WeakReference<Activity>? = null
    private var mFragment: WeakReference<Fragment>? = null

    internal var mGeolocationEnabled = false
    private var mUploadableFileTypes = "*/*"


    val PACKAGE_NAME_DOWNLOAD_MANAGER = "com.android.providers.downloads"
    internal val DATABASES_SUB_FOLDER = "/databases"
    private val LANGUAGE_DEFAULT_ISO3 = "eng"
    private val CHARSET_DEFAULT = "UTF-8"

    /** File upload callback for platform versions prior to Android 5.0  */
    private var mFileUploadCallbackFirst: ValueCallback<Uri>? = null

    /** File upload callback for Android 5.0+  */
    private var mFileUploadCallbackSecond: ValueCallback<Array<Uri>>? = null
    private var mLastError: Long = 0
    private var mLanguageIso3: String? = null
    private val mHttpHeaders = HashMap<String, String>()

    companion object {
        const val TAG = "[DE][SDK] NWebView"
    }

    init {
        Log.d(TAG, "init")
        NWebChromeClient(context, this@NWebView).apply {
            mNWebChromeClient = this
            super.setWebChromeClient(this)
        }
        NWebViewClient(this@NWebView).apply {
            mNWebViewClient = this
            super.setWebViewClient(this)
        }
        initContext(context)
    }

    override fun setWebViewClient(client: WebViewClient) {
        Log.d(TAG, "setWebViewClient")
        mNWebViewClient?.mViewClient = client
    }

    override fun setWebChromeClient(client: WebChromeClient?) {
        Log.d(TAG, "setWebChromeClient")
        mNWebChromeClient?.mChromeClient = client
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initContext(context: Context) {
        Log.d(TAG, "initContext")
        if (isInEditMode) return

        if (context is Activity) {
            // WeakReference로 구성하여도 문제가 없을까...?
            mActivity = WeakReference(context)
        }

        mLanguageIso3 = getLanguageIso3()

        isFocusable = true
        isFocusableInTouchMode = true

        isSaveEnabled = true

//        val filesDir = context.filesDir.path
//        val databaseDir = filesDir.substring(0, filesDir.lastIndexOf("/")) + DATABASES_SUB_FOLDER

        // Cookie
        setMixedContentAllowed(allowed = true)
        setCookiesEnabled(true)
        setThirdPartyCookiesEnabled(true)

        setDownloadListener { url, userAgent, contentDisposition, mimeType, contentLength ->
            mListener ?: return@setDownloadListener

            val suggestedFilename = URLUtil.guessFileName(url, contentDisposition, mimeType)
            mListener!!.onDownloadRequested(url, suggestedFilename, mimeType, contentLength, contentDisposition, userAgent)
        }
    }

    fun setListener(activity: Activity, listener: NWebListener)
            = setListener(activity, listener, REQUEST_CODE_FILE_PICKER)

    private fun setListener(activity: Activity, listener: NWebListener, requestCodeFilePicker: Int) {
        mActivity = WeakReference(activity)
        setListener(listener, requestCodeFilePicker)
    }

    fun setListener(fragment: Fragment, listener: NWebListener)
            = setListener(fragment, listener, REQUEST_CODE_FILE_PICKER)

    private fun setListener(fragment: Fragment, listener: NWebListener, requestCodeFilePicker: Int) {
        mFragment = WeakReference(fragment)
        setListener(listener, requestCodeFilePicker)
    }

    private fun setListener(listener: NWebListener, requestCodeFilePicker: Int) {
        mListener = listener
        mRequestCodeFilePicker = requestCodeFilePicker
    }

    fun setUploadableFileTypes(mimeType: String) {
        mUploadableFileTypes = mimeType
    }

    /**
     * Loads and displays the provided HTML source text
     *
     * @param html the HTML source text to load
     */
    fun loadHtml(html: String?) {
        loadHtml(html, null)
    }

    /**
     * Loads and displays the provided HTML source text
     *
     * @param html the HTML source text to load
     * @param baseUrl the URL to use as the page's base URL
     */
    fun loadHtml(html: String?, baseUrl: String?) {
        loadHtml(html, baseUrl, null)
    }

    /**
     * Loads and displays the provided HTML source text
     *
     * @param html the HTML source text to load
     * @param baseUrl the URL to use as the page's base URL
     * @param historyUrl the URL to use for the page's history entry
     */
    fun loadHtml(html: String?, baseUrl: String?, historyUrl: String?) {
        loadHtml(html, baseUrl, historyUrl, "utf-8")
    }

    /**
     * Loads and displays the provided HTML source text
     *
     * @param html the HTML source text to load
     * @param baseUrl the URL to use as the page's base URL
     * @param historyUrl the URL to use for the page's history entry
     * @param encoding the encoding or charset of the HTML source text
     */
    fun loadHtml(html: String?, baseUrl: String?, historyUrl: String?, encoding: String?) {
        loadDataWithBaseURL(baseUrl, html!!, "text/html", encoding, historyUrl)
    }

    @SuppressLint("NewApi")
    override fun onResume() {
        Log.d(TAG, "onResume")
        resumeTimers()
    }

    @SuppressLint("NewApi")
    override fun onPause() {
        Log.d(TAG, "onPause")
        pauseTimers()
    }

    override fun evaluateJavascript(script: String, resultCallback: ValueCallback<String>?) {
        Log.d(TAG, "evaluateJavascript")
        super.evaluateJavascript(script, resultCallback)
    }

    fun onDestroy() {
        Log.d(TAG, "onDestroy")
        try {
            (parent as ViewGroup).removeView(this)
        } catch (e: Exception) {
            Log.e(TAG, "onDestroy Exception[${e.message}]")
        }
        try {
            removeAllViews()
        } catch (e: Exception) {
            Log.e(TAG, "onDestroy Exception[${e.message}]")
        }
        destroy()
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        Log.d(TAG, "onActivityResult")
        if (requestCode == mRequestCodeFilePicker) {
            if (resultCode == Activity.RESULT_OK) {
                intent ?: return

//                mFileUploadCallbackFirst?.apply {
//                    onReceiveValue(intent.data)
//                    mFileUploadCallbackFirst = null
//                    return
//                }

                if (mFileUploadCallbackFirst != null) {
                    mFileUploadCallbackFirst!!.onReceiveValue(intent.data)
                    mFileUploadCallbackFirst = null
                }
                else if (mFileUploadCallbackSecond != null) {
                    var dataUris: Array<Uri>? = null
                    try {
                        if (intent.dataString != null) {
                            dataUris = arrayOf(Uri.parse(intent.dataString))
                        }
                        else if (intent.clipData != null) {
                            val numSelectedFiles = intent.clipData!!.itemCount
                            val list = ArrayList<Uri>()
                            for (i in 0 until numSelectedFiles) {
//                                dataUris[i] = intent.clipData!!.getItemAt(i).uri
                                list.add(intent.clipData!!.getItemAt(i).uri)
                            }
                            dataUris = list.toTypedArray()
                        }
                    } catch (ignored: Exception) {

                    }
                    mFileUploadCallbackSecond!!.onReceiveValue(dataUris)
                    mFileUploadCallbackSecond = null
                }
            } else {
                if (mFileUploadCallbackFirst != null) {
                    mFileUploadCallbackFirst!!.onReceiveValue(null)
                    mFileUploadCallbackFirst = null
                } else if (mFileUploadCallbackSecond != null) {
                    mFileUploadCallbackSecond!!.onReceiveValue(null)
                    mFileUploadCallbackSecond = null
                }
            }
        }
    }

    /**
     * Adds an additional HTTP header that will be sent along with every HTTP `GET` request
     *
     * This does only affect the main requests, not the requests to included resources (e.g. images)
     *
     * If you later want to delete an HTTP header that was previously added this way, call `removeHttpHeader()`
     *
     * The `WebView` implementation may in some cases overwrite headers that you set or unset
     *
     * @param name the name of the HTTP header to add
     * @param value the value of the HTTP header to send
     */
    fun addHttpHeader(name: String, value: String) {
        mHttpHeaders[name] = value
    }

    /**
     * Removes one of the HTTP headers that have previously been added via `addHttpHeader()`
     *
     * If you want to unset a pre-defined header, set it to an empty string with `addHttpHeader()` instead
     *
     * The `WebView` implementation may in some cases overwrite headers that you set or unset
     *
     * @param name the name of the HTTP header to remove
     */
    fun removeHttpHeader(name: String) {
        mHttpHeaders.remove(name)
    }

    fun onBackPressed(): Boolean {
        Log.d(TAG, "onBackPressed")
        return if (canGoBack()) {
            goBack()
            false
        } else {
            true
        }
    }

    // 대체 사용
    // androidx.webkit.WebViewAssetLoader
//    @SuppressLint("NewApi")
//    private fun setAllowAccessFromFileUrls(webSettings: WebSettings, allowed: Boolean) {
//        webSettings.allowFileAccessFromFileURLs = allowed
//        webSettings.allowUniversalAccessFromFileURLs = allowed
//    }

    fun setCookiesEnabled(enabled: Boolean) {
        CookieManager.getInstance().setAcceptCookie(enabled)
    }

    fun setThirdPartyCookiesEnabled(enabled: Boolean) {
        CookieManager.getInstance().setAcceptThirdPartyCookies(this, enabled)
    }

    fun setMixedContentAllowed(allowed: Boolean) {
        setMixedContentAllowed(settings, allowed)
    }

    @SuppressLint("NewApi")
    internal fun setMixedContentAllowed(webSettings: WebSettings, allowed: Boolean) {
        webSettings.mixedContentMode =
            if (allowed) WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
            else WebSettings.MIXED_CONTENT_NEVER_ALLOW
    }

    override fun loadUrl(url: String, additionalHttpHeaders: MutableMap<String, String>) {
        Log.d(TAG, "loadUrl")

        takeIf { mHttpHeaders.size > 0 }?.apply {
            additionalHttpHeaders.putAll(mHttpHeaders)
        }
        super.loadUrl(url, additionalHttpHeaders)
    }

    override fun loadUrl(url: String) {
        Log.d(TAG, "loadUrl")

        takeIf { mHttpHeaders.size > 0 }?.apply {
            super.loadUrl(url, mHttpHeaders)
        } ?: run {
            super.loadUrl(url)
        }

//        if (mHttpHeaders.size > 0)
//            super.loadUrl(url, mHttpHeaders)
//        else
//            super.loadUrl(url)
    }

    public fun loadUrl(url: String, preventCaching: Boolean) {
//        takeIf { preventCaching }.apply {
//            loadUrl(makeUrlUnique(url))
//        } ?: run {
//            loadUrl(url)
//        }
        loadUrl( if(preventCaching) makeUrlUnique(url) else url)
    }

    public fun loadUrl(url: String, preventCaching: Boolean, additionalHttpHeaders: MutableMap<String, String>) {
        takeIf { preventCaching }?.apply {
            loadUrl(makeUrlUnique(url), additionalHttpHeaders)
        } ?: run {
            loadUrl(url, additionalHttpHeaders)
        }
    }

    private fun makeUrlUnique(url: String): String {
        val unique = StringBuilder().apply {
            append(url)
            if (url.contains("?")) {
                append('&')
            } else {
                if (url.lastIndexOf('/') <= 7)
                    append('/')
                append('?')
            }
            append(System.currentTimeMillis())
            append('=')
            append(1)
        }
        return unique.toString()
    }

    internal fun setLastError() {
        mLastError = System.currentTimeMillis()
    }

    internal fun hasError(): Boolean {
        return mLastError + 500 >= System.currentTimeMillis()
    }

    private fun getLanguageIso3(): String {
        return try {
            Locale.getDefault().isO3Language.lowercase(Locale.US)
        } catch (e: MissingResourceException) {
            LANGUAGE_DEFAULT_ISO3
        }
    }

    /**
     * Provides localizations for the 25 most widely spoken languages that have a ISO 639-2/T code
     *
     * @return the label for the file upload prompts as a string
     */
    private fun getFileUploadPromptLabel(): String? {
        try {
            when (mLanguageIso3) {
                "zho" -> return decodeBase64("6YCJ5oup5LiA5Liq5paH5Lu2")
                "spa" -> return decodeBase64("RWxpamEgdW4gYXJjaGl2bw==")
                "hin" -> return decodeBase64("4KSP4KSVIOCkq+CkvOCkvuCkh+CksiDgpJrgpYHgpKjgpYfgpII=")
                "ben" -> return decodeBase64("4KaP4KaV4Kaf4Ka/IOCmq+CmvuCmh+CmsiDgpqjgpr/gprDgp43gpqzgpr7gpprgpqg=")
                "ara" -> return decodeBase64("2KfYrtiq2YrYp9ixINmF2YTZgSDZiNin2K3Yrw==")
                "por" -> return decodeBase64("RXNjb2xoYSB1bSBhcnF1aXZv")
                "rus" -> return decodeBase64("0JLRi9Cx0LXRgNC40YLQtSDQvtC00LjQvSDRhNCw0LnQuw==")
                "jpn" -> return decodeBase64("MeODleOCoeOCpOODq+OCkumBuOaKnuOBl+OBpuOBj+OBoOOBleOBhA==")
                "pan" -> return decodeBase64("4KiH4Kmx4KiVIOCoq+CovuCoh+CosiDgqJrgqYHgqKPgqYs=")
                "deu" -> return decodeBase64("V8OkaGxlIGVpbmUgRGF0ZWk=")
                "jav" -> return decodeBase64("UGlsaWggc2lqaSBiZXJrYXM=")
                "msa" -> return decodeBase64("UGlsaWggc2F0dSBmYWls")
                "tel" -> return decodeBase64("4LCS4LCVIOCwq+CxhuCxluCwsuCxjeCwqOCxgSDgsI7gsILgsJrgsYHgsJXgsYvgsILgsKHgsL8=")
                "vie" -> return decodeBase64("Q2jhu41uIG3hu5l0IHThuq1wIHRpbg==")
                "kor" -> return decodeBase64("7ZWY64KY7J2YIO2MjOydvOydhCDshKDtg50=")
                "fra" -> return decodeBase64("Q2hvaXNpc3NleiB1biBmaWNoaWVy")
                "mar" -> return decodeBase64("4KSr4KS+4KSH4KSyIOCkqOCkv+CkteCkoeCkvg==")
                "tam" -> return decodeBase64("4K6S4K6w4K+BIOCuleCvh+CuvuCuquCvjeCuquCviCDgrqTgr4fgrrDgr43grrXgr4E=")
                "urd" -> return decodeBase64("2KfbjNqpINmB2KfYptmEINmF24zauiDYs9uSINin2YbYqtiu2KfYqCDaqdix24zaug==")
                "fas" -> return decodeBase64("2LHYpyDYp9mG2KrYrtin2Kgg2qnZhtuM2K8g24zaqSDZgdin24zZhA==")
                "tur" -> return decodeBase64("QmlyIGRvc3lhIHNlw6dpbg==")
                "ita" -> return decodeBase64("U2NlZ2xpIHVuIGZpbGU=")
                "tha" -> return decodeBase64("4LmA4Lil4Li34Lit4LiB4LmE4Lif4Lil4LmM4Lir4LiZ4Li24LmI4LiH")
                "guj" -> return decodeBase64("4KqP4KqVIOCqq+CqvuCqh+CqsuCqqOCrhyDgqqrgqrjgqoLgqqY=")
            }
        } catch (ignored: Exception) {

        }
        return "Choose a file"
    }

    @Throws(IllegalArgumentException::class, UnsupportedEncodingException::class)
    private fun decodeBase64(base64: String): String {
        val bytes = Base64.decode(base64, Base64.DEFAULT)
        return String(bytes, Charset.forName(CHARSET_DEFAULT))
    }

    @SuppressLint("NewApi")
    internal fun openFileInput(
        fileUploadCallbackFirst: ValueCallback<Uri>?,
        fileUploadCallbackSecond: ValueCallback<Array<Uri>>?,
        allowMultiple: Boolean
    ) {
        if (mFileUploadCallbackFirst != null) {
            mFileUploadCallbackFirst!!.onReceiveValue(null)
        }
        mFileUploadCallbackFirst = fileUploadCallbackFirst
        if (mFileUploadCallbackSecond != null) {
            mFileUploadCallbackSecond!!.onReceiveValue(null)
        }
        mFileUploadCallbackSecond = fileUploadCallbackSecond
        val i = Intent(Intent.ACTION_GET_CONTENT)
        i.addCategory(Intent.CATEGORY_OPENABLE)
        if (allowMultiple) {
            i.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        }
        i.type = mUploadableFileTypes
        if (mFragment != null && mFragment!!.get() != null) {
            mFragment!!.get()!!.startActivityForResult(
                Intent.createChooser(i, getFileUploadPromptLabel()),
                mRequestCodeFilePicker
            )
        } else if (mActivity != null && mActivity!!.get() != null) {
            mActivity!!.get()!!.startActivityForResult(
                Intent.createChooser(i, getFileUploadPromptLabel()),
                mRequestCodeFilePicker
            )
        }
    }

    /**
     * Handles a download by loading the file from `fromUrl` and saving it to `toFilename` on the external storage
     * This requires the two permissions `android.permission.INTERNET` and `android.permission.WRITE_EXTERNAL_STORAGE`
     * Only supported on API level 9 (Android 2.3) and above
     *
     * @param context a valid `Context` reference
     * @param fromUrl the URL of the file to download, e.g. the one from `AdvancedWebView.onDownloadRequested(...)`
     * @param toFilename the name of the destination file where the download should be saved, e.g. `myImage.jpg`
     * @return whether the download has been successfully handled or not
     * @throws IllegalStateException if the storage or the target directory could not be found or accessed
     */
//    fun handleDownload(context: Context, fromUrl: String?, toFilename: String?): Boolean {
//        val request = DownloadManager.Request(Uri.parse(fromUrl))
//        request.allowScanningByMediaScanner()
//        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
//        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, toFilename)
//        val dm = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
//        return try {
//            try {
//                dm.enqueue(request)
//            } catch (e: SecurityException) {
//                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
//                dm.enqueue(request)
//            }
//            true
//        } // if the download manager app has been disabled on the device
//        catch (e: IllegalArgumentException) {
//            // show the settings screen where the user can enable the download manager app again
//            openAppSettings(context, PACKAGE_NAME_DOWNLOAD_MANAGER)
//            false
//        }
//    }
//
//    private fun openAppSettings(context: Context, packageName: String): Boolean {
//        return try {
//            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
//            intent.data = Uri.parse("package:$packageName")
//            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
//            context.startActivity(intent)
//            true
//        } catch (e: Exception) {
//            false
//        }
//    }

    fun setUrlPrefixesForDefaultIntent(specialUrls: ArrayList<Any>) {
        WebViewUtils.mUrlBlacklist = specialUrls
    }

    fun setOriginWhitelist(originWhitelist: List<Pattern>) {
        WebViewUtils.mUrlWhitelist = originWhitelist
    }

    fun setActivityResult(activityResultLauncher: ActivityResultLauncher<Intent>) {
        DownloadUtils.activityResultLauncher = activityResultLauncher
    }
}