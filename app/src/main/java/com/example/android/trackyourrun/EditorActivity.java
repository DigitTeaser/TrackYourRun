package com.example.android.trackyourrun;

import android.app.DialogFragment;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.trackyourrun.data.RunContract.RunEntry;
import com.example.android.trackyourrun.data.RunDbHelper;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Allows user to add a new run or edit an existing one.
 */
public class EditorActivity extends AppCompatActivity {

    /**
     * Tag for the log messages.
     */
    private static final String LOG_TAG = EditorActivity.class.getSimpleName();

    /**
     * Indicator that tells whether {@link EditorActivity} is its first time rendering or not.
     */
    private boolean firstTimeRendering = false;

    /**
     * EditText field to enter the date, time, duration of the run.
     */
    private TextView mDateView, mTimeView, mDurationView;

    /**
     * EditText field to enter the distance of the run.
     */
    private EditText mDistanceEditText;

    /**
     * Spinner that select the distance unit of the run.
     */
    private Spinner mDistanceUnitSpinner;

    /**
     * Distance unit of the run. The possible valid values are in the RunContract.java file:
     * {@link RunEntry#DISTANCE_UNIT_KILO}, {@link RunEntry#DISTANCE_UNIT_MILE}.
     */
    private String mDistanceUnit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        // Find all relevant views that we will need to read user input from.
        mDateView = findViewById(R.id.date_view);
        mTimeView = findViewById(R.id.time_view);
        mDurationView = findViewById(R.id.duration_view);
        mDistanceEditText = findViewById(R.id.edit_distance);
        mDistanceUnitSpinner = findViewById(R.id.spinner_distance_unit);

        // Set the distance EditText length filter and input filter.
        mDistanceEditText.setFilters(new InputFilter[]{
                new InputFilter.LengthFilter(4),
                new EditTextInputFilter(1, 100)});
        // Setup the distance unit spinner.
        setupDistanceUnitSpinner();

        // The indicator saves in onSaveInstanceState, retrieve it before putting into if statement.
        if (savedInstanceState != null) {
            firstTimeRendering = savedInstanceState.getBoolean("firstTimeRendering");
        }

        if (!firstTimeRendering) {
            Bundle bundle = getIntent().getExtras();
            if (bundle != null) {
                // When EditorActivity has intent extras, set them to the views.
                mDateView.setText(bundle.getString("itemDate"));
                mTimeView.setText(bundle.getString("itemTime"));
                mDurationView.setText(bundle.getString("itemDuration"));
                mDistanceEditText.setText(String.valueOf(bundle.getDouble("itemDistance")));

                if (bundle.getString("itemDistanceUnit").equals(getString(R.string.distance_unit_kilo))) {
                    mDistanceUnitSpinner.setSelection(RunEntry.DISTANCE_UNIT_KILO);
                } else if (bundle.getString("itemDistanceUnit").equals(getString(R.string.distance_unit_mile))) {
                    mDistanceUnitSpinner.setSelection(RunEntry.DISTANCE_UNIT_MILE);
                }
            } else {
                // Use the current date and time as the default values for the TextView.
                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat dateFormat =
                        new SimpleDateFormat("EEE, MMM d, yyyy", Locale.getDefault());
                mDateView.setText(dateFormat.format(calendar.getTime()));
                SimpleDateFormat timeFormat =
                        new SimpleDateFormat("HH:mm", Locale.getDefault());
                mTimeView.setText(timeFormat.format(calendar.getTime()));
            }
            // Set the indicator to true. Make sure that this happens only once.
            firstTimeRendering = true;
        } else if (savedInstanceState != null) {
            // Restore the view values, when activity restarts.
            mDateView.setText(savedInstanceState.getString("dateString"));
            mTimeView.setText(savedInstanceState.getString("timeString"));
            mDurationView.setText(savedInstanceState.getString("durationString"));
        }

