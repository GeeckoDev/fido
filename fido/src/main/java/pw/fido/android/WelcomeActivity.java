package pw.fido.android;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class WelcomeActivity extends ActionBarActivity {

    public SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new WelcomeFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.welcome, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        // Do nothing!
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class WelcomeFragment extends Fragment {

        private WelcomeActivity activity;
        private Spinner sp_accessnum;
        private EditText et_register_email;
        private Button btn_register;
        private EditText et_verify_num;
        private Button btn_verify;
        private Button btn_start;

        public static WelcomeFragment newInstance() {
            WelcomeFragment fragment = new WelcomeFragment();
            return fragment;
        }

        public WelcomeFragment() {
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            this.activity = (WelcomeActivity) getActivity();
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_welcome, container, false);

            sp_accessnum = (Spinner) rootView.findViewById(R.id.sp_accessnum);
            et_register_email = (EditText) rootView.findViewById(R.id.et_register_email);
            btn_register = (Button) rootView.findViewById(R.id.btn_register);
            et_verify_num = (EditText) rootView.findViewById(R.id.et_verify_num);
            btn_verify = (Button) rootView.findViewById(R.id.btn_verify);
            btn_start = (Button) rootView.findViewById(R.id.btn_start);

            // Setup the spinner.
            CharSequence[] choices = new CharSequence[]{
                    "Choose a country...",
                    "Australia",
                    "Canada",
                    "Finland",
                    "Germany",
                    "Norway",
                    "Sweden",
                    "United Kingdom",
                    "United States"
            };

            ArrayAdapter<CharSequence> adapter =
                    new ArrayAdapter<CharSequence>(
                            activity,
                            android.R.layout.simple_spinner_item,
                            choices);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            sp_accessnum.setAdapter(adapter);
            sp_accessnum.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {
                    String[] phoneNumbers = new String[]{
                            "",
                            "+61 43 929 4335",
                            "+1 587 316 3643",
                            "+358 457 395 07 99",
                            "+49 177 178 9339",
                            "+47 594 41 585",
                            "+46 76 943 8228",
                            "+44 75 0733 2662",
                            "+1 605 888 5005"
                    };

                    if (pos > 0) {
                        SharedPreferences.Editor editor = activity.prefs.edit();
                        editor.putString("access_number", phoneNumbers[pos]);
                        editor.commit();

                        Toast.makeText(activity, phoneNumbers[pos] + " is used.", Toast.LENGTH_LONG).show();

                        btn_register.setEnabled(true);
                        et_register_email.setEnabled(true);
                        btn_start.setEnabled(true);
                    } else {
                        btn_register.setEnabled(false);
                        et_register_email.setEnabled(false);
                        btn_verify.setEnabled(false);
                        et_verify_num.setEnabled(false);
                        btn_start.setEnabled(false);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                }
            });

            btn_register.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String email = et_register_email.getText().toString();

                    if (!email.contains("@") || !email.contains(".")) {
                        Toast.makeText(activity, "Invalid email address...", Toast.LENGTH_LONG).show();
                        return;
                    }

                    SmsManager sms = SmsManager.getDefault();
                    sms.sendTextMessage(activity.prefs.getString("access_number", ""), null,
                            "register " + email, null, null);

                    Toast.makeText(activity, "Registration sent.\nWait for the verification code.", Toast.LENGTH_LONG).show();

                    btn_start.setEnabled(false);
                    et_verify_num.setEnabled(true);
                    et_verify_num.requestFocus();
                    btn_verify.setEnabled(true);
                }
            });

            btn_verify.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String code = et_verify_num.getText().toString();

                    if (code.length() != 6) {
                        Toast.makeText(activity, "Invalid verification code...", Toast.LENGTH_LONG).show();
                        return;
                    }

                    SmsManager sms = SmsManager.getDefault();
                    sms.sendTextMessage(activity.prefs.getString("access_number", ""), null,
                            "verify " + code, null, null);

                    Toast.makeText(activity, "Verification code sent.\nCheck your email.", Toast.LENGTH_LONG).show();

                    btn_start.setEnabled(true);
                }
            });

            btn_start.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SharedPreferences.Editor editor = activity.prefs.edit();
                    editor.putBoolean("first_launch", false);
                    editor.commit();

                    activity.finish();
                }
            });

            // Dismiss keyboard
            activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

            return rootView;
        }
    }

}
