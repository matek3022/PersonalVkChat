package company.matek3022.personalvkchat.fragments;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import company.matek3022.personalvkchat.R;
import company.matek3022.personalvkchat.activitys.BaseActivity;
import company.matek3022.personalvkchat.fragments.states.DialogState;
import company.matek3022.personalvkchat.managers.PreferencesManager;
import company.matek3022.personalvkchat.sqlite.DBHelper;
import company.matek3022.personalvkchat.transformation.CircularTransformation;
import company.matek3022.personalvkchat.vkobjects.ItemMess;
import company.matek3022.personalvkchat.vkobjects.ServerResponse;
import company.matek3022.personalvkchat.vkobjects.User;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import me.ilich.juggler.change.Add;
import me.ilich.juggler.change.Remove;
import me.ilich.juggler.gui.JugglerFragment;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static company.matek3022.personalvkchat.App.service;
import static com.vk.sdk.VKUIHelper.getApplicationContext;

/**
 * Created by matek on 08.07.2017.
 */

public class FriendListFragment extends JugglerFragment {
    static final String ARGUMENT_FORWARD_MESS = "arg_forward_mess";

    public static FriendListFragment getInstance(String forwardMess) {

        FriendListFragment pageFragment = new FriendListFragment();
        Bundle arguments = new Bundle();
        arguments.putString(ARGUMENT_FORWARD_MESS, forwardMess);
        pageFragment.setArguments(arguments);
        return pageFragment;
    }