        // When user click on the date TextView, popup the date picker dialog.
        mDateView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment fragment = new DatePickerFragment();
                fragment.show(getFragmentManager(), "datePicker");
            }
        });
        // When user click on the time TextView, popup the time picker dialog.
        mTimeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment fragment = new TimePickerFragment();
                fragment.show(getFragmentManager(), "timePicker");
            }
        });
        // When user click on the duration TextView, popup the duration dialog.
        mDurationView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Setup duration dialog that allows user input the duration of the run.
                AlertDialog.Builder builder = new AlertDialog.Builder(EditorActivity.this);
                builder.setItems(R.array.array_duration_options, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case RunEntry.DURATION_HALF_HOUR:
                                mDurationView.setText(R.string.duration_half_hour);
                                break;
                            case RunEntry.DURATION_ONE_HOUR:
                                mDurationView.setText(R.string.duration_one_hour);
                                break;
                            case RunEntry.DURATION_TWO_HOUR:
                                mDurationView.setText(R.string.duration_two_hour);
                                break;
                            case RunEntry.DURATION_CUSTOM:
                                // Setup the dialog and get the get the custom duration input.
                                customDurationDialog();
                                break;
                            default:
                                break;
                        }
                    }
                }).create().show();
            }
        });
    }

    /**
     * Setup the custom duration dialog that allows user input custom duration of the run.
     */
    private void customDurationDialog() {
        // Inflate the dialog view and find the two edit text.
        View view = getLayoutInflater().inflate(R.layout.dialog_duration, null);
        final EditText hourEditText = view.findViewById(R.id.custom_duration_hour);
        final EditText minuteEditText = view.findViewById(R.id.custom_duration_minute);
        // Set the two edit text filter.
        hourEditText.setFilters(new InputFilter[]{new EditTextInputFilter(1, 24)});
        minuteEditText.setFilters(new InputFilter[]{new EditTextInputFilter(1, 59)});

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view).setTitle(R.string.custom_duration_title)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        String hour = hourEditText.getText().toString().trim();
                        String minute = minuteEditText.getText().toString().trim();
                        // If user do not input any digits or input zeros,
                        // set the duration to the hint one.
                        String customDuration = getString(R.string.editor_hint_duration_hour) +
                                getString(R.string.time_hour) +
                                getString(R.string.editor_hint_duration_minute) +
                                getString(R.string.time_minutes);
                        // Set the right string format for hour/hours and minute/minutes.
                        if (hour.isEmpty() || hour.equals("0")) {
                            if (minute.equals("1")) {
                                customDuration = minute + getString(R.string.time_minute);
                            } else if (!minute.isEmpty() && !minute.equals("0")) {
                                customDuration = minute + getString(R.string.time_minutes);
                            }
                        } else if (minute.isEmpty() || minute.equals("0")) {
                            if (hour.equals("1")) {
                                customDuration = hour + getString(R.string.time_hour);
                            } else {
                                customDuration = hour + getString(R.string.time_hours);
                            }
                        } else if (hour.equals("1") && minute.equals("1")) {
                            customDuration = hour + getString(R.string.time_hour) +
                                    minute + getString(R.string.time_minute);
                        } else if (hour.equals("1")) {
                            customDuration = hour + getString(R.string.time_hour) +
                                    minute + getString(R.string.time_minutes);
                        } else if (minute.equals("1")) {
                            customDuration = hour + getString(R.string.time_hours) +
                                    minute + getString(R.string.time_minute);
                        } else {
                            customDuration = hour + getString(R.string.time_hours) +
                                    minute + getString(R.string.time_minutes);
                        }
                        // Set the custom duration string to the TextView.
                        mDurationView.setText(customDuration);
                    }
                }).setNegativeButton(android.R.string.cancel, null).create().show();
    }

    /**
     * Setup the dropdown spinner that allows the user to select the distance unit of the run.
     */
    private void setupDistanceUnitSpinner() {
        // Create adapter for spinner. The list options are from the String array it will use
        // the spinner will use the default layout.
        ArrayAdapter spinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_distance_unit_options, android.R.layout.simple_spinner_item);

        // Specify dropdown layout style - simple list view with 1 item per line.
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        // Apply the adapter to the spinner.
        mDistanceUnitSpinner.setAdapter(spinnerAdapter);

        // Load the distance unit spinner position from SharedPreferences and set it.
        loadSpinnerPosition();

        // Set the integer mSelected to the constant values.
        mDistanceUnitSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Set the distance unit according to the user selection.
                if (position == RunEntry.DISTANCE_UNIT_KILO) {
                    mDistanceUnit = getString(R.string.distance_unit_kilo);
                } else if (position == RunEntry.DISTANCE_UNIT_MILE) {
                    mDistanceUnit = getString(R.string.distance_unit_mile);
                }

                // Save the position to SharedPreferences.
                saveSpinnerPosition(mDistanceUnitSpinner.getSelectedItemPosition());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Set the distance unit to kilometer by default.
                mDistanceUnit = getString(R.string.distance_unit_kilo);
            }
        });
    }

    /**
     * Get user input from editor and save new run into database.
     */
    private void insertRuns() {
        // Create database helper
        RunDbHelper mDbHelper = new RunDbHelper(this);

        // Gets the database in write mode
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        // Create a ContentValues object where column names are the keys,
        // and pet attributes from the editor are the values.
        ContentValues values = new ContentValues();
        values.put(RunEntry.COLUMN_RUN_DATE, mDateView.getText().toString().trim());
        values.put(RunEntry.COLUMN_RUN_TIME, mTimeView.getText().toString().trim());
        values.put(RunEntry.COLUMN_RUN_DURATION, mDurationView.getText().toString().trim());
        values.put(RunEntry.COLUMN_RUN_DISTANCE,
                Double.parseDouble(mDistanceEditText.getText().toString().trim()));
        values.put(RunEntry.COLUMN_RUN_DISTANCE_UNIT, mDistanceUnit);

        // Insert a new row for pet in the database, returning the ID of that new row.
        long newRowId = db.insert(RunEntry.TABLE_NAME, null, values);

        // Show a toast message depending on whether or not the insertion was successful.
        if (newRowId == -1) {
            // If the row ID is -1, then there was an error with insertion.
            Toast.makeText(this, getString(R.string.run_add_error), Toast.LENGTH_SHORT).show();
        } else {
            // Otherwise, the insertion was successful and display a toast with the row ID.
            Toast.makeText(this, getString(R.string.run_add_success), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Get user input from editor and update the run into database.
     */
    private void updateRuns(int itemId) {
        // Create database helper
        RunDbHelper mDbHelper = new RunDbHelper(this);

        // Gets the database in write mode
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        // Create a ContentValues object where column names are the keys,
        // and pet attributes from the editor are the values.
        ContentValues values = new ContentValues();
        values.put(RunEntry.COLUMN_RUN_DATE, mDateView.getText().toString().trim());
        values.put(RunEntry.COLUMN_RUN_TIME, mTimeView.getText().toString().trim());
        values.put(RunEntry.COLUMN_RUN_DURATION, mDurationView.getText().toString().trim());
        values.put(RunEntry.COLUMN_RUN_DISTANCE,
                Double.parseDouble(mDistanceEditText.getText().toString().trim()));
        values.put(RunEntry.COLUMN_RUN_DISTANCE_UNIT, mDistanceUnit);

        // Set the clause so that it only update the right row of data.
        String selection = RunEntry._ID + " LIKE ?";
        String[] selectionArgs = {String.valueOf(itemId)};

        // Update the row in the database, returning the ID of that updated row.
        long updatedRowId = db.update(RunEntry.TABLE_NAME, values, selection, selectionArgs);

        // Show a toast message depending on whether or not the update was successful.
        if (updatedRowId == -1) {
            // If the row ID is -1, then there was an error with insertion.
            Toast.makeText(this, getString(R.string.run_update_error), Toast.LENGTH_SHORT).show();
        } else {
            // Otherwise, the insertion was successful and display a toast with the row ID.
            Toast.makeText(this, getString(R.string.run_update_success), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save UI state changes to the savedInstanceState.
        // This bundle will be passed to onCreate if the process is
        // killed and restarted.
        savedInstanceState.putBoolean("firstTimeRendering", firstTimeRendering);
        savedInstanceState.putString("dateString", mDateView.getText().toString());
        savedInstanceState.putString("timeString", mTimeView.getText().toString());
        savedInstanceState.putString("durationString", mDurationView.getText().toString());

        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar menu.
        // Respond to a click on the "Save" menu option.
        if (item.getItemId() == R.id.action_save) {

            if (mDistanceEditText.getText().toString().trim().isEmpty()) {
                // If user didn't enter the distance of the run, make a toast to inform them.
                Toast.makeText(this, "Please enter the distance of the run first.",
                        Toast.LENGTH_SHORT).show();
            } else {
                // When user click one of the list item, update the run, instead of adding one.
                Bundle bundle = getIntent().getExtras();

                if (bundle != null) {
                    // Update the run to database.
                    updateRuns(bundle.getInt("itemId"));
                } else {
                    // Save run to database.
                    insertRuns();
                }
                // Exit activity.
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Inner class that implements {@link InputFilter},
     * which help {@link EditText} set the input filter.
     */
    private class EditTextInputFilter implements InputFilter {
        /**
         * Two values that set the minimum and maximum values of the {@link InputFilter}.
         */
        private double mMinValue, mMaxValue;

        /**
         * Constructor of {@link EditTextInputFilter}.
         *
         * @param minValue is the minimum values of the {@link InputFilter}.
         * @param MaxValue is the maximum values of the {@link InputFilter}.
         */
        private EditTextInputFilter(double minValue, double MaxValue) {
            mMinValue = minValue;
            mMaxValue = MaxValue;
        }

        /**
         * This method is called when the buffer is going to replace the range (dstart … dend) of dest
         * with the new text from the range (start … end) of source.
         * <p>
         * Return "" to reject any input that is not in range,
         * or return null to accept the original replacement.
         */
        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            try {
                // Get the input string regardless the input sequence,
                // which means it always get the actual value of the input.
                String inputString = dest.toString().substring(0, dstart) +
                        source.toString().substring(start, end) +
                        dest.toString().substring(dend, dest.toString().length());
                if (!inputString.isEmpty()) {
                    // Parse the input string into double, in order to support decimal number.
                    double inputValue = Double.parseDouble(inputString);
                    // When the input value is in range, return null.
                    if (isInRange(mMinValue, mMaxValue, inputValue)) {
                        return null;
                    }
                }
            } catch (NumberFormatException e) {
                Log.e(LOG_TAG, "Problem parsing the input number ", e);
            }
            // When the input value is out of range, return "".
            return "";
        }

        /**
         * Helper method that tells whether the input value is in range or not.
         *
         * @param minValue   is the minimum value set by the constructor.
         * @param MaxValue   is the maximum value set by the constructor.
         * @param inputValue is the input value that passed in.
         * @return true when the input value is in range, vice versa.
         */
        private boolean isInRange(double minValue, double MaxValue, double inputValue) {
            return MaxValue > minValue && inputValue >= minValue && inputValue <= MaxValue;
        }
    }

    /**
     * Helper method that save {@link Spinner} position to {@link SharedPreferences}.
     *
     * @param spinnerPosition is the position of the distance unit spinner.
     */
    private void saveSpinnerPosition(int spinnerPosition) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("spinnerPosition", spinnerPosition);
        editor.apply();
    }

    /**
     * Helper method that load {@link Spinner} position from {@link SharedPreferences} and set it.
     */
    private void loadSpinnerPosition() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        int position = sharedPreferences.getInt("spinnerPosition", -1);
        // Only when position is valid, set it to the spinner.
        if (position != -1) {
            mDistanceUnitSpinner.setSelection(position);
        }
    }
}
