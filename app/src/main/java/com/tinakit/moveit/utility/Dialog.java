package com.tinakit.moveit.utility;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.EditText;

import com.tinakit.moveit.R;

/**
 * Created by Tina on 7/3/2015.
 */
public class Dialog {

    public static void displayAlertDialog(Context context, String title, String message, String positiveButtonLabel){

        AlertDialog.Builder alert = new AlertDialog.Builder(context);

        alert.setTitle(title);
        alert.setMessage(message);
        alert.setPositiveButton(positiveButtonLabel, null);
        alert.show();
    }
}
