package loredan13.hearth_client;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.FrameLayout;

/**
 * Created by loredan13 on 03.02.2015.
 */
public class SetServerDialogFragment extends DialogFragment {
    EditText serverField;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        FrameLayout form = new FrameLayout(getActivity());

        serverField = new EditText(getActivity());
        serverField.setHint(getString(R.string.server_hint));
        form.addView(serverField);

        builder.setView(form);

        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String server = serverField.getText().toString();
                Networking.setServer(server);
            }
        });

        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                SetServerDialogFragment.this.getDialog().cancel();
            }
        });

        return builder.create();
    }
}
