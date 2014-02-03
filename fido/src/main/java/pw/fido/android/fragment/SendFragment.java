package pw.fido.android.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import pw.fido.android.MainActivity;
import pw.fido.android.R;

public class SendFragment extends Fragment {

    private MainActivity activity;
    private Button btn_contact;
    private Button btn_send;
    private EditText et_amount;
    private CheckBox cb_confirm;
    private CheckBox cb_notify;
    private String contactNumber;

    public static SendFragment newInstance() {
        SendFragment fragment = new SendFragment();
        return fragment;
    }

    public SendFragment() {
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = (MainActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_send, container, false);

        btn_contact = (Button) rootView.findViewById(R.id.btn_contact);
        btn_send = (Button) rootView.findViewById(R.id.btn_send);
        et_amount = (EditText) rootView.findViewById(R.id.et_amount);
        cb_confirm = (CheckBox) rootView.findViewById(R.id.cb_confirm);
        cb_notify = (CheckBox) rootView.findViewById(R.id.cb_notify);

        btn_contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent contactInfo = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                startActivityForResult(contactInfo, MainActivity.REQUEST_CODE_CONTACT_NUMBER);
            }
        });
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                send();
            }
        });

        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == MainActivity.REQUEST_CODE_CONTACT_NUMBER &&
            resultCode == MainActivity.RESULT_OK) {
            showPickNumberDialog(data.getData());
        }
    }

    private CharSequence[] getContactNumbers(long contactId) {
        final String[] projection = new String[]{
                ContactsContract.Data.DATA1, ContactsContract.Data.DATA2, ContactsContract.Data.DATA3, ContactsContract.Data.MIMETYPE
        };
        final String where = ContactsContract.Data.CONTACT_ID + "=? AND ("
                + ContactsContract.Data.MIMETYPE + "=?)";
        final String[] whereArgs = new String[]{
                String.valueOf(contactId),
                ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE
        };

        final Cursor cursor = activity.getContentResolver().query(ContactsContract.Data.CONTENT_URI,
                projection, where, whereArgs, ContactsContract.Data.MIMETYPE);

        if (cursor == null) {
            return null;
        }

        final int count = cursor.getCount();
        final int dataIndex = cursor.getColumnIndex(ContactsContract.Data.DATA1);
        final int typeIndex = cursor.getColumnIndex(ContactsContract.Data.DATA2);
        final int labelIndex = cursor.getColumnIndex(ContactsContract.Data.DATA3);

        if (count == 0) {
            cursor.close();
            return null;
        }

        final CharSequence[] entries = new CharSequence[count];

        for (int i = 0; i < count; i++) {
            cursor.moveToPosition(i);

            String data = cursor.getString(dataIndex);
            int type = cursor.getInt(typeIndex);
            String label = cursor.getString(labelIndex);

            entries[i] = ContactsContract.CommonDataKinds.Phone.getTypeLabel(getResources(), type, label) + ": " + data;
        }

        cursor.close();

        return entries;
    }

    private void showPickNumberDialog(Uri contactUri) {
        long contactId = -1;
        String displayName = null;

        final String[] projection = new String[]{
                ContactsContract.Contacts._ID, ContactsContract.Contacts.DISPLAY_NAME
        };
        final Cursor cursor = activity.getContentResolver().query(contactUri,
                projection, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                contactId = cursor.getLong(0);
                displayName = cursor.getString(1);
            }
            cursor.close();
        }

        final CharSequence[] entries = (contactId >= 0) ? getContactNumbers(contactId) : null;

        if (contactId < 0 || entries == null) {
            Toast.makeText(activity, "Cannot find contact...", Toast.LENGTH_LONG).show();
            return;
        }

        // I only want to store one value :((
        final Integer[] itemSelected = new Integer[]{0};
        final String[] name = new String[]{displayName};

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setIcon(R.drawable.ic_launcher);
        builder.setTitle(displayName);
        builder.setSingleChoiceItems(entries, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                itemSelected[0] = which;
            }
        });
        builder.setPositiveButton(android.R.string.ok,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String entry = entries[itemSelected[0]].toString();
                        String number = entry.split(":")[1].substring(1);

                        updateContact(name[0], number);
                    }
                });
        builder.setNegativeButton(android.R.string.cancel, null);

        builder.show();
    }

    private void updateContact(String name, String number) {
        btn_contact.setText(name);
        contactNumber = number.replaceAll(" ", "").replaceAll("-", "");
    }

    private void send() {
        String message;

        if (contactNumber == null) {
            Toast.makeText(activity, "Please select a contact.", Toast.LENGTH_LONG).show();
            return;
        } else if (!contactNumber.startsWith("+") || contactNumber.startsWith("00")) {
            Toast.makeText(activity, "Please select a valid international number.\nFor example, a french number should be prefixed with +33.", Toast.LENGTH_LONG).show();
            return;
        } else if (et_amount.getText().length() == 0) {
            Toast.makeText(activity, "Please input an amount.", Toast.LENGTH_LONG).show();
            return;
        }

        message = "tip " + contactNumber + " " + et_amount.getText();

        if (cb_notify.isChecked()) {
            message += " notify";
        }

        if (cb_confirm.isChecked()) {
            message += " confirm";
        }

        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(activity.prefs.getString("access_number", ""), null,
                message, null, null);

        Toast.makeText(activity, "Successfully sent doge.", Toast.LENGTH_LONG).show();
    }
}
