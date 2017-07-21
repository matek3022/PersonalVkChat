package company.matek3022.personalvkchat.vkobjects.longpolling;

/**
 * Created by matek on 09.07.2017.
 */

public class GetLpSrvr {
    private String key;
    private String server;
    private long ts;
    private long pts;

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

    public void setKey(String key) {
        this.key = key;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public void setTs(long ts) {
        this.ts = ts;
    }

    public void setPts(long pts) {
        this.pts = pts;
    }
}
