package ir.beigirad.todoapp.Activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;

import fr.ganfra.materialspinner.MaterialSpinner;
import ir.beigirad.todoapp.MyApplication;
import ir.beigirad.todoapp.Utils.Constants;
import ir.beigirad.todoapp.R;
import ir.beigirad.todoapp.Utils.RealmUtils;
import ir.beigirad.todoapp.Utils.SharedPrefUtil;
import ir.beigirad.todoapp.Models.ToDoItem;
import ir.beigirad.todoapp.Services.TodoNotificationService;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ReminderActivity extends AppCompatActivity {
    private TextView mtoDoTextTextView;
    private Button mRemoveToDoButton;
    private MaterialSpinner mSnoozeSpinner;
    private String[] snoozeOptionsArray;
    private ToDoItem mItem;
    public static final String EXIT = "ir.beigirad.exit";
    private TextView mSnoozeTextView;
    MyApplication app;

    RealmUtils realmUtils;
    SharedPrefUtil sharedPrefUtil;
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        app = (MyApplication) getApplication();
        app.send(this);

        if (MyApplication.getPref().getTheme().equals(Constants.Theme.LightTheme)) {
            setTheme(R.style.CustomStyle_LightTheme);
        } else {
            setTheme(R.style.CustomStyle_DarkTheme);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.reminder_layout);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        realmUtils =new RealmUtils();
        sharedPrefUtil=app.getPref();

        Intent i = getIntent();
        String id =  i.getStringExtra(TodoNotificationService.TODOUUID);


        mItem=realmUtils.getTodo(id);

        snoozeOptionsArray = getResources().getStringArray(R.array.snooze_options);
        mRemoveToDoButton = (Button) findViewById(R.id.toDoReminderRemoveButton);
        mtoDoTextTextView = (TextView) findViewById(R.id.toDoReminderTextViewBody);
        mSnoozeTextView = (TextView) findViewById(R.id.reminderViewSnoozeTextView);
        mSnoozeSpinner = (MaterialSpinner) findViewById(R.id.todoReminderSnoozeSpinner);

//        mtoDoTextTextView.setBackgroundColor(item.getTodoColor());
        mtoDoTextTextView.setText(mItem.getToDoText());

        if (sharedPrefUtil.getTheme().equals(Constants.Theme.LightTheme)) {
            mSnoozeTextView.setTextColor(getResources().getColor(R.color.secondary_text));
        } else {
            mSnoozeTextView.setTextColor(Color.WHITE);
            mSnoozeTextView.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_snooze_white_24dp, 0, 0, 0
            );
        }

        mRemoveToDoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                app.send("Removed Todo", "Todo Removed from Reminder Activity");
                realmUtils.removeTodo(mItem);
                sharedPrefUtil.setChange(true);
                closeApp();
//                finish();
            }
        });


//        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, snoozeOptionsArray);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_text_view, snoozeOptionsArray);
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);

        mSnoozeSpinner.setAdapter(adapter);
//        mSnoozeSpinner.setSelection(0);

    }

    private void closeApp() {
        finish();
//        Intent i = new Intent(ReminderActivity.this, MainActivity.class);
//        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
////        i.putExtra(EXIT, true);
////        SharedPreferences sharedPreferences = getSharedPreferences(MainActivity.SHARED_PREF_DATA_SET_CHANGED, MODE_PRIVATE);
////        SharedPreferences.Editor editor = sharedPreferences.edit();
////        editor.putBoolean(EXIT, true);
////        editor.apply();
//        startActivity(i);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_reminder, menu);
        return true;
    }

    private void changeOccurred() {
        sharedPrefUtil.setChange(true);
    }

    private Date addTimeToDate(int mins) {
        app.send("Snooze","For " + mins + " minutes");
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MINUTE, mins);
        return calendar.getTime();
    }

    private int valueFromSpinner() {
        switch (mSnoozeSpinner.getSelectedItemPosition()) {
            case 0:
                return 10;
            case 1:
                return 30;
            case 2:
                return 60;
            default:
                return 0;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.toDoReminderDoneMenuItem:
                Date date = addTimeToDate(valueFromSpinner());

                ToDoItem.Builder builder=new ToDoItem.Builder();
                builder.setmTodoId(mItem.getTodoId())
                        .setmToDoText(mItem.getToDoText())
                        .setmTodoColor(mItem.getTodoColor())
                        .setmToDoDate(date)
                        .setmHasReminder(true);

                sharedPrefUtil.setChange(true);
                realmUtils.addTodo(builder.build());
                closeApp();
                //foo
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
