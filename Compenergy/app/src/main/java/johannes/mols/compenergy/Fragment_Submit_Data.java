/*
 * Copyright (c) Johannes Mols 2017.
 */

package johannes.mols.compenergy;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class Fragment_Submit_Data extends Fragment {

    private Context mContext;

    private ExpandableListView expandableListView;
    private List<String> categories_list;
    private HashMap<String, List<Carrier>> carriers_list;
    private SubmitDataExpandableListAdapter adapter;
    private EditText searchEditText;

    private List<Carrier> items_to_send = new ArrayList<>();

    private DatabaseHelper dbHelper;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle(R.string.nav_item_submit_data);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_submit_data_layout, container, false);
        mContext = getContext();
        expandableListView = (ExpandableListView) view.findViewById(R.id.fragment_submit_data_expandable_list_view);
        dbHelper = new DatabaseHelper(mContext, null, null, 1);

        displayList();

        expandAllGroups();

        setHasOptionsMenu(true);

        searchEditText = (EditText) view.findViewById(R.id.fragment_submit_data_search);
        searchEditText.setOnTouchListener(editSearchOnTouchListener);
        searchEditText.setFilters(new InputFilter[] { Util.filter });
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.filterData(s.toString());
                expandAllGroups();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        expandableListView.setOnChildClickListener(toggleSendData);

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_submit_data_toolbar, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.fragment_submit_data_toolbar_send:
                sendEmail();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void expandAllGroups() {
        for(int i = 0; i < adapter.getGroupCount(); i++) {
            expandableListView.expandGroup(i);
        }
    }

    private void displayList() {
        prepareListData();

        adapter = new SubmitDataExpandableListAdapter(mContext, categories_list, carriers_list);
        expandableListView.setAdapter(adapter);

        expandAllGroups();
    }

    private void prepareListData() {
        categories_list = new ArrayList<>();
        carriers_list = new HashMap<>();

        categories_list = dbHelper.getCategoryListThatContainsCustoms();

        for(int i = 0; i < categories_list.size(); i++) {
            List<Carrier> carrierList = dbHelper.getCustomCarriersWithCategory(categories_list.get(i));
            carriers_list.put(categories_list.get(i), carrierList);

            //Add every custom item to this list at the beginning
            for (Carrier item : carrierList) {
                items_to_send.add(item);
            }
        }

        //Sort
        Collections.sort(categories_list, CustomComparators.ALPHABETICAL_ORDER);
        CustomComparators.CarrierComparator comparator = new CustomComparators.CarrierComparator();
        for (List<Carrier> l : carriers_list.values())
            Collections.sort(l, comparator);
    }

    ExpandableListView.OnChildClickListener toggleSendData = new ExpandableListView.OnChildClickListener() {

        @Override
        public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
            Carrier item = (Carrier) adapter.getChild(groupPosition, childPosition);
            CheckBox checkBox = (CheckBox) v.findViewById(R.id.fragment_submit_data_toggle);
            boolean result = checkBox.isChecked();
            checkBox.setChecked(!result);

            if (items_to_send.contains(item)) {
                //Remove from list
                items_to_send.remove(item);
            } else {
                //Add to list
                items_to_send.add(item);
            }

            return true; //indicates that the event is consumed, meaning the long click listener can't be triggered too
        }
    };

    View.OnTouchListener editSearchOnTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            //final int DRAWABLE_LEFT = 0;
            //final int DRAWABLE_TOP = 1;
            final int DRAWABLE_RIGHT = 2;
            //final int DRAWABLE_BOTTOM = 3;

            if(event.getAction() == MotionEvent.ACTION_UP) {
                if(event.getRawX() >= (searchEditText.getRight() - searchEditText.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                    searchEditText.setText("");
                    return true;
                }
            }
            return false;
        }
    };

    private void sendEmail() {
        final ProgressDialog progressDialog = new ProgressDialog(mContext);
        sendCustomData sendCustomData = new sendCustomData(new sendCustomData.AsyncResponse() {
            @Override
            public void processFinish(Boolean output) {
                if (output) {
                    Log.i("AsyncTask", getString(R.string.data_sent));
                    Toast.makeText(mContext, getString(R.string.data_sent), Toast.LENGTH_LONG).show();
                } else {
                    Log.i("AsyncTask", getString(R.string.data_not_sent));
                    Toast.makeText(mContext, getString(R.string.data_not_sent), Toast.LENGTH_LONG).show();
                }
                progressDialog.dismiss();
            }
        });

        if (items_to_send.size() > 0) {
            sendCustomData.execute(items_to_send);
        } else {
            Toast.makeText(mContext, getString(R.string.select_at_least_one), Toast.LENGTH_LONG).show();
        }

        if (sendCustomData.getStatus() == AsyncTask.Status.RUNNING) {
            progressDialog.setMessage(getString(R.string.dialog_sending_data));
            progressDialog.show();
        }
    }
}

class sendCustomData extends AsyncTask<List<Carrier>, Integer, Boolean> {

    interface AsyncResponse {
        void processFinish(Boolean output);
    }

    private AsyncResponse delegate = null;

    sendCustomData(AsyncResponse delegate) {
        this.delegate = delegate;
    }

    @SafeVarargs
    @Override
    protected final Boolean doInBackground(List<Carrier>... params) {
        try {
            GMailSender sender = new GMailSender("compenergy.app@gmail.com", "johannes.mols.compenergy");
            return sender.sendMail("Data suggestion [" + getDate() + " GMT]", collectData(params[0]), "compenergy.app@gmail.com", "compenergy.app@gmail.com");
        } catch (Exception e) {
            Log.e("SendMail", e.getMessage(), e);
            return false;
        }
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    private String getDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.GERMAN);
        return sdf.format(new Date());
    }

    private String collectData(List<Carrier> data_to_send) {

        //Generate the pseudo xml to attach in the mail
        String xml_items = "";
        for (Carrier item : data_to_send) {
            xml_items += "<carrier>" + "\n" +
                         "   <name>" + item.get_name() + "</name>" + "\n" +
                         "   <category>" + item.get_category() + "</category>" + "\n" +
                         "   <unit>" + item.get_unit() + "</unit>" + "\n" +
                         "   <energy>" + item.get_energy() + "</energy>" + "\n" +
                         "   <custom>" + "false" + "</custom>" + "\n" +
                         "   <favorite>" + "false" + "</favorite>" + "\n" +
                         "</carrier>" + "\n";
        }

        return  "Date: " + getDate() + " GMT" + "\n" +
                "OS API Level: " + Build.VERSION.SDK_INT + "\n" +
                "Device: " + Build.DEVICE + "\n" +
                "Model and Product: " + Build.MODEL + " (" + Build.PRODUCT + ")" + "\n\n" +
                "--------------------" + "\n\n" +
                xml_items + "\n";
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        delegate.processFinish(aBoolean);
    }
}