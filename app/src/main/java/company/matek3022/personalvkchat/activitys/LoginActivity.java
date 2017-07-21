package company.matek3022.personalvkchat.activitys;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import company.matek3022.personalvkchat.R;
import company.matek3022.personalvkchat.managers.PreferencesManager;
import company.matek3022.personalvkchat.sqlite.DBHelper;

public class LoginActivity extends AppCompatActivity {

    public static String EXTRA_LOGOUT = "logout";
    SQLiteDatabase dataBase;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        boolean logout = false;
        if(extras != null) {
            logout = extras.getBoolean(EXTRA_LOGOUT, false);
        }
        setContentView(R.layout.activity_login);

        dataBase = DBHelper.getInstance().getWritableDatabase();
        dataBase.delete(DBHelper.TABLE_DIALOGS, null, null);
        dataBase.delete(DBHelper.TABLE_USERS, null, null);
        dataBase.delete(DBHelper.TABLE_FRIENDS, null, null);
        dataBase.delete(DBHelper.TABLE_USERS_IN_MESSAGES, null, null);
        dataBase.delete(DBHelper.TABLE_MESSAGES, null, null);

        WebView web = (WebView) findViewById(R.id.webView);
        web.getSettings().setJavaScriptEnabled(true);
        if (logout) {
            CookieSyncManager.createInstance(web.getContext()).sync();
            CookieManager man = CookieManager.getInstance();
            man.removeAllCookie();
        }
        web.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        web.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (url.startsWith("https://oauth.vk.com/blank.html")) {
                    doneWithThis(url);
                }
            }
        });
        //8388607
        String url = "https://oauth.vk.com/authorize?" +
                "client_id=" + 5658788 + "&" +
                "scope=" + 8388607 + "&" +
                "redirect_uri=" + "https://oauth.vk.com/blank.html" + "&" +
                "display=touch&" +
                "v=" + "5.57" + "&" +
                "response_type=token";
        web.loadUrl(url);
        web.setVisibility(View.VISIBLE);

    }

    static Intent getIntent (Context context, boolean logout){
        Intent intent = new Intent(context, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(EXTRA_LOGOUT,logout);
        return intent;
    }

    public void doneWithThis(String url) {
        String token = extract(url, "access_token=(.*?)&");
        int uid = Integer.parseInt(extract(url, "user_id=(\\d*)"));

        PreferencesManager.getInstance().setToken(token);
        PreferencesManager.getInstance().setUserID(uid);

        goNext();
    }

    public String extract(String from, String patt) {
        Pattern ptrn = Pattern.compile(patt);
        Matcher mtch = ptrn.matcher(from);
        if (!mtch.find())
            return null;
        return mtch.toMatchResult().group(1);
    }

    public void goNext() {
//        Intent intent = new Intent();
//        intent.setClass(getApplicationContext(), DialogsActivity.class);
        LoginActivity.this.finish();
//        startActivity(DialogsActivity.getIntent(getApplicationContext(),false,false));
        startActivity(new Intent(this, BaseActivity.class));
    }
}