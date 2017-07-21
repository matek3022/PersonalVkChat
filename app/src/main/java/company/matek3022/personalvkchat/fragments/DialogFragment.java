package company.matek3022.personalvkchat.fragments;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.luseen.autolinklibrary.AutoLinkMode;
import com.luseen.autolinklibrary.AutoLinkOnClickListener;
import com.luseen.autolinklibrary.AutoLinkTextView;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;
import com.squareup.picasso.Picasso;
import com.stfalcon.frescoimageviewer.ImageViewer;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

import company.matek3022.personalvkchat.R;
import company.matek3022.personalvkchat.activitys.BaseActivity;
import company.matek3022.personalvkchat.fragments.states.DialogListState;
import company.matek3022.personalvkchat.fragments.states.ForwardMessagesState;
import company.matek3022.personalvkchat.fragments.states.FriendListState;
import company.matek3022.personalvkchat.managers.PreferencesManager;
import company.matek3022.personalvkchat.sqlite.DBHelper;
import company.matek3022.personalvkchat.transformation.CircularTransformation;
import company.matek3022.personalvkchat.utils.CryptUtils;
import company.matek3022.personalvkchat.utils.GuiUtils;
import company.matek3022.personalvkchat.utils.Util;
import company.matek3022.personalvkchat.vkobjects.Attachment;
import company.matek3022.personalvkchat.vkobjects.Dialogs;
import company.matek3022.personalvkchat.vkobjects.ItemMess;
import company.matek3022.personalvkchat.vkobjects.ServerResponse;
import company.matek3022.personalvkchat.vkobjects.User;
import company.matek3022.personalvkchat.vkobjects.VideoInformation;
import company.matek3022.personalvkchat.vkobjects.longpolling.LongPollEvent;
import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;
import me.ilich.juggler.change.Add;
import me.ilich.juggler.gui.JugglerFragment;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.vk.sdk.VKUIHelper.getApplicationContext;
import static company.matek3022.personalvkchat.App.service;

/**
 * Created by matek on 08.07.2017.
 */

public class DialogFragment extends JugglerFragment {

    private static final String EXTRA_USER_ID = "userID";
    private static final String EXTRA_CHAT_ID = "ChatID";
    private static final String EXTRA_FORWARD_MESS = "frwrd_mess";

    public static DialogFragment getInstance(int userId, int chatId, String forwardMess) {
        DialogFragment fragment = new DialogFragment();
        Bundle args = new Bundle();
        args.putInt(EXTRA_USER_ID, userId);
        args.putInt(EXTRA_CHAT_ID, chatId);
        args.putString(EXTRA_FORWARD_MESS, forwardMess);
        fragment.setArguments(args);
        return fragment;
    }

    private ArrayList<Integer> frwdMessages = new ArrayList<>();

    private int profileId = PreferencesManager.getInstance().getUserID();
    private int user_id;
    private int chat_id;
    private String title;
    private boolean frwd;
    private Adapter adapter;
    private Button sendButton;
    private RecyclerView recyclerView;
    private SwipyRefreshLayout refreshLayout;
    private int off;
    private ArrayList<Dialogs> items;
    private ArrayList<User> names;
    private ArrayList<Integer> namesIds;
    private SQLiteDatabase dataBase;
    private PreferencesManager preferencesManager;
    private EmojiconEditText mess;
    private View typingTV;
    private Button forwardButton;
    private String inputForwardMess;

