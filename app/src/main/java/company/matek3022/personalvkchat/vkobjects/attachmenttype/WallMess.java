package company.matek3022.personalvkchat.vkobjects.attachmenttype;

import company.matek3022.personalvkchat.vkobjects.Attachment;

import java.util.ArrayList;

/**
 * Created by matek on 13.11.2016.
 */

public class WallMess {
    private int id;
    private int from_id;
    private long date;
    private String post_type;
    private String text;
    private ArrayList<Attachment> attachments;
    private post_source post_source;
    private comments comments;
    private likes likes;
    private reposts reposts;
    private ArrayList<WallMess> copy_history;
    public int getId() {
        return id;
    }

    public int getFrom_id() {
        return from_id;
    }

    public long getDate() {
        return date;
    }

    public String getPost_type() {
        return post_type;
    }

    public String getText() {
        return text;
    }

    public ArrayList<Attachment> getAttachments() {
        return attachments;
    }

    public WallMess.post_source getPost_source() {
        return post_source;
    }

    public WallMess.comments getComments() {
        return comments;
    }

    public WallMess.likes getLikes() {
        return likes;
    }

    public WallMess.reposts getReposts() {
        return reposts;
    }

    public ArrayList<WallMess> getCopy_history() {
        return copy_history;
    }

    public class post_source {
        private String type;

        public String getType() {
            return type;
        }
    }

    public class comments {
        private int count;
        private int can_post;

        public int getCan_post() {
            return can_post;
        }

        public int getCount() {
            return count;
        }
    }

    public class likes{
        private int count;
        private int user_likes;
        private int can_like;
        private int can_publish;

        public int getCount() {
            return count;
        }

        public int getCan_like() {
            return can_like;
        }

        public int getUser_likes() {
            return user_likes;
        }

        public int getCan_publish() {
            return can_publish;
        }
    }
    public class reposts{
        private int count;
        private int user_reposted;

        public int getCount() {
            return count;
        }

        public int getUser_reposted() {
            return user_reposted;
        }
    }
}
