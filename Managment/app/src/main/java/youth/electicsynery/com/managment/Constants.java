package youth.electicsynery.com.managment;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by Emeka on 4/4/2018.
 */

public class Constants {
    public static boolean checkVerified = false;

    public static boolean getLog(Context context){
        SharedPreferences settings = context.getSharedPreferences("prefs", 0);
        return settings.getBoolean("logged", false);
    }

    public static void setLog(Context context, boolean isLoggedIn){
        SharedPreferences settings = context.getSharedPreferences("prefs", 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean("logged", isLoggedIn);
        editor.apply();
    }

    public static boolean isDeviceOnline(Context context) {
        ConnectivityManager connMgr =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

}