    private boolean crypting;
    private String cryptKey;

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
                            if (currChatId == 0) {
                                if (currUserId == user_id) {
                                    if (items.get(items.size()-1).getId() == 0) {
                                        items.get(items.size() - 1).setId(event.mid);
                                        items.get(items.size() - 1).setDate(System.currentTimeMillis() / 1000L);
                                    } else {
                                        items.add(new Dialogs(event.mid, currUserId, currChatId, profileId, event.message, read, out, System.currentTimeMillis() / 1000L));
                                    }
                                    if (adapter != null) {
                                        return true;
                                    }
                                }
                            } else {
                                if (currChatId == chat_id) {
                                    if (items.get(items.size()-1).getId() == 0) {
                                        items.get(items.size() - 1).setId(event.mid);
                                        items.get(items.size() - 1).setDate(System.currentTimeMillis() / 1000L);
                                    } else {
                                        items.add(new Dialogs(event.mid, currUserId, currChatId, profileId, event.message, read, out, System.currentTimeMillis() / 1000L));
                                    }
                                    if (adapter != null) {
                                        return true;
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
                        if (aBoolean) {
                            recyclerView.scrollToPosition(items.size()-1);
                            adapter.notifyDataSetChanged();
                        }
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
                        if (aBoolean) {
                            adapter.notifyDataSetChanged();
                        }
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
                        if (aBoolean) {
                            adapter.notifyDataSetChanged();
                        }
                    }
                }
            }.execute(intent);
        }
    };

    private BroadcastReceiver typingReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            new AsyncTask<Intent, Void, User>() {
                @Override
                protected User doInBackground(Intent... params) {
                    LongPollEvent event = (LongPollEvent) params[0].getSerializableExtra(LongPollEvent.INTENT_EXTRA_SERIALIZABLE);
                    if (event.chatId == 0) {
                        if (chat_id == 0) {
                            if (user_id == event.userId) {
                                for (int i = 0; i < names.size(); i++) {
                                    if (user_id == names.get(i).getId()) {
                                        return names.get(i);
                                    }
                                }
                            }
                        }
                    } else {
                        if (chat_id == event.chatId) {
                            for (int i = 0; i < names.size(); i++) {
                                if (event.userId == names.get(i).getId()) {
                                    return names.get(i);
                                }
                            }
                        }
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(User user) {
                    if (user != null) {
                        typingTV.setVisibility(View.VISIBLE);
                        ((TextView) typingTV).setText(user.getFirst_name() + " " + "набирает сообщение...");
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                typingTV.setVisibility(View.INVISIBLE);
                            }
                        }, 5000L);
                    }
                }
            }.execute(intent);
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
                            if (event.userId == user_id || event.userId == chat_id + 2000000000) {
                                for (int i = 0; i < items.size(); i++) {
                                    if (items.get(i).getOut() == 0) {
                                        items.get(i).setRead_state(1);
                                    }
                                }
                            }
                            return true;
                        }
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(Boolean aBoolean) {
                    if (aBoolean != null) {
                        if (aBoolean) {
                            adapter.notifyDataSetChanged();
                        }
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
                            if (event.userId == user_id || event.userId == chat_id + 2000000000) {
                                for (int i = 0; i < items.size(); i++) {
                                    if (items.get(i).getOut() == 1) {
                                        items.get(i).setRead_state(1);
                                    }
                                }
                            }
                            return true;
                        }
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(Boolean aBoolean) {
                    if (aBoolean != null) {
                        if (aBoolean) {
                            adapter.notifyDataSetChanged();
                        }
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
        return inflater.inflate(R.layout.fragment_dialog, container, false);
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
        frwd = false;
        dataBase = DBHelper.getInstance().getWritableDatabase();
        preferencesManager = PreferencesManager.getInstance();
        user_id = getArguments().getInt(EXTRA_USER_ID, 0);
        chat_id = getArguments().getInt(EXTRA_CHAT_ID, 0);
        crypting = preferencesManager.getIsCryptById(chat_id == 0 ? user_id : 2000000000 + chat_id);
        cryptKey = preferencesManager.getCryptKeyById(chat_id == 0 ? user_id : 2000000000 + chat_id);
        if (cryptKey.equals("")) cryptKey = preferencesManager.getCryptKey();
        inputForwardMess = getArguments().getString(EXTRA_FORWARD_MESS, null);
        if (inputForwardMess != null)
            frwdMessages = new Gson().fromJson(inputForwardMess, new TypeToken<ArrayList<Integer>>() {
            }.getType());
        items = new ArrayList<>();
        names = new ArrayList<>();
        namesIds = new ArrayList<>();
        adapter = new Adapter();
        typingTV = view.findViewById(R.id.typing_tv);
        recyclerView = (RecyclerView) view.findViewById(R.id.list);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        mess = (EmojiconEditText) view.findViewById(R.id.editText);
        refreshLayout = (SwipyRefreshLayout) view.findViewById(R.id.refresh);
        sendButton = (Button) view.findViewById(R.id.button);
        forwardButton = (Button) view.findViewById(R.id.fab);
        ImageView imageEmoji = (ImageView) view.findViewById(R.id.emoji_button);
        imageEmoji.setImageResource(R.drawable.smiley);
        setHasOptionsMenu(true);
        EmojIconActions emojIconActions = new EmojIconActions(getActivity(), view.findViewById(R.id.rootContainer), mess, imageEmoji);
        emojIconActions.ShowEmojIcon();

        if (frwdMessages.size() > 0) mess.setHint("Выбрано " + frwdMessages.size());

        llm.setOrientation(LinearLayoutManager.VERTICAL);
        llm.setStackFromEnd(true);
        recyclerView.setLayoutManager(llm);
        recyclerView.setAdapter(adapter);

        refreshLayout.setColorSchemeResources(R.color.accent);

        refreshLayout.setOnRefreshListener(new SwipyRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(SwipyRefreshLayoutDirection direction) {
                if (direction == SwipyRefreshLayoutDirection.BOTTOM) {
                    if (off != 0) {
                        off -= 100;
                    }
                    refresh(off);
                } else {
                    off += 100;
                    refresh(off);
                }
            }
        });
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!frwd) {
                    refreshLayout.setRefreshing(true);
                    if ((!mess.getText().toString().equals("")) || (frwdMessages.size() > 0)) {
                        String message = mess.getText().toString();
                        if (crypting) message = CryptUtils.cryptWritibleString(message, cryptKey);
                        mess.setText("");
                        int kek = user_id;
                        if (chat_id != 0) {
                            kek = 0;
                        }

                        String strIdMess = "";
                        for (int i = 0; i < frwdMessages.size(); i++) {
                            strIdMess += "," + frwdMessages.get(i);
                        }
                        final String messageFinal = message;
                        items.add(new Dialogs(0, user_id, chat_id, profileId, messageFinal, 0, 1, 0));
                        recyclerView.scrollToPosition(items.size()-1);
                        adapter.notifyDataSetChanged();
                        String TOKEN = preferencesManager.getToken();
                        Call<ServerResponse> call = service.sendMessage(TOKEN, kek, message, chat_id, 2000000000 + chat_id, strIdMess);

                        call.enqueue(new Callback<ServerResponse>() {
                            @Override
                            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                                if (response.body() == null) GuiUtils.showRequestLimitErrorMessage(getActivity());
                                frwdMessages.clear();
                                mess.setHint(getString(R.string.WRITE_MESSAGE));
                                refreshLayout.setRefreshing(false);
//                                off = 0;
//                                refresh(off);
                            }

                            @Override
                            public void onFailure(Call<ServerResponse> call, Throwable t) {
                                refreshLayout.setRefreshing(false);
                                Toast toast = Toast.makeText(getApplicationContext(),
                                        getString(R.string.LOST_INTERNET_CONNECTION), Toast.LENGTH_SHORT);
                                for (int i = 0; i < items.size(); i++) {
                                    if (items.get(i).getBody().equals(messageFinal)) {
                                        items.remove(i);
                                        adapter.notifyDataSetChanged();
                                    }
                                }
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                toast.show();
                            }
                        });
                    } else {
                        refreshLayout.setRefreshing(false);
                        Toast toast = Toast.makeText(getApplicationContext(),
                                getString(R.string.VOID_MESSAGE), Toast.LENGTH_SHORT);
                        toast.show();
                    }
                } else {
                    refreshLayout.setRefreshing(false);
                }
            }
        });

        Cursor cursor = dataBase.query(DBHelper.TABLE_MESSAGES, null, DBHelper.KEY_ID_DIALOG + " = ?", new String[]{user_id + ""}, null, null, DBHelper.KEY_TIME_MESSAGES);
        Cursor cursor1 = dataBase.query(DBHelper.TABLE_USERS_IN_MESSAGES, null, DBHelper.KEY_ID_DIALOG + " = ?", new String[]{user_id + ""}, null, null, DBHelper.KEY_ID);
        Log.i("dataBase", cursor.getCount() + " " + cursor1.getCount());
        if (cursor.moveToFirst()) {
            Log.i("dataBase", cursor.getCount() + " " + cursor1.getCount());
            cursor1.moveToFirst();
            items.clear();
            names.clear();
            Gson gson = new Gson();
            int dialog = cursor.getColumnIndex(DBHelper.KEY_OBJ);
            int name = cursor1.getColumnIndex(DBHelper.KEY_OBJ);
            for (int i = 0; i < cursor.getCount(); i++) {
                items.add(gson.fromJson(cursor.getString(dialog), Dialogs.class));
                cursor.moveToNext();
            }
            for (int i = 0; i < cursor1.getCount(); i++) {
                names.add(gson.fromJson(cursor1.getString(name), User.class));
                cursor1.moveToNext();
            }
            adapter.reserv.addAll(items);
            off = 0;
            refresh(off);
        } else {
            off = 0;
            refresh(off);
        }
        cursor.close();
        cursor1.close();

    }

    public void nameRec(Dialogs contain_mess) {
        boolean chek = false;
        for (int i = 0; i < namesIds.size(); i++) {
            if (namesIds.get(i) == contain_mess.getUser_id()) {
                chek = true;
            }
        }
        if (!chek) {
            namesIds.add(contain_mess.getUser_id());
        }
        for (int i = 0; i < contain_mess.getFwd_messages().size(); i++) {
            nameRec(contain_mess.getFwd_messages().get(i));
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        new UpdateDataBase(user_id, items, names).executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.dialog_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.dialog_menu_reload:
                off = 0;
                refresh(off);
                return true;
            case R.id.dialog_menu_security:
                AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                final View view = getActivity().getLayoutInflater().inflate(R.layout.layout_alert_dialog_security_setting, null);
                ((Switch) view.findViewById(R.id.switch_security)).setChecked(crypting);
                ((Switch) view.findViewById(R.id.switch_security)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        crypting = b;
                    }
                });
                ((EditText)view.findViewById(R.id.et_security)).setText(preferencesManager.getCryptKeyById(chat_id == 0 ? user_id : 2000000000 + chat_id));
                alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
                        preferencesManager.setIsCryptById(chat_id == 0 ? user_id : 2000000000 + chat_id, crypting);
                        String localCryptKey = ((EditText)view.findViewById(R.id.et_security)).getText().toString();
                        if (localCryptKey.equals("")) cryptKey = preferencesManager.getCryptKey();
                        else cryptKey = localCryptKey;
                        preferencesManager.setCryptKeyById(chat_id == 0 ? user_id : 2000000000 + chat_id, localCryptKey);
                        adapter.notifyDataSetChanged();
                    }
                });
                alertDialog.setView(view);
                alertDialog.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void refresh(final int offset) {
        if (!frwd) {
            refreshLayout.setRefreshing(true);

            String TOKEN = preferencesManager.getToken();
            Call<ServerResponse<ItemMess<ArrayList<Dialogs>>>> call = service.getHistory(TOKEN, 100, offset, user_id == 0 ? chat_id + 2000000000 : user_id);

            call.enqueue(new Callback<ServerResponse<ItemMess<ArrayList<Dialogs>>>>() {
                @Override
                public void onResponse(Call<ServerResponse<ItemMess<ArrayList<Dialogs>>>> call, Response<ServerResponse<ItemMess<ArrayList<Dialogs>>>> response) {
                    if (response.body().getResponse() != null) {
                        ArrayList<Dialogs> l = response.body().getResponse().getitem();
                        String people_id = "" + l.get(0).getUser_id();
                        namesIds.clear();
                        if (offset == 0) {
                            items.clear();
                            for (int i = 0; i < l.size(); i++) {
                                nameRec(l.get(i));
                                items.add(0, l.get(i));
                            }
                        } else {
                            for (int i = 0; i < l.size(); i++) {
                                nameRec(l.get(i));
                                items.add(0, l.get(i));
                            }
                        }
                        for (int i = 0; i < namesIds.size(); i++) {
                            people_id += "," + namesIds.get(i);
                        }
                        people_id += ", " + preferencesManager.getUserID();
                        Log.i("chek", people_id);
                        refreshLayout.setRefreshing(false);

                        String TOKEN = preferencesManager.getToken();
                        Call<ServerResponse<ArrayList<User>>> call1 = service.getUser(TOKEN, people_id, "photo_100,photo_200,photo_400_orig,photo_max_orig, online,city,country,education, universities, schools,bdate,contacts");

                        call1.enqueue(new Callback<ServerResponse<ArrayList<User>>>() {
                            @Override
                            public void onResponse(Call<ServerResponse<ArrayList<User>>> call1, Response<ServerResponse<ArrayList<User>>> response) {
                                ArrayList<User> l = response.body().getResponse();
                                if (l != null) {
                                    if (offset == 0) {
                                        names.clear();
                                    }
                                    for (int i = 0; i < l.size(); i++) {
                                        names.add(l.get(i));
                                    }
                                    refreshLayout.setRefreshing(false);
                                    recyclerView.scrollToPosition(items.size() - offset);
                                    adapter.fwd_mess.clear();
                                    adapter.reserv.clear();
                                    adapter.reserv.addAll(items);
                                    adapter.notifyDataSetChanged();
                                } else {
                                    refreshLayout.setRefreshing(false);
                                    GuiUtils.showRequestLimitErrorMessage(getActivity());
                                }
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
                        GuiUtils.showRequestLimitErrorMessage(getActivity());
                    }
                }

                @Override
                public void onFailure(Call<ServerResponse<ItemMess<ArrayList<Dialogs>>>> call, Throwable t) {
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

    public static String convertMonth(int num) {
        String[] months = {"", "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul",
                "Aug", "Sep", "Oct", "Nov", "Dec"};
        return months[num];
    }

    private class ViewHolder extends RecyclerView.ViewHolder {
        ImageView photo;
        ImageView online;
        AutoLinkTextView body;
        TextView time;
        RelativeLayout background;
        LinearLayout line;
        RelativeLayout foo;

        public ViewHolder(View itemView) {
            super(itemView);
            photo = (ImageView) itemView.findViewById(R.id.imageView);
            online = (ImageView) itemView.findViewById(R.id.imageView6);
            body = (AutoLinkTextView) itemView.findViewById(R.id.textView2);
            time = (TextView) itemView.findViewById(R.id.textView);
            background = (RelativeLayout) itemView.findViewById(R.id.relativeLayout);
            line = (LinearLayout) itemView.findViewById(R.id.line);
            foo = (RelativeLayout) itemView.findViewById(R.id.foo);
        }
    }

    private class Adapter extends RecyclerView.Adapter<ViewHolder> {
        SimpleDateFormat year = new SimpleDateFormat("yyyy");
        SimpleDateFormat month = new SimpleDateFormat("MM");
        SimpleDateFormat day = new SimpleDateFormat("dd");
        SimpleDateFormat hour = new SimpleDateFormat("HH");
        SimpleDateFormat min = new SimpleDateFormat("mm");
        SimpleDateFormat time = new SimpleDateFormat("HH:mm");
        ArrayList<ArrayList<Dialogs>> fwd_mess;
        ArrayList<Dialogs> reserv;
        ArrayList<Integer> pos;

        public Adapter() {
            fwd_mess = new ArrayList<>();
            reserv = new ArrayList<>();
            pos = new ArrayList<>();
        }

        @Override
        public int getItemViewType(int position) {
            return items.get(position).getOut();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == 0) {
                return new ViewHolder(View.inflate(getActivity(), R.layout.messin, null));
            } else {
                return new ViewHolder(View.inflate(getActivity(), R.layout.messout, null));
            }
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            final Dialogs dialog = items.get(position);
            final int finalPos = position;
            User user = new User();
            for (int i = 0; i < names.size(); i++) {
                if (dialog.getOut() == 0) {
                    if (dialog.getUser_id() == names.get(i).getId()) {
                        user = names.get(i);
                        break;
                    }
                } else {
                    if (dialog.getFrom_id() == names.get(i).getId()) {
                        user = names.get(i);
                        break;
                    }
                }
            }

            if (dialog.getRead_state() == 0) {
                holder.foo.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.accent));
            } else {
                holder.foo.setBackgroundColor(Color.WHITE);
            }
            for (int i = 0; i < frwdMessages.size(); i++) {
                if (dialog.getId() == frwdMessages.get(i)) {
                    holder.foo.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.primary_dark));
                }
            }
            holder.line.removeAllViews();
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
            final User userFinal = user;
            final ViewHolder viewHolder = holder;

            if (frwdMessages.size() == 0) forwardButton.setVisibility(View.INVISIBLE);
            View.OnClickListener forwardListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean chek = false;
                    for (int i = 0; i < frwdMessages.size(); i++) {
                        if (frwdMessages.get(i) == dialog.getId()) {
                            frwdMessages.remove(i);
                            if (dialog.getRead_state() == 1) {
                                viewHolder.foo.setBackgroundColor(Color.WHITE);
                            } else {
                                viewHolder.foo.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.accent));
                            }
                            chek = true;
                            break;
                        }
                    }

                    if (!chek) {
                        frwdMessages.add(dialog.getId());
                        viewHolder.foo.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.primary_dark));
                    }

                    if (frwdMessages.size() > 0) {
                        mess.setHint("Выбрано " + frwdMessages.size());
                    } else {
                        mess.setHint(getString(R.string.WRITE_MESSAGE));
                    }
                    if (frwdMessages.size() > 0) {
                        forwardButton.setVisibility(View.VISIBLE);
                        forwardButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                final AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                                LayoutInflater inflater = getActivity().getLayoutInflater();
                                View view = inflater.inflate(R.layout.layout_alert_dialog_forward_question, null);
                                view.findViewById(R.id.dialog_variant).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        alertDialog.dismiss();
                                        navigateTo().state(Add.newActivity(new DialogListState(new Gson().toJson(frwdMessages)), BaseActivity.class));
                                        frwdMessages.clear();
                                        adapter.notifyDataSetChanged();
                                        mess.setHint(getString(R.string.WRITE_MESSAGE));
                                    }
                                });
                                view.findViewById(R.id.friend_variant).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        alertDialog.dismiss();
                                        navigateTo().state(Add.newActivity(new FriendListState(new Gson().toJson(frwdMessages)), BaseActivity.class));
                                        frwdMessages.clear();
                                        adapter.notifyDataSetChanged();
                                        mess.setHint(getString(R.string.WRITE_MESSAGE));
                                    }
                                });
                                alertDialog.setView(view);
                                alertDialog.show();
                            }
                        });
                    } else {
                        forwardButton.setVisibility(View.INVISIBLE);
                    }
                }
            };
            holder.itemView.setOnClickListener(forwardListener);
            holder.body.setOnClickListener(forwardListener);

