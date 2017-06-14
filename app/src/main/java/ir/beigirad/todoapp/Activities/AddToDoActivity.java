package ir.beigirad.todoapp.Activities;

import android.animation.Animator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import ir.beigirad.todoapp.MyApplication;
import ir.beigirad.todoapp.Services.TodoNotificationService;
import ir.beigirad.todoapp.Utils.AlarmUtil;
import ir.beigirad.todoapp.Utils.Constants;
import ir.beigirad.todoapp.R;
import ir.beigirad.todoapp.Models.ToDoItem;
import ir.beigirad.todoapp.Utils.RealmUtils;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by farhad-mbp on 6/13/17.
 */

public class AddToDoActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    //  Widgets
    private EditText mToDoTextBodyEditText;
    private SwitchCompat mToDoDateSwitch;
    private LinearLayout mUserDateSpinnerContainingLinearLayout;
    private TextView mReminderTextView;
    private Toolbar mToolbar;
    private FloatingActionButton mToDoSendFloatingActionButton;
    private LinearLayout mContainerLayout;
    private EditText mDateEditText;
    private EditText mTimeEditText;
    protected ImageButton reminderIconImageButton;
    private TextView reminderRemindMeTextView;


    //  Variables
    private String mUserEnteredText;
    private boolean mUserHasReminder;
    private Date mUserReminderDate;
    private int mUserColor;
    private String mUserId;

    private ToDoItem mUserToDoItem;

    private MyApplication app;
    private ToDoItem.Builder builder;
    private String TAG = this.getClass().getSimpleName();


    private void initUI() {
        //Show an X in place of <-
        final Drawable cross = getResources().getDrawable(R.drawable.ic_clear_white_24dp);
        if (cross != null) {
            cross.setColorFilter(getResources().getColor(R.color.icons), PorterDuff.Mode.SRC_ATOP);
        }

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setElevation(0);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(cross);

        }

        mContainerLayout = (LinearLayout) findViewById(R.id.todoReminderAndDateContainerLayout);
        mUserDateSpinnerContainingLinearLayout = (LinearLayout) findViewById(R.id.toDoEnterDateLinearLayout);
        mToDoTextBodyEditText = (EditText) findViewById(R.id.userToDoEditText);
        mToDoDateSwitch = (SwitchCompat) findViewById(R.id.toDoHasDateSwitchCompat);
        mToDoSendFloatingActionButton = (FloatingActionButton) findViewById(R.id.makeToDoFloatingActionButton);
        mReminderTextView = (TextView) findViewById(R.id.newToDoDateTimeReminderTextView);
        reminderIconImageButton = (ImageButton) findViewById(R.id.userToDoReminderIconImageButton);
        reminderRemindMeTextView = (TextView) findViewById(R.id.userToDoRemindMeTextView);
        mDateEditText = (EditText) findViewById(R.id.newTodoDateEditText);
        mTimeEditText = (EditText) findViewById(R.id.newTodoTimeEditText);


        if (MyApplication.getPref().getTheme().equals(Constants.Theme.DarkTheme)) {
            reminderIconImageButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_alarm_add_white_24dp));
            reminderRemindMeTextView.setTextColor(Color.WHITE);
        }

        mContainerLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard(mToDoTextBodyEditText);
            }
        });


        if (mUserHasReminder && (mUserReminderDate != null)) {
            setReminderTextView();
            setEnterDateLayoutVisibleWithAnimations(true);
        }
        if (mUserReminderDate == null) {
            mToDoDateSwitch.setChecked(false);
            mReminderTextView.setVisibility(View.INVISIBLE);
        }

        mToDoTextBodyEditText.requestFocus();
        mToDoTextBodyEditText.setText(mUserEnteredText);

        InputMethodManager imm = (InputMethodManager) this.getSystemService(INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        mToDoTextBodyEditText.setSelection(mToDoTextBodyEditText.length());


        mToDoTextBodyEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mUserEnteredText = s.toString();

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        setEnterDateLayoutVisible(mToDoDateSwitch.isChecked());

        mToDoDateSwitch.setChecked(mUserHasReminder && (mUserReminderDate != null));
        mToDoDateSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) {
                    mUserReminderDate = null;
                }
                mUserHasReminder = isChecked;
                setDateAndTimeEditText();
                setEnterDateLayoutVisibleWithAnimations(isChecked);
                hideKeyboard(mToDoTextBodyEditText);
            }
        });


        mToDoSendFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mToDoTextBodyEditText.length() <= 0) {
                    mToDoTextBodyEditText.setError(getString(R.string.todo_error));
                } else if (mUserReminderDate != null && mUserReminderDate.before(new Date())) {
                    makeResult(RESULT_CANCELED);
                } else {
                    addToDB();
                    makeResult(RESULT_OK);
                    finish();
                }
                hideKeyboard(mToDoTextBodyEditText);
            }
        });


        mDateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Date date = new Date();
                hideKeyboard(mToDoTextBodyEditText);
                if (mUserToDoItem != null) {
                    if (mUserToDoItem.getToDoDate() != null)
                        date = mUserReminderDate;
                    else
                        date = new Date();
                } else {
                    date = new Date();
                }
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);


                DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(AddToDoActivity.this, year, month, day);
                if (MyApplication.getPref().getTheme().equals(Constants.Theme.DarkTheme)) {
                    datePickerDialog.setThemeDark(true);
                }
                datePickerDialog.show(getFragmentManager(), "DateFragment");

            }
        });


        mTimeEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Date date;
                hideKeyboard(mToDoTextBodyEditText);
                if (mUserToDoItem != null) {
                    if (mUserToDoItem.getToDoDate() != null)
                        date = mUserReminderDate;
                    else
                        date = new Date();
                } else {
                    date = new Date();
                }
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = TimePickerDialog.newInstance(AddToDoActivity.this, hour, minute, DateFormat.is24HourFormat(AddToDoActivity.this));
                if (MyApplication.getPref().getTheme().equals(Constants.Theme.DarkTheme)) {
                    timePickerDialog.setThemeDark(true);
                }
                timePickerDialog.show(getFragmentManager(), "TimeFragment");
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        app = (MyApplication) getApplication();


        if (MyApplication.getPref().getTheme().equals(Constants.Theme.LightTheme)) {
            setTheme(R.style.CustomStyle_LightTheme);
        } else {
            setTheme(R.style.CustomStyle_DarkTheme);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo_test);

        initUI();


        if (getIntent().getExtras() != null) {
            String itemId = getIntent().getStringExtra(Constants.TODOITEM_ID);

            mUserToDoItem = RealmUtils.getTodo(itemId);

            mUserEnteredText = mUserToDoItem.getToDoText();
            mUserHasReminder = mUserToDoItem.hasReminder();
            mUserReminderDate = mUserToDoItem.getToDoDate();
            mUserColor = mUserToDoItem.getTodoColor();
            mUserId = mUserToDoItem.getTodoId();

        } else {
            mUserToDoItem = new ToDoItem("", false, null);
        }

        setTitleAndSwich();
        setDateAndTimeEditText();


    }

    private void addToDB() {
        if (mUserId == null)
            mUserToDoItem = new ToDoItem(mUserEnteredText, mUserHasReminder, mUserReminderDate);
        else
            mUserToDoItem = new ToDoItem(mUserId, mUserEnteredText, mUserHasReminder, mUserReminderDate);

//        Intent i = new Intent(AddToDoActivity.this, TodoNotificationService.class);
//        i.putExtra(TodoNotificationService.TODOTEXT, mUserToDoItem.getToDoText());
//        i.putExtra(TodoNotificationService.TODOUUID, mUserToDoItem.getTodoId());
//        new AlarmUtil(this).createAlarm(i, mUserToDoItem.getTodoId().hashCode(), mUserToDoItem.getToDoDate().getTime());
        RealmUtils.addTodo(mUserToDoItem);

    }

    private void setTitleAndSwich() {
            if (mUserToDoItem.getToDoText().length() > 0) {
                mToDoTextBodyEditText.setText(mUserToDoItem.getToDoText());
            }


            mToDoDateSwitch.setChecked(mUserToDoItem.hasReminder());

    }

    private void setDateAndTimeEditText() {

        if (mUserToDoItem.hasReminder() && mUserReminderDate != null) {
            String userDate = formatDate("d MMM, yyyy", mUserReminderDate);
            String formatToUse;
            if (DateFormat.is24HourFormat(this)) {
                formatToUse = "k:mm";
            } else {
                formatToUse = "h:mm a";

            }
            String userTime = formatDate(formatToUse, mUserReminderDate);
            mTimeEditText.setText(userTime);
            mDateEditText.setText(userDate);


        } else {
            mDateEditText.setText(getString(R.string.date_reminder_default));
            boolean time24 = DateFormat.is24HourFormat(this);
            Calendar cal = Calendar.getInstance();
            if (time24) {
                cal.set(Calendar.HOUR_OF_DAY, cal.get(Calendar.HOUR_OF_DAY) + 1);
            } else {
                cal.set(Calendar.HOUR, cal.get(Calendar.HOUR) + 1);
            }
            cal.set(Calendar.MINUTE, 0);
            mUserReminderDate = cal.getTime();
            String timeString;
            if (time24) {
                timeString = formatDate("k:mm", mUserReminderDate);
            } else {
                timeString = formatDate("h:mm a", mUserReminderDate);
            }
            mTimeEditText.setText(timeString);

        }
    }


    public void hideKeyboard(EditText et) {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(et.getWindowToken(), 0);
    }


    public void setDate(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        int hour, minute;

        Calendar reminderCalendar = Calendar.getInstance();
        reminderCalendar.set(year, month, day);

        if (reminderCalendar.before(calendar)) {
            Toast.makeText(this, "از تاریخ انتخاب شده گذشته است.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (mUserReminderDate != null) {
            calendar.setTime(mUserReminderDate);
        }

        if (DateFormat.is24HourFormat(this)) {
            hour = calendar.get(Calendar.HOUR_OF_DAY);
        } else {

            hour = calendar.get(Calendar.HOUR);
        }
        minute = calendar.get(Calendar.MINUTE);

        calendar.set(year, month, day, hour, minute);
        mUserReminderDate = calendar.getTime();
        setReminderTextView();
//        setDateAndTimeEditText();
        setDateEditText();
    }

    public void setTime(int hour, int minute) {
        Calendar calendar = Calendar.getInstance();
        if (mUserReminderDate != null) {
            calendar.setTime(mUserReminderDate);
        }

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        calendar.set(year, month, day, hour, minute, 0);
        mUserReminderDate = calendar.getTime();

        setReminderTextView();
//        setDateAndTimeEditText();
        setTimeEditText();
    }

    public void setDateEditText() {
        String dateFormat = "d MMM, yyyy";
        mDateEditText.setText(formatDate(dateFormat, mUserReminderDate));
    }

    public void setTimeEditText() {
        String dateFormat;
        if (DateFormat.is24HourFormat(this)) {
            dateFormat = "k:mm";
        } else {
            dateFormat = "h:mm a";

        }
        mTimeEditText.setText(formatDate(dateFormat, mUserReminderDate));
    }

    public void setReminderTextView() {
        if (mUserReminderDate != null) {
            mReminderTextView.setVisibility(View.VISIBLE);
            if (mUserReminderDate.before(new Date())) {
                mReminderTextView.setText(getString(R.string.date_error_check_again));
                mReminderTextView.setTextColor(Color.RED);
                return;
            }
            Date date = mUserReminderDate;
            String dateString = formatDate("d MMM, yyyy", date);
            String timeString;
            String amPmString = "";

            if (DateFormat.is24HourFormat(this)) {
                timeString = formatDate("k:mm", date);
            } else {
                timeString = formatDate("h:mm", date);
                amPmString = formatDate("a", date);
            }
            String finalString = String.format(getResources().getString(R.string.remind_date_and_time), dateString, timeString, amPmString);
            mReminderTextView.setTextColor(getResources().getColor(R.color.secondary_text));
            mReminderTextView.setText(finalString);
        } else {
            mReminderTextView.setVisibility(View.INVISIBLE);

        }
    }

    public void makeResult(int result) {
        Intent i = new Intent();
        builder = new ToDoItem.Builder();

        if (mUserEnteredText.length() > 0) {

            String capitalizedString = Character.toUpperCase(mUserEnteredText.charAt(0)) + mUserEnteredText.substring(1);
//            mUserToDoItem.setToDoText(capitalizedString);
            builder.setmToDoText(capitalizedString);
        } else {
            builder.setmToDoText(mUserEnteredText);
//            mUserToDoItem.setToDoText(mUserEnteredText);
        }
//        mUserToDoItem.setLastEdited(mLastEdited);
        if (mUserReminderDate != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(mUserReminderDate);
            calendar.set(Calendar.SECOND, 0);
            mUserReminderDate = calendar.getTime();
        }
        builder.setmHasReminder(mUserHasReminder);
//        mUserToDoItem.setHasReminder(mUserHasReminder);
        builder.setmToDoDate(mUserReminderDate);
//        mUserToDoItem.setToDoDate(mUserReminderDate);
        builder.setmTodoColor(mUserColor);
//        mUserToDoItem.setTodoColor(mUserColor);

        i.putExtra(Constants.TODOITEM_ID, builder.build().getTodoId());
        setResult(result, i);
    }

    @Override
    public void onBackPressed() {
        if (mUserReminderDate.before(new Date())) {
            builder.setmToDoDate(null);
        }
        makeResult(RESULT_OK);
        super.onBackPressed();
    }


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }


    public static String formatDate(String formatString, Date dateToFormat) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(formatString);
        return simpleDateFormat.format(dateToFormat);
    }

    @Override
    public void onTimeSet(RadialPickerLayout radialPickerLayout, int hour, int minute) {
        setTime(hour, minute);
    }

    @Override
    public void onDateSet(DatePickerDialog datePickerDialog, int year, int month, int day) {
        setDate(year, month, day);
    }

    public void setEnterDateLayoutVisible(boolean checked) {
        if (checked) {
            mUserDateSpinnerContainingLinearLayout.setVisibility(View.VISIBLE);
        } else {
            mUserDateSpinnerContainingLinearLayout.setVisibility(View.INVISIBLE);
        }
    }

    public void setEnterDateLayoutVisibleWithAnimations(boolean checked) {
        if (checked) {
            setReminderTextView();
            mUserDateSpinnerContainingLinearLayout.animate().alpha(1.0f).setDuration(500).setListener(
                    new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                            mUserDateSpinnerContainingLinearLayout.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {
                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {
                        }
                    }
            );
        } else {
            mUserDateSpinnerContainingLinearLayout.animate().alpha(0.0f).setDuration(500).setListener(
                    new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            mUserDateSpinnerContainingLinearLayout.setVisibility(View.INVISIBLE);
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {

                        }
                    }
            );
        }

    }
}

