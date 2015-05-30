package com.squeezymo.boloid.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squeezymo.boloid.R;
import com.squeezymo.boloid.model.TaskItem;
import com.squeezymo.boloid.ui.activities.ListToMapCallback;

import org.w3c.dom.Text;

import java.util.List;

public class TasksListAdapter extends RecyclerView.Adapter<TasksListAdapter.TaskViewHolder> {
    private static final String LOG_TAG = TasksListAdapter.class.getCanonicalName();

    private Context mContext;
    private List<TaskItem> mTasks;

    public TasksListAdapter(Context context) {
        if (context == null)
            throw new IllegalArgumentException("Context must not be null");

        if (!(context instanceof ListToMapCallback))
            throw new ClassCastException("Context must implement " + ListToMapCallback.class.getCanonicalName());

        mContext = context;
    }

    public class TaskViewHolder
            extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        private TaskItem task;
        private TextView title;
        private TextView text;
        private TextView pricesCnt;

        public TaskViewHolder(View v) {
            super(v);

            title = (TextView) v.findViewById(R.id.title);
            text = (TextView) v.findViewById(R.id.text);
            pricesCnt = (TextView) v.findViewById(R.id.prices_cnt);

            v.setOnClickListener(this);
        }

        public void bindItem(TaskItem task) {
            this.task = task;

            title.setText(task.getTitle());
            text.setText(task.getText());
            if (task.getPrices().size() == 0) {
                pricesCnt.setText(mContext.getResources().getString(R.string.no_prices));
            }
            else {
                pricesCnt.setText(mContext.getResources().getQuantityString(
                                R.plurals.prices_count,
                                task.getPrices().size(),
                                task.getPrices().size())
                );
            }
        }

        @Override
        public void onClick(View view) {
            Log.d(LOG_TAG, "Item clicked:\n" + task);
            ((ListToMapCallback) mContext).locate(task);
        }
    }

    public void setTaskItems(List<TaskItem> tasks) {
        mTasks = tasks;
        notifyDataSetChanged();
    }

    @Override
    public TaskViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_task, parent, false);
        return new TaskViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(TaskViewHolder holder, int position) {
        holder.bindItem(mTasks.get(position));
    }

    @Override
    public int getItemCount() {
        return mTasks == null ? 0 : mTasks.size();
    }
}
