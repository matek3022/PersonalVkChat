package company.matek3022.personalvkchat.vkobjects.longpolling;

import company.matek3022.personalvkchat.vkobjects.ServerResponse;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by matek on 09.07.2017.
 */

public class LongPollResponse extends ServerResponse {
    private String key;
    private String server;
    private long ts;
    private long pts;
    @SerializedName("updates")
    public ArrayList<LongPollEvent> updates;
    @SerializedName("failed")
    public int failed = 0;

    public String getKey() {
        return key;
    }

    public String getServer() {
        return server;
    }

    public long getTs() {
        return ts;
    }

    public long getPts() {
        return pts;
    }
}
