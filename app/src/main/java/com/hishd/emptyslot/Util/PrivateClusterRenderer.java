package com.hishd.emptyslot.Util;

import android.content.Context;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.hishd.emptyslot.R;

public class PrivateClusterRenderer extends DefaultClusterRenderer<SlotMarkerItem> {
    private final Context mContext;

    public PrivateClusterRenderer(Context context, GoogleMap map, ClusterManager<SlotMarkerItem> clusterManager) {
        super(context, map, clusterManager);
        mContext = context;
    }

    @Override
    protected void onBeforeClusterItemRendered(SlotMarkerItem item, MarkerOptions markerOptions) {
        final BitmapDescriptor markerDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.private_marker_dot);

        markerOptions.icon(markerDescriptor).snippet(item.getTitle());
    }
}
