package pw.fido.android.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import pw.fido.android.MainActivity;
import pw.fido.android.R;

public class ReceiveFragment extends Fragment {

    private MainActivity activity;
    private Button btn_request_wd;
    private EditText et_address;
    private Button btn_update_wd;

    public static ReceiveFragment newInstance() {
        ReceiveFragment fragment = new ReceiveFragment();
        return fragment;
    }

    public ReceiveFragment() {
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = (MainActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_receive, container, false);

        btn_request_wd = (Button)rootView.findViewById(R.id.btn_request_wd);
        et_address = (EditText)rootView.findViewById(R.id.et_address);
        btn_update_wd = (Button)rootView.findViewById(R.id.btn_update_wd);

        btn_request_wd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SmsManager sms = SmsManager.getDefault();
                sms.sendTextMessage(activity.prefs.getString("access_number", ""), null,
                        "withdraw", null, null);

                Toast.makeText(activity, "Withdraw request sent.\nCheck your wallet.", Toast.LENGTH_LONG).show();
            }
        });

        btn_update_wd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String address = et_address.getText().toString();

                if (!address.startsWith("D") || address.length() != 34) {
                    Toast.makeText(activity, "This doesn't look like a valid address...", Toast.LENGTH_LONG).show();
                    return;
                }

                SmsManager sms = SmsManager.getDefault();
                sms.sendTextMessage(activity.prefs.getString("access_number", ""), null,
                        "set autowithdraw " + address, null, null);

                Toast.makeText(activity, "Dogecoin withdraw address updated.", Toast.LENGTH_LONG).show();
            }
        });

        return rootView;
    }

}
