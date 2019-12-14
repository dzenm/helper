package com.dzenm.helper.base;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.DownloadListener;
import android.webkit.GeolocationPermissions;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.dzenm.helper.dialog.EditDialog;
import com.dzenm.helper.dialog.InfoDialog;
import com.dzenm.helper.os.OsHelper;
import com.dzenm.helper.os.ScreenHelper;
import com.dzenm.helper.share.ShareHelper;
import com.dzenm.helper.toast.ToastHelper;

/**
 * @author dinzhenyan
 * @date 2019-04-30 20:03
 * Android的WebView在低版本和高版本采用了不同的webkit版本内核，4.4后直接使用了Chrome。
 * 添加访问网络权限 <uses-permission android:name="android.permission.INTERNET"/>
 */
public abstract class AbsWebActivity extends AbsBaseActivity {

    protected WebView mWebView;
    protected String mCurrentTitle, mCurrentUrl;

    /**
     * 设置WebView
     *
     * @param viewGroup   WebView的ViewGroup, 必须设置
     * @param progressBar 进度条显示, 可有可无
     * @param url         初次加载的url
     */
    protected void setEnabledWebView(ViewGroup viewGroup, ProgressBar progressBar, String url) {
        setWebView(viewGroup, url);
        setWebClient(mWebView, progressBar);
        setWebSettings(mWebView);
    }

    /**
     * 设置webView属性
     *
     * @param viewGroup WebView的ViewGroup, 必须设置
     * @param url       初次加载的url
     */
    protected void setWebView(ViewGroup viewGroup, String url) {
        // 避免WebView内存泄露。不在xml中定义 WebView ，而是在需要的时候在Activity中创建，并且Context使用 getApplicationContext()
        getSupportActionBar().setTitle("正在加载中...");

        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mWebView = new WebView(getApplicationContext());
        mWebView.setLayoutParams(params);
        viewGroup.addView(mWebView);

        logD("current load url: " + url);
        // 方式一：加载一个网页
        mWebView.loadUrl(url);

        // 方式二：加载应用资源文件内的网页
//        mWebView.loadUrl("file:///android_asset/test.html");

        // 方式三：加载手机本地的html页面
//        mWebView.loadUrl("content://com.android.htmlfileprovider/sdcard/test.html");

        // 方式四：加载 HTML 页面的一小段内容
//        webView.loadData(String data,String mimeType, String encoding);
        // 参数1：需要截取展示的内容, 内容里不能出现 ’#’, ‘%’, ‘\’ , ‘?’ 这四个字符，若出现了需用 %23, %25, %27, %3f 对应来替代，否则会出现异常
        // 参数2：展示内容的类型
        // 参数3：字节码
    }

    /**
     * 设置webClient属性
     *
     * @param webView     加载网页的webView
     * @param progressBar 进度条显示, 可有可无
     */
    protected void setWebClient(final WebView webView, final ProgressBar progressBar) {
        // 此方法可以在webview中打开链接而不会跳转到外部浏览器
        webView.setWebViewClient(new WebViewClient() {
            // 重定向URL请求，返回true表示拦截此url，返回false表示不拦截此url。
            @SuppressLint("SetJavaScriptEnabled")
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                String url = request.getUrl().getPath();
                if (!TextUtils.isEmpty(url)) {
                    // 如果WebView需要使用file协议，则应该禁用file协议的Javascript功能。
                    // 具体方法为：在调用loadUrl方法前，以及在shouldOverrideUrlLoading方法中判断url的scheme是否为file。
                    // 如果是file协议，就禁用Javascript，否则启用Javascript。

                    // 判断是否为file协议
                    if ("file".equals(request.getUrl().getScheme())) {
                        view.getSettings().setJavaScriptEnabled(false);
                    } else {
                        view.getSettings().setJavaScriptEnabled(true);
                    }
                    // 作用1：重定向url
                    assert url != null;
                    if (url.startsWith("weixin://")) {
                        url = url.replace("weixin://", "http://");
                        webView.loadUrl(url);
                    }
                    // 作用2：在本页面的WebView打开，防止外部浏览器打开此链接
                    view.loadUrl(url);
                }
                return true;
            }

            // 在开始加载网页时会回调
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            // 加载页面的服务器出现错误（比如404）时回调。
            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
            }

            // 加载完成的时候会回调
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }

            // 重写此方法才能处理浏览器中的按键事件。
            @Override
            public boolean shouldOverrideKeyEvent(WebView view, KeyEvent event) {
                return super.shouldOverrideKeyEvent(view, event);
            }

