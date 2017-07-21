package company.matek3022.personalvkchat.fragments.states;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;

import company.matek3022.personalvkchat.R;
import company.matek3022.personalvkchat.fragments.FriendListFragment;
import company.matek3022.personalvkchat.fragments.ToolbarFragment;

import me.ilich.juggler.gui.JugglerFragment;
import me.ilich.juggler.states.ContentBelowToolbarState;
import me.ilich.juggler.states.State;

/**
 * Created by matek on 08.07.2017.
 */

public class FriendListState extends ContentBelowToolbarState<FriendListState.Params> {

    public FriendListState(String forwardMessages) {
        super(new Params(forwardMessages));
    }

    @Override
    public String getTitle(Context context, FriendListState.Params params) {
        return "Выберите друга";
    }

    @Override
    public Drawable getUpNavigationIcon(Context context, FriendListState.Params params) {
        return context.getResources().getDrawable(R.drawable.ic_navigate_back);
    }

    @Override
    protected JugglerFragment onConvertContent(FriendListState.Params params, @Nullable JugglerFragment fragment) {
        return FriendListFragment.getInstance(params.forwardMesseges);
    }

    @Override
    protected JugglerFragment onConvertToolbar(FriendListState.Params params, @Nullable JugglerFragment fragment) {
        return ToolbarFragment.createNavigation();
    }

    static class Params extends State.Params{
        String forwardMesseges;
        Params(String forwardMessages){
            this.forwardMesseges = forwardMessages;
        }
    }
}
