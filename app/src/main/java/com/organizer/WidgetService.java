package com.organizer;

import android.content.Intent;
import android.widget.RemoteViewsService;

public class WidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        TaskViewsFactory taskViewsFactory = new TaskViewsFactory(getApplicationContext(), intent);
        return taskViewsFactory;
    }
}
