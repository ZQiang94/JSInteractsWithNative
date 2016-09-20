##js与native的交互

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
####带参&不带参
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
<image url="微信截图_20160920211757.png"/>
####js中：
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

###方法2：


参考链接：
http://blog.csdn.net/wu56yue/article/details/51236587


