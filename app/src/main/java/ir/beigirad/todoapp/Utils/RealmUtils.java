package ir.beigirad.todoapp.Utils;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import ir.beigirad.todoapp.MyApplication;
import ir.beigirad.todoapp.Models.ToDoItem;

/**
 * Created by farhad-mbp on 6/13/17.
 */

public class RealmUtils {
    private static Realm mRealm;

    public static void addTodo(ToDoItem item) {
        mRealm= MyApplication.getRealm();

        Log.i("realm count before : ",""+mRealm.where(ToDoItem.class).findAll().size());

        mRealm.beginTransaction();
        mRealm.copyToRealmOrUpdate(item);
        mRealm.commitTransaction();

        Log.i("realm count after : ",""+mRealm.where(ToDoItem.class).findAll().size());

    }

    public static void addAllTodo(List<ToDoItem> items) {
        Log.i("zzzz count before : ",""+mRealm.where(ToDoItem.class).findAll().size()+" | "+items.size());

        mRealm.beginTransaction();
        mRealm.where(ToDoItem.class).findAll().deleteAllFromRealm();
        mRealm.commitTransaction();

        Log.i("zzzz after clear: ",""+mRealm.where(ToDoItem.class).findAll().size()+" | "+items.size());

        mRealm.beginTransaction();
            mRealm.copyToRealm(items);

        mRealm.commitTransaction();

        Log.i("zzzzz count after : ",""+mRealm.where(ToDoItem.class).findAll().size());

    }

    public static ArrayList<ToDoItem> getLocallyStoredData() {
        mRealm= MyApplication.getRealm();
        ArrayList<ToDoItem> items = new ArrayList<>();

        RealmResults<ToDoItem> list = MyApplication.getRealm().where(ToDoItem.class).findAll();
        for (ToDoItem i : list) {
            items.add(i);
        }
        return items;

    }

    public static ToDoItem getTodo(String id){
        mRealm= MyApplication.getRealm();
        return mRealm.where(ToDoItem.class).equalTo("mTodoId",id).findFirst();
    }

    public static void removeTodo(String id){
        mRealm= MyApplication.getRealm();
        ToDoItem todoId = mRealm.where(ToDoItem.class).equalTo("mTodoId", id).findFirst();
        mRealm.beginTransaction();
        todoId.deleteFromRealm();
        mRealm.commitTransaction();
    }

    public void removeTodo(ToDoItem item){
        mRealm= MyApplication.getRealm();
        mRealm.beginTransaction();
        item.deleteFromRealm();
        mRealm.commitTransaction();
    }
}
