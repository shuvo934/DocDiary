package ttit.com.shuvo.docdiary.dashboard.dialogue;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.google.android.material.button.MaterialButton;

import ttit.com.shuvo.docdiary.R;
import ttit.com.shuvo.docdiary.dashboard.interfaces.ErrorHandlerListener;


public class ErrorHandlerDialogue extends AppCompatDialogFragment {

    TextView errorMessage;
    MaterialButton logOut, exit, retry;

    AlertDialog dialog;

    String message;
    String destination;

    public ErrorHandlerDialogue(){}

    public static ErrorHandlerDialogue newInstance(String message, String destination) {
        ErrorHandlerDialogue fragment = new ErrorHandlerDialogue();
        Bundle args = new Bundle();
        args.putString("message", message);
        args.putString("destination", destination);
        fragment.setArguments(args);
        return fragment;
    }

    private ErrorHandlerListener errorHandlerListener;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        if (getActivity() instanceof ErrorHandlerListener)
            errorHandlerListener = (ErrorHandlerListener) getActivity();

        View view = inflater.inflate(R.layout.error_screen_logout_layout, null);

        if (getArguments() != null) {
            message = getArguments().getString("message", "No Message Found");
            destination = getArguments().getString("destination", "0");
        }

        errorMessage = view.findViewById(R.id.error_message_for_handler);
        logOut = view.findViewById(R.id.log_out_button_for_handler);
        exit = view.findViewById(R.id.exit_button_for_handler);
        retry = view.findViewById(R.id.retry_button_for_handler);

        builder.setView(view);

        dialog = builder.create();

        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        setCancelable(false);

        errorMessage.setText(message);

        logOut.setOnClickListener(v -> {
            if(errorHandlerListener != null)
                errorHandlerListener.onButtonChoose(2, destination);

            dialog.dismiss();
        });

        exit.setOnClickListener(v -> {
            if(errorHandlerListener != null)
                errorHandlerListener.onButtonChoose(0, destination);

            dialog.dismiss();
        });

        retry.setOnClickListener(v -> {
            if(errorHandlerListener != null)
                errorHandlerListener.onButtonChoose(1, destination);

            dialog.dismiss();
        });

        return dialog;
    }
}
