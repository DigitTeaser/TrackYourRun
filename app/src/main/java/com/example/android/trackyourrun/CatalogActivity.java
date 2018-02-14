package com.example.android.trackyourrun;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.SpannableStringBuilder;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.android.trackyourrun.data.RunContract.RunEntry;
import com.example.android.trackyourrun.data.RunDbHelper;

import java.util.ArrayList;
import java.util.List;

public class CatalogActivity extends AppCompatActivity
        implements RecyclerItemTouchHelper.RecyclerItemTouchHelperListener {

    /**
     * {@link TextView} that display the empty state when the list is empty.
     */
    private TextView mEmptyView;

    /**
     * A list of runs.
     */
    private List<Run> mRunsList;

    /**
     * {@link RecyclerView} that displays the list of runs.
     */
    private RecyclerView mRecyclerView;

    /**
     * Adapter for the list of runs.
     */
    private RunAdapter mAdapter;

    /**
     * Database helper that will provide us access to the database
     */
    private RunDbHelper mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        // Find empty view and set the onClick listener to start editor activity.
        mEmptyView = findViewById(R.id.empty_view);
        mEmptyView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open CatalogActivity to add a run.
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        // To access the database, instantiate our subclass of SQLiteOpenHelper
        // and pass the context, which is the current activity.
        mDbHelper = new RunDbHelper(this);

        // Find a reference to RecyclerView in the layout.
        mRecyclerView = findViewById(R.id.list);
        // Create an RunAdapter object, whose data source is a list of Run object.
        // The adapter knows how to create list items for each item in the list.
        mAdapter = new RunAdapter(this, new ArrayList<Run>());
        // Setup the DefaultItemAnimator for the ItemAnimator of RecyclerView.
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        // Setup the LinearLayoutManager for the LayoutManager of RecyclerView.
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        // Make RecycleView} use RunAdapter created above.
        mRecyclerView.setAdapter(mAdapter);
        // Add a divider between items in RecyclerView, using DividerItemDecoration.
        mRecyclerView.addItemDecoration(
                new DividerItemDecoration(mRecyclerView.getContext(), layoutManager.getOrientation()));
        // Setup an OnItemClickListener to handle the click event of the RecyclerView item.
        mAdapter.setOnItemClickListener(new RunAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
            }
        });
        // Add item touch helper.
        // Only ItemTouchHelper.LEFT added to detect Right to Left swipe.
        ItemTouchHelper.SimpleCallback itemTouchHelperCallback =
                new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(mRecyclerView);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Display all database data in RecyclerView.
        displayDatabaseInfo();
    }

    /**
     * Helper method to display information in the onscreen TextView about the state of
     * the pets database.
     */
    private void displayDatabaseInfo() {
        // Create and/or open a database to read from it.
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                RunEntry._ID,
                RunEntry.COLUMN_RUN_DATE,
                RunEntry.COLUMN_RUN_TIME,
                RunEntry.COLUMN_RUN_DURATION,
                RunEntry.COLUMN_RUN_DISTANCE,
                RunEntry.COLUMN_RUN_DISTANCE_UNIT};

        // Perform a query on the pets table
        Cursor cursor = db.query(
                RunEntry.TABLE_NAME,   // The table to query
                projection,            // The columns to return
                null,                  // The columns for the WHERE clause
                null,                  // The values for the WHERE clause
                null,                  // Don't group the rows
                null,                  // Don't filter by row groups
                null);                   // The sort order

        try {
            mRunsList = new ArrayList<>();

            // Figure out the index of each column.
            int idColumnIndex = cursor.getColumnIndex(RunEntry._ID);
            int dateColumnIndex = cursor.getColumnIndex(RunEntry.COLUMN_RUN_DATE);
            int timeColumnIndex = cursor.getColumnIndex(RunEntry.COLUMN_RUN_TIME);
            int durationColumnIndex = cursor.getColumnIndex(RunEntry.COLUMN_RUN_DURATION);
            int distanceColumnIndex = cursor.getColumnIndex(RunEntry.COLUMN_RUN_DISTANCE);
            int distanceUnitColumnIndex = cursor.getColumnIndex(RunEntry.COLUMN_RUN_DISTANCE_UNIT);

            // Iterate through all the returned rows in the cursor
            while (cursor.moveToNext()) {
                // Use that index to extract the String or Int value of the word
                // at the current row the cursor is on.
                int currentID = cursor.getInt(idColumnIndex);
                String currentDate = cursor.getString(dateColumnIndex);
                String currentTime = cursor.getString(timeColumnIndex);
                String currentDuration = cursor.getString(durationColumnIndex);
                Double currentDistance = cursor.getDouble(distanceColumnIndex);
                String currentDistanceUnit = cursor.getString(distanceUnitColumnIndex);

                // Save the data into Run object and add them to ArrayList.
                Run run = new Run(currentID, currentDate, currentTime, currentDuration,
                        currentDistance, currentDistanceUnit);
                mRunsList.add(run);
            }

            // If there is a valid list of runs, add them to the adapter's data set.
            if (!mRunsList.isEmpty()) {
                // After clear the adapter of previous data, add the list to RecyclerView.
                mAdapter.clear();
                mAdapter.addAll(mRunsList);

                mEmptyView.setVisibility(View.GONE);
            } else {
                // Display empty view if there is no item in the list.
                mEmptyView.setVisibility(View.VISIBLE);
            }
        } finally {
            // Always close the cursor when you're done reading from it. This releases all its
            // resources and makes it invalid.
            cursor.close();
        }
    }

    /**
     * Helper method that delete one row or all rows in database.
     *
     * @param itemId is the ID of which row should be deleted,
     *               pass in null if delete all rows.
     */
    private void deleteRuns(@Nullable Integer itemId) {
        // Create and/or open a database to write to it.
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        if (itemId == null) {
            // Delete one row in the database.
            db.delete(RunEntry.TABLE_NAME, null, null);
        } else {
            // Set the clause that tells which row to delete.
            String selection = RunEntry._ID + " LIKE ?";
            String[] selectionArgs = {String.valueOf(itemId)};

            // Delete one row in the database.
            db.delete(RunEntry.TABLE_NAME, selection, selectionArgs);
        }
    }

    /**
     * Because this activity implements {@link RecyclerItemTouchHelper.RecyclerItemTouchHelperListener},
     * therefore, override onSwiped method here to handle the swipe gesture.
     */
    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof RunAdapter.MyViewHolder) {
            // Backup of removed item for undo purpose.
            final Run deletedItem = mRunsList.get(viewHolder.getAdapterPosition());
            final int deletedIndex = viewHolder.getAdapterPosition();

            // Remove the item from recycler view.
            mAdapter.removeItem(viewHolder.getAdapterPosition());

            // Showing snack bar with Undo option.
            Snackbar.make(findViewById(R.id.catalog_container), getString(R.string.run_delete), Snackbar.LENGTH_LONG)
                    .setAction(R.string.action_undo, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Restore the deleted item.
                            mAdapter.restoreItem(deletedItem, deletedIndex);
                            // If the deleted item index is 0, scroll to the top of the list.
                            if (deletedIndex == 0) {
                                mRecyclerView.scrollToPosition(0);
                            }
                        }
                    })
                    .addCallback(new Snackbar.Callback() {
                        @Override
                        public void onDismissed(Snackbar transientBottomBar, int event) {
                            if (event != Snackbar.Callback.DISMISS_EVENT_ACTION) {
                                // When snack bar closed normally, delete the item from database.
                                deleteRuns(deletedItem.getId());

                                if (event == Snackbar.Callback.DISMISS_EVENT_TIMEOUT &&
                                        mAdapter.getItemCount() == 0) {
                                    // If all items are deleted, delete the last item in database,
                                    // and show empty view tell user what to do next.
                                    deleteRuns(null);
                                    mEmptyView.setVisibility(View.VISIBLE);
                                }
                            }
                        }
                    })
                    .show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar menu.
        switch (item.getItemId()) {
            // Respond to a click on the "Insert data" menu option.
            case R.id.action_insert_data:
                // Open CatalogActivity to add a run.
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                startActivity(intent);
                return true;
            // Respond to a click on the "Delete all entries" menu option.
            case R.id.action_delete_all_entries:
                // Setup the deletion warning message view.
                TextView warningText = new TextView(this);
                // Make the "Warning" text bold.
                SpannableStringBuilder stringBuilder =
                        new SpannableStringBuilder(getString(R.string.deletion_warning));
                stringBuilder.setSpan(
                        new android.text.style.StyleSpan(android.graphics.Typeface.BOLD),
                        0, 8, SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE);
                warningText.setText(stringBuilder);
                warningText.setTextColor(getResources().getColor(android.R.color.black));
                warningText.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                        getResources().getDimensionPixelOffset(R.dimen.dialog_message_text_size));
                warningText.setPadding(
                        getResources().getDimensionPixelOffset(R.dimen.dialog_spacing),
                        getResources().getDimensionPixelOffset(R.dimen.dialog_spacing),
                        getResources().getDimensionPixelOffset(R.dimen.dialog_spacing),
                        getResources().getDimensionPixelOffset(R.dimen.dialog_spacing));
                warningText.setGravity(Gravity.CENTER_VERTICAL);
                // Popup a dialog that let user confirm the deletion.
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.confirm_deletion).setView(warningText)
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                // Delete all rows in the database.
                                deleteRuns(null);
                                // Clear the list and display empty view.
                                mAdapter.clear();
                                mEmptyView.setVisibility(View.VISIBLE);
                            }
                        }).setNegativeButton(android.R.string.cancel, null).create().show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