    private String forwardMess;
    private RecyclerView recyclerView;
    private Adapter adapter;
    SwipeRefreshLayout refreshLayout;
    private ArrayList<User> info;
    private ArrayList<User> usersFinal;
    SQLiteDatabase dataBase;
    PreferencesManager preferencesManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_friend, null);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        dataBase = DBHelper.getInstance().getWritableDatabase();
        preferencesManager = PreferencesManager.getInstance();
        info = new ArrayList<>();
        forwardMess = getArguments().getString(ARGUMENT_FORWARD_MESS);
        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh);

        recyclerView = (RecyclerView) view.findViewById(R.id.list);
        adapter = new Adapter();
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);
        recyclerView.setAdapter(adapter);
        EditText editText = (EditText) view.findViewById(R.id.filter);
        usersFinal = new ArrayList<>();
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().equalsIgnoreCase("")) {
                    info.clear();
                    for (int i = 0; i < usersFinal.size(); i++) {
                        String name = usersFinal.get(i).getFirst_name() + " " + usersFinal.get(i).getLast_name();
                        if (name.toLowerCase().contains(s) || name.contains(s) || s.toString().equalsIgnoreCase(usersFinal.get(i).getFirst_name()) || s.toString().equalsIgnoreCase(usersFinal.get(i).getLast_name()) || s.toString().equalsIgnoreCase(name)) {
                            info.add(usersFinal.get(i));
                        }
                    }
                } else {
                    info.clear();
                    info.addAll(usersFinal);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });
        Cursor cursor = dataBase.query(DBHelper.TABLE_FRIENDS, null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            info.clear();
            Gson gson = new Gson();
            int user = cursor.getColumnIndex(DBHelper.KEY_OBJ);
            for (int i = 0; i < cursor.getCount(); i++) {
                info.add(gson.fromJson(cursor.getString(user), User.class));
                cursor.moveToNext();
            }
            usersFinal.clear();
            usersFinal.addAll(info);
            refresh();
        } else {
            refresh();
        }
        cursor.close();
    }

    @Override
    public void onPause() {
        super.onPause();
        new UpdateDataBase(1, info).executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);

    }

    private void refresh() {
        refreshLayout.setRefreshing(true);
        String TOKEN = preferencesManager.getToken();
        Call<ServerResponse<ItemMess<ArrayList<User>>>> call = service.getFriends(TOKEN, "photo_100,photo_200,photo_400_orig,photo_max_orig, online,city,country,education, universities, schools,bdate,contacts");

        call.enqueue(new Callback<ServerResponse<ItemMess<ArrayList<User>>>>() {
            @Override
            public void onResponse(Call<ServerResponse<ItemMess<ArrayList<User>>>> call, Response<ServerResponse<ItemMess<ArrayList<User>>>> response) {
                ArrayList<User> l = response.body().getResponse().getitem();
                info.clear();
                info.addAll(l);
                usersFinal.clear();
                usersFinal.addAll(l);
                adapter.notifyDataSetChanged();
                Log.wtf("getCount", "" + info.size());
                refreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<ServerResponse<ItemMess<ArrayList<User>>>> call, Throwable t) {
                refreshLayout.setRefreshing(false);
                Toast toast = Toast.makeText(getApplicationContext(),
                        getString(R.string.LOST_INTERNET_CONNECTION), Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
        });
    }

    class UpdateDataBase extends AsyncTask<Void, Void, Void> {
        int user_id;
        ArrayList<User> userUpdate;

        public UpdateDataBase(int userId, ArrayList<User> userArrayList) {
            userUpdate = new ArrayList<>();
            userUpdate.addAll(userArrayList);
            user_id = userId;
        }

        @Override
        protected Void doInBackground(Void... params) {
            dataBase.beginTransaction();
            try {
                dataBase.delete(DBHelper.TABLE_FRIENDS, null, null);
                ContentValues contentValues = new ContentValues();
                Gson gson = new Gson();

                for (int i = 0; i < userUpdate.size(); i++) {
                    contentValues.put(DBHelper.KEY_ID_USER, userUpdate.get(i).getId());
                    contentValues.put(DBHelper.KEY_OBJ, gson.toJson(userUpdate.get(i)));
                    dataBase.insert(DBHelper.TABLE_FRIENDS, null, contentValues);
                }
                dataBase.setTransactionSuccessful();
            } catch (Exception e) {

            } finally {
                dataBase.endTransaction();
            }
            return null;
        }
    }

    private class ViewHolder extends RecyclerView.ViewHolder {
        ImageView photo;
        ImageView online;
        TextView name;
        TextView someInformation;

        public ViewHolder(View itemView) {
            super(itemView);
            photo = (ImageView) itemView.findViewById(R.id.image);
            online = (ImageView) itemView.findViewById(R.id.image2);
            name = (TextView) itemView.findViewById(R.id.text);
            someInformation = (TextView) itemView.findViewById(R.id.text2);

        }
    }

    private class Adapter extends RecyclerView.Adapter<ViewHolder> {

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(View.inflate(getContext(), R.layout.item_friend_list, null));
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            final User user = info.get(position);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    startActivity(UserActivity.getIntent(getContext(),user.getId(),new Gson().toJson(user)));
                    navigateTo().state(Remove.closeCurrentActivity());
                    navigateTo().state(Add.newActivity(new DialogState(user.getFirst_name() + " " + user.getLast_name(), user.getId(), 0, forwardMess), BaseActivity.class));
                }
            });

            holder.name.setText(user.getFirst_name() + " " + user.getLast_name());
            if (user.getCity() != (null)) {
                holder.someInformation.setText(user.getCity().getTitle());
            } else {
                holder.someInformation.setText("");
            }
            if (user.getPhoto_200().equals("")) {
                Picasso.with(getContext())
                        .load(R.drawable.soviet200)
                        .transform(new CircularTransformation())
                        .into(holder.photo);
            } else {
                Log.i("photo", user.getPhoto_200());
                Picasso.with(getContext())
                        .load(user.getPhoto_200())
                        .transform(new CircularTransformation())
                        .into(holder.photo);
            }
            if (user.getOnline() == 1) {
                holder.online.setVisibility(View.VISIBLE);
            } else {
                holder.online.setVisibility(View.INVISIBLE);
            }
        }

        @Override
        public int getItemCount() {
            return info.size();
        }
    }
}
