package company.matek3022.personalvkchat.fragments.states;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;

import company.matek3022.personalvkchat.R;
import company.matek3022.personalvkchat.fragments.DialogFragment;
import company.matek3022.personalvkchat.fragments.ToolbarFragment;

import me.ilich.juggler.gui.JugglerFragment;
import me.ilich.juggler.states.ContentBelowToolbarState;
import me.ilich.juggler.states.State;

/**
 * Created by matek on 08.07.2017.
 */

public class DialogState extends ContentBelowToolbarState<DialogState.Params> {

    public DialogState(String title, int userId, int chatId, String forwardMess) {
        super(new Params(title, userId, chatId, forwardMess));
    }

    @Override
    public String getTitle(Context context, Params params) {
        return params.title;
    }

    @Override
    public Drawable getUpNavigationIcon(Context context, Params params) {
        return context.getResources().getDrawable(R.drawable.ic_navigate_back);
    }

    @Override
    protected JugglerFragment onConvertContent(Params params, @Nullable JugglerFragment fragment) {
        return DialogFragment.getInstance(params.userId, params.chatId, params.forwardMess);
    }

    @Override
    protected JugglerFragment onConvertToolbar(Params params, @Nullable JugglerFragment fragment) {
        return ToolbarFragment.createBack();
    }

    static class Params extends State.Params{
        String title;
        int userId;
        int chatId;
        String forwardMess;
        Params(String title, int userId, int chatId, String forwardMess){
            this.title = title;
            this.userId = userId;
            this.chatId = chatId;
            this.forwardMess = forwardMess;
        }
    }
}
