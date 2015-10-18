package com.tinakit.moveit.utility;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.EditText;

import com.tinakit.moveit.R;

/**
 * Created by Tina on 7/3/2015.
 */
public class DialogUtility {

    public static void displayAlertDialog(Context context, String title, String message, String positiveButtonLabel){

        AlertDialog.Builder alert = new AlertDialog.Builder(context);

        alert.setTitle(title);
        alert.setMessage(message);
        alert.setPositiveButton(positiveButtonLabel, null);
        alert.show();
    }

    public static Dialog displayConfirmDialog(Context context, String confirmMessage, DialogInterface.OnClickListener listenerPositive, DialogInterface.OnClickListener listenerNegative){

        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(confirmMessage)
                .setPositiveButton(R.string.yes, listenerPositive)
                .setNegativeButton(R.string.cancel, listenerNegative);

        // Create the AlertDialog object and return it
        return builder.create();
    }

}
