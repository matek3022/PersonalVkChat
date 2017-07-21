package company.matek3022.personalvkchat.fragments;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import company.matek3022.personalvkchat.R;
import company.matek3022.personalvkchat.activitys.BaseActivity;
import company.matek3022.personalvkchat.fragments.states.DialogState;
import company.matek3022.personalvkchat.fragments.states.FriendListState;
import company.matek3022.personalvkchat.fragments.states.SettingState;
import company.matek3022.personalvkchat.managers.PreferencesManager;
import company.matek3022.personalvkchat.sqlite.DBHelper;
import company.matek3022.personalvkchat.transformation.CircularTransformation;
import company.matek3022.personalvkchat.vkobjects.Dialogs;
import company.matek3022.personalvkchat.vkobjects.Item;
import company.matek3022.personalvkchat.vkobjects.ItemMess;
import company.matek3022.personalvkchat.vkobjects.ServerResponse;
import company.matek3022.personalvkchat.vkobjects.User;
import company.matek3022.personalvkchat.vkobjects.longpolling.LongPollEvent;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

import me.ilich.juggler.change.Add;
import me.ilich.juggler.change.Remove;
import me.ilich.juggler.gui.JugglerFragment;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static company.matek3022.personalvkchat.App.service;
import static com.vk.sdk.VKUIHelper.getApplicationContext;

/**
 * Created by matek on 03.07.2017.
 */

public class DialogListFragment extends JugglerFragment {

    private static final String EXTRA_FORWARD_MESS = "extra_forward_mess";

    public static DialogListFragment getInstance(String forwardMessages){
        DialogListFragment fragment = new DialogListFragment();
        Bundle args = new Bundle();
        args.putString(EXTRA_FORWARD_MESS, forwardMessages);
        fragment.setArguments(args);
        return fragment;
    }

    private View addButton;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout refreshLayout;
    private RecyclerView.Adapter adapter;
    private SQLiteDatabase dataBase;
    private PreferencesManager preferencesManager;
    private int profileId = PreferencesManager.getInstance().getUserID();

    private String stroka = "";
    private int off = 0;
    private ArrayList<User> names;
    private ArrayList<Item> items;
    private String token;

    private String forwardMess;

    private BroadcastReceiver messagesReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            new AsyncTask<Intent, Void, Boolean>() {
                @Override
                protected Boolean doInBackground(Intent... params) {
                    if (items != null) {
                        if (items.size() > 0) {
                            LongPollEvent event = (LongPollEvent) params[0].getSerializableExtra(LongPollEvent.INTENT_EXTRA_SERIALIZABLE);
                            int value = event.flags;
                            int read = 1;
                            int out = 0;
                            if (value >= 65536) {
                                value-=65536;
                            }
                            if (value >= 512) {
                                value-= 512;
                            }
                            if (value >= 256) {
                                value-=256;
                            }
                            if (value >= 128) {
                                value-=128;
                            }
                            if (value >= 64) {
                                value-=64;
                            }
                            if (value >=32) {
                                value-=32;
                                out = 0;
                            }
                            if (value >=16) {
                                value-=16;
                                out = 0;
                            }
                            if (value >=8) {
                                value-=8;
                            }
                            if (value >=4) {
                                value-=4;
                            }
                            if (value >=2) {
                                value-=2;
                                out = 1;
                            }
                            if (value >=1) {
                                value-=1;
                                read = 0;
                            }
                            int currUserId = event.userId;
                            int currChatId = 0;
                            if (event.userId > 2000000000) {
                                currChatId = currUserId - 2000000000;
                                try {
                                    currUserId = Integer.valueOf(event.obj.get("from"));
                                } catch (Exception ignored) {
                                }
                                if (currUserId == profileId) {
                                    out = 1;
                                } else {
                                    out = 0;
                                }
                            }

                            for (int i = 0; i < items.size(); i++) {
                                if (currChatId == 0) {
                                    if (items.get(i).getMessage().getUser_id() == currUserId && items.get(i).getMessage().getChat_id() == 0) {
                                        items.get(i).setMessage(new Dialogs(event.mid, currUserId, currChatId, profileId, event.message, read, out,  System.currentTimeMillis() / 1000L));
                                        if (out == 1) {
                                            items.get(i).setUnread(0);
                                        } else {
                                            items.get(i).incUnread();
                                        }
                                        Item newItem = items.get(i);
                                        items.remove(i);
                                        items.add(0, newItem);
                                        if (adapter != null) {
                                            return true;
                                        }
                                        break;
                                    }
                                } else {
                                    if (items.get(i).getMessage().getChat_id() == currChatId) {
                                        Dialogs newDialog = new Dialogs(event.mid, currUserId, currChatId, profileId, event.message, read, out,  System.currentTimeMillis() / 1000L);
                                        newDialog.setPhoto_100(items.get(i).getMessage().getPhoto_100());
                                        newDialog.setTitle(items.get(i).getMessage().getTitle());
                                        items.get(i).setMessage(newDialog);
                                        if (out == 1) {
                                            items.get(i).setUnread(0);
                                        } else {
                                            items.get(i).incUnread();
                                        }
                                        Item newItem = items.get(i);
                                        items.remove(i);
                                        items.add(0, newItem);
                                        if (adapter != null) {
                                            return true;
                                        }
                                        break;
                                    }
                                }
                            }
                        }
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(Boolean aBoolean) {
                    if (aBoolean != null) {
                        if (aBoolean) adapter.notifyDataSetChanged();
                    }
                }
            }.execute(intent);

        }
    };

