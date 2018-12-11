package com.organizer;//package com.sk.tdlist;
//
//
//import android.content.ContentResolver;
//import android.content.ContentUris;
//import android.content.ContentValues;
//import android.content.Context;
//import android.database.Cursor;
//import android.net.Uri;
//import android.provider.CalendarContract;
//import android.util.Log;
//
//import java.util.TimeZone;
//
//public class RealCalendarModule {
//    private final static int CALENDAR_ID = 1;
//    public final static String description = "This event has been added via TDList App";
//    public final static long REMINDER_TIME = 6 * 60;
//
//    public static void addEvent(Context context, String title, long startTimeMillis) {
//        ContentValues event = new ContentValues();
//        event.put(CalendarContract.Events.CALENDAR_ID, CALENDAR_ID);
//
//        event.put(CalendarContract.Events.TITLE, title);
//        event.put(CalendarContract.Events.DESCRIPTION, description);
//
//        event.put(CalendarContract.Events.DTSTART, startTimeMillis);
//        event.put(CalendarContract.Events.DTEND, startTimeMillis + 36000000);
//        event.put(CalendarContract.Events.ALL_DAY, 1);   // 0 for false, 1 for true
//        event.put(CalendarContract.Events.HAS_ALARM, 1); // 0 for false, 1 for true
//
//        Log.d("RealCalendarModule", "startTimeMillis: " + startTimeMillis);
//
//        String timeZone = TimeZone.getTimeZone("UTC").getID();
//        event.put(CalendarContract.Events.EVENT_TIMEZONE, timeZone);
//
//
//        String baseUriString = "content://calendar/";
//
//        Uri baseUri;
//        baseUri = Uri.parse(baseUriString + "events");
//
//        Uri eventAdded = context.getContentResolver().insert(baseUri, event);    //This statement adds the event to calendar without reminder
//
//        Uri REMINDERS_URI = Uri.parse(baseUriString + "reminders");
//        event = new ContentValues();
//        event.put("event_id", Long.parseLong(eventAdded.getLastPathSegment()));
//        event.put("method", 1);
//        event.put("minutes", REMINDER_TIME);
//        context.getContentResolver().insert(REMINDERS_URI, event);   //This statement adds the reminder part
//
//        Log.d("TDCustomRealCalendar", "Calendar Event added");
//    }
//
//    static int deleteEvent(ContentResolver resolver, String eventTitle) {
//        Uri eventsUri;
//        Cursor cursor;
//        int noOfEventsDeleted = 0;
//
//        /**
//         * Following are the columns that can be used in the selection part
//         * String[] COLS={"calendar_id", "title", "description", "dtstart", "dtend","eventTimezone", "eventLocation"};
//         */
//
//        eventsUri = Uri.parse("content://com.android.calendar/events");
//        cursor = resolver.query(eventsUri, new String[]{"_id"}, "calendar_id=" + CALENDAR_ID + " and title='" + eventTitle + "' and description='" + description + "'", null, null);
//
//        while (cursor.moveToNext()) {
//            long eventId = cursor.getLong(cursor.getColumnIndex("_id"));
//            noOfEventsDeleted += resolver.delete(ContentUris.withAppendedId(eventsUri, eventId), null, null);
//            Log.d("RealCalendarModule", "title=" + eventTitle + ", eventId" + eventId);
//        }
//        cursor.close();
//        return noOfEventsDeleted;
//
//    }
//}
