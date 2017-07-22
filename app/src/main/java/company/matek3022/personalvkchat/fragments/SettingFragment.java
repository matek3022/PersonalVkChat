package company.matek3022.personalvkchat.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import company.matek3022.personalvkchat.R;
import company.matek3022.personalvkchat.activitys.StartActivity;
import company.matek3022.personalvkchat.managers.PreferencesManager;
import me.ilich.juggler.gui.JugglerFragment;

/**
 * Created by matek on 08.07.2017.
 */

public class SettingFragment extends JugglerFragment {

    PreferencesManager preferencesManager;
    EditText editText;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_setting_app, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Switch photouser = (Switch) view.findViewById(R.id.switch1);
        Switch photochat = (Switch) view.findViewById(R.id.switch3);
        Switch online = (Switch) view.findViewById(R.id.switch2);
        Button button = (Button) view.findViewById(R.id.button);
        Button applyButton = (Button) view.findViewById(R.id.apply);

        editText = (EditText) view.findViewById(R.id.editText);
        preferencesManager = PreferencesManager.getInstance();
        if (preferencesManager.getCryptKey()!=""&&preferencesManager.getCryptKey()!=null){
            editText.setText(preferencesManager.getCryptKey());
        }
        photouser.setChecked(preferencesManager.getSettingPhotoUserOn());
        photochat.setChecked(preferencesManager.getSettingPhotoChatOn());
        online.setChecked(preferencesManager.getSettingOnline());

        photouser.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                preferencesManager.setSettingPhotoUserOn(isChecked);
            }
        });
        photochat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                preferencesManager.setSettingPhotoChatOn(isChecked);
            }
        });
        online.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                preferencesManager.setSettingOnline(isChecked);
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                preferencesManager.setToken("");
                getActivity().finish();
                startActivity(StartActivity.getIntent(getActivity(),true,true));
            }
        });

        applyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                preferencesManager.setCryptKey(editText.getText().toString());
                Toast.makeText(getActivity(),"Ключ сохранен", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onPause() {
        super.onPause();
        preferencesManager.setCryptKey(editText.getText().toString());
    }
}
