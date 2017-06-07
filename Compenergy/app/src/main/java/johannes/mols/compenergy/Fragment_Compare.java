/*
 * Copyright (c) Johannes Mols 2017.
 */

package johannes.mols.compenergy;

import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class Fragment_Compare extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_compare_layout, container, false);

        View rootLayout = view.findViewById(R.id.fragment_compare_root_layout);
        GradientDrawable gd = new GradientDrawable(GradientDrawable.Orientation.TL_BR, new int[] {0xFFFFEE58, 0xFFBB6689});
        gd.setCornerRadius(0f);
        rootLayout.setBackground(gd);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getActivity().setTitle(R.string.nav_item_compare);
    }
}
