package ir.beigirad.todoapp.Models;

import android.os.Parcel;
import android.os.Parcelable;

import com.amulyakhare.textdrawable.util.ColorGenerator;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class ToDoItem extends RealmObject {

    @PrimaryKey
    private String mTodoId;
    private String mToDoText;
    private boolean mHasReminder;
    private int mTodoColor;

    private ToDoItem(String mTodoId, String mToDoText, boolean mHasReminder, int mTodoColor, Date mToDoDate) {
        this.mTodoId = mTodoId;
        this.mToDoText = mToDoText;
        this.mHasReminder = mHasReminder;
        this.mTodoColor = mTodoColor;
        this.mToDoDate = mToDoDate;
    }

    private Date mToDoDate;


    public ToDoItem() {
    }

    public ToDoItem(String todoBody, boolean hasReminder, Date toDoDate) {
        mTodoId = UUID.randomUUID().toString();
        mToDoText = todoBody;
        mHasReminder = hasReminder;
        mToDoDate = toDoDate;
        mTodoColor = ColorGenerator.MATERIAL.getRandomColor();
    }

    public ToDoItem(String todoId,String todoBody, boolean hasReminder, Date toDoDate) {
        mTodoId = todoId;
        mToDoText = todoBody;
        mHasReminder = hasReminder;
        mToDoDate = toDoDate;
        mTodoColor = ColorGenerator.MATERIAL.getRandomColor();
    }

    public ToDoItem(String todoId,String todoBody, boolean hasReminder, Date toDoDate,int color) {
        mTodoId = todoId;
        mToDoText = todoBody;
        mHasReminder = hasReminder;
        mToDoDate = toDoDate;
        mTodoColor = color;
    }


    public String getToDoText() {
        return mToDoText;
    }

//    public void setToDoText(String mToDoText) {
//        this.mToDoText = mToDoText;
//    }

    public boolean hasReminder() {
        return mHasReminder;
    }

//    public void setHasReminder(boolean mHasReminder) {
//        this.mHasReminder = mHasReminder;
//    }

    public Date getToDoDate() {
        return mToDoDate;
    }

    public int getTodoColor() {
        return mTodoColor;
    }

//    public void setTodoColor(int mTodoColor) {
//        this.mTodoColor = mTodoColor;
//    }

//    public void setToDoDate(Date mToDoDate) {
//        this.mToDoDate = mToDoDate;
//    }

    public String getTodoId() {
        return mTodoId;
    }

//    public void setTodoId(String todoId){
//        mTodoId=todoId;
//    }

    public static class Builder {
        public Builder setmTodoId(String mTodoId) {
            this.mTodoId = mTodoId;
            return this;
        }

        public Builder setmToDoText(String mToDoText) {
            this.mToDoText = mToDoText;
            return this;
        }

        public Builder setmHasReminder(boolean mHasReminder) {
            this.mHasReminder = mHasReminder;
            return this;
        }

        public Builder setmTodoColor(int mTodoColor) {
            this.mTodoColor = mTodoColor;
            return this;
        }

        public Builder setmToDoDate(Date mToDoDate) {
            this.mToDoDate = mToDoDate;
            return this;
        }

        public ToDoItem build() {
            return new ToDoItem(mTodoId,mToDoText,mHasReminder,mTodoColor,mToDoDate);
        }

        private String mTodoId;
        private String mToDoText;
        private boolean mHasReminder;
        private int mTodoColor;
        private Date mToDoDate;


    }

}

