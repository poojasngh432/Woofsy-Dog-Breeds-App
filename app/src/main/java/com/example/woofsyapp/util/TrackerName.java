package com.example.woofsyapp.util;

/**
 * Enum used to identify the tracker that needs to be used for tracking.
 * <p>
 * A single tracker is usually enough for most purposes. In case you do need multiple trackers,
 * storing them all in Application object helps ensure that they are created only once per
 * application instance.
 */
public enum TrackerName {
    APP_TRACKER // Tracker used only in this app.
}