    private BroadcastReceiver onlineReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            new AsyncTask<Intent, Void, Boolean>() {
                @Override
                protected Boolean doInBackground(Intent... params) {
                    if (names != null) {
                        if (names.size() > 0) {
                            LongPollEvent event = (LongPollEvent) params[0].getSerializableExtra(LongPollEvent.INTENT_EXTRA_SERIALIZABLE);
                            for (int i = 0; i < names.size(); i++) {
                                if (names.get(i).getId() == event.userId) {
                                    names.get(i).setOnline(1);
                                    if (adapter != null) return true;
                                    break;
                                }
                            }
                        }
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(Boolean aBoolean) {
                    if (aBoolean != null) {
                        if (aBoolean) adapter.notifyDataSetChanged();
                    }
                }
            }.execute(intent);

        }
    };

    private BroadcastReceiver offlineReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            new AsyncTask<Intent, Void, Boolean>() {
                @Override
                protected Boolean doInBackground(Intent... params) {
                    if (names != null) {
                        if (names.size() > 0) {
                            LongPollEvent event = (LongPollEvent) params[0].getSerializableExtra(LongPollEvent.INTENT_EXTRA_SERIALIZABLE);
                            for (int i = 0; i < names.size(); i++) {
                                if (names.get(i).getId() == event.userId) {
                                    names.get(i).setOnline(0);
                                    if (adapter != null) return true;
                                    break;
                                }
                            }
                        }
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(Boolean aBoolean) {
                    if (aBoolean != null) {
                        if (aBoolean) adapter.notifyDataSetChanged();
                    }
                }
            }.execute(intent);
        }
    };

