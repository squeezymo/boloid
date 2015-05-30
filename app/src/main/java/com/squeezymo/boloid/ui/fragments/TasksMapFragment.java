package com.squeezymo.boloid.ui.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squeezymo.boloid.R;
import com.squeezymo.boloid.model.TaskItem;
import com.squeezymo.boloid.ui.activities.TaskLookupActivity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.yandex.yandexmapkit.MapController;
import ru.yandex.yandexmapkit.MapView;
import ru.yandex.yandexmapkit.OverlayManager;
import ru.yandex.yandexmapkit.overlay.Overlay;
import ru.yandex.yandexmapkit.overlay.OverlayItem;
import ru.yandex.yandexmapkit.overlay.balloon.BalloonItem;
import ru.yandex.yandexmapkit.overlay.balloon.OnBalloonListener;
import ru.yandex.yandexmapkit.utils.GeoPoint;

public class TasksMapFragment extends Fragment implements OnBalloonListener {
    private static final String LOG_TAG = TasksMapFragment.class.getCanonicalName();

    private MapController mMapController;
    private Map<OverlayItem, TaskItem> mOverlayToTask;
    private OverlayManager mOverlayManager;
    private Overlay mOverlay;

    // retained state
    private GeoPoint mRetainedLocation;
    private float mRetainedZoom;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        final MapView mapView = (MapView) view.findViewById(R.id.map);
        mMapController = mapView.getMapController();
        mOverlayManager = mMapController.getOverlayManager();
        mOverlayManager.getMyLocation().setEnabled(false);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (mRetainedLocation != null) {
            mMapController.setPositionNoAnimationTo(mRetainedLocation);
        }

        if (mRetainedZoom != 0f) {
            mMapController.setZoomCurrent(mRetainedZoom);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();

        mRetainedLocation = mMapController.getMapCenter();
        mRetainedZoom = mMapController.getZoomCurrent();
    }

    public void locate(TaskItem task) {
        if (task == null)
            return;

        mMapController.setZoomCurrent(task.getZoomLevel());
        mMapController.setPositionAnimationTo(task.getGeoPoint());
    }

    public void setTaskItems(List<TaskItem> tasks) {
        if (mOverlayToTask == null) {
            mOverlayToTask = new HashMap<>();
        }
        else {
            mOverlayToTask.clear();
        }

        if (mOverlay != null) {
            mOverlay.clearOverlayItems();
            mOverlayManager.removeOverlay(mOverlay);
        }

        if (tasks == null) {
            return;
        }

        mOverlay = new Overlay(mMapController);

        for (TaskItem task : tasks) {
            OverlayItem overlayItem = new OverlayItem(task.getGeoPoint(), getActivity().getResources().getDrawable(R.drawable.marker_default, null));
            BalloonItem balloon = new BalloonItem(getActivity(), overlayItem.getGeoPoint());
            balloon.setOnBalloonListener(this);
            balloon.setText(task.getTitle());

            overlayItem.setBalloonItem(balloon);
            mOverlayToTask.put(overlayItem, task);

            mOverlay.addOverlayItem(overlayItem);
        }

        mOverlayManager.addOverlay(mOverlay);
    }

    @Override
    public void onBalloonViewClick(BalloonItem balloonItem, View view)  {
        OverlayItem item = balloonItem.getOverlayItem();
        TaskItem task = mOverlayToTask.get(item);

        Intent intent = new Intent(getActivity(), TaskLookupActivity.class);
        intent.putExtra(TaskLookupFragment.EXTRA_TASK, task);
        startActivity(intent);
    }

    @Override
    public void onBalloonShow(BalloonItem balloonItem) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onBalloonHide(BalloonItem balloonItem) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onBalloonAnimationStart(BalloonItem balloonItem) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onBalloonAnimationEnd(BalloonItem balloonItem) {
        // TODO Auto-generated method stub

    }
}
