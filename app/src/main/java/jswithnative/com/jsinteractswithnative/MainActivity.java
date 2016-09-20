package jswithnative.com.jsinteractswithnative;

import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

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
}
