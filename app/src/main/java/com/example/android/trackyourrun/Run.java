package com.example.android.trackyourrun;

/**
 * A {@link Run} object contains information related to a run.
 * It includes that the date, time, duration and distance with length unit.
 */
public class Run {

    /**
     * ID of the run.
     */
    private int mId;

    /**
     * Date of the run.
     */
    private String mDate;

    /**
     * Time of the run.
     */
    private String mTime;

    /**
     * Duration of the run.
     */
    private String mDuration;

    /**
     * Distance of the run.
     */
    private double mDistance;

    /**
     * Distance unit of the run.
     */
    private String mDistanceUnit;

    /**
     * Create a new Run object.
     *
     * @param id           is the ID of the run.
     * @param date         is the date of the run.
     * @param time         is the time of the run.
     * @param duration     is the duration of the run.
     * @param distance     is the distance of the run.
     * @param distanceUnit is the distance unit of the run.
     */
    public Run(int id, String date, String time, String duration,
               double distance, String distanceUnit) {
        mId = id;
        mDate = date;
        mTime = time;
        mDuration = duration;
        mDistance = distance;
        mDistanceUnit = distanceUnit;
    }

    /**
     * Return the ID of the run.
     */
    public int getId() {
        return mId;
    }

    /**
     * Return the date of the run.
     */
    public String getDate() {
        return mDate;
    }

    /**
     * Return the time of the run.
     */
    public String getTime() {
        return mTime;
    }

    /**
     * Return the duration of the run.
     */
    public String getDuration() {
        return mDuration;
    }

    /**
     * Return the distance of the run.
     */
    public double getDistance() {
        return mDistance;
    }

    /**
     * Return the distance unit of the run.
     */
    public String getDistanceUnit() {
        return mDistanceUnit;
    }
}
