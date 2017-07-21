package company.matek3022.personalvkchat.vkobjects;

import company.matek3022.personalvkchat.utils.Link;
import company.matek3022.personalvkchat.vkobjects.attachmenttype.AudioMess;
import company.matek3022.personalvkchat.vkobjects.attachmenttype.DocMess;
import company.matek3022.personalvkchat.vkobjects.attachmenttype.PhotoMess;
import company.matek3022.personalvkchat.vkobjects.attachmenttype.VideoMess;

public class Attachment {

    private String type;
    private PhotoMess photo;
    private PhotoMess sticker;
    private PhotoMess gift;
    private VideoMess video;
    private Link link;
    private DocMess doc;
    private AudioMess audio;
    public String getType() {
        return type;
    }
    public PhotoMess getPhoto() {
        return photo;
    }

    public PhotoMess getGift() {
        return gift;
    }

    public PhotoMess getSticker() {
        return sticker;
    }

    public VideoMess getVideo() {
        return video;
    }

    public Link getLink() {
        return link;
    }

    public DocMess getDoc() {
        return doc;
    }

    public AudioMess getAudio() {
        return audio;
    }
}
