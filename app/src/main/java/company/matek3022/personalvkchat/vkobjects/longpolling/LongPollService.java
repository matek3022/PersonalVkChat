package company.matek3022.personalvkchat.vkobjects.longpolling;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.util.ArrayList;

import company.matek3022.personalvkchat.R;
import company.matek3022.personalvkchat.activitys.BaseActivity;
import company.matek3022.personalvkchat.managers.PreferencesManager;
import company.matek3022.personalvkchat.utils.NetworkUtils;
import company.matek3022.personalvkchat.vkobjects.ServerResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static company.matek3022.personalvkchat.App.service;

/**
 * Created by matek on 20.07.2017.
 */

public class LongPollService extends Service {
    private static final long INTERNET_DELAY = 5000L;
    public static boolean showNotif = true;

    private PreferencesManager preferencesManager;
    private String token;
    private GetLpSrvr getLpSrvr;

    private boolean isRunning = false;
    private Handler handler = new Handler();
    private LocalBroadcastManager localBroadcastManager;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        if (!isRunning) {
            isRunning = true;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    initPrefs();
                    updateLongPoll();
                }
            }).start();
        }
        return START_STICKY;
    }

    private void showNotification(LongPollEvent event) {
        if (showNotif) {
            PreferencesManager preferencesManager = PreferencesManager.getInstance();
            if (preferencesManager.getUseNotification()) {
                int profileId = preferencesManager.getUserID();
                int user_id = preferencesManager.getChatUserId();
                String title = preferencesManager.getTitle();
                boolean useLed = preferencesManager.getUseNotificationLed();
                boolean useVibration = preferencesManager.getUseNotificationVibration();
                boolean useSound = preferencesManager.getUseNotificationSound();

                int value = event.flags;
                int read = 1;
                int out = 0;
                if (value >= 65536) {
                    value -= 65536;
                }
                if (value >= 512) {
                    value -= 512;
                }
                if (value >= 256) {
                    value -= 256;
                }
                if (value >= 128) {
                    value -= 128;
                }
                if (value >= 64) {
                    value -= 64;
                }
                if (value >= 32) {
                    value -= 32;
                    out = 0;
                }
                if (value >= 16) {
                    value -= 16;
                    out = 0;
                }
                if (value >= 8) {
                    value -= 8;
                }
                if (value >= 4) {
                    value -= 4;
                }
                if (value >= 2) {
                    value -= 2;
                    out = 1;
                }
                if (value >= 1) {
                    value -= 1;
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
                        if (out == 0) {
                            Intent intent = new Intent(this, BaseActivity.class);
                            PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                            NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.launcher))
                                    .setSmallIcon(R.drawable.launcher)
                                    .setContentTitle(title)
                                    .setContentText(event.message)
                                    .setContentIntent(pIntent);
                            NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
                            Notification notification = builder.build();
                            if (useLed && useVibration && useSound) {
                                notification.defaults = Notification.DEFAULT_ALL;
                            } else if (useLed && useVibration) {
                                notification.defaults = Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE;
                            } else if (useLed && useSound) {
                                notification.defaults = Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND;
                            } else if (useVibration && useSound) {
                                notification.defaults = Notification.DEFAULT_VIBRATE | Notification.DEFAULT_SOUND;
                            } else if (useLed) {
                                notification.defaults = Notification.DEFAULT_LIGHTS;
                            } else if (useSound) {
                                notification.defaults = Notification.DEFAULT_SOUND;
                            } else if (useVibration) {
                                notification.defaults = Notification.DEFAULT_VIBRATE;
                            }
                            notificationManager.notify(1337, notification);
                        }
                    }
                }
            }
        }
    }

    private void initPrefs() {
        preferencesManager = PreferencesManager.getInstance();
        token = preferencesManager.getToken();
    }

    private void startRequest() {
        service.connect("https://" + getLpSrvr.getServer(), getLpSrvr.getKey(), getLpSrvr.getTs(),"a_check", 25, 128).enqueue(new Callback<LongPollResponse>() {
            @Override
            public void onResponse(Call<LongPollResponse> call, Response<LongPollResponse> response) {
                Log.i("ResponseLongPoll", response.toString());
                ArrayList<LongPollEvent> longPollEvents = response.body().updates;
                sendBroadcasts(longPollEvents);
                getLpSrvr.setTs(response.body().getTs());
                startRequest();
            }

            @Override
            public void onFailure(Call<LongPollResponse> call, Throwable t) {
                if (!NetworkUtils.hasInternetConnection(getBaseContext())) {
//                    Toast.makeText(getApplicationContext(), "Произошла ошибка! Мы уже перезапускаем сервис", Toast.LENGTH_SHORT).show();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            restartService();
                        }
                    }, INTERNET_DELAY);
                } else {
                    updateLongPoll();
                }
//                if (t.getMessage().contains("Unable")) {
////                    Toast.makeText(getApplicationContext(), "Произошла ошибка! Мы уже перезапускаем сервис", Toast.LENGTH_SHORT).show();
//                    handler.postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            restartService();
//                        }
//                    }, INTERNET_DELAY);
//                } else {
//                    updateLongPoll();
//                }
            }
        });
    }

    private void updateLongPoll() {
        service.getLongPollServer(token).enqueue(new Callback<ServerResponse<GetLpSrvr>>() {
            @Override
            public void onResponse(Call<ServerResponse<GetLpSrvr>> call, Response<ServerResponse<GetLpSrvr>> response) {
                getLpSrvr = response.body().getResponse();
                startRequest();
            }

            @Override
            public void onFailure(Call<ServerResponse<GetLpSrvr>> call, Throwable t) {
                if (!NetworkUtils.hasInternetConnection(getBaseContext())) {
//                    Toast.makeText(getApplicationContext(), "Произошла ошибка! Мы уже перезапускаем сервис", Toast.LENGTH_SHORT).show();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            restartService();
                        }
                    }, INTERNET_DELAY);
                } else {
                    updateLongPoll();
                }
//                if (t.getMessage().contains("Unable")) {
////                    Toast.makeText(getApplicationContext(), "Произошла ошибка! Мы уже перезапускаем сервис", Toast.LENGTH_SHORT).show();
//                    handler.postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            restartService();
//                        }
//                    }, INTERNET_DELAY);
//                } else {
//                    updateLongPoll();
//                }
            }
        });
    }

    private void restartService() {
        isRunning = false;
        startService(new Intent(this, LongPollService.class));
    }

    private void sendBroadcasts(final ArrayList<LongPollEvent> longPollEvents) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                for (int i = 0; i < longPollEvents.size(); i++) {
                    longPollEvents.get(i).init();
                    Intent intent = null;
                    switch (longPollEvents.get(i).type){
                        case LongPollEvent.NEW_MESSAGE_EVENT:
                            showNotification(longPollEvents.get(i));
                            intent = new Intent(LongPollEvent.NEW_MESSAGE_INTENT);
//                            showNotification(longPollEvents.get(i).message);
                            break;
                        case LongPollEvent.READ_IN_EVENT:
                            intent = new Intent(LongPollEvent.READ_IN_INTENT);
                            break;
                        case LongPollEvent.READ_OUT_EVENT:
                            intent = new Intent(LongPollEvent.READ_OUT_INTENT);
                            break;
//                        case LongPollEvent.ONLINE_EVENT:
//                            intent = new Intent(LongPollEvent.ONLINE_INTENT);
//                            break;
//                        case LongPollEvent.OFFLINE_EVENT:
//                            intent = new Intent(LongPollEvent.OFFLINE_INTENT);
//                            break;
                        case LongPollEvent.TYPING_IN_USER_EVENT:
                            intent = new Intent(LongPollEvent.TYPING_IN_USER_INTENT);
                            break;
                        case LongPollEvent.TYPING_IN_CHAT_EVENT:
                            intent = new Intent(LongPollEvent.TYPING_IN_CHAT_INTENT);
                            break;
//                        case LongPollEvent.NEW_COUNT_EVENT:
//                            intent = new Intent(LongPollEvent.NEW_COUNT_INTENT);
//                            break;
                    }
                    if (intent != null) {
                        intent.putExtra(LongPollEvent.INTENT_EXTRA_SERIALIZABLE, longPollEvents.get(i));
                        localBroadcastManager.sendBroadcast(intent);
                    }
                }
                return null;
            }
        }.execute();
    }
}
