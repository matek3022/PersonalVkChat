package company.matek3022.personalvkchat.vkobjects.longpolling;

import android.text.TextUtils;

import com.google.gson.internal.LinkedTreeMap;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by matek on 20.07.2017.
 */

public class LongPollEvent extends ArrayList<Object> implements Serializable {

    public static final String INTENT_EXTRA_SERIALIZABLE = "intent_extra_serializable";

    public static final int NEW_MESSAGE_EVENT = 4;
    public static final int READ_IN_EVENT = 6;
    public static final int READ_OUT_EVENT = 7;
    public static final int ONLINE_EVENT = 8;
    public static final int OFFLINE_EVENT = 9;
    public static final int TYPING_IN_USER_EVENT = 61;
    public static final int TYPING_IN_CHAT_EVENT = 62;
    public static final int NEW_COUNT_EVENT = 80;

    public static final String NEW_MESSAGE_INTENT = "com.example.app.vkobjects.longpollingNEW_MESSAGE_INTENT";
    public static final String READ_IN_INTENT = "com.example.app.vkobjects.longpollingREAD_IN_INTENT";
    public static final String READ_OUT_INTENT = "com.example.app.vkobjects.longpollingREAD_OUT_INTENT";
    public static final String ONLINE_INTENT = "com.example.app.vkobjects.longpollingONLINE_INTENT";
    public static final String OFFLINE_INTENT = "com.example.app.vkobjects.longpollingOFFLINE_INTENT";
    public static final String TYPING_IN_USER_INTENT = "com.example.app.vkobjects.longpollingTYPING_IN_USER_INTENT";
    public static final String TYPING_IN_CHAT_INTENT = "com.example.app.vkobjects.longpollingTYPING_IN_CHAT_INTENT";
    public static final String NEW_COUNT_INTENT = "com.example.app.vkobjects.longpollingNEW_COUNT_INTENT";

    public int type;
    public int userId = 0;
    public int count;
    public int mid;
    public int flags;
    public int ts;
    public int chatId = 0;
    public int randomId = 0;
    public String title;
    public String message;
    public LinkedTreeMap<String, String> obj;

    public void init() {
        type = ((Double) get(0)).intValue();
        switch (type) {
            case NEW_MESSAGE_EVENT:
                mid = ((Double) get(1)).intValue();
                flags = ((Double) get(2)).intValue();
                userId = ((Double) get(3)).intValue();
                ts = ((Double) get(4)).intValue();
                title = get(5).toString();
                message = get(6).toString();
                if (TextUtils.isEmpty(message) || message.equals("")) message = "Вложение";
                try {
                    obj = (LinkedTreeMap<String, String>) get(7);
                }catch (Exception ignored){
                }
                try {
                    randomId = ((Double) get(7)).intValue();
                }catch (Exception ignored){
                    randomId = 0;
                }
                break;
            case READ_IN_EVENT:
                userId = ((Double) get(1)).intValue();
                mid = ((Double) get(2)).intValue();
                break;
            case READ_OUT_EVENT:
                userId = ((Double) get(1)).intValue();
                mid = ((Double) get(2)).intValue();
                break;
            case ONLINE_EVENT:
                userId = -((Double) get(1)).intValue();
                break;
            case OFFLINE_EVENT:
                userId = -((Double) get(1)).intValue();
                break;
            case TYPING_IN_USER_EVENT:
                userId = ((Double) get(1)).intValue();
                break;
            case TYPING_IN_CHAT_EVENT:
                userId = ((Double) get(1)).intValue();
                chatId = ((Double) get(2)).intValue();
                break;
            case NEW_COUNT_EVENT:
                count = ((Double) get(1)).intValue();
                break;
        }
    }
}
