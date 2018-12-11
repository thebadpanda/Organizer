package com.organizer;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class TaskAdapter extends ArrayAdapter<TaskItem> {
    private ArrayList<TaskItem> dataSet;
    private SQLiteDatabase sqlDB;
    private FragmentManager fm;

    private Context mContext;

    private static class ViewHolder {
        TextView taskName;
        ImageView delIcon;
        CheckBox cbox;
        TextView dateView;
    }

    TaskAdapter(ArrayList<TaskItem> data, Context context, SQLiteDatabase sqlDB, FragmentManager fm) {
        super(context, R.layout.task_list_row_item, data);
        this.dataSet = data;
        this.mContext=context;
        this.sqlDB=sqlDB;
        this.fm=fm;
    }

    private int lastPosition = -1;
    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        final TaskItem dataModel = getItem(position);
        final ViewHolder viewHolder;
        final View result;

        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.task_list_row_item, parent, false);
            viewHolder.taskName = convertView.findViewById(R.id.txtView);
            viewHolder.delIcon = convertView.findViewById(R.id.delIcon);
            viewHolder.cbox = convertView.findViewById(R.id.chckBox);
            result=convertView;

            convertView.setTag(viewHolder); //not used
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }

        Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        result.startAnimation(animation);
        lastPosition = position;

        viewHolder.taskName.setText(dataModel.getTask());
        viewHolder.cbox.setChecked(dataModel.getStatus());

        viewHolder.cbox.setOnClickListener(view -> {
            if(viewHolder.cbox.isChecked()){
                dataModel.setStatus(true);
                sqlDB.execSQL("UPDATE ToDoList SET Status='true' WHERE Task='"+dataModel.getTask()+"'");
                int[] ids = AppWidgetManager.getInstance(getContext()).getAppWidgetIds(new ComponentName(getContext(), WidgetHandler.class));
                Intent intent = new Intent(getContext(), WidgetHandler.class);
                intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
                intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
                getContext().sendBroadcast(intent);
            }
            else{
                dataModel.setStatus(false);
                sqlDB.execSQL("UPDATE ToDoList SET Status='false' WHERE Task='"+dataModel.getTask()+"'");
                int[] ids = AppWidgetManager.getInstance(getContext()).getAppWidgetIds(new ComponentName(getContext(), WidgetHandler.class));
                Intent intent = new Intent(getContext(), WidgetHandler.class);
                intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
                intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
                getContext().sendBroadcast(intent);
            }
        });

        viewHolder.delIcon.setOnClickListener(view -> {
            final int positionOfTaskInList=dataSet.indexOf(dataModel);
            final TaskItem delElem=dataSet.remove(positionOfTaskInList);
            notifyDataSetChanged();
            sqlDB.execSQL("DELETE FROM ToDoList WHERE Task='"+delElem.getTask()+"'");
            int[] ids = AppWidgetManager.getInstance(getContext()).getAppWidgetIds(new ComponentName(getContext(), WidgetHandler.class));
            Intent intent = new Intent(getContext(), WidgetHandler.class);
            intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
            getContext().sendBroadcast(intent);

            final Snackbar deleteSB=Snackbar.make(view,"Task deleted",Snackbar.LENGTH_LONG);
            deleteSB.setAction("UNDO", v -> {
                dataSet.add(positionOfTaskInList,delElem);
                notifyDataSetChanged();
                sqlDB.execSQL("INSERT INTO ToDoList VALUES('"+delElem.getTask()+"','"+delElem.getStatus()+"','')");
                intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
                intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
                getContext().sendBroadcast(intent);
            });
            deleteSB.setActionTextColor(getContext().getResources().getColor(R.color.colorAccent));
            deleteSB.show();
        });
        return convertView;
    }
}

