package company.matek3022.personalvkchat.activitys;//package com.example.app.activitys;
//
//import android.content.Context;
//import android.content.Intent;
//import android.os.Bundle;
//import android.support.v7.app.AppCompatActivity;
//import android.view.MenuItem;
//import android.view.View;
//import android.widget.Button;
//import android.widget.CompoundButton;
//import android.widget.EditText;
//import android.widget.Switch;
//import android.widget.Toast;
//
//import com.example.app.R;
//import com.example.app.managers.PreferencesManager;
//import com.example.app.utils.CryptUtils;
//
//public class SettingActivity extends AppCompatActivity {
//    PreferencesManager preferencesManager;
//    EditText editText;
//    byte[] crypt;
//    String decrypt;
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setTitle(getString(R.string.SETTINGS));
//        setContentView(R.layout.activity_setting);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        Switch photouser = (Switch) findViewById(R.id.switch1);
//        Switch photochat = (Switch) findViewById(R.id.switch3);
//        Switch online = (Switch) findViewById(R.id.switch2);
//        Button button = (Button) findViewById(R.id.button);
//        Button applyButton = (Button) findViewById(R.id.apply);
//        Button buttonCrypt = (Button) findViewById(R.id.crypt);
//        Button buttonDecrypt = (Button) findViewById(R.id.decrypt);
//        Button deleteFirstByte = (Button) findViewById(R.id.deleteFirstByte);
//        Button deleteLastByte = (Button) findViewById(R.id.deleteLastByte);
//
//        final EditText editText2 = (EditText) findViewById(R.id.editText2);
//        editText = (EditText) findViewById(R.id.editText);
//        preferencesManager = PreferencesManager.getInstance();
//        if (preferencesManager.getCryptKey()!=""&&preferencesManager.getCryptKey()!=null){
//            editText.setText(preferencesManager.getCryptKey());
//        }
//        photouser.setChecked(preferencesManager.getSettingPhotoUserOn());
//        photochat.setChecked(preferencesManager.getSettingPhotoChatOn());
//        online.setChecked(preferencesManager.getSettingOnline());
//
//        photouser.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                preferencesManager.setSettingPhotoUserOn(isChecked);
//            }
//        });
//        photochat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                preferencesManager.setSettingPhotoChatOn(isChecked);
//            }
//        });
//        online.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                preferencesManager.setSettingOnline(isChecked);
//            }
//        });
//
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                preferencesManager.setToken("");
//                startActivity(StartActivity.getIntent(SettingActivity.this,true,true));
//                SettingActivity.this.finish();
//            }
//        });
//
//        buttonCrypt.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                crypt = CryptUtils.cryptString(editText2.getText().toString());
//                editText2.setText(new String(crypt));
//            }
//        });
//
//        buttonDecrypt.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                decrypt = CryptUtils.decryptString(crypt);
//                editText2.setText(decrypt);
//            }
//        });
//        applyButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                preferencesManager.setCryptKey(editText.getText().toString());
//                Toast.makeText(getApplicationContext(),"Ключ сохранен", Toast.LENGTH_SHORT).show();
//            }
//        });
//
//        deleteFirstByte.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (crypt.length!=0){
//                    crypt[0]=0;
//                    editText2.setText(new String(crypt));
//                }
//            }
//        });
//
//        deleteLastByte.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (crypt.length!=0){
//                    crypt[crypt.length-1]=0;
//                    editText2.setText(new String(crypt));
//                }
//            }
//        });
//
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        preferencesManager.setCryptKey(editText.getText().toString());
//    }
//
//    static Intent getIntent (Context context){
//        Intent intent = new Intent(context, SettingActivity.class);
//        return intent;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case android.R.id.home:
//                this.finish();
//                return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }
//}
