package company.matek3022.personalvkchat;

import android.app.Application;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.vk.sdk.VKSdk;

import company.matek3022.personalvkchat.managers.PreferencesManager;
import company.matek3022.personalvkchat.sqlite.DBHelper;
import company.matek3022.personalvkchat.utils.VKService;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by matek on 26.12.2016.
 */

public class App extends Application {
    public static final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://api.vk.com/method/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();
    public static final VKService service = retrofit.create(VKService.class);

    @Override
    public void onCreate() {
        super.onCreate();
        VKSdk.initialize(getApplicationContext());
        Fresco.initialize(getApplicationContext());
        DBHelper.init(getApplicationContext());
        PreferencesManager.init(getApplicationContext());
    }
}
