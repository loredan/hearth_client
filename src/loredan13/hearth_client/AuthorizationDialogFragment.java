package loredan13.hearth_client;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.inputmethod.EditorInfo;
import android.widget.*;

/**
 * Created by loredan13 on 03.02.2015.
 */
public class AuthorizationDialogFragment extends DialogFragment {
    private RadioGroup userGroup;
    private EditText passwordField;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LinearLayout form = new LinearLayout(getActivity());
        form.setOrientation(LinearLayout.VERTICAL);

        userGroup = new RadioGroup(getActivity());
        RadioButton maleButton = new RadioButton(getActivity());
        maleButton.setText(R.string.male);
        RadioButton femaleButton = new RadioButton(getActivity());
        femaleButton.setText(R.string.female);
        userGroup.addView(maleButton);
        userGroup.addView(femaleButton);
        form.addView(userGroup);

        passwordField = new EditText(getActivity());
        passwordField.setHint(R.string.password_hint);
        passwordField.setInputType(EditorInfo.TYPE_TEXT_VARIATION_PASSWORD);
        form.addView(passwordField);

        builder.setView(form);

        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int id) {
                String userString = ((RadioButton) userGroup.findViewById(userGroup.getCheckedRadioButtonId()))
                        .getText().toString();
                final String user;
                if (userString.equals(getString(R.string.male))) {
                    user = getString(R.string.api_user_male);
                } else if (userString.equals(getString(R.string.female))) {
                    user = getString(R.string.api_user_female);
                } else {
                    user = "none";
                }

                final String password = passwordField.getText().toString();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Networking.auth(user, password);
                            Toast.makeText(getActivity(), R.string.auth_success, Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(getActivity(), R.string.auth_failure + ": " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                AuthorizationDialogFragment.this.getDialog().cancel();
            }
        });

        return builder.create();
    }
}
