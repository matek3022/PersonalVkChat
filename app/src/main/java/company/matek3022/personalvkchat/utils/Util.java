package company.matek3022.personalvkchat.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

/**
 * Created by Каракатица on 16.10.2016.
 */

public class Util {

    public static void goToUrl(Context context, String url){
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        context.startActivity(intent);
    }

    public static void setOnline(){

    }
}
