package com.squeezymo.boloid.ui.activities;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.astuetz.PagerSlidingTabStrip;
import com.squeezymo.boloid.R;
import com.squeezymo.boloid.model.TaskItem;
import com.squeezymo.boloid.services.TaskService;
import com.squeezymo.boloid.ui.adapters.TasksPagerAdapter;
import com.squeezymo.boloid.ui.fragments.TasksListFragment;
import com.squeezymo.boloid.ui.fragments.TasksMapFragment;

import java.util.List;

public class MainActivity extends AppCompatActivity implements ServiceConnection, ListToMapCallback {
    private static final String LOG_TAG = MainActivity.class.getCanonicalName();

    private TaskService mTaskService;
    private BroadcastReceiver mReceiver;
    private MenuItem mRefreshMenuItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* setting tabs */
        PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        ViewPager pager = (ViewPager) findViewById(R.id.pager);

        pager.setAdapter(new TasksPagerAdapter(this));
        tabs.setViewPager(pager);

        /* initializing receiver */
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(TaskService.ACTION_TASKS_UPD)) {
                    setInProgress(false);

                    switch (intent.getIntExtra(TaskService.EXTRA_RESULT, -1)) {
                        case TaskService.V_SUCCESS: {
                            setTaskItems(mTaskService.getTasks());

                            Snackbar.make(
                                    MainActivity.this.findViewById(android.R.id.content),
                                    mTaskService.getTasks().size() == 0 ?
                                            MainActivity.this.getResources().getString(R.string.info_no_tasks_retrieved) :
                                            MainActivity.this.getResources().getQuantityString(
                                                    R.plurals.info_tasks_retrieved,
                                                    mTaskService.getTasks().size(),
                                                    mTaskService.getTasks().size()
                                            ),
                                    Snackbar.LENGTH_LONG
                            ).show();

                            break;
                        }
                        case TaskService.V_NO_CONNECTION: {
                            Snackbar.make(
                                    MainActivity.this.findViewById(android.R.id.content),
                                    MainActivity.this.getResources().getString(R.string.no_connection),
                                    Snackbar.LENGTH_LONG
                            ).show();

                            break;
                        }
                        case TaskService.V_ERR: {
                            Snackbar.make(
                                    MainActivity.this.findViewById(android.R.id.content),
                                    MainActivity.this.getResources().getString(R.string.err_unknown),
                                    Snackbar.LENGTH_LONG
                            ).show();

                            break;
                        }
                        default: {
                            throw new IllegalArgumentException("Illegal result status: " + intent.getIntExtra(TaskService.EXTRA_RESULT, -1));
                        }
                    }
                }
            }
        };

        /* binding service */
        Intent bindIntent = new Intent(this, TaskService.class);
        bindService(bindIntent, this, BIND_AUTO_CREATE);

        /* registering receiver */
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(TaskService.ACTION_TASKS_UPD);

        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, intentFilter);
    }

    @Override
    protected void onDestroy() {
        /* unbinding service */
        if (mTaskService != null) {
            unbindService(this);
        }

        /* unregistering receiver */
        if (mReceiver != null) {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver);
        }

        super.onDestroy();
    }

    private void setTaskItems(List<TaskItem> tasks) {
        TasksMapFragment mapFragment = (TasksMapFragment) getFragmentManager().findFragmentById(R.id.page_map);
        TasksListFragment listFragment = (TasksListFragment) getFragmentManager().findFragmentById(R.id.page_list);

        if (mapFragment != null) {
            mapFragment.setTaskItems(tasks);
        }

        if (listFragment != null) {
            listFragment.setTaskItems(tasks);
        }
    }

    private void setInProgress(boolean inProgress) {
        if (mRefreshMenuItem != null) {
            mRefreshMenuItem.setVisible(!inProgress);
        }

        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressbar);
        if (progressBar != null) {
            progressBar.setVisibility(inProgress ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    public void locate(TaskItem task) {
        ViewPager pager = (ViewPager) findViewById(R.id.pager);
        TasksMapFragment mapFragment = (TasksMapFragment) getFragmentManager().findFragmentById(R.id.page_map);

        if (pager != null && mapFragment != null) {
            pager.setCurrentItem(0);
            mapFragment.locate(task);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        mRefreshMenuItem = menu.findItem(R.id.item_refresh);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch ( item.getItemId() ) {
            case R.id.item_refresh: {
                if (mTaskService != null) {
                    setInProgress(true);
                    mTaskService.requestAsyncUpdate();
                }
                else {
                    throw new IllegalStateException("Service not bound");
                }

                break;
            }
        }

        return true;
    }

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        mTaskService = ((TaskService.LocalBinder) iBinder).getService();
        setTaskItems(mTaskService.getTasks());
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        mTaskService = null;
    }
}
