package company.matek3022.personalvkchat.utils;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

/**
 * Created by matek on 08.07.2017.
 */

public class GuiUtils {

    /**
     * Скрываем клавиатуру
     *
     * @param v       любая {@link View}
     */
    public static void hideKeyboard(View v) {
        if (v != null && v.getContext()!= null) {
            if (!v.isFocused()) {
                v.requestFocus();
            }
            InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }

    public static void showRequestErrorMessage(Context context) {
        Toast.makeText(context, "При запросе произошла ошибка, повторите поздней", Toast.LENGTH_SHORT).show();
    }

    public static void showMessage(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}
