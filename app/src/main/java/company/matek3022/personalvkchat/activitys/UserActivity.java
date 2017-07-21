package company.matek3022.personalvkchat.activitys;//package com.example.app.activitys;
//
//import android.content.Context;
//import android.content.Intent;
//import android.graphics.Color;
//import android.os.Bundle;
//import android.support.design.widget.CollapsingToolbarLayout;
//import android.support.v4.content.ContextCompat;
//import android.support.v7.app.AppCompatActivity;
//import android.support.v7.widget.Toolbar;
//import android.view.Gravity;
//import android.view.MenuItem;
//import android.view.View;
//import android.widget.Button;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.example.app.R;
//import com.example.app.managers.PreferencesManager;
//import com.example.app.vkobjects.ItemMess;
//import com.example.app.vkobjects.ServerResponse;
//import com.example.app.vkobjects.User;
//import com.example.app.vkobjects.attachmenttype.PhotoMess;
//import com.google.gson.Gson;
//import com.squareup.picasso.Picasso;
//import com.stfalcon.frescoimageviewer.ImageViewer;
//
//import java.util.ArrayList;
//
//import retrofit2.Call;
//import retrofit2.Callback;
//import retrofit2.Response;
//
//import static com.example.app.App.service;
//
//public class UserActivity extends AppCompatActivity {
//    PreferencesManager preferencesManager;
//    private static final String EXTRA_USER_ID="userID";
//    private static final String EXTRA_USER_JSON="userJson";
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_user);
//
//        final Toolbar toolbar = (Toolbar) findViewById(R.id.MyToolbar);
//        toolbar.setNavigationIcon(R.mipmap.back_button);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setDisplayShowHomeEnabled(true);
//
//        CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapse_toolbar);
//        Context context=this;
//        collapsingToolbarLayout.setContentScrimColor(ContextCompat.getColor(context,R.color.primary));
//
//        preferencesManager = PreferencesManager.getInstance();
//        final int user_id = getIntent().getIntExtra(EXTRA_USER_ID, 0);
//        final User user = new Gson().fromJson(getIntent().getStringExtra(EXTRA_USER_JSON), User.class);
//        collapsingToolbarLayout.setExpandedTitleColor(Color.WHITE);
//        collapsingToolbarLayout.setCollapsedTitleTextColor(Color.WHITE);
//        collapsingToolbarLayout.setTitle(user.getFirst_name() + " " + user.getLast_name());
//
//        final LinearLayout lineBottom = (LinearLayout) findViewById(R.id.lineBottom);
//
////        LayoutInflater inflater = getLayoutInflater();
////        View cont1 = inflater.inflate(R.layout.text_container, null);
//
//        final TextView tv = new TextView(UserActivity.this);
//        final TextView tv1 = new TextView(UserActivity.this);
//        final TextView tv2 = new TextView(UserActivity.this);
//        final TextView tv3 = new TextView(UserActivity.this);
//        final TextView tv4 = new TextView(UserActivity.this);
//        final TextView tv5 = new TextView(UserActivity.this);
//        final TextView tv6 = new TextView(UserActivity.this);
//        final TextView tv7 = new TextView(UserActivity.this);
//        final TextView tv8 = new TextView(UserActivity.this);
//        final TextView tv9 = new TextView(UserActivity.this);
//
//        lineBottom.addView(tv);
//        if (user.getOnline() == 1) {
//            tv.setText(R.string.ONLINE);
//        } else {
//            tv.setText(R.string.OFFLINE);
//        }
//        if (user.getCity() != null) {
//            tv1.setText(user.getCity().getTitle());
//            lineBottom.addView(tv1);
//        }
//        if (user.getCountry() != null) {
//            tv2.setText(user.getCountry().getTitle());
//            lineBottom.addView(tv2);
//        }
//        if ((user.getBdate() != "") && (user.getBdate() != null)) {
//            tv3.setText(getString(R.string.BIRTHSDAY) + user.getBdate());
//            lineBottom.addView(tv3);
//        }
//        if ((user.getUniversity_name() != "") && (user.getUniversity_name() != null)) {
//            tv4.setText(getString(R.string.UNIVERSITY) + user.getUniversity_name());
//            lineBottom.addView(tv4);
//        }
//        if ((user.getFaculty_name() != "") && (user.getFaculty_name() != null)) {
//            tv5.setText(user.getFaculty_name());
//            lineBottom.addView(tv5);
//        }
//        if ((user.getEducation_form() != "") && (user.getEducation_form() != null)) {
//            tv6.setText(user.getEducation_form());
//            lineBottom.addView(tv6);
//        }
//        if ((user.getEducation_status() != "") && (user.getEducation_status() != null)) {
//            tv7.setText(user.getEducation_status());
//            lineBottom.addView(tv7);
//        }
//        if ((user.getMobile_phone() != "") && (user.getMobile_phone() != null)) {
//            tv8.setText(getString(R.string.MOBILE_PHONE) + user.getMobile_phone());
//            lineBottom.addView(tv8);
//        }
//        if ((user.getHome_phone() != "") && (user.getHome_phone() != null)) {
//            tv9.setText(getString(R.string.HOME_PHONE) + user.getHome_phone());
//            lineBottom.addView(tv9);
//        }
//        String photoUserUrl = "";
//        if (user.getPhoto_max_orig() != null) {
//            photoUserUrl = user.getPhoto_max_orig();
//        } else {
//            if (user.getPhoto_400_orig() != null) {
//                photoUserUrl = user.getPhoto_400_orig();
//            } else {
//                if (user.getPhoto_200() != null) {
//                    photoUserUrl = user.getPhoto_200();
//                } else {
//                    if (user.getPhoto_100() != null) {
//                        photoUserUrl = user.getPhoto_100();
//                    } else {
//                        if (user.getPhoto_50() != null) {
//                            photoUserUrl = user.getPhoto_50();
//                        }
//                    }
//                }
//            }
//        }
//        Picasso.with(UserActivity.this)
//                .load(photoUserUrl)
//                .into((ImageView) findViewById(R.id.imageView2));
//        ImageView imageView = (ImageView) findViewById(R.id.imageView2);
//        imageView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String TOKEN = preferencesManager.getToken();
//                Call<ServerResponse<ItemMess<ArrayList<PhotoMess>>>> call1 = service.getPhotos(TOKEN, user_id);
//                call1.enqueue(new Callback<ServerResponse<ItemMess<ArrayList<PhotoMess>>>>() {
//                    @Override
//                    public void onResponse(Call<ServerResponse<ItemMess<ArrayList<PhotoMess>>>> call1, Response<ServerResponse<ItemMess<ArrayList<PhotoMess>>>> response) {
//                        ArrayList<PhotoMess> l1 = response.body().getResponse().getitem();
//                        final ArrayList<String> photo = new ArrayList<>();
//                        for (int i = 0; i < l1.size(); i++) {
//                            if (l1.get(i).getPhoto_1280() != null) {
//                                photo.add(l1.get(i).getPhoto_1280());
//                            } else {
//                                if (l1.get(i).getPhoto_807() != null) {
//                                    photo.add(l1.get(i).getPhoto_807());
//                                } else {
//                                    if (l1.get(i).getPhoto_604() != null) {
//                                        photo.add(l1.get(i).getPhoto_604());
//                                    } else {
//                                        if (l1.get(i).getPhoto_130() != null) {
//                                            photo.add(l1.get(i).getPhoto_130());
//                                        } else {
//                                            if (l1.get(i).getPhoto_75() != null) {
//                                                photo.add(l1.get(i).getPhoto_75());
//                                            }
//                                        }
//                                    }
//                                }
//                            }
//                        }
//                        new ImageViewer.Builder(UserActivity.this, photo)
//                                .show();
//                    }
//
//                    @Override
//                    public void onFailure(Call<ServerResponse<ItemMess<ArrayList<PhotoMess>>>> call1, Throwable t) {
//                        Toast toast = Toast.makeText(getApplicationContext(),
//                                getString(R.string.LOST_INTERNET_CONNECTION), Toast.LENGTH_SHORT);
//                        toast.setGravity(Gravity.CENTER, 0, 0);
//                        toast.show();
//                    }
//                });
//            }
//        });
//        Button button = (Button) findViewById(R.id.button5);
//        Button button1 = (Button) findViewById(R.id.button6);
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(DialogMessageActivity.getIntent(UserActivity.this, user_id, 0, "", user.getFirst_name() + " " + user.getLast_name(), false));
//            }
//        });
//        button1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(FriendsActivity.getIntent(UserActivity.this, user_id, false));
//            }
//        });
//    }
//
//    public static Intent getIntent (Context context, int userId, String userJson){
//        Intent intent = new Intent(context, UserActivity.class);
//        intent.putExtra(EXTRA_USER_ID, userId);
//        intent.putExtra(EXTRA_USER_JSON, userJson);
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
