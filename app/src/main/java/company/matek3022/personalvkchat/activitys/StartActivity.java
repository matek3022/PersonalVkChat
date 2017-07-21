package company.matek3022.personalvkchat.activitys;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import company.matek3022.personalvkchat.R;
import company.matek3022.personalvkchat.managers.PreferencesManager;


public class StartActivity extends AppCompatActivity {
    private static final String EXTRA_LOGOUT="logout";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        if (PreferencesManager.getInstance().getToken()==""){
            startActivity(LoginActivity.getIntent(StartActivity.this,getIntent().getBooleanExtra(EXTRA_LOGOUT,true)));
            StartActivity.this.finish();
        }else {
//            startActivity(DialogsActivity.getIntent(StartActivity.this,false,false));
            startActivity(new Intent(this, BaseActivity.class));
            StartActivity.this.finish();
        }
    }

    public static Intent getIntent (Context context, boolean logout,boolean clearStack){
        Intent intent = new Intent(context, StartActivity.class);
        intent.putExtra(EXTRA_LOGOUT,logout);
        if (clearStack){
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        return intent;
    }
}
