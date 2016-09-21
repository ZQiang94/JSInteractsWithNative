package jswithnative.com.jsinteractswithnative;

import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private WebView mWebView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mWebView = (WebView) findViewById(R.id.webview);
        //支持JavaScript脚本
        mWebView.getSettings().setJavaScriptEnabled(true);
        // 加载html
        mWebView.loadUrl("file:///android_asset/web.html");
        //android为flag
        mWebView.addJavascriptInterface(MainActivity.this, "android");
        //设置ChromeClient
        mWebView.setWebChromeClient(new HarlanWebChromeClient());

        //调用js函数
        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWebView.loadUrl("javascript:javaCallJs()");
            }
        });
        //调用js函数并携带参数
        final String param = "'这是参数，注意这个参数的格式'";
        findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 传递参数调用
                mWebView.loadUrl("javascript:javaCallJswithParam(" + param + ")");
            }
        });
    }

    //由于安全原因 需要加 @JavascriptInterface
    @JavascriptInterface
    public void startFunction() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new AlertDialog.Builder(MainActivity.this).setMessage("native方法触发").show();
            }
        });
    }

    @JavascriptInterface
    public void startFunction(final String text) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new AlertDialog.Builder(MainActivity.this).setMessage(text).show();
            }
        });
    }


    /***
     * webChromeClient主要是将javascript中相应的方法翻译成android本地方法
     * <p>
     * 例如：我们重写了onJsAlert方法，那么当页面中需要弹出alert窗口时，便
     * 会执行我们的代码，按照我们的Toast的形式提示用户。
     */
    class HarlanWebChromeClient extends WebChromeClient {

        /*此处覆盖的是javascript中的alert方法。
         *当网页需要弹出alert窗口时，会执行onJsAlert中的方法
         * 网页自身的alert方法不会被调用。
         */
        @Override
        public boolean onJsAlert(WebView view, String url, String message,
                                 JsResult result) {
            show("onJsAlert");
            result.confirm();
            return true;
        }

        /*此处覆盖的是javascript中的confirm方法。
         *当网页需要弹出confirm窗口时，会执行onJsConfirm中的方法
         * 网页自身的confirm方法不会被调用。
         */
        @Override
        public boolean onJsConfirm(WebView view, String url,
                                   String message, JsResult result) {
            show("onJsConfirm");
            result.confirm();
            return true;
        }

        /*此处覆盖的是javascript中的confirm方法。
         *当网页需要弹出confirm窗口时，会执行onJsConfirm中的方法
         * 网页自身的confirm方法不会被调用。
         */
        @Override
        public boolean onJsPrompt(WebView view, String url,
                                  String message, String defaultValue,
                                  JsPromptResult result) {
            show("onJsPrompt....");
            result.confirm();
            return true;
        }
    }

    private void show(String warring) {
        Toast.makeText(this, warring, Toast.LENGTH_SHORT).show();
    }
}
