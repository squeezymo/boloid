package com.squeezymo.boloid.ui.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squeezymo.boloid.R;
import com.squeezymo.boloid.model.TaskItem;
import com.squeezymo.boloid.ui.adapters.TasksListAdapter;

import java.util.List;

public class TasksListFragment extends Fragment {

    private RecyclerView mRecyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);

        mRecyclerView = new RecyclerView(getActivity());
        mRecyclerView = (RecyclerView) view.findViewById(R.id.list_view);
        mRecyclerView.setHasFixedSize(true);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(llm);
        mRecyclerView.setAdapter(new TasksListAdapter(getActivity()));
    }

    public void setTaskItems(List<TaskItem> tasks) {
        ((TasksListAdapter) mRecyclerView.getAdapter()).setTaskItems(tasks);
    }

}
