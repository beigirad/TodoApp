package ir.beigirad.todoapp.Services;

import android.app.IntentService;
import android.content.Intent;

import java.util.ArrayList;

import ir.beigirad.todoapp.Models.ToDoItem;
import ir.beigirad.todoapp.MyApplication;
import ir.beigirad.todoapp.Services.TodoNotificationService;
import ir.beigirad.todoapp.Utils.RealmUtils;
import ir.beigirad.todoapp.Utils.SharedPrefUtil;
import ir.beigirad.todoapp.Utils.Utils;

public class DeleteNotificationService extends IntentService {

    private ArrayList<ToDoItem> mToDoItems;
    private ToDoItem mItem;

    public DeleteNotificationService(){
        super("DeleteNotificationService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String todoID = intent.getStringExtra(TodoNotificationService.TODOUUID);

        RealmUtils mRealmUtils=new RealmUtils();
        mRealmUtils.removeTodo(todoID);

    }

    private void dataChanged() {
        SharedPrefUtil sharedPrefUtil = MyApplication.getPref();
        sharedPrefUtil.setChange(true);
    }




}
