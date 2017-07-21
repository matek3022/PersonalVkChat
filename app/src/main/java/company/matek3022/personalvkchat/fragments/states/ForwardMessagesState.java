package company.matek3022.personalvkchat.fragments.states;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;

import company.matek3022.personalvkchat.R;
import company.matek3022.personalvkchat.fragments.ForwardMessagesFragment;
import company.matek3022.personalvkchat.fragments.ToolbarFragment;

import me.ilich.juggler.gui.JugglerFragment;
import me.ilich.juggler.states.ContentBelowToolbarState;
import me.ilich.juggler.states.State;

/**
 * Created by matek on 08.07.2017.
 */

public class ForwardMessagesState extends ContentBelowToolbarState<ForwardMessagesState.Params> {

    public ForwardMessagesState(String dialogs, String users, int chatId) {
        super(new ForwardMessagesState.Params(dialogs, users, chatId));
    }

    @Override
    public String getTitle(Context context, ForwardMessagesState.Params params) {
        return "Пересланые сообщения";
    }

    @Override
    public Drawable getUpNavigationIcon(Context context, ForwardMessagesState.Params params) {
        return context.getResources().getDrawable(R.drawable.ic_navigate_back);
    }

    @Override
    protected JugglerFragment onConvertContent(ForwardMessagesState.Params params, @Nullable JugglerFragment fragment) {
        return ForwardMessagesFragment.getInstance(params.dialogs, params.users, params.chatId);
    }

    @Override
    protected JugglerFragment onConvertToolbar(ForwardMessagesState.Params params, @Nullable JugglerFragment fragment) {
        return ToolbarFragment.createBack();
    }

    static class Params extends State.Params{
        String dialogs;
        String users;
        int chatId;
        Params(String dialogs, String users, int chatId){
            this.dialogs = dialogs;
            this.users = users;
            this.chatId = chatId;
        }
    }
}
