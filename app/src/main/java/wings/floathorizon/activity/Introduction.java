package wings.floathorizon.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;

import wings.floathorizon.MainActivity;
import wings.floathorizon.R;

/**
 * 说明
 */
public class Introduction extends AppCompatActivity
{
    /*重载*/
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);      //隐藏标题栏
        setContentView(R.layout.activity_introduction);
        if(getSupportActionBar()!=null)     //隐藏标题栏
            getSupportActionBar().hide();

        final WebView webView=(WebView)findViewById(R.id.activityIntroduction_webView);
        webView.setWebViewClient(new WebViewClient()
        {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url)
            {
                webView.loadUrl(url);

                return true;
            }
        });
        webView.loadUrl(getResources().getString(R.string.url_introduction));

        ImageButton back=(ImageButton)findViewById(R.id.activityIntroduction_back);
        back.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent=new Intent(Introduction.this,MainActivity.class);
                startActivity(intent);
            }
        });
    }
}
