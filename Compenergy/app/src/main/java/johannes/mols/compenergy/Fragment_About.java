/*
 * Copyright (c) Johannes Mols 2017.
 */

package johannes.mols.compenergy;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Fragment_About extends Fragment {

    private Context mContext;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getActivity().setTitle(R.string.nav_item_about);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about_layout, container, false);

        mContext = getContext();

        TextView author = (TextView) view.findViewById(R.id.fragment_about_author_value);
        author.setText(R.string.about_author_value);

        TextView contact = (TextView) view.findViewById(R.id.fragment_about_contact_value);
        contact.setText(R.string.about_contact_value);
        contact.setOnClickListener(contactClick);

        TextView version = (TextView) view.findViewById(R.id.fragment_about_version_value);
        version.setText(BuildConfig.VERSION_NAME);

        Date buildDate = new Date(BuildConfig.TIMESTAMP);
        Calendar cal = Calendar.getInstance();
        cal.setTime(buildDate);
        String buildDateDisplay = String.valueOf(cal.get(Calendar.DAY_OF_MONTH)) + "." + String.valueOf(cal.get(Calendar.MONTH) + 1) + "." + String.valueOf(cal.get(Calendar.YEAR));
        TextView build = (TextView) view.findViewById(R.id.fragment_about_build_value);
        build.setText(String.valueOf(BuildConfig.VERSION_CODE) + " (" + buildDateDisplay + ")");

        NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.ENGLISH); //Use dots in all language settings as decimal separator
        DecimalFormat df = (DecimalFormat) numberFormat;
        TextView db_version = (TextView) view.findViewById(R.id.fragment_about_default_database_version_number_value);
        db_version.setText(df.format(DatabaseHelper.DATABASE_VERSION));

        return view;
    }

    View.OnClickListener contactClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Util.openEmailApplication(mContext, getString(R.string.about_contact_value), 0);
        }
    };
}