    private BroadcastReceiver typingReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
//            new AsyncTask<Intent, Void, User>() {
//                @Override
//                protected User doInBackground(Intent... params) {
//                    LongPollEvent event = (LongPollEvent) params[0].getSerializableExtra(LongPollEvent.INTENT_EXTRA_SERIALIZABLE);
//                    if (event.chatId == 0) {
//                        if (chat_id == 0) {
//                            if (user_id == event.userId) {
//                                for (int i = 0; i < names.size(); i++) {
//                                    if (user_id == names.get(i).getId()) {
//                                        return names.get(i);
//                                    }
//                                }
//                            }
//                        }
//                    } else {
//                        if (chat_id == event.chatId) {
//                            for (int i = 0; i < names.size(); i++) {
//                                if (event.userId == names.get(i).getId()) {
//                                    return names.get(i);
//                                }
//                            }
//                        }
//                    }
//                    return null;
//                }
//
//                @Override
//                protected void onPostExecute(User user) {
//                    if (user != null) {
//                        typingTV.setVisibility(View.VISIBLE);
//                        ((TextView) typingTV).setText(user.getFirst_name() + " " + "набирает сообщение...");
//                        new Handler().postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//                                typingTV.setVisibility(View.INVISIBLE);
//                            }
//                        }, 5000L);
//                    }
//                }
//            }.execute(intent);
        }
    };

    private BroadcastReceiver readInReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            new AsyncTask<Intent, Void, Boolean>() {
                @Override
                protected Boolean doInBackground(Intent... params) {
                    if (items != null) {
                        if (items.size() > 0) {
                            LongPollEvent event = (LongPollEvent) params[0].getSerializableExtra(LongPollEvent.INTENT_EXTRA_SERIALIZABLE);
                            for (int i = 0; i < items.size(); i++) {
                                if (event.userId == items.get(i).getMessage().getUser_id() || event.userId == items.get(i).getMessage().getChat_id() + 2000000000) {
                                    items.get(i).getMessage().setRead_state(1);
                                    items.get(i).setUnread(0);
                                }
                            }
                            if (adapter != null)
                                return true;
                        }
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(Boolean aBoolean) {
                    if (aBoolean != null) {
                        if (aBoolean) adapter.notifyDataSetChanged();
                    }
                }
            }.execute(intent);

        }
    };
    private BroadcastReceiver readOutReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            new AsyncTask<Intent, Void, Boolean>() {
                @Override
                protected Boolean doInBackground(Intent... params) {
                    if (items != null) {
                        if (items.size() > 0) {
                            LongPollEvent event = (LongPollEvent) params[0].getSerializableExtra(LongPollEvent.INTENT_EXTRA_SERIALIZABLE);
                            for (int i = 0; i < items.size(); i++) {
                                if (event.userId == items.get(i).getMessage().getUser_id() || event.userId == items.get(i).getMessage().getChat_id() + 2000000000) {
                                    items.get(i).getMessage().setRead_state(1);
                                    items.get(i).setUnread(0);

                                }
                            }
                            if (adapter != null)
                                return true;
                        }
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(Boolean aBoolean) {
                    if (aBoolean != null) {
                        if (aBoolean) adapter.notifyDataSetChanged();
                    }
                }
            }.execute(intent);

        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(getContext());
        localBroadcastManager.registerReceiver(messagesReceiver, new IntentFilter(LongPollEvent.NEW_MESSAGE_INTENT));
        localBroadcastManager.registerReceiver(onlineReceiver, new IntentFilter(LongPollEvent.ONLINE_INTENT));
        localBroadcastManager.registerReceiver(offlineReceiver, new IntentFilter(LongPollEvent.OFFLINE_INTENT));
        localBroadcastManager.registerReceiver(typingReceiver, new IntentFilter(LongPollEvent.TYPING_IN_USER_INTENT));
        localBroadcastManager.registerReceiver(typingReceiver, new IntentFilter(LongPollEvent.TYPING_IN_CHAT_INTENT));
        localBroadcastManager.registerReceiver(readInReceiver, new IntentFilter(LongPollEvent.READ_IN_INTENT));
        localBroadcastManager.registerReceiver(readOutReceiver, new IntentFilter(LongPollEvent.READ_OUT_INTENT));
        return inflater.inflate(R.layout.fragment_list_dialog, container, false);
    }

    @Override
    public void onDestroyView() {
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(getContext());
        localBroadcastManager.unregisterReceiver(messagesReceiver);
        localBroadcastManager.unregisterReceiver(onlineReceiver);
        localBroadcastManager.unregisterReceiver(offlineReceiver);
        localBroadcastManager.unregisterReceiver(typingReceiver);
        localBroadcastManager.unregisterReceiver(typingReceiver);
        localBroadcastManager.unregisterReceiver(readInReceiver);
        localBroadcastManager.unregisterReceiver(readOutReceiver);
        super.onDestroyView();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        dataBase = DBHelper.getInstance().getWritableDatabase();
        preferencesManager = PreferencesManager.getInstance();
        token = preferencesManager.getToken();
        items = new ArrayList<>();
        names = new ArrayList<>();
        forwardMess = getArguments().getString(EXTRA_FORWARD_MESS, null);
        setHasOptionsMenu(forwardMess == null);
        addButton = view.findViewById(R.id.add_button);
        recyclerView = (RecyclerView) view.findViewById(R.id.list);
        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh);
        addButton.setVisibility(forwardMess == null ? View.VISIBLE : View.GONE);
        adapter = new Adapter();
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);
        recyclerView.setAdapter(adapter);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigateTo().state(Add.newActivity(new FriendListState(null), BaseActivity.class));
            }
        });

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (preferencesManager.getSettingOnline()) {
                    String TOKEN = preferencesManager.getToken();
                    Call<ServerResponse> call = service.setOnline(TOKEN);

                    call.enqueue(new Callback<ServerResponse>() {
                        @Override
                        public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                            off = 0;
                            refresh(off);
                        }

                        @Override
                        public void onFailure(Call<ServerResponse> call, Throwable t) {
                            refreshLayout.setRefreshing(false);
                            Toast toast = Toast.makeText(getApplicationContext(),
                                    getString(R.string.LOST_INTERNET_CONNECTION), Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                        }
                    });
                } else {
                    off = 0;
                    refresh(off);
                }
            }
        });
        Cursor cursor = dataBase.query(DBHelper.TABLE_DIALOGS, null, null, null, null, null, null);
        Cursor cursor1 = dataBase.query(DBHelper.TABLE_USERS, null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            cursor1.moveToFirst();
            items.clear();
            names.clear();
            Gson gson = new Gson();
            int dialog = cursor.getColumnIndex(DBHelper.KEY_OBJ);
            int name = cursor1.getColumnIndex(DBHelper.KEY_OBJ);
            for (int i = 0; i < cursor.getCount(); i++) {
                items.add(gson.fromJson(cursor.getString(dialog), Item.class));
                cursor.moveToNext();
            }
            for (int i = 0; i < cursor1.getCount(); i++) {
                names.add(gson.fromJson(cursor1.getString(name), User.class));
                cursor1.moveToNext();
            }
            off=0;
            refresh(off);
        } else {
            off = 0;
            refresh(off);
        }
        cursor.close();
        cursor1.close();
    }

    @Override
    public void onPause() {
        super.onPause();
        new UpdateDataBase(items,names).executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.dialog_list_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_reload:
                off = 0;
                refresh(off);
                return true;
            case R.id.menu_setting:
                navigateTo().state(Add.newActivity(new SettingState(), BaseActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void refresh(final int offset) {
        refreshLayout.setRefreshing(true);
        stroka = "";
        String TOKEN = preferencesManager.getToken();
        Call<ServerResponse<ItemMess<ArrayList<Item>>>> call = service.getDialogs(TOKEN, 20, offset);

        call.enqueue(new Callback<ServerResponse<ItemMess<ArrayList<Item>>>>() {
            @Override
            public void onResponse(Call<ServerResponse<ItemMess<ArrayList<Item>>>> call,
                                   Response<ServerResponse<ItemMess<ArrayList<Item>>>> response) {
                if (response.body().getResponse() != null) {
                    ArrayList<Item> l = response.body().getResponse().getitem();
                    if (l.size() != 0) stroka += "" + l.get(0).getMessage().getUser_id();
                    if (offset == 0) {
                        items.clear();
                    }
                    for (int i = 0; i < l.size(); i++) {
                        items.add(l.get(i));
                        if (i != 0) {
                            stroka += "," + l.get(i).getMessage().getUser_id();
                        }
                    }
                    final int UID = preferencesManager.getUserID();

                    stroka += "," + UID;
                    String TOKEN = preferencesManager.getToken();
                    Call<ServerResponse<ArrayList<User>>> call1 = service.getUser(TOKEN,
                            stroka,
                            "photo_100, online, photo_400_orig,photo_max_orig,city,country,education, universities, schools, bdate, contacts");
                    call1.enqueue(new Callback<ServerResponse<ArrayList<User>>>() {
                        @Override
                        public void onResponse(Call<ServerResponse<ArrayList<User>>> call1, Response<ServerResponse<ArrayList<User>>> response) {
                            ArrayList<User> l1 = response.body().getResponse();
                            if (l1 != null) {
                                if (offset == 0) {
                                    names.clear();
                                }
                                for (int i = 0; i < l1.size(); i++) {
                                    if (l1.get(i).getId() == UID) {
                                        preferencesManager.setUserGson(new Gson().toJson(l1.get(i)));
                                    }
                                    names.add(l1.get(i));
                                }

                                adapter.notifyDataSetChanged();
                                off = offset;
                            }
                            refreshLayout.setRefreshing(false);
                        }

                        @Override
                        public void onFailure(Call<ServerResponse<ArrayList<User>>> call1, Throwable t) {
                            refreshLayout.setRefreshing(false);
                            Toast toast = Toast.makeText(getApplicationContext(),
                                    getString(R.string.LOST_INTERNET_CONNECTION), Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                        }
                    });
                } else {
                    refreshLayout.setRefreshing(false);
                }
            }

            @Override
            public void onFailure(Call<ServerResponse<ItemMess<ArrayList<Item>>>> call, Throwable t) {
                refreshLayout.setRefreshing(false);
                Toast toast = Toast.makeText(getApplicationContext(),
                        getString(R.string.LOST_INTERNET_CONNECTION), Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
        });
    }

    public static String convertMonth(int num) {
        String[] months = {"", "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul",
                "Aug", "Sep", "Oct", "Nov", "Dec"};
        return months[num];
    }

    private class ViewHolder extends RecyclerView.ViewHolder {
        ImageView photo;
        ImageView online;
        TextView name;
        TextView body;
        TextView time;
        TextView unreadCnt;
        RelativeLayout background;

        public ViewHolder(View itemView) {
            super(itemView);
            photo = (ImageView) itemView.findViewById(R.id.imageView4);
            online = (ImageView) itemView.findViewById(R.id.imageView5);
            name = (TextView) itemView.findViewById(R.id.textView);
            body = (TextView) itemView.findViewById(R.id.textView6);
            time = (TextView) itemView.findViewById(R.id.textView8);
            unreadCnt = (TextView) itemView.findViewById(R.id.textView9);
            background = (RelativeLayout) itemView.findViewById(R.id.background);
        }
    }

    private class Adapter extends RecyclerView.Adapter<ViewHolder> {

        SimpleDateFormat year = new SimpleDateFormat("yyyy");
        SimpleDateFormat month = new SimpleDateFormat("MM");
        SimpleDateFormat day = new SimpleDateFormat("dd");
        SimpleDateFormat hour = new SimpleDateFormat("HH");
        SimpleDateFormat min = new SimpleDateFormat("mm");
        SimpleDateFormat time = new SimpleDateFormat("HH:mm");

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(View.inflate(getActivity(), R.layout.item_list, null));
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            final Item item = items.get(position);
            final Dialogs dialog = item.getMessage();
            User user = new User();
            for (int i = 0; i < names.size(); i++) {
                if (dialog.getUser_id() == names.get(i).getId()) {
                    user = names.get(i);
                    break;
                }
            }
            final User userFinal = user;
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int chatId = dialog.getChat_id();
                    int userId = dialog.getUser_id();
                    if (forwardMess != null) {
                        navigateTo().state(Remove.closeCurrentActivity());
                    }
                    navigateTo().state(Add.newActivity(new DialogState(dialog.getChat_id() == 0 ? userFinal.getFirst_name() + " " + userFinal.getLast_name() : dialog.getTitle(),
                            chatId == 0 ? userId : 0,
                            chatId, forwardMess), BaseActivity.class));

                }
            });
            if (dialog.getUser_id() >= 0) {
                if (dialog.getChat_id() == 0) {

                    if (user.getOnline() != 0) {
                        holder.online.setVisibility(View.VISIBLE);
                    } else {
                        holder.online.setVisibility(View.INVISIBLE);
                    }
                    if (preferencesManager.getSettingPhotoUserOn()) {
                        Picasso.with(getActivity())
                                .load(user.getPhoto_100())
                                .transform(new CircularTransformation())
                                .into(holder.photo);
                    } else {
                        Picasso.with(getActivity())
                                .load(R.drawable.soviet100)
                                .transform(new CircularTransformation())
                                .into(holder.photo);
                    }
                    holder.name.setText(user.getFirst_name() + " " + user.getLast_name());
                    if (user.getOnline() == 0) {
                        holder.online.setVisibility(View.INVISIBLE);
                    } else {
                        holder.online.setVisibility(View.VISIBLE);
                    }
                } else {
                    if (preferencesManager.getSettingPhotoUserOn())
                        if (dialog.getPhoto_100() != null) {
                            Picasso.with(getActivity())
                                    .load(dialog.getPhoto_100())
                                    .transform(new CircularTransformation())
                                    .into(holder.photo);
                        } else {
                            Picasso.with(getActivity())
                                    .load(R.drawable.soviet100)
                                    .transform(new CircularTransformation())
                                    .into(holder.photo);
                        }
                    holder.name.setText(dialog.getTitle());
                    holder.online.setVisibility(View.INVISIBLE);
                }
            } else {
                Picasso.with(getActivity())
                        .load(R.drawable.soviet100)
                        .transform(new CircularTransformation())
                        .into(holder.photo);
                holder.name.setText(getString(R.string.COMMUNITY));
                holder.online.setVisibility(View.INVISIBLE);
            }
            if (dialog.getOut() == 0) {
                if (dialog.getAttachments().size() > 0) {
                    holder.body.setText(dialog.getAttachments().get(0).getType());
                } else {
                    if (!dialog.getFwd_messages().isEmpty()) {
                        holder.body.setText(getString(R.string.FORWARD_MESSAGES));
                    } else {
                        holder.body.setText(dialog.getBody());
                    }
                }
            } else {
                if (dialog.getAttachments().size() > 0) {
                    holder.body.setText(getString(R.string.YOU) +  dialog.getAttachments().get(0).getType() );
                } else {
                    if (!dialog.getFwd_messages().isEmpty()) {
                        holder.body.setText(getString(R.string.YOU) + getString(R.string.FORWARD_MESSAGES));
                    } else {
                        holder.body.setText(getString(R.string.YOU) + dialog.getBody());
                    }
                }
            }
            if (dialog.getAction()!=null){
                if (dialog.getAction().equals("chat_kick_user"))
                    holder.body.setText(getString(R.string.left_chat));
            }

            year.setTimeZone(TimeZone.getDefault());
            month.setTimeZone(TimeZone.getDefault());
            day.setTimeZone(TimeZone.getDefault());
            hour.setTimeZone(TimeZone.getDefault());
            min.setTimeZone(TimeZone.getDefault());
            time.setTimeZone(TimeZone.getDefault());
            Date dateCurr = new Date(System.currentTimeMillis());
            Date dateTs = new Date(dialog.getDate() * 1000L);
            String time_day = day.format(dateTs);
            String time_time = time.format(dateTs);
            String time_year = year.format(dateTs);
            if (year.format(dateTs).equals(year.format(dateCurr))) {
                if ((day.format(dateTs).equals(day.format(dateCurr))) && (month.format(dateTs).equals(month.format(dateCurr)))) {
                    holder.time.setText("" + time_time);
                } else {
                    holder.time.setText("" + time_day + " "
                            + convertMonth(Integer.parseInt(month.format(dateTs))));
                }
            } else {
                holder.time.setText("" + time_year);
            }
            if (item.getUnread() != 0) {
                holder.background.setBackgroundResource(R.color.accent);
                holder.unreadCnt.setVisibility(View.VISIBLE);
                holder.unreadCnt.setText("" + item.getUnread());
                holder.unreadCnt.setBackgroundResource(R.drawable.circleiterationdialogs);
            } else {
                holder.background.setBackgroundColor(Color.WHITE);
                holder.unreadCnt.setVisibility(View.INVISIBLE);
            }
            if (dialog.getRead_state() == 0) {
                holder.body.setBackgroundResource(R.drawable.circledialogs);
            } else {
                holder.body.setBackgroundColor(Color.WHITE);
            }
            if (position == off + 19) {

                refresh(off + 20);
            }
        }

        @Override
        public int getItemCount() {
            return items.size();
        }
    }

    class UpdateDataBase extends AsyncTask<Void, Void, Void> {
        ArrayList<Item> items;
        ArrayList<User> names;
        public UpdateDataBase (ArrayList<Item> itemArrayList, ArrayList<User> userArrayList){
            items = new ArrayList<>();
            names = new ArrayList<>();
            items.addAll(itemArrayList);
            names.addAll(userArrayList);
        }
        @Override
        protected Void doInBackground(Void... params) {
            dataBase.beginTransaction();
            try {
                dataBase.delete(DBHelper.TABLE_DIALOGS, null, null);
                dataBase.delete(DBHelper.TABLE_USERS, null, null);
                ContentValues contentValues = new ContentValues();
                Gson gson = new Gson();

                for (int i = 0; i < items.size(); i++) {
                    if (items.get(i).getMessage().getChat_id() == 0) {
                        contentValues.put(DBHelper.KEY_ID_USER, items.get(i).getMessage().getUser_id());
                    } else {
                        contentValues.put(DBHelper.KEY_ID_USER, items.get(i).getMessage().getChat_id() + 2000000000);
                    }
                    contentValues.put(DBHelper.KEY_OBJ, gson.toJson(items.get(i)));
                    dataBase.insert(DBHelper.TABLE_DIALOGS, null, contentValues);
                }

                for (int i = 0; i < names.size(); i++) {
                    contentValues.put(DBHelper.KEY_ID_USER, names.get(i).getId());
                    contentValues.put(DBHelper.KEY_OBJ, gson.toJson(names.get(i)));
                    dataBase.insert(DBHelper.TABLE_USERS, null, contentValues);
                }
                dataBase.setTransactionSuccessful();
            }catch (Exception e){

            }finally {
                dataBase.endTransaction();
            }
            return null;
        }
    }
}
