package com.organizer;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class TaskItem implements Parcelable{
    private String Task;
    private boolean status;
    private String deadlineDate;

    TaskItem(String Task,boolean status,String deadlineDate){
        this.Task=Task;
        this.status=status;
        this.deadlineDate=deadlineDate;
    }

    private TaskItem(Parcel in) {
        Task = in.readString();
        status = in.readByte() != 0;
        deadlineDate = in.readString();
    }

    public static final Creator<TaskItem> CREATOR = new Creator<TaskItem>() {
        @Override
        public TaskItem createFromParcel(Parcel in) {
            return new TaskItem(in);
        }

        @Override
        public TaskItem[] newArray(int size) {
            return new TaskItem[size];
        }
    };

    String getTask() {
        return Task;
    }

    boolean getStatus() {
        return status;
    }

    void setStatus(boolean status) {
        this.status = status;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeStringArray(new String[] {this.Task,
                String.valueOf(this.status),
                this.deadlineDate});
    }

    public static int findTaskPosition(ArrayList<TaskItem> mainList, String taskName){
        for(int i=0;i<mainList.size();i++){
            if(mainList.get(i).getTask()==taskName){
                return i;
            }
        }
        return -1;
    }
}
