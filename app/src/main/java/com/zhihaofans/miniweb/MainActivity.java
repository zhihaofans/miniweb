package com.zhihaofans.miniweb;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.DownloadListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    String HomePage="http://www.baidu.com";
    private WebView webView;
    private Toolbar toolbar;
    private ProgressBar ProgressBar;
    private Menu  Menu;
    private long exitTime = 0;
    private boolean webloading=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        ProgressBar = (ProgressBar) findViewById(R.id.progressBar_loading);
        setSupportActionBar(toolbar);

        CloseActivityClass.activityList.add(this);  //退出代码初始化

        //WebView
        webView= (WebView) findViewById(R.id.WebView1);
        webView.getSettings().setJavaScriptEnabled(true);  //设置WebView属性,运行执行js脚本
        webView.getSettings().setAllowFileAccess(true);  //启用或禁用WebView访问文件数据
        //webview优化↓
        WebSettings settings = webView.getSettings();
        settings.setAppCacheEnabled(true);
        settings.setDatabaseEnabled(true);
        settings.setDomStorageEnabled(true);//开启DOM缓存
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);

        webView.loadUrl(HomePage);
        webView.setWebViewClient(new WebViewClient() {
            //设置在webView点击打开的新网页在当前界面显示,而不跳转到新的浏览器中
            @Override
            public void onPageFinished(WebView view, String url)
            {
                //结束
                webloading=false;
                Menu.findItem(R.id.action_stop).setVisible(false);
                Menu.findItem(R.id.action_refresh).setVisible(true);
                if(webView.canGoForward()){
                    Menu.findItem(R.id.action_next).setVisible(true);
                }
                if(webView.canGoBack()){
                    Menu.findItem(R.id.action_back).setVisible(true);
                }
                ProgressBar.setVisibility(view.INVISIBLE);
                toolbar.setTitle(webView.getTitle());
                super.onPageFinished(view, url);
            }
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon)
            {
                //开始
                webloading=true;
                toolbar.setTitle("加载中......");
                ProgressBar.setVisibility(view.VISIBLE);
                Menu.findItem(R.id.action_stop).setVisible(true);
                Menu.findItem(R.id.action_back).setVisible(false);
                Menu.findItem(R.id.action_refresh).setVisible(false);
                Menu.findItem(R.id.action_next).setVisible(false);
                super.onPageStarted(view, url, favicon);
            }
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        webView.setDownloadListener(new DownloadListener(){     //下载监视
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition,
                                        String mimetype, long contentLength) {
                Log.e("HEHE","开始下载");
                midToast("即将调用外部下载");
                Uri uri = Uri.parse(url);
                Intent intent = new Intent(Intent.ACTION_VIEW,uri);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        Menu=menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {   //菜单
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()){
            case R.id.action_openurl:
                openurl();
                return true;
            case R.id.action_settings:
                midToast("设置开发中");
                return true;
            case R.id.action_homepage:
                webView.loadUrl(HomePage);
                return true;
            case R.id.action_back:
                if(webloading){
                    webView.stopLoading();
                    webloading=false;
                }
                if(webView.canGoBack()){
                    webView.goBack();
                }
                return true;
            case R.id.action_next:
                if(webloading){
                    webView.stopLoading();
                    webloading=false;
                }
                if(webView.canGoForward()){
                    webView.goForward();
                }
                return true;
            case R.id.action_refresh:
                if(webloading){
                    webView.stopLoading();
                    webloading=false;
                }
                webView.reload();
                return true;
            case R.id.action_stop:
                if(webloading){
                    webView.stopLoading();
                    webloading=false;
                }
                return true;
            case R.id.action_exit:
                webView.stopLoading();
                CloseActivityClass.exitClient(MainActivity.this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
        //noinspection SimplifiableIfStatement
    }

    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                midToast("再按一次退出程序");
                exitTime = System.currentTimeMillis();
            } else {
                super.onBackPressed();
            }

        }
    }
    public void openurl() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout layout = (LinearLayout)inflater.inflate(R.layout.dialogview, null);
        dialog.setView(layout);
        final EditText Einput = (EditText)layout.findViewById(R.id.input);
        dialog.setPositiveButton("打开", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                String newurl = Einput.getText().toString();
                if(newurl.length()!=0){
                    if(!newurl.contains("//")){
                        newurl="http://"+newurl;
                    }
                    if(!newurl.contains("http://")){
                        if(!newurl.contains("https://")){
                            newurl="http://m.baidu.com/s?word="+newurl;
                        }
                    }
                    webView.loadUrl(newurl);

                }
            }
        });
        dialog.setNegativeButton("百度搜索", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                String newurl = Einput.getText().toString();
                if(newurl.length()!=0){
                    newurl="http://m.baidu.com/s?word="+newurl;
                    webView.loadUrl(newurl);
                }
            }
        });
        dialog.show();
    }
    void midToast(String str)
    {
        Toast toast = Toast.makeText(MainActivity.this, str, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER_VERTICAL| Gravity.CENTER_HORIZONTAL , 0, 0);  //设置显示位置
        TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
        v.setTextColor(Color.WHITE);     //设置字体颜色
        toast.show();
    }
}
