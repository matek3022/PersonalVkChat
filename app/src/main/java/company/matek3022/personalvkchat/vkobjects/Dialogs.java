package company.matek3022.personalvkchat.vkobjects;

import android.os.Parcel;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Каракатица on 08.10.2016.
 */

public class Dialogs implements Serializable {

    public  Dialogs (Parcel in){
        access_token = in.readString();
        id = in.readInt();
        date = in.readLong();
        out = in.readInt();
        user_id = in.readInt();
        read_state = in.readInt();
        title = in.readString();
        body = in.readString();
        chat_id = in.readInt();
    }
    public Dialogs (int id, int user_id, int chat_id, int from_id, String body, int read_state, int out, long date) {
        this.id = id;
        this.out = out;
        this.user_id = user_id;
        this.chat_id = chat_id;
        this.from_id = from_id;
        this.body = body;
        this.read_state = read_state;
        this.date = date;
        fwd_messages = new ArrayList<Dialogs>();
        attachments = new ArrayList<Attachment>();
    }
    public Dialogs (int random_id, int id, int user_id, int chat_id, int from_id, String body, int read_state, int out, long date) {
        this.id = id;
        this.out = out;
        this.random_id = random_id;
        this.user_id = user_id;
        this.chat_id = chat_id;
        this.from_id = from_id;
        this.body = body;
        this.read_state = read_state;
        this.date = date;
        fwd_messages = new ArrayList<Dialogs>();
        attachments = new ArrayList<Attachment>();
    }
    public  Dialogs (int usid,String bady, int rs,int o, long d) {
        //access_token=at;
        user_id=usid;
        body=bady;
        read_state=rs;
        out=o;
        date=d;
        fwd_messages = new ArrayList<Dialogs>();
        attachments = new ArrayList<Attachment>();
    }
    public Dialogs (){
        read_state=20;
        fwd_messages = new ArrayList<Dialogs>();
        attachments = new ArrayList<Attachment>();
    }
    private String access_token;
    private int id;
    private long date;
    private int out;
    private int random_id;
    private int user_id;
    private int from_id;
    private int read_state;
    private ArrayList<Dialogs> fwd_messages;
    private String title;
    private String action;
    private String body;
    private String photo_50;
    private String photo_100;
    private int[] chat_active;
    private int chat_id;
    private ArrayList<Attachment> attachments;

    public int getRandom_id() {
        return random_id;
    }

    public void setRandom_id(int random_id) {
        this.random_id = random_id;
    }

    public String getAction() {
        return action;
    }

    public int getFrom_id() {
        return from_id;
    }

    public int[] getChat_active() {
        return chat_active;
    }

    public int getChat_id(){return chat_id;}
    public ArrayList<Attachment> getAttachments () {
        return attachments;
    }

    public ArrayList<Dialogs> getFwd_messages() {
        return fwd_messages;
    }

    public String getAccess_token() {
        return access_token;
    }
    public int getId() {return id;}
    public long getDate() {return date;}
    public int getOut() {return out;}
    public int getUser_id() {return user_id;}
    public int getRead_state() {return read_state;}
    public String getTitle() {return title;}
    public String getBody() {return body;}
    public String getPhoto_50() {return photo_50;}

    public String getPhoto_100() {
        return photo_100;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public void setOut(int out) {
        this.out = out;
    }

    public void setPhoto_50(String photo_50) {
        this.photo_50 = photo_50;
    }

    public void setPhoto_100(String photo_100) {
        this.photo_100 = photo_100;
    }

    public void setAttachments (ArrayList<Attachment> at) {attachments=at;}

    public void setTitle(String title) {
        this.title = title;
    }

    public void setRead_state(int read_state) {
        this.read_state = read_state;
    }
}
