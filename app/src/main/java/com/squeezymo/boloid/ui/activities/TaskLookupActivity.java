package com.squeezymo.boloid.ui.activities;

import android.app.Activity;
import android.app.FragmentManager;
import android.os.Bundle;
import android.view.Window;

import com.squeezymo.boloid.R;
import com.squeezymo.boloid.ui.fragments.TaskLookupFragment;

public class TaskLookupActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lookup);

        TaskLookupFragment lookupFragment = (TaskLookupFragment) getFragmentManager().findFragmentById(R.id.task_container);

        if (lookupFragment == null) {
            Bundle bundle = new Bundle();
            bundle.putParcelable(TaskLookupFragment.EXTRA_TASK, getIntent().getParcelableExtra(TaskLookupFragment.EXTRA_TASK));

            lookupFragment = TaskLookupFragment.instantiate(bundle);
            getFragmentManager().beginTransaction().replace(R.id.task_container, lookupFragment).commit();
        }
    }

}
