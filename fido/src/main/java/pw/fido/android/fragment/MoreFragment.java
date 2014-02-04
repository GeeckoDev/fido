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

public class MoreFragment extends Fragment {

    private MainActivity activity;
    private Button btn_statement;
    private EditText et_new_fidoname;
    private Button btn_update_fidoname;
    private EditText et_email;
    private Button btn_update_email;
    private EditText et_yoyodays;
    private Button btn_update_yoyodays;

    public static MoreFragment newInstance() {
        MoreFragment fragment = new MoreFragment();
        return fragment;
    }

    public MoreFragment() {
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = (MainActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_more, container, false);

        btn_statement = (Button) rootView.findViewById(R.id.btn_statement);
        et_new_fidoname = (EditText) rootView.findViewById(R.id.et_new_fidoname);
        btn_update_fidoname = (Button) rootView.findViewById(R.id.btn_update_fidoname);
        et_email = (EditText) rootView.findViewById(R.id.et_email);
        btn_update_email = (Button) rootView.findViewById(R.id.btn_update_email);
        et_yoyodays = (EditText) rootView.findViewById(R.id.et_yoyodays);
        btn_update_yoyodays = (Button) rootView.findViewById(R.id.btn_update_yoyodays);

        btn_statement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SmsManager sms = SmsManager.getDefault();
                sms.sendTextMessage(activity.prefs.getString("access_number", ""), null,
                        "statement", null, null);

                Toast.makeText(activity, "Statement request sent.\nCheck your email.", Toast.LENGTH_LONG).show();
            }
        });

        btn_update_fidoname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (et_new_fidoname.length() == 0) {
                    Toast.makeText(activity, "Invalid fidoname...", Toast.LENGTH_LONG).show();
                    return;
                }

                SmsManager sms = SmsManager.getDefault();
                sms.sendTextMessage(activity.prefs.getString("access_number", ""), null,
                        "set fidoname " + et_new_fidoname.getText(), null, null);

                et_new_fidoname.setText("");
                Toast.makeText(activity, "Fidoname updated.", Toast.LENGTH_LONG).show();
            }
        });

        btn_update_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = et_email.getText().toString();

                if (!email.contains("@") || !email.contains(".")) {
                    Toast.makeText(activity, "Invalid email...", Toast.LENGTH_LONG).show();
                    return;
                }

                SmsManager sms = SmsManager.getDefault();
                sms.sendTextMessage(activity.prefs.getString("access_number", ""), null,
                        "set email " + et_email.getText(), null, null);

                et_email.setText("");
                Toast.makeText(activity, "Email updated.", Toast.LENGTH_LONG).show();
            }
        });

        btn_update_yoyodays.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (et_yoyodays.length() == 0) {
                    Toast.makeText(activity, "Invalid yoyodays count...", Toast.LENGTH_LONG).show();
                    return;
                }

                int yoyodays = Integer.parseInt(et_yoyodays.getText().toString());

                if (yoyodays < 7 || yoyodays > 180) {
                    Toast.makeText(activity, "Invalid yoyodays count...", Toast.LENGTH_LONG).show();
                    return;
                }

                SmsManager sms = SmsManager.getDefault();
                sms.sendTextMessage(activity.prefs.getString("access_number", ""), null,
                        "set yoyodays " + yoyodays, null, null);

                et_yoyodays.setText("");
                Toast.makeText(activity, "Yoyodays count updated.", Toast.LENGTH_LONG).show();
            }
        });

        return rootView;
    }

}