//            holder.photo.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    startActivity(UserActivity.getIntent(DialogMessageActivity.this,userFinal.getId(),new Gson().toJson(userFinal)));
//                }
//            });
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
            if (dialog.getDate() != 0) {
                if (chat_id != 0) {

                    if (year.format(dateTs).equals(year.format(dateCurr))) {
                        if ((day.format(dateTs).equals(day.format(dateCurr))) && (month.format(dateTs).equals(month.format(dateCurr)))) {
                            holder.time.setText(user.getFirst_name() + " " + user.getLast_name() + ", " + time_time);
                        } else {
                            holder.time.setText(user.getFirst_name() + " " + user.getLast_name() + ", " + time_day + " "
                                    + convertMonth(Integer.parseInt(month.format(dateTs))));
                        }
                    } else {
                        holder.time.setText(user.getFirst_name() + " " + user.getLast_name() + ", " + time_year);
                    }
                } else {

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
                }
            } else  {
                holder.time.setText("Отправляем...");
            }
            if (user.getOnline() == 0) {
                holder.online.setVisibility(View.INVISIBLE);
            } else {
                holder.online.setVisibility(View.VISIBLE);
            }
            holder.body.addAutoLinkMode(AutoLinkMode.MODE_URL);
            holder.body.setUrlModeColor(Color.rgb(0, 200, 250));
            holder.body.setAutoLinkOnClickListener(new AutoLinkOnClickListener() {
                @Override
                public void onAutoLinkTextClick(AutoLinkMode autoLinkMode, String matchedText) {
                    if (AutoLinkMode.MODE_URL.equals(autoLinkMode)) {
                        while (matchedText.contains(" ")) {
                            matchedText = matchedText.replace(" ", "");
                        }
                        while (matchedText.contains("\n")) {
                            matchedText = matchedText.replace("\n", "");
                        }
                        Util.goToUrl(getActivity(), matchedText);
                    }
                }
            });
            String bodyContainer = dialog.getBody();

            if (dialog.getFwd_messages().size() != 0) {
                LayoutInflater inflater = getActivity().getLayoutInflater();
                View cont = inflater.inflate(R.layout.attachment_conteiner_dinamic, null);
                TextView text = (TextView) cont.findViewById(R.id.textView3);
                text.setTextColor(Color.BLUE);
                text.setText(getString(R.string.FORWARD_MESSAGES));
                holder.line.addView(cont);
                text.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        navigateTo().state(Add.newActivity(new ForwardMessagesState(new Gson().toJson(dialog.getFwd_messages()), new Gson().toJson(names), chat_id == 0 ? user_id : 2000000000 + chat_id), BaseActivity.class));
                    }
                });
            }
            for (int i = 0; i < dialog.getAttachments().size(); i++) {
                switch (dialog.getAttachments().get(i).getType()) {
                    case "photo": {
                        if (preferencesManager.getSettingPhotoChatOn()) {
                            String photo = "";
                            String photomess = "";
                            if (dialog.getAttachments().get(i).getPhoto().getPhoto_1280() != null) {
                                photo = dialog.getAttachments().get(i).getPhoto().getPhoto_1280();
                                photomess = dialog.getAttachments().get(i).getPhoto().getPhoto_604();
                            } else {
                                if (dialog.getAttachments().get(i).getPhoto().getPhoto_807() != null) {
                                    photo = dialog.getAttachments().get(i).getPhoto().getPhoto_807();
                                    photomess = dialog.getAttachments().get(i).getPhoto().getPhoto_604();
                                } else {
                                    if (dialog.getAttachments().get(i).getPhoto().getPhoto_604() != null) {
                                        photomess = photo = dialog.getAttachments().get(i).getPhoto().getPhoto_604();
                                    } else {
                                        if (dialog.getAttachments().get(i).getPhoto().getPhoto_130() != null) {
                                            photomess = photo = dialog.getAttachments().get(i).getPhoto().getPhoto_130();
                                        } else {
                                            if (dialog.getAttachments().get(i).getPhoto().getPhoto_75() != null) {
                                                photomess = photo = dialog.getAttachments().get(i).getPhoto().getPhoto_75();
                                            }
                                        }
                                    }
                                }
                            }
                            LayoutInflater inflater = getActivity().getLayoutInflater();
                            View cont = inflater.inflate(R.layout.attachment_conteiner_dinamic, null);
                            ImageView photochka = (ImageView) cont.findViewById(R.id.imageView);
                            TextView text = (TextView) cont.findViewById(R.id.textView3);
                            text.setText(getString(R.string.PHOTO));
                            final String finalPhoto = photo;
                            Picasso.with(getActivity())
                                    .load(photomess)
                                    .placeholder(R.drawable.loadshort)
                                    .error(R.drawable.errorshort)
                                    .into(photochka);
                            holder.line.addView(cont);
                            photochka.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    new ImageViewer.Builder(getActivity(), new String[]{finalPhoto})
                                            .show();
                                }
                            });
                        } else {
                            bodyContainer += "\n" + getString(R.string.PHOTO);
                        }
                        break;
                    }
                    case "sticker": {
                        LayoutInflater inflater = getActivity().getLayoutInflater();
                        View cont = inflater.inflate(R.layout.attachment_conteiner_dinamic, null);
                        ImageView photochka = (ImageView) cont.findViewById(R.id.imageView);
                        TextView text = (TextView) cont.findViewById(R.id.textView3);
                        text.setText(R.string.STICKER);
                        Picasso.with(getActivity())
                                .load(dialog.getAttachments().get(i).getSticker().getPhoto_256())
                                .placeholder(R.drawable.loadshort)
                                .error(R.drawable.errorshort)
                                .resize(200, 200)
                                .centerCrop()
                                .into(photochka);
                        holder.line.addView(cont);
                        break;
                    }
                    case "link": {
                        bodyContainer += "\n" + dialog.getAttachments().get(i).getLink().getUrl();
                        break;
                    }
                    case "video": {
                        LayoutInflater inflater = getActivity().getLayoutInflater();
                        View cont = inflater.inflate(R.layout.attachment_conteiner_dinamic, null);
                        ImageView photochka = (ImageView) cont.findViewById(R.id.imageView);
                        TextView text = (TextView) cont.findViewById(R.id.textView3);
                        text.setText(dialog.getAttachments().get(i).getVideo().getTitle());
                        Picasso.with(getActivity())
                                .load(dialog.getAttachments().get(i).getVideo().getPhoto_320())
                                .placeholder(R.drawable.loadshort)
                                .error(R.drawable.errorshort)
                                .resize(400, 300)
                                .centerCrop()
                                .into(photochka);
                        holder.line.addView(cont);
                        final String video = dialog.getAttachments().get(i).getVideo().getOwner_id() + "_" + dialog.getAttachments().get(i).getVideo().getId() + "_" + dialog.getAttachments().get(i).getVideo().getAccess_key();
                        photochka.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Toast toast = Toast.makeText(getApplicationContext(),
                                        getString(R.string.LOADING), Toast.LENGTH_LONG);
                                toast.show();
                                String TOKEN = preferencesManager.getToken();
                                Call<ServerResponse<ItemMess<ArrayList<VideoInformation>>>> call = service.getVideos(TOKEN, video);

                                call.enqueue(new Callback<ServerResponse<ItemMess<ArrayList<VideoInformation>>>>() {
                                    @Override
                                    public void onResponse(Call<ServerResponse<ItemMess<ArrayList<VideoInformation>>>> call, Response<ServerResponse<ItemMess<ArrayList<VideoInformation>>>> response) {
                                        String res = response.body().getResponse().getitem().get(0).getPlayer();
                                        Uri address = Uri.parse(res);
                                        Intent openlink = new Intent(Intent.ACTION_VIEW, address);
                                        startActivity(openlink);
                                    }

                                    @Override
                                    public void onFailure(Call<ServerResponse<ItemMess<ArrayList<VideoInformation>>>> call, Throwable t) {
                                        Toast toast = Toast.makeText(getApplicationContext(),
                                                getString(R.string.LOST_INTERNET_CONNECTION), Toast.LENGTH_SHORT);
                                        toast.setGravity(Gravity.CENTER, 0, 0);
                                        toast.show();
                                    }
                                });
                            }
                        });
                        break;
                    }
                    case "doc": {
                        LayoutInflater inflater = getActivity().getLayoutInflater();
                        View cont = inflater.inflate(R.layout.attachment_conteiner_dinamic, null);
                        ImageView photochka = (ImageView) cont.findViewById(R.id.imageView);
                        TextView text = (TextView) cont.findViewById(R.id.textView3);
                        text.setText(dialog.getAttachments().get(i).getDoc().getTitle());
                        if (dialog.getAttachments().get(i).getDoc().getType() == 1) {
                            Picasso.with(getActivity())
                                    .load(R.drawable.doc)
                                    .resize(150, 150)
                                    .centerCrop()
                                    .into(photochka);
                        } else {
                            Picasso.with(getActivity())
                                    .load(R.drawable.zip)
                                    .resize(150, 150)
                                    .centerCrop()
                                    .into(photochka);
                        }
                        holder.line.addView(cont);
                        final Attachment att = dialog.getAttachments().get(i);
                        photochka.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String res = att.getDoc().getUrl();
                                Uri address = Uri.parse(res);
                                Intent openlink = new Intent(Intent.ACTION_VIEW, address);
                                startActivity(openlink);
                            }
                        });
                        break;
                    }
                    case "audio": {
                        LayoutInflater inflater = getActivity().getLayoutInflater();
                        View cont = inflater.inflate(R.layout.attachment_conteiner_audio_dinamic, null);
                        TextView text = (TextView) cont.findViewById(R.id.textView);
                        text.setText(dialog.getAttachments().get(i).getAudio().getArtist() + " - " + dialog.getAttachments().get(i).getAudio().getTitle());
                        Button button = (Button) cont.findViewById(R.id.button);
                        Button button1 = (Button) cont.findViewById(R.id.button1);
                        Button button2 = (Button) cont.findViewById(R.id.button2);
                        Button button3 = (Button) cont.findViewById(R.id.button3);
                        Button button4 = (Button) cont.findViewById(R.id.button4);
                        holder.line.addView(cont);
                        final String url = dialog.getAttachments().get(i).getAudio().getUrl();
                        button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (BaseActivity.mediaPlayer != null) {
                                    BaseActivity.mediaPlayer.seekTo(BaseActivity.mediaPlayer.getCurrentPosition() - 5000);
                                }
                            }
                        });
                        button1.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (BaseActivity.mediaPlayer != null) {
                                    if (BaseActivity.mediaPlayer.getCurrentPosition() == 0) {
                                        try {
                                            BaseActivity.mediaPlayer.release();
                                            BaseActivity.mediaPlayer = null;
                                            BaseActivity.mediaPlayer = new MediaPlayer();
                                            BaseActivity.mediaPlayer.setDataSource(url);
                                            BaseActivity.mediaPlayer.prepare();
                                            BaseActivity.mediaPlayer.start();
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    } else {
                                        BaseActivity.mediaPlayer.start();
                                    }
                                } else {
                                    try {
                                        BaseActivity.mediaPlayer = new MediaPlayer();
                                        BaseActivity.mediaPlayer.setDataSource(url);
                                        BaseActivity.mediaPlayer.prepare();
                                        BaseActivity.mediaPlayer.start();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }

                            }
                        });
                        button2.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (BaseActivity.mediaPlayer != null) {
                                    BaseActivity.mediaPlayer.pause();
                                }
                            }
                        });
                        button3.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (BaseActivity.mediaPlayer != null) {
                                    BaseActivity.mediaPlayer.seekTo(BaseActivity.mediaPlayer.getCurrentPosition() + 5000);
                                }
                            }
                        });
                        button4.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (BaseActivity.mediaPlayer != null) {
                                    BaseActivity.mediaPlayer.pause();
                                    BaseActivity.mediaPlayer.seekTo(0);
                                }
                            }
                        });
                        break;
                    }
                    case "wall": {
                        bodyContainer += "\n" + dialog.getAttachments().get(i).getType();
                        break;
                    }
                    case "gift": {
                        LayoutInflater inflater = getActivity().getLayoutInflater();
                        View cont = inflater.inflate(R.layout.attachment_conteiner_dinamic, null);
                        ImageView photochka = (ImageView) cont.findViewById(R.id.imageView);
                        TextView text = (TextView) cont.findViewById(R.id.textView3);
                        text.setText(R.string.GIFT);
                        Picasso.with(getActivity())
                                .load(dialog.getAttachments().get(i).getGift().getThumb_256())
                                .placeholder(R.drawable.loadshort)
                                .error(R.drawable.errorshort)
                                .resize(200, 200)
                                .centerCrop()
                                .into(photochka);
                        holder.line.addView(cont);
                        break;
                    }
                }

            }
            if (crypting) bodyContainer = CryptUtils.decryptWritibleString(bodyContainer, cryptKey);
            holder.body.setTextColor(getResources().getColor(crypting ? R.color.orange : R.color.black));
            holder.body.setAutoLinkText(bodyContainer);
            View.OnLongClickListener copyTextListener = new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("", holder.body.getText().toString());
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(getActivity(), "Текст скопирован в буфер", Toast.LENGTH_SHORT).show();
                    return true;
                }
            };
            holder.itemView.setOnLongClickListener(copyTextListener);
            holder.body.setOnLongClickListener(copyTextListener);
            if (dialog.getAction() != null) {
                if (dialog.getAction().equals("chat_kick_user"))
                    holder.body.setAutoLinkText(getString(R.string.left_chat));
            }
        }

        @Override
        public int getItemCount() {
            return items.size();
        }
    }

    class UpdateDataBase extends AsyncTask<Void, Void, Void> {
        ArrayList<Dialogs> items;
        ArrayList<User> names;
        int user_id;

        public UpdateDataBase(int id, ArrayList<Dialogs> itemArrayList, ArrayList<User> userArrayList) {
            items = new ArrayList<>();
            names = new ArrayList<>();
            items.addAll(itemArrayList);
            names.addAll(userArrayList);
            user_id = id;
        }

        @Override
        protected Void doInBackground(Void... params) {
            dataBase.beginTransaction();
            try {
                int howmuch = 0;
                howmuch = dataBase.delete(DBHelper.TABLE_MESSAGES, DBHelper.KEY_ID_DIALOG + " = " + user_id, null);
                Log.i("howMuch", howmuch + "");
                howmuch = dataBase.delete(DBHelper.TABLE_USERS_IN_MESSAGES, DBHelper.KEY_ID_DIALOG + " = " + user_id, null);
                Log.i("howMuch", howmuch + "");
                ContentValues contentValues = new ContentValues();
                Gson gson = new Gson();

                for (int i = 0; i < items.size(); i++) {
                    contentValues.put(DBHelper.KEY_ID_DIALOG, user_id);
                    contentValues.put(DBHelper.KEY_TIME_MESSAGES, items.get(i).getDate());
                    contentValues.put(DBHelper.KEY_OBJ, gson.toJson(items.get(i)));
                    dataBase.insert(DBHelper.TABLE_MESSAGES, null, contentValues);
                }
                ContentValues contentValues1 = new ContentValues();
                for (int i = 0; i < names.size(); i++) {
                    contentValues1.put(DBHelper.KEY_ID_DIALOG, user_id);
                    contentValues1.put(DBHelper.KEY_OBJ, gson.toJson(names.get(i)));
                    long num = 0;
                    num = dataBase.insert(DBHelper.TABLE_USERS_IN_MESSAGES, null, contentValues1);
                    Log.i("namesChat", "" + names.get(i).getFirst_name() + " " + num);
                }
                dataBase.setTransactionSuccessful();
            } catch (Exception e) {

            } finally {
                dataBase.endTransaction();
            }
            return null;
        }
    }
}
