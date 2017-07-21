package company.matek3022.personalvkchat.fragments;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import company.matek3022.personalvkchat.R;
import company.matek3022.personalvkchat.activitys.BaseActivity;
import company.matek3022.personalvkchat.fragments.states.ForwardMessagesState;
import company.matek3022.personalvkchat.managers.PreferencesManager;
import company.matek3022.personalvkchat.transformation.CircularTransformation;
import company.matek3022.personalvkchat.utils.CryptUtils;
import company.matek3022.personalvkchat.utils.Util;
import company.matek3022.personalvkchat.vkobjects.Attachment;
import company.matek3022.personalvkchat.vkobjects.Dialogs;
import company.matek3022.personalvkchat.vkobjects.ItemMess;
import company.matek3022.personalvkchat.vkobjects.ServerResponse;
import company.matek3022.personalvkchat.vkobjects.User;
import company.matek3022.personalvkchat.vkobjects.VideoInformation;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.luseen.autolinklibrary.AutoLinkMode;
import com.luseen.autolinklibrary.AutoLinkOnClickListener;
import com.luseen.autolinklibrary.AutoLinkTextView;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.squareup.picasso.Picasso;
import com.stfalcon.frescoimageviewer.ImageViewer;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

import me.ilich.juggler.change.Add;
import me.ilich.juggler.gui.JugglerFragment;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static company.matek3022.personalvkchat.App.service;
import static com.vk.sdk.VKUIHelper.getApplicationContext;

/**
 * Created by matek on 08.07.2017.
 */

public class ForwardMessagesFragment extends JugglerFragment {

    private static final String EXTRA_DIALOGS_LIST = "extra_dialogs_list";
    private static final String EXTRA_USERS_LIST = "extra_users_list";
    private static final String EXTRA_CHAT_ID = "extra_chat_id";

    public static ForwardMessagesFragment getInstance(String dialogs, String users, int chatId) {
        ForwardMessagesFragment fragment = new ForwardMessagesFragment();
        Bundle args = new Bundle();
        args.putString(EXTRA_DIALOGS_LIST, dialogs);
        args.putString(EXTRA_USERS_LIST, users);
        args.putInt(EXTRA_CHAT_ID, chatId);
        fragment.setArguments(args);
        return fragment;
    }

    private ArrayList<Dialogs> dialogs;
    private ArrayList<User> users;
    private PreferencesManager preferencesManager;
    private RecyclerView recyclerView;
    private SwipyRefreshLayout refreshLayout;

    private int chatId;
    private boolean crypting;
    private String cryptKey;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dialog, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        preferencesManager = PreferencesManager.getInstance();
        chatId = getArguments().getInt(EXTRA_CHAT_ID);
        crypting = preferencesManager.getIsCryptById(chatId);
        cryptKey = preferencesManager.getCryptKeyById(chatId);
        view.findViewById(R.id.fab).setVisibility(View.GONE);
        view.findViewById(R.id.inputContainer).setVisibility(View.GONE);
        recyclerView = (RecyclerView) view.findViewById(R.id.list);
        refreshLayout = (SwipyRefreshLayout) view.findViewById(R.id.refresh);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        llm.setStackFromEnd(true);
        refreshLayout.setEnabled(false);
        recyclerView.setLayoutManager(llm);
        recyclerView.setAdapter(new Adapter());
        dialogs = new Gson().fromJson(getArguments().getString(EXTRA_DIALOGS_LIST), new TypeToken<ArrayList<Dialogs>>(){}.getType());
        users = new Gson().fromJson(getArguments().getString(EXTRA_USERS_LIST), new TypeToken<ArrayList<User>>(){}.getType());
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

        public Adapter() {

        }

        @Override
        public int getItemViewType(int position) {
            return dialogs.get(position).getOut();
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
            final Dialogs dialog = dialogs.get(position);
            final int finalPos = position;
            User user = new User();
            for (int i = 0; i < users.size(); i++) {
                if (dialog.getOut() == 0) {
                    if (dialog.getUser_id() == users.get(i).getId()) {
                        user = users.get(i);
                        break;
                    }
                } else {
                    if (dialog.getFrom_id() == users.get(i).getId()) {
                        user = users.get(i);
                        break;
                    }
                }
            }

            if (dialog.getRead_state() == 0) {
                holder.foo.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.accent));
            } else {
                holder.foo.setBackgroundColor(Color.WHITE);
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
                    holder.time.setText(user.getFirst_name() + " " + user.getLast_name() + ", " + time_time);
                } else {
                    holder.time.setText(user.getFirst_name() + " " + user.getLast_name() + ", " + time_day + " "
                            + convertMonth(Integer.parseInt(month.format(dateTs))));
                }
            } else {
                holder.time.setText(user.getFirst_name() + " " + user.getLast_name() + ", " + time_year);
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
//                        frwd=true;
//                        ArrayList<Dialogs> fwrd = new ArrayList<>();
//                        for (int i = 0; i < dialog.getFwd_messages().size(); i++)
//                            fwrd.add(dialog.getFwd_messages().get(i));
//                        fwd_mess.add(fwrd);
//                        pos.add(finalPos);
//                        items = fwd_mess.get(fwd_mess.size() - 1);
//                        adapter.notifyDataSetChanged();
                        navigateTo().state(Add.newActivity(new ForwardMessagesState(new Gson().toJson(dialog.getFwd_messages()), new Gson().toJson(users), chatId), BaseActivity.class));
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
            return dialogs.size();
        }
    }
}
