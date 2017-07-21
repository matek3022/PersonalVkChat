package company.matek3022.personalvkchat.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import company.matek3022.personalvkchat.R;

import me.ilich.juggler.gui.JugglerFragment;
import me.ilich.juggler.gui.JugglerToolbarFragment;

public class ToolbarFragment extends JugglerToolbarFragment {

    public static JugglerFragment create() {
        ToolbarFragment f = new ToolbarFragment();
        Bundle b = addDisplayOptionsToBundle(null, ActionBar.DISPLAY_SHOW_TITLE);
        f.setArguments(b);
        return f;
    }

    public static JugglerFragment createNavigation() {
        ToolbarFragment f = new ToolbarFragment();
        Bundle b = addDisplayOptionsToBundle(null, ActionBar.DISPLAY_SHOW_TITLE | ActionBar.DISPLAY_HOME_AS_UP);
        f.setArguments(b);
        return f;
    }


    public static JugglerFragment createBack() {
        ToolbarFragment f = new ToolbarFragment();
        Bundle b = addDisplayOptionsToBundle(null, ActionBar.DISPLAY_SHOW_TITLE | ActionBar.DISPLAY_HOME_AS_UP);
        f.setArguments(b);
        return f;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_toolbar, container, false);
    }

    @Override
    protected int getToolbarId() {
        return R.id.toolbar;
    }

}
