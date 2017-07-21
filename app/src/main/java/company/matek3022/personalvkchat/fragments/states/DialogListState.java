package company.matek3022.personalvkchat.fragments.states;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;

import company.matek3022.personalvkchat.R;
import company.matek3022.personalvkchat.fragments.DialogListFragment;
import company.matek3022.personalvkchat.fragments.ToolbarFragment;

import me.ilich.juggler.gui.JugglerFragment;
import me.ilich.juggler.states.ContentBelowToolbarState;
import me.ilich.juggler.states.State;

/**
 * Created by matek on 03.07.2017.
 */

public class DialogListState extends ContentBelowToolbarState<DialogListState.Params> {

    public DialogListState(String forwardMessages) {
        super(new Params(forwardMessages));
    }

    @Override
    public String getTitle(Context context, DialogListState.Params params) {
        if (params.forwardMesseges == null) return context.getString(R.string.title_dialogs);
        else return "Выберите диалог";
    }

    @Override
    public Drawable getUpNavigationIcon(Context context, DialogListState.Params params) {
        return context.getResources().getDrawable(R.drawable.ic_navigate_back);
    }

    @Override
    protected JugglerFragment onConvertContent(DialogListState.Params params, @Nullable JugglerFragment fragment) {
        return DialogListFragment.getInstance(params.forwardMesseges);
    }

    @Override
    protected JugglerFragment onConvertToolbar(DialogListState.Params params, @Nullable JugglerFragment fragment) {
        if (params.forwardMesseges == null) return ToolbarFragment.create();
        else return ToolbarFragment.createNavigation();
    }

    static class Params extends State.Params{
        String forwardMesseges;
        Params(String forwardMessages){
            this.forwardMesseges = forwardMessages;
        }
    }
}
