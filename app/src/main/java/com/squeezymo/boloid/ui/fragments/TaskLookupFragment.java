package com.squeezymo.boloid.ui.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.squeezymo.boloid.R;
import com.squeezymo.boloid.model.TaskItem;
import com.squeezymo.boloid.ui.adapters.PriceListAdapter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class TaskLookupFragment extends Fragment {
    public static final String EXTRA_TASK = TaskLookupFragment.class.getSimpleName() + "." + "task";
    private static final String PATTERN_DATE_UI = "d MMMM yyyy";

    private TaskItem mTask;
    private TextView mDateAndLocationView;

    private TextView mTextView;
    private TextView mDescriptionView;
    private TextView mPricesTitleView;
    private ListView mPricesView;
    private Button mOkBtn;

    public static TaskLookupFragment instantiate(Bundle args) {
        TaskLookupFragment fragment = new TaskLookupFragment();
        fragment.setArguments(args);

        return fragment;
    }

    public static TaskLookupFragment instantiate() {
        return TaskLookupFragment.instantiate(null);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_lookup, container, false);

        mDateAndLocationView = (TextView) view.findViewById(R.id.date_and_location);
        mTextView = (TextView) view.findViewById(R.id.title);
        mDescriptionView = (TextView) view.findViewById(R.id.description);
        mPricesTitleView = (TextView) view.findViewById(R.id.prices_title);
        mPricesView = (ListView) view.findViewById(R.id.prices);
        mOkBtn = (Button) view.findViewById(R.id.ok);

        if (getArguments() != null &&
            getArguments().getParcelable(EXTRA_TASK) != null &&
            getArguments().getParcelable(EXTRA_TASK) instanceof TaskItem) {

            DateFormat formatter = new SimpleDateFormat(PATTERN_DATE_UI);
            mTask = getArguments().getParcelable(EXTRA_TASK);

            mDateAndLocationView.setText(Html.fromHtml(
                    "<i>" +
                    (TextUtils.isEmpty(mTask.getLocationText()) ? "" : "<b>" + mTask.getLocationText() + "</b>, ") +
                    formatter.format(mTask.getDate()) +
                    "</i>"

            ));
            mTextView.setText(mTask.getText());
            mDescriptionView.setText(mTask.getLongText());
            if (mTask.getPrices().size() == 0) {
                mPricesTitleView.setText(getActivity().getResources().getString(R.string.no_prices));
            }
            mPricesView.setAdapter(new PriceListAdapter(getActivity(), mTask.getPrices()));
        }

        mOkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });

        return view;
    }

}
