package company.matek3022.personalvkchat.fragments.states;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;

import company.matek3022.personalvkchat.R;
import company.matek3022.personalvkchat.fragments.SettingFragment;
import company.matek3022.personalvkchat.fragments.ToolbarFragment;

import me.ilich.juggler.gui.JugglerFragment;
import me.ilich.juggler.states.ContentBelowToolbarState;
import me.ilich.juggler.states.VoidParams;

/**
 * Created by matek on 08.07.2017.
 */

public class SettingState extends ContentBelowToolbarState<VoidParams> {

    public SettingState() {
        super(VoidParams.instance());
    }

    @Override
    public String getTitle(Context context, VoidParams params) {
        return context.getString(R.string.title_settings_app);
    }

    @Override
    public Drawable getUpNavigationIcon(Context context, VoidParams params) {
        return context.getResources().getDrawable(R.drawable.ic_navigate_back);
    }

    @Override
    protected JugglerFragment onConvertContent(VoidParams params, @Nullable JugglerFragment fragment) {
        return new SettingFragment();
    }

    @Override
    protected JugglerFragment onConvertToolbar(VoidParams params, @Nullable JugglerFragment fragment) {
        return ToolbarFragment.createNavigation();
    }
}
