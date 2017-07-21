package company.matek3022.personalvkchat.activitys;//package com.example.app.activitys;
//
//import android.content.ContentValues;
//import android.content.Context;
//import android.content.Intent;
//import android.database.Cursor;
//import android.database.sqlite.SQLiteDatabase;
//import android.os.AsyncTask;
//import android.os.Bundle;
//import android.support.design.widget.NavigationView;
//import android.support.v4.app.Fragment;
//import android.support.v4.app.FragmentManager;
//import android.support.v4.app.FragmentPagerAdapter;
//import android.support.v4.view.GravityCompat;
//import android.support.v4.view.PagerAdapter;
//import android.support.v4.view.ViewPager;
//import android.support.v4.widget.DrawerLayout;
//import android.support.v4.widget.SwipeRefreshLayout;
//import android.support.v7.app.ActionBarDrawerToggle;
//import android.support.v7.app.AppCompatActivity;
//import android.support.v7.widget.Toolbar;
//import android.util.Log;
//import android.view.Gravity;
//import android.view.Menu;
//import android.view.MenuItem;
//import android.view.View;
//import android.widget.ImageView;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.example.app.R;
//import com.example.app.fragments.FriendPageFragment;
//import com.example.app.managers.PreferencesManager;
//import com.example.app.sqlite.DBHelper;
//import com.example.app.transformation.CircularTransformation;
//import com.example.app.vkobjects.ItemMess;
//import com.example.app.vkobjects.ServerResponse;
//import com.example.app.vkobjects.User;
//import com.google.gson.Gson;
//import com.squareup.picasso.Picasso;
//
//import java.util.ArrayList;
//
//import retrofit2.Call;
//import retrofit2.Callback;
//import retrofit2.Response;
//
//import static com.example.app.App.service;
//
//public class FriendsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
//
//    ViewPager pager;
//    PagerAdapter pagerAdapter;
//    SwipeRefreshLayout refreshLayout;
//    static final int PAGE_COUNT = 2;
//    public static int page = 1; //на какой странице мы сейчас
//    public static ArrayList<User> info;
//    public static String ALL_FRIENDS="";
//    public static String ONLINE_FRIENDS="";
//    SQLiteDatabase dataBase;
//    PreferencesManager preferencesManager;
//    int user_id;
//    private static final String EXTRA_USER_ID="userID";
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        dataBase = DBHelper.getInstance().getWritableDatabase();
//        user_id = getIntent().getIntExtra(EXTRA_USER_ID, 0);
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_friends);
//        setTitle(getString(R.string.FRIENDS));
//        preferencesManager = PreferencesManager.getInstance();
//        info = new ArrayList<>();
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        pager = (ViewPager) findViewById(R.id.pager);
//        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh);
//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
//
//        setSupportActionBar(toolbar);
//
//        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
//                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
//        drawer.setDrawerListener(toggle);
//        toggle.syncState();
//
//
//        navigationView.setNavigationItemSelectedListener(this);
//        final String uidgson = preferencesManager.getUserGson();
//        if (uidgson!="") {
//            final User iuser = new Gson().fromJson(uidgson, User.class);
//            Picasso.with(FriendsActivity.this)
//                    .load(iuser.getPhoto_100())
//                    .transform(new CircularTransformation())
//                    .into((ImageView) navigationView.getHeaderView(0).findViewById(R.id.imageView20));
//            ((ImageView) navigationView.getHeaderView(0).findViewById(R.id.imageView20)).setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//
////                    startActivity(UserActivity.getIntent(FriendsActivity.this,iuser.getId(),uidgson));
//                }
//            });
//            if (iuser.getOnline()==1){
//                ((ImageView) navigationView.getHeaderView(0).findViewById(R.id.imageView21)).setVisibility(View.VISIBLE);
//            }else {
//                ((ImageView) navigationView.getHeaderView(0).findViewById(R.id.imageView21)).setVisibility(View.INVISIBLE);
//            }
//            ((TextView) navigationView.getHeaderView(0).findViewById(R.id.textView20)).setText(iuser.getFirst_name()+" "+iuser.getLast_name());
//            if (iuser.getCity() != (null)) {
//                ((TextView) navigationView.getHeaderView(0).findViewById(R.id.textView21)).setText(iuser.getCity().getTitle());
//            } else {
//                ((TextView) navigationView.getHeaderView(0).findViewById(R.id.textView21)).setText("");
//            }
//        }
//
//        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                refresh(user_id);
//            }
//        });
//        if (user_id==0) {
//            Cursor cursor = dataBase.query(DBHelper.TABLE_FRIENDS, null, null, null, null, null, null);
//            if (cursor.moveToFirst()) {
//                info.clear();
//                Gson gson = new Gson();
//                int user = cursor.getColumnIndex(DBHelper.KEY_OBJ);
//                for (int i = 0; i < cursor.getCount(); i++) {
//                    info.add(gson.fromJson(cursor.getString(user), User.class));
//                    cursor.moveToNext();
//                }
//                pagerAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager());
//                pager.setAdapter(pagerAdapter);
//
//                setCountFriends();
//
//                refresh(user_id);
//            } else {
//                refresh(user_id);
//            }
//            cursor.close();
//        }else {
//            refresh(user_id);
//        }
//    }
//
//    @Override
//    protected void onStop() {
//        if (user_id==0) {
//            new UpdateDataBase(user_id, info).executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
//        }
//        super.onStop();
//    }
//
//    static Intent getIntent(Context context, int userId, boolean clearStack){
//        Intent intent = new Intent(context, FriendsActivity.class);
//        if (clearStack){
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
//        }
//        intent.putExtra(EXTRA_USER_ID,userId);
//        return intent;
//    }
//
//    class UpdateDataBase extends AsyncTask<Void,Void,Void>{
//        int user_id;
//        ArrayList<User> userUpdate;
//        public UpdateDataBase (int userId, ArrayList<User> userArrayList){
//            userUpdate = new ArrayList<>();
//            userUpdate.addAll(userArrayList);
//            user_id=userId;
//        }
//
//        @Override
//        protected Void doInBackground(Void... params) {
//            if (user_id==0) {
//                dataBase.beginTransaction();
//                try {
//                    dataBase.delete(DBHelper.TABLE_FRIENDS, null, null);
//                    ContentValues contentValues = new ContentValues();
//                    Gson gson = new Gson();
//
//                    for (int i = 0; i < userUpdate.size(); i++) {
//                        contentValues.put(DBHelper.KEY_ID_USER, userUpdate.get(i).getId());
//                        contentValues.put(DBHelper.KEY_OBJ, gson.toJson(userUpdate.get(i)));
//                        dataBase.insert(DBHelper.TABLE_FRIENDS, null, contentValues);
//                    }
//                    dataBase.setTransactionSuccessful();
//                }catch (Exception e){
//
//                }finally {
//                    dataBase.endTransaction();
//                }
//            }
//            return null;
//        }
//    }
//
//    public void setCountFriends (){
//        int onlineCount =0;
//        for (int i=0;i<info.size();i++){
//            if (info.get(i).getOnline()==1){
//                onlineCount++;
//            }
//        }
//        setAllFriendsCount(info.size());
//        setOnlineFriendsCount(onlineCount);
//    }
//
//    public void setAllFriendsCount(int cnt) {
//        ALL_FRIENDS = getString(R.string.ALL_FRIENDS) + " (" + cnt + ")";
//    }
//
//    public void setOnlineFriendsCount(int cnt) {
//        ONLINE_FRIENDS = getString(R.string.ONLINE) + " (" + cnt + ")";
//    }
//
//    private void refresh(int user_id) {
//        refreshLayout.setRefreshing(true);
//        String TOKEN = preferencesManager.getToken();
//        Call<ServerResponse<ItemMess<ArrayList<User>>>> call = service.getFriends(TOKEN, "photo_100,photo_200,photo_400_orig,photo_max_orig, online,city,country,education, universities, schools,bdate,contacts");
//
//        call.enqueue(new Callback<ServerResponse<ItemMess<ArrayList<User>>>>() {
//            @Override
//            public void onResponse(Call<ServerResponse<ItemMess<ArrayList<User>>>> call, Response<ServerResponse<ItemMess<ArrayList<User>>>> response) {
//                ArrayList<User> l = response.body().getResponse().getitem();
//                info.clear();
//                info.addAll(l);
//                Log.wtf ("getCount",""+info.size());
//                if (pager != null) {
//                    page = pager.getCurrentItem();
//                }
//                pagerAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager());
//
//                setCountFriends();
//
//                pager.setAdapter(pagerAdapter);
//                pager.setCurrentItem(page);
//                refreshLayout.setRefreshing(false);
//            }
//
//            @Override
//            public void onFailure(Call<ServerResponse<ItemMess<ArrayList<User>>>> call, Throwable t) {
//                refreshLayout.setRefreshing(false);
//                Toast toast = Toast.makeText(getApplicationContext(),
//                        getString(R.string.LOST_INTERNET_CONNECTION), Toast.LENGTH_SHORT);
//                toast.setGravity(Gravity.CENTER, 0, 0);
//                toast.show();
//            }
//        });
//    }
//
//    private class MyFragmentPagerAdapter extends FragmentPagerAdapter {
//
//        public MyFragmentPagerAdapter(FragmentManager fm) {
//            super(fm);
//        }
//
//        @Override
//        public Fragment getItem(int position) {
//            return FriendPageFragment.newInstance(position, new Gson().toJson(info));
//        }
//
//        @Override
//        public int getCount() {
//            return PAGE_COUNT;
//        }
//
//        @Override
//        public CharSequence getPageTitle(int position) {
//            if (position == 0) {
//                return ALL_FRIENDS;
//            } else {
//                return ONLINE_FRIENDS;
//            }
//        }
//    }
//
//    @Override
//    public void onBackPressed() {
//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        if (drawer.isDrawerOpen(GravityCompat.START)) {
//            drawer.closeDrawer(GravityCompat.START);
//        } else {
//            super.onBackPressed();
//        }
//    }
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.main, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.titleee:
//                refresh(0);
//                return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }
//
//    @SuppressWarnings("StatementWithEmptyBody")
//    @Override
//    public boolean onNavigationItemSelected(MenuItem item) {
//        // Handle navigation view Item clicks here.
//        int id = item.getItemId();
//
//        if (id == R.id.nav_dialogs) {
////            startActivity(DialogsActivity.getIntent(FriendsActivity.this,false,true));
//            FriendsActivity.this.finish();
//
//        } else if (id == R.id.nav_friends) {
//            startActivity(FriendsActivity.getIntent(FriendsActivity.this,0,true));
//            FriendsActivity.this.finish();
//        } else if (id == R.id.nav_settings) {
//            startActivity(SettingActivity.getIntent(FriendsActivity.this));
//        }
//
//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        drawer.closeDrawer(GravityCompat.START);
//        return true;
//    }
//}
