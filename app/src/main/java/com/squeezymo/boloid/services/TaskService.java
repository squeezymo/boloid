package com.squeezymo.boloid.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.squeezymo.boloid.model.TaskItem;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ru.yandex.yandexmapkit.utils.GeoPoint;

public class TaskService extends Service {
    private static final String LOG_TAG = TaskService.class.getCanonicalName();
    private static final String URL_TASKS = "http://test.boloid.com:9000/tasks";

    public static final String ACTION_TASKS_UPD = TaskService.class.getSimpleName() + "." + "TASKS_UPD";
    public static final String EXTRA_RESULT = TaskService.class.getSimpleName() + "." + "RESULT";
    public static final int V_SUCCESS = 0x10;
    public static final int V_NO_CONNECTION = 0x11;
    public static final int V_ERR = 0x12;

    private static List<TaskItem> sTasks;

    private LocalBinder mLocalBinder;
    private JsonParser mJsonParser;
    private Gson mGson;
    private ExecutorService mThreadPool;

    public class LocalBinder extends Binder {
        public TaskService getService() {
            return TaskService.this;
        }
    }

    public TaskService() {
        mLocalBinder = new LocalBinder();
        mJsonParser = new JsonParser();
        mGson = new GsonBuilder()
                .registerTypeAdapter(GeoPoint.class, new JsonDeserializer<GeoPoint>() {
                    @Override
                    public GeoPoint deserialize(JsonElement json, Type type, JsonDeserializationContext context) {
                        JsonObject jsonObj = json.getAsJsonObject();
                        return new GeoPoint(jsonObj.get("lat").getAsDouble(), jsonObj.get("lon").getAsDouble());
                    }
                })
                .create();
        mThreadPool = Executors.newCachedThreadPool();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mLocalBinder;
    }

    public void requestAsyncUpdate() {
        Runnable fetcher = new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(ACTION_TASKS_UPD);
                HttpURLConnection urlConnection = null;

                try {
                    ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

                    if ( connManager.getActiveNetworkInfo() == null ||
                            !connManager.getActiveNetworkInfo().isAvailable() ||
                            !connManager.getActiveNetworkInfo().isConnected() ) {

                        intent.putExtra(EXTRA_RESULT, V_NO_CONNECTION);
                        LocalBroadcastManager.getInstance(TaskService.this).sendBroadcast(intent);

                        return;
                    }

                    URL url = new URL(URL_TASKS);
                    urlConnection = (HttpURLConnection) url.openConnection();

                    InputStream stream = urlConnection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
                    StringBuilder jsonBuilder = new StringBuilder(stream.available());

                    for (String line; (line = reader.readLine()) != null; jsonBuilder.append(line)) ;

                    sTasks = mGson.fromJson(
                            mJsonParser.parse(jsonBuilder.toString()).getAsJsonObject().getAsJsonArray("tasks"),
                            new TypeToken<List<TaskItem>>(){}.getType()
                    );

                    intent.putExtra(EXTRA_RESULT, V_SUCCESS);
                    LocalBroadcastManager.getInstance(TaskService.this).sendBroadcast(intent);
                }
                catch (Exception e) {
                    Log.e(LOG_TAG, Log.getStackTraceString(e));

                    intent.putExtra(EXTRA_RESULT, V_ERR);
                    LocalBroadcastManager.getInstance(TaskService.this).sendBroadcast(intent);
                }
                finally {
                    if (urlConnection != null) {
                        urlConnection.disconnect();
                    }
                }
            }
        };

        mThreadPool.execute(fetcher);
    }

    public List<TaskItem> getTasks() {
        return sTasks == null ? new ArrayList<TaskItem>() : sTasks;
    }
}
