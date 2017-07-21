package company.matek3022.personalvkchat.vkobjects;

/**
 * Created by Каракатица on 29.10.2016.
 */

public class Item {
    private int unread = 0;
    private Dialogs message;

    public Dialogs getMessage() {
        return message;
    }

    public int getUnread() {
        return unread;
    }
    public Item(Dialogs dialogs, int unread) {
        message = dialogs;
        this.unread = unread;
    }
    public Item(){
        message = new Dialogs();
    }

    public void incUnread() {
        unread++;
    }

    public void setUnread(int unread) {
        this.unread = unread;
    }

    public void setMessage(Dialogs message) {
        this.message = message;
    }
}
