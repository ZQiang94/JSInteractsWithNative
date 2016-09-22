##js与native的交互
####概述
目前所常用的native与js交互有两种方式，分别为 下面提到的方法1与方法2，这两种方式各有利弊，在4.2之前使用方法1存在安全问题，
类似与sql的注入漏洞，这是运行时虚拟机的漏洞，暂且这样理解吧。另外无论哪种方式，都要与页面开发人员定要协议。
###sample运行效果图
<div align=center><img src="https://github.com/ZQiang94/JSInteractsWithNative/blob/master/imgs/GIF.gif"/></div>

###方法1(webClient.loadUrl())：
####native:加载web页面并进行相关setting;
```javascript
        mWebView = (WebView) findViewById(R.id.webview);
        //支持JavaScript脚本
        mWebView.getSettings().setJavaScriptEnabled(true);
        // 加载html
        mWebView.loadUrl("file:///android_asset/web.html");
        //android为flag
        mWebView.addJavascriptInterface(MainActivity.this, "android");
```
####native中调用（带参&不带参）js方法实现
```javascript
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
```
<img src="https://github.com/ZQiang94/JSInteractsWithNative/blob/master/imgs/微信截图_20160920211757.png"/>
####js响应native方法实现：
```javascript
 <script type="text/javascript">

        function javaCallJs(){
        	 document.getElementById("content").innerHTML ="<br\>JAVA调用了JS的无参函数";
        }

        function javaCallJswithParam(arg){
        	 document.getElementById("content").innerHTML =
        	 ("<br\>"+arg);
        }

  </script>
```
####web页面中，调用native方法具体实现
```javascript
<input type="button" value="调用native方法"
       onclick="window.android.startFunction()"/>
<br/><br/>
<input type="button" value="调用native方法并传参"
       onclick="window.android.startFunction('native方法被js调用，并传参')"/>
```
####native中，被调用的方法实现
```javascript
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
```
#####:red_circle:上段备注中提到“由于安全原因 需要加@JavascriptInterface”，是指在4.2版本之前的addjavascriptInterface接口引起的漏洞，可能导致恶意网页通过Js方法遍历刚刚通过addjavascriptInterface注入进来的类的所有方法从中获取到getClass方法，然后通过反射获取到Runtime对象，进而调用Runtime对象的exec方法执行一些操作，恶意的Js代码如下：
```javascript
function execute(args) {
    for (var obj in window) {
        if ("getClass" in window[obj]) {
            alert(obj);
            return  window[obj].getClass().forName("java.lang.Runtime")
                 .getMethod("getRuntime",null).invoke(null,null).exec(args);
        }
    }
}
```
在Android API Level 17（Android 4.2）之后，可以通过添加@JavascriptInterface这个注解来避免该漏洞，在4.1及之前可以使用方法2
实现native与js之间的交互。

###方法2（prompt()$onJsPrompt()/confirm()$onConfirm()/alert()$onAlert()）：
####继承WebChromeClient重写该方法的onJsPrompt()/onConfirm()/onAlert()方法，用到那个方法重写那个就行，然后设置webView的WebChromeClient为该重写的类
####具体实现
```javascript
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
```
然后给webView设置该重写的类
```javascript
//设置ChromeClient
mWebView.setWebChromeClient(new HarlanWebChromeClient());
```
####相应的在JS中的具体实现代码如下
```javascript
        function cfm() {
            confirm("")
        }

        function pmt() {
           prompt("","");
        }

        function onAlert()
        {
            alert("这是网页中的alert方法，如果重写了mWebView的onAlert方法，该方法不会执行");
        }
```
####触发页面中的js函数，例如：
```javascript
<p><input type="button" onclick="cfm()" value="Confirm"/></p>
<p><input type="button" onclick="pmt()" value="Prompt"/></p>
<p><input type="button" onclick="onAlert()" value="Alert"/>
```
实现原理就是在页面中触发的方法被webView中设置的WebChromeClient给拦截了，从而执行了WebChromeClient中重写的onXxx()方法，
没有执行页面中相应的onXxx()方法，这是方式相对简单，而且安全。




参考链接：
https://github.com/Sunzxyong/RainbowBridge


