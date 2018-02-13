package com.example.android.trackyourrun;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.util.Log;
import android.widget.DatePicker;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Build up a date picker dialog that allows user enter the date of the run.
 */
public class DatePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {

    /**
     * Tag for the log messages.
     */
    private static final String LOG_TAG = DatePickerFragment.class.getSimpleName();

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker.
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        // Set the date to the TextView.
        try {
            TextView dateView = getActivity().findViewById(R.id.date_view);
            // Parse the set date to Date object, so it can be formatted.
            // Month in Calendar begins at 0, while in Date is 1, so month needs to add one.
            String dateIn = Integer.toString(year) + "-" +
                    Integer.toString(month + 1) + "-" +
                    Integer.toString(day);
            SimpleDateFormat inFormat = new SimpleDateFormat("yyyy-M-d",
                    Locale.getDefault());
            Date dateOut = inFormat.parse(dateIn);
            // Define the time format that the app wants and set it to the TextView.
            SimpleDateFormat outFormat =
                    new SimpleDateFormat("EEE, MMM d, yyyy", Locale.getDefault());
            dateView.setText(outFormat.format(dateOut));
        } catch (ParseException e) {
            Log.e(LOG_TAG, "Problem parsing the date ", e);
        }
    }
}
