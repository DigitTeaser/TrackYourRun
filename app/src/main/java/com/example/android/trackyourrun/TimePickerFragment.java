package com.example.android.trackyourrun;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.TextView;
import android.widget.TimePicker;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Build up a time picker dialog that allows user enter the time of the run.
 */
public class TimePickerFragment extends DialogFragment
        implements TimePickerDialog.OnTimeSetListener {

    /**
     * Tag for the log messages.
     */
    private static final String LOG_TAG = TimePickerFragment.class.getSimpleName();

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker.
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        // Create a new instance of TimePickerDialog and return it.
        return new TimePickerDialog(getActivity(), this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        // Set the time to the TextView.
        try {
            TextView timeView = getActivity().findViewById(R.id.time_view);
            // Parse the set time to Date object, so it can be formatted.
            String timeIn = hourOfDay + ":" + minute;
            SimpleDateFormat inFormat = new SimpleDateFormat("H:m",
                    Locale.getDefault());
            Date timeOut = inFormat.parse(timeIn);
            // Define the time format that the app wants and set it to the TextView.
            SimpleDateFormat outFormat =
                    new SimpleDateFormat("HH:mm", Locale.getDefault());
            timeView.setText(outFormat.format(timeOut));
        } catch (ParseException e) {
            Log.e(LOG_TAG, "Problem parsing the date ", e);
        }
    }
}
