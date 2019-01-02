package com.whoami.litlteinstagram.lib;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import java.util.Date;

/**
 * Created by Syahrul on 28/11/2016.
 */
public class UtilityHelper
{
    public static void showAlert(Context context, String title, String message)
    {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);

        dialogBuilder.setTitle(title);
        dialogBuilder.setMessage(message);
        dialogBuilder.setNegativeButton("OK", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();
            }
        });

        dialogBuilder.show();
    }

    public static String dateNow()
    {
        Date now = new Date();

        String strNow = now.toString();

        return strNow;
    }
}
