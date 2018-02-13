package com.example.android.trackyourrun.data;

import android.provider.BaseColumns;

/**
 * API Contract for the app.
 */
public final class RunContract {

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    private RunContract() {
    }

    /**
     * Inner class that defines constant values for the runs database table.
     * Each entry in the table represents a single run.
     */
    public static final class RunEntry implements BaseColumns {

        /**
         * Name of database table for pets
         */
        public final static String TABLE_NAME = "runs";

        /**
         * Unique ID number for the run (only for use in the database table).
         * <p>
         * Type: INTEGER
         */
        public final static String _ID = BaseColumns._ID;

        /**
         * Date of the run.
         * <p>
         * Type: TEXT
         */
        public final static String COLUMN_RUN_DATE = "date";

        /**
         * Time of the run.
         * <p>
         * Type: TEXT
         */
        public final static String COLUMN_RUN_TIME = "time";

        /**
         * Duration of the run.
         * <p>
         * Type: TEXT
         */
        public final static String COLUMN_RUN_DURATION = "duration";

        /**
         * Distance of the run.
         * <p>
         * Type: REAL
         */
        public final static String COLUMN_RUN_DISTANCE = "distance";

        /**
         * Distance unit of the run.
         * <p>
         * Type: TEXT
         */
        public final static String COLUMN_RUN_DISTANCE_UNIT = "distance_unit";

        /**
         * Possible values for the duration of the run.
         */
        public static final int DURATION_HALF_HOUR = 0;
        public static final int DURATION_ONE_HOUR = 1;
        public static final int DURATION_TWO_HOUR = 2;
        public static final int DURATION_CUSTOM = 3;

        /**
         * Possible values for the distance unit.
         */
        public static final int DISTANCE_UNIT_KILO = 0;
        public static final int DISTANCE_UNIT_MILE = 1;
    }
}