            // 页面每一次请求资源之前都会调用这个方法（非UI线程调用）。
            @Nullable
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                return super.shouldInterceptRequest(view, request);
            }

            // 补充，关键方法调用流程：
            // 情况一：loadUrl()无重定向时
            // onPageStarted->onPageFinished

            // 情况二：loadUrl()网页A重定向到B时
            // onPageStarted->shouldOverrideUrlLoading->onPageStarted->onPageFinished->onPageFinished

            // 情况三：在已加载的页面中点击链接，加载页面A（无重定向）
            // shouldOverrideUrlLoading->onPageStarted->onPageFinished

            // 情况四：在已加载的页面中点击链接，加载页面A（页面A重定向至页面B）
            // shouldOverrideUrlLoading->onPageStarted->shouldOverrideUrlLoading->onPageStarted->onPageFinished->onPageFinished

            // 情况五：执行goBack/goForward/reload方法
            // onPageStarted->onPageFinished

            // 情况六：发生资源加载
            // shouldInterceptRequest->onLoadResource

        });

        // 设置WebChromeClient类
        webView.setWebChromeClient(new WebChromeClient() {
            // 获得网页的加载进度并显示
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (progressBar != null) {
                    if (newProgress >= 0 && newProgress < 100) {
                        progressBar.setVisibility(View.VISIBLE);
                        progressBar.setProgress(newProgress);
                    } else {
                        progressBar.setVisibility(View.GONE);
                    }
                }
            }

            // 获取Web页中的标题
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                if (!TextUtils.isEmpty(title)) {
                    getSupportActionBar().setTitle(title);
                    mCurrentTitle = title;                                   // 获取当前页面标题
                    mCurrentUrl = view.getUrl();                             // 获取当前页面URL
                }

                if (OsHelper.isMarshmallow()) {
                    if (title.contains("404") || title.contains("500") || title.contains("Error")) {
                        view.loadUrl("about:blank");                            // 避免出现默认的错误界面
                    }
                }

            }

            // 获得网页的图标时回调
            @Override
            public void onReceivedIcon(WebView view, Bitmap icon) {
                super.onReceivedIcon(view, icon);
            }

            // ******************************* HTML定位问题 *******************************
//             需要添加一下权限
//            <uses-permission android:name="android.permission.INTERNET" />
//            <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
//            <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />

            // 地理位置隐藏提示
            @Override
            public void onGeolocationPermissionsHidePrompt() {
                super.onGeolocationPermissionsHidePrompt();
            }

            // 地理位置显示提示
            @Override
            public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
                // 第二个参数是否同意定位权限，第三个参数是否希望内核记住
                callback.invoke(origin, true, false);
                super.onGeolocationPermissionsShowPrompt(origin, callback);
            }
            // ******************************* HTML定位问题 *******************************


            // ******************************* 多窗口问题 *******************************

            // 打开新窗口时回调
            @Override
            public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
                WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
                transport.setWebView(view);
                resultMsg.sendToTarget();
                return true;
            }

            // 关闭窗口时回调
            @Override
            public void onCloseWindow(WebView window) {
                super.onCloseWindow(window);
            }

            // ******************************* 多窗口问题 *******************************

            // 支持javascript的警告框
            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                Toast.makeText(AbsWebActivity.this, message, Toast.LENGTH_SHORT).show();
                // 返回true表示不弹出系统的提示框，返回false表示弹出
                return true;
            }

            // 支持javascript的确认框
            @Override
            public boolean onJsConfirm(WebView view, String url, String message, final JsResult result) {
                InfoDialog.newInstance(AbsWebActivity.this)
                        .setTitle("是否确定？")
                        .setMessage(message).setOnClickListener(new InfoDialog.OnInfoClickListener() {
                    @Override
                    public boolean onClick(InfoDialog dialog, boolean confirm) {
                        if (confirm) {
                            result.confirm();
                        } else {
                            result.cancel();
                        }
                        return true;
                    }
                }).show();
                // 返回true表示不弹出系统的提示框，返回false表示弹出
                return true;
            }

            // 支持javascript输入框
            @Override
            public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, final JsPromptResult result) {
                EditDialog.newInstance(AbsWebActivity.this)
                        .setTitle(message)
                        .setMessage(defaultValue)
                        .setOnClickListener(new EditDialog.OnEditClickListener() {
                            @Override
                            public boolean onClick(EditDialog dialog, boolean confirm) {
                                if (confirm) {
                                    result.confirm((dialog).getMessage());
                                } else {
                                    result.cancel();
                                }
                                return true;
                            }
                        }).show();
                // 返回true表示不弹出系统的提示框，返回false表示弹出
                return true;
            }

            // HTML5 video 在 WebView 全屏显示, 很多的手机版本在网页视频播放时是不会调用这个方法的，所以这个方法局限性很大
            @Override
            public void onShowCustomView(View view, CustomViewCallback callback) {
            }

            // HTML5 video 在 WebView 隐藏
            @Override
            public void onHideCustomView() {

            }
        });

        // 当下载文件时打开系统自带的浏览器进行下载，当然也可以对捕获到的 url 进行处理在应用内下载。
        webView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
            }
        });

        // 设置长按事件
        webView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                WebView.HitTestResult result = ((WebView) v).getHitTestResult();
                if (result != null) {
                    switch (result.getType()) {
                        case WebView.HitTestResult.UNKNOWN_TYPE:                // 未知类型:
                            break;
                        case WebView.HitTestResult.PHONE_TYPE:                  // 电话类型
                            break;
                        case WebView.HitTestResult.EMAIL_TYPE:                  // 电子邮件类型
                            break;
                        case WebView.HitTestResult.GEO_TYPE:                    // 地图类型
                            break;
                        case WebView.HitTestResult.SRC_ANCHOR_TYPE:             // 超链接类型
                            break;
                        case WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE:       // 带有链接的图片类型
                            break;
                        case WebView.HitTestResult.IMAGE_TYPE:                  // 单纯的图片类型
                            String imgUrl = result.getExtra();
                            break;
                        case WebView.HitTestResult.EDIT_TEXT_TYPE:              // 选中的文字类型
                            break;
                    }
                }
                return false;
            }
        });
    }

    /**
     * 设置webSettings属性
     *
     * @param webView 加载网页的webView
     */
    @SuppressLint("SetJavaScriptEnabled")
    protected void setWebSettings(WebView webView) {
        WebSettings webSettings = webView.getSettings();

        // JS相关设置
        webSettings.setJavaScriptEnabled(true);                                     // 如果访问的页面中要与Javascript交互，则webView必须设置支持Javascript
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);                 // 支持通过JS打开新窗口

        // User-Agent相关设置
        String userAgent = webSettings.getUserAgentString();
