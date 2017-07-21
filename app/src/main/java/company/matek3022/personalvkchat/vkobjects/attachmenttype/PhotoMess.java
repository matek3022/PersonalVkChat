package company.matek3022.personalvkchat.vkobjects.attachmenttype;

/**
 * Created by Каракатица on 16.10.2016.
 */

public class PhotoMess {
    private int id;
    private int owner_id;
    private String photo_75;
    private String photo_130;
    private String photo_128; //for sticker
    private String photo_256; //for sticker
    private String thumb_256; //for gift
    private String photo_604;
    private String photo_807;
    private String photo_1280;
    private String photo_2560;
    private int width;
    private int height;
    private String text;
    private long date;
    private String access_key;

    public String getPhoto_256() {
        return photo_256;
    }

    public String getThumb_256() {
        return thumb_256;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public int getId() {
        return id;
    }

    public int getOwner_id() {
        return owner_id;
    }

    public long getDate() {
        return date;
    }

    public String getAccess_key() {
        return access_key;
    }

    public String getPhoto_75() {
        return photo_75;
    }

    public String getPhoto_130() {
        return photo_130;
    }

    public String getPhoto_128() {
        return photo_128;
    }

    public String getPhoto_604() {
        return photo_604;
    }

    public String getPhoto_807() {
        return photo_807;
    }

    public String getPhoto_1280() {
        return photo_1280;
    }

    public String getPhoto_2560() {
        return photo_2560;
    }

    public String getText() {
        return text;
    }
}
