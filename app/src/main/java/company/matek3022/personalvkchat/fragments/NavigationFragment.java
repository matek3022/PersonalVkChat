package company.matek3022.personalvkchat.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import company.matek3022.personalvkchat.R;
import company.matek3022.personalvkchat.activitys.BaseActivity;
import company.matek3022.personalvkchat.fragments.states.DialogListState;
import company.matek3022.personalvkchat.fragments.states.FriendListState;
import company.matek3022.personalvkchat.fragments.states.SettingState;
import company.matek3022.personalvkchat.utils.GuiUtils;

import me.ilich.juggler.change.Add;
import me.ilich.juggler.change.Remove;
import me.ilich.juggler.gui.JugglerFragment;
import me.ilich.juggler.gui.JugglerNavigationFragment;
import me.ilich.juggler.states.State;


public class NavigationFragment extends JugglerNavigationFragment {

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 404404;

    public static JugglerFragment create(int itemIndex) {
        NavigationFragment f = new NavigationFragment();
        Bundle b = new Bundle();
        Log.v("Sokolov", "create = " + itemIndex);
        addSelectedItemToBundle(b, itemIndex);
        f.setArguments(b);
        return f;
    }

    private NavigationView navigationView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_navigation, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        navigationView.setCheckedItem(getDefaultSelectedItem());
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getDrawerLayout().setDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                GuiUtils.hideKeyboard(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
            }

            @Override
            public void onDrawerStateChanged(int newState) {
            }
        });
        navigationView = (NavigationView) view.findViewById(R.id.navigation_view);
        navigationView.inflateMenu(R.menu.navigation);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                final boolean r;
                switch (item.getItemId()) {
                    case R.id.menu_dialogs:
                        navigate(new DialogListState(null));
                        r = true;
                        break;
                    case R.id.menu_friends:
                        navigate(new FriendListState(null));
                        r = true;
                        break;
                    case R.id.menu_settings:
                        navigate(new SettingState());
                        r = true;
                        break;
                    default:
                        r = false;
                }
                if (r) {
                    close();
                }
                return r;
            }
        });
    }

    private void navigate(State state){
        if (getJugglerActivity().getJuggler().getLayoutId() == me.ilich.juggler.R.layout.juggler_layout_content_toolbar_navigation){
          navigateTo().state(Remove.all(), Add.deeper(state));
        } else {
            navigateTo().state(Remove.closeCurrentActivity(), Add.newActivity(state, BaseActivity.class));
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//        navigate();
    }

}