//        webSettings.setUserAgentString("Mozilla/5.0 (Windows NT 10.0; WOW64; rv:50.0) Gecko/20100101 Firefox/50.0");
        logD("userAgent: " + userAgent);

        // 5.1以上默认禁止了https和http混用，以下方式是开启
        if (OsHelper.isLollipop())
            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);

        // 渲染相关设置
        webSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);             // 提高渲染的优先级

        // 布局相关设置
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);  // 支持内容重新布局

        // 缓存相关设置：
        // LOAD_CACHE_ONLY: 不使用网络，只读取本地缓存数据
        // LOAD_DEFAULT: （默认）根据cache-control决定是否从网络上取数据。
        // LOAD_NO_CACHE: 不使用缓存，只从网络获取数据.
        // LOAD_CACHE_ELSE_NETWORK，只要本地有，无论是否过期，或者no-cache，都使用缓存中的数据。
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);              // 关闭webview中缓存

        // 字体相关设置
        webSettings.setStandardFontFamily("");             // 设置 WebView 的字体，默认字体为 "sans-serif"
        webSettings.setDefaultFontSize(20);                // 设置 WebView 字体的大小，默认大小为 16
        webSettings.setMinimumFontSize(12);                // 设置 WebView 支持的最小字体大小，默认为 8

        // 缩放相关设置
        webSettings.setSupportZoom(true);                  // 支持缩放，默认为true。是下面那个的前提
        webSettings.setBuiltInZoomControls(true);          // 设置内置的缩放控件。若为false，则该WebView不可缩放
        webSettings.setDisplayZoomControls(false);         // 隐藏原生的缩放控件

        // 自适应屏幕相关设置，两者合用
        webSettings.setUseWideViewPort(true);              // 将图片调整到适合webview的大小
        webSettings.setLoadWithOverviewMode(true);         // 缩放至屏幕的大小

        // HTML存储相关设置
        webSettings.setDomStorageEnabled(true);            // 开启 DOM storage API 功能 较大存储空间，使用简单
        webSettings.setDatabaseEnabled(true);              // 开启 database storage API 功能, 设置数据库缓存路径 存储管理复杂数据 方便对数据进行增加、删除、修改、查询 不推荐使用
        webSettings.setAppCacheEnabled(true);              // 开启 Application Caches 功能 方便构建离线APP 不推荐使用
        // 有时候网页需要自己保存一些关键数据， Android WebView需要自己设置
        String appCachePath = getApplicationContext().getCacheDir().getAbsolutePath();
        webSettings.setAppCachePath(appCachePath);

        // 访问本地文件相关设置。 不允许访问本地文件（不影响assets和resources资源的加载）, 外部JavaScript攻击
        webSettings.setAllowFileAccess(false);             // 设置可以访问文件
        webSettings.setAllowFileAccessFromFileURLs(false);
        webSettings.setAllowUniversalAccessFromFileURLs(false);

        // 多窗口问题
        webSettings.setSupportMultipleWindows(false);     // HTML中的_bank标签就是新建窗口打开，有时会打不开，需要加以下然后腹泻 WebChromeClient的onCreateWindow方法

        //其他细节操作
        webSettings.setLoadsImagesAutomatically(true);    // 支持自动加载图片
        webSettings.setDefaultTextEncodingName("utf-8");  // 设置编码格式
        webSettings.setGeolocationEnabled(true);          // 允许网页执行定位操作
    }

    /**
     * 重新加载当前页面(当前页面所有资源会重新加载)
     */
    protected void reload() {
        mWebView.reload();
    }

    /**
     * 分享当前页面
     *
     * @param prompt 分享的文本内容
     */
    protected void share(String prompt) {
        ShareHelper.newInstance(this)
                .setText(mWebView.getTitle() + "  分享来自【" + prompt + "】 " + mWebView.getUrl())
                .share();
    }

    /**
     * 复制当前页面链接
     */
    protected void copy() {
        ScreenHelper.copy(this, mWebView.getUrl());
        ToastHelper.show("复制成功");
    }

    /**
     * 在其他浏览器打开当前页面
     */
    protected void openInBrowser() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(mWebView.getUrl()));
        startActivity(intent);
    }

    /**
     * 清除缓存数据
     */
    protected void clearCache() {
        // 清除网页访问留下的缓存
        // 由于内核缓存是全局的因此这个方法不仅仅针对webView而是针对整个应用程序.
        mWebView.clearCache(true);

        // 清除当前webView访问的历史记录
        // 只会webView访问历史记录里的所有记录除了当前访问记录
        mWebView.clearHistory();

        // 这个api仅仅清除自动完成填充的表单数据，并不会清除WebView存储到本地的数据
        mWebView.clearFormData();
        ToastHelper.show("清除缓存成功");
    }

    /**
     * 可以前进时前进网页
     */
    protected void goForward() {
        if (mWebView.canGoForward()) mWebView.goForward();
    }

    /**
     * 可以后退时后退网页
     */
    protected void goBack() {
        if (mWebView.canGoBack()) mWebView.goBack();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        /*
         * Back键控制网页后退, 重写onKeyDown，当浏览网页，WebView可以后退时执行后退操作。
         * 问题：在不做任何处理前提下 ，浏览网页时点击系统的“Back”键,整个 Browser 会调用 finish()而结束自身
         * 目标：点击返回后，是网页回退而不是推出浏览器
         * 解决方案：在当前Activity中处理并消费掉该 Back 事件
         */
        if (keyCode == KeyEvent.KEYCODE_BACK && mWebView.canGoBack()) {
            goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 激活WebView为活跃状态，能正常执行网页的响应
     */
    @Override
    protected void onResume() {
        super.onResume();
        mWebView.onResume();
        // 恢复pauseTimers状态
        mWebView.resumeTimers();
        mWebView.getSettings().setJavaScriptEnabled(true);
    }

    /**
     * 当页面被失去焦点被切换到后台不可见状态，需要执行onPause
     * 通过onPause动作通知内核暂停所有的动作，比如DOM的解析、plugin的执行、JavaScript执行。
     */
    @Override
    protected void onPause() {
        super.onPause();
        mWebView.onPause();
        // 当应用程序(存在webView)被切换到后台时，这个方法不仅仅针对当前的webView而是全局的全应用程序的webView
        // 它会暂停所有webView的layout，parsing，JavaScriptTimer。降低CPU功耗。
        mWebView.pauseTimers();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mWebView.getSettings().setJavaScriptEnabled(false);
    }

    /**
     * 设置返回键动作（防止按返回键直接退出程序
     */
    @Override
    public void onBackPressed() {
        if (mWebView.copyBackForwardList().getCurrentIndex() > 0) {        // 判断当前历史列表是否
            goBack();
            if (!mWebView.canGoBack()) super.onBackPressed();
        } else {
            finish();
        }
    }

    /**
     * 销毁WebView
     * 在关闭了Activity时，如果WebView的音乐或视频，还在播放。就必须销毁WebView
     * 但是注意：webView调用 destroy 时,webView仍绑定在Activity上
     * 这是由于自定义webView构建时传入了该Activity的context对象
     * 因此需要先从父容器中移除webView, 然后再销毁webView:
     */
    @Override
    protected void onDestroy() {
        // 在 Activity 销毁（ WebView ）的时候，先让 WebView 加载null内容，然后移除 WebView，再销毁 WebView，最后置空。
        if (mWebView != null) {
            mWebView.clearHistory();
            mWebView.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);

            ViewGroup viewGroup = (ViewGroup) mWebView.getParent();
            if (viewGroup != null) viewGroup.removeView(mWebView);
            mWebView.stopLoading();
            mWebView.setWebViewClient(null);
            mWebView.setWebChromeClient(null);
            mWebView.destroy();
            mWebView = null;
        }
        super.onDestroy();
    }
}
