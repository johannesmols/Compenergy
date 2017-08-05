/*
 * Copyright (c) Johannes Mols 2017.
 */

package johannes.mols.compenergy;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public class Fragment_Submit_Data extends Fragment {

    private Context mContext;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mContext = getContext();

        getActivity().setTitle(R.string.nav_item_submit_data);

        sendEmail();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_submit_data_layout, container, false);
    }

    private void sendEmail() {
        final ProgressDialog progressDialog = new ProgressDialog(mContext);
        sendCustomData sendCustomData = new sendCustomData(new sendCustomData.AsyncResponse() {
            @Override
            public void processFinish(Boolean output) {
                if (output) {
                    Log.i("AsyncTask", "Email sent successfully");
                    Toast.makeText(mContext, "Email sent", Toast.LENGTH_LONG).show();
                } else {
                    Log.i("AsyncTask", "Email failed to send");
                    Toast.makeText(mContext, "Email not sent", Toast.LENGTH_LONG).show();
                }
                progressDialog.dismiss();
            }
        });
        sendCustomData.execute(mContext);
        if (sendCustomData.getStatus() == AsyncTask.Status.RUNNING) {
            progressDialog.setMessage("Sending Data...");
            progressDialog.show();
        }
    }
}

class sendCustomData extends AsyncTask<Context, Integer, Boolean> {

    interface AsyncResponse {
        void processFinish(Boolean output);
    }

    private AsyncResponse delegate = null;

    sendCustomData(AsyncResponse delegate) {
        this.delegate = delegate;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Boolean doInBackground(Context... params) {
        try {
            GMailSender sender = new GMailSender("compenergy.app@gmail.com", "johannes.mols.compenergy");
            sender.sendMail("Test subject", "Test body", "compenergy.app@gmail.com", "compenergy.app@gmail.com");
            return true;
        } catch (Exception e) {
            Log.e("SendMail", e.getMessage(), e);
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        delegate.processFinish(aBoolean);
    }
}
