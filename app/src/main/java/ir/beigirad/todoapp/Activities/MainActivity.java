package ir.beigirad.todoapp.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import ir.beigirad.todoapp.MyApplication;
import ir.beigirad.todoapp.CustomUI.BasicListAdapter;
import ir.beigirad.todoapp.Utils.AlarmUtil;
import ir.beigirad.todoapp.Utils.Constants;
import ir.beigirad.todoapp.CustomUI.CustomRecyclerScrollViewListener;
import ir.beigirad.todoapp.CustomUI.RecyclerViewEmptySupport;
import ir.beigirad.todoapp.CustomUI.ItemTouchHelperClass;
import ir.beigirad.todoapp.R;
import ir.beigirad.todoapp.Utils.RealmUtils;
import ir.beigirad.todoapp.Utils.SharedPrefUtil;
import ir.beigirad.todoapp.Models.ToDoItem;
import ir.beigirad.todoapp.Services.TodoNotificationService;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;

import static ir.beigirad.todoapp.Utils.Constants.TODOITEM_ID;


public class MainActivity extends AppCompatActivity implements BasicListAdapter.Listener {
    private RecyclerViewEmptySupport mRecyclerView;
    private FloatingActionButton mAddToDoItemFAB;
    private ArrayList<ToDoItem> mToDoItemsArrayList;
    private CoordinatorLayout mCoordLayout;
    View fakeView;
    private Toolbar toolbar;


    private SharedPrefUtil mPrefUtil;
    private static final int REQUEST_ID_TODO_ITEM = 100;

    //    private ToDoItem mJustDeletedToDoItem;
    private int mIndexOfDeletedToDoItem;
    public ItemTouchHelper itemTouchHelper;

    private CustomRecyclerScrollViewListener customRecyclerScrollViewListener;
    private int mTheme = -1;


    private String theme = "name_of_the_theme";

    private MyApplication app;
    private BasicListAdapter adapter;

    @Override
    protected void onResume() {
        super.onResume();
        app.send(this);

//        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREF_DATA_SET_CHANGED, MODE_PRIVATE);
//        if (sharedPreferences.getBoolean(ReminderActivity.EXIT, false)) {
//            SharedPreferences.Editor editor = sharedPreferences.edit();
//            editor.putBoolean(ReminderActivity.EXIT, false);
//            editor.apply();
//            finish();
//        }
        if (app.getPref().getRecreate()) {
            app.getPref().setRecreate(false);
            recreate();
        }

        showCase();

    }

    private void showCase(){
        ShowcaseConfig config = new ShowcaseConfig();
        config.setRenderOverNavigationBar(true);
        config.setDelay(500); // half second between each showcase view
        config.setMaskColor(getResources().getColor(R.color.amber_dark));
        MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(this, Constants.MAIN_SHOWCASE_ID);

        sequence.setConfig(config);

        sequence.addSequenceItem(mAddToDoItemFAB,
                getResources().getString(R.string.add_from_here), getResources().getString(R.string.ok));

        sequence.addSequenceItem(fakeView,
                getResources().getString(R.string.swap_to_remove), getResources().getString(R.string.ok));

        sequence.addSequenceItem(toolbar,
                getResources().getString(R.string.show_setting_menu), getResources().getString(R.string.ok));

        sequence.start();
    }
    @Override
    protected void onStart() {
        app = (MyApplication) getApplication();
        super.onStart();
//        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREF_DATA_SET_CHANGED, MODE_PRIVATE);
//        if (sharedPreferences.getBoolean(CHANGE_OCCURED, false)) {
//
//            mToDoItemsArrayList = getLocallyStoredData(storeRetrieveData);
//            adapter = new BasicListAdapter(this, mToDoItemsArrayList, this);
////            adapter = new BasicListAdapter(mToDoItemsArrayList);
//            mRecyclerView.setAdapter(adapter);
//            setAlarms();
//
//            SharedPreferences.Editor editor = sharedPreferences.edit();
//            editor.putBoolean(CHANGE_OCCURED, false);
////            editor.commit();
//            editor.apply();
//
//
//        }
    }

    private void setAlarms() {
        if (mToDoItemsArrayList != null) {
            for (ToDoItem item : mToDoItemsArrayList) {
                if (item.hasReminder() && item.getToDoDate() != null) {
                    if (item.getToDoDate().before(new Date())) {
//                        item.setHasReminder(false);
//                        item.setToDoDate(null);
                        continue;
                    }
                    Intent i = new Intent(this, TodoNotificationService.class);
                    i.putExtra(TodoNotificationService.TODOUUID, item.getTodoId());
                    i.putExtra(TodoNotificationService.TODOTEXT, item.getToDoText());
                    new AlarmUtil(this).createAlarm(i, item.getTodoId().hashCode(), item.getToDoDate().getTime());
                }
            }
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        mPrefUtil = new SharedPrefUtil(this);

        app = (MyApplication) getApplication();

        if (mPrefUtil.getTheme().equals(Constants.Theme.LightTheme)) {
            mTheme = R.style.CustomStyle_LightTheme;
        } else {
            mTheme = R.style.CustomStyle_DarkTheme;
        }
        this.setTheme(mTheme);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (mPrefUtil.getFirstOpen()){
            mPrefUtil.setFirstOprn(false);
            initMockInfo();
        }

        mToDoItemsArrayList = new RealmUtils().getLocallyStoredData();
        adapter = new BasicListAdapter(this, mToDoItemsArrayList, this);
        setAlarms();


        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fakeView=findViewById(R.id.fakeView);

        mCoordLayout = (CoordinatorLayout) findViewById(R.id.myCoordinatorLayout);
        mAddToDoItemFAB = (FloatingActionButton) findViewById(R.id.addToDoItemFAB);

        mAddToDoItemFAB.setOnClickListener(new View.OnClickListener() {

            @SuppressWarnings("deprecation")
            @Override
            public void onClick(View v) {
                Intent newTodo = new Intent(MainActivity.this, AddToDoActivity.class);
//                int color = ColorGenerator.MATERIAL.getRandomColor();
//                ToDoItem item = new ToDoItem("", false, null,color);
//                newTodo.putExtra(TODOITEM_ID, item.getTodoId());


                startActivityForResult(newTodo, REQUEST_ID_TODO_ITEM);
            }
        });


//        mRecyclerView = (RecyclerView)findViewById(R.id.toDoRecyclerView);
        mRecyclerView = (RecyclerViewEmptySupport) findViewById(R.id.toDoRecyclerView);
        if (mPrefUtil.getTheme().equals(Constants.Theme.LightTheme)) {
            mRecyclerView.setBackgroundColor(getResources().getColor(R.color.primary_lightest));
        }
        mRecyclerView.setEmptyView(findViewById(R.id.toDoEmptyView));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

//        setUpTransitions();


        customRecyclerScrollViewListener = new CustomRecyclerScrollViewListener() {
            @Override
            public void show() {

                mAddToDoItemFAB.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start();
//                mAddToDoItemFAB.animate().translationY(0).setInterpolator(new AccelerateInterpolator(2.0f)).start();
            }

            @Override
            public void hide() {

                CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams) mAddToDoItemFAB.getLayoutParams();
                int fabMargin = lp.bottomMargin;
                mAddToDoItemFAB.animate().translationY(mAddToDoItemFAB.getHeight() + fabMargin).setInterpolator(new AccelerateInterpolator(2.0f)).start();
            }
        };
        mRecyclerView.addOnScrollListener(customRecyclerScrollViewListener);


        ItemTouchHelper.Callback callback = new ItemTouchHelperClass(adapter);
        itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);


        mRecyclerView.setAdapter(adapter);


    }

    private void initMockInfo() {
        String [] mockTodoest=new String[]{"انجام پروژه","ارسال پروژه"};
        for (int i = 0; i < 2; i++) {
            Date date = new Date();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.MINUTE, i*10);

            ToDoItem toDoItem=new ToDoItem(mockTodoest[i],i%2==0,calendar.getTime());
            RealmUtils.addTodo(toDoItem);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.aboutMeMenuItem:
                Intent i = new Intent(this, AboutActivity.class);
                startActivity(i);
                return true;
            case R.id.preferences:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_CANCELED && requestCode == REQUEST_ID_TODO_ITEM) {
//            String itemId = data.getStringExtra(TODOITEM_ID);
//            ToDoItem item =RealmUtils.getTodo(itemId) ;
//            if (item.getToDoText().length() <= 0) {
//                return;
//            }
//
//            if (item.hasReminder() && item.getToDoDate() != null) {
//                Intent i = new Intent(this, TodoNotificationService.class);
//                i.putExtra(TodoNotificationService.TODOTEXT, item.getToDoText());
//                i.putExtra(TodoNotificationService.TODOUUID, item.getTodoId());
//                createAlarm(i, item.getTodoId().hashCode(), item.getToDoDate().getTime());
//            }
//
//            RealmUtils.addTodo(item);
            mToDoItemsArrayList.clear();
            mToDoItemsArrayList = RealmUtils.getLocallyStoredData();
            adapter.updateList(mToDoItemsArrayList);
            Log.i("aaray count ", "" + mToDoItemsArrayList.size() + " | " + RealmUtils.getLocallyStoredData().size());
            adapter.notifyDataSetChanged();
            setAlarms();


        }
    }




    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRecyclerView.removeOnScrollListener(customRecyclerScrollViewListener);
        if (!app.getRealm().isClosed())
            MyApplication.getRealm().close();
    }

    @Override
    public void onSelectItem(int position, ToDoItem item) {
        Intent i = new Intent(MainActivity.this, AddToDoActivity.class);
        i.putExtra(TODOITEM_ID, item.getTodoId());
        startActivityForResult(i, REQUEST_ID_TODO_ITEM);
    }

    @Override
    public void onTochRemovedItem(int position) {
        app.send("Swap Todo", "Swiped Todo Away");
        final ToDoItem choosedItem = mToDoItemsArrayList.get(position);
        final ToDoItem temp=new ToDoItem(choosedItem.getTodoId(),choosedItem.getToDoText(),choosedItem.hasReminder(),choosedItem.getToDoDate(),choosedItem.getTodoColor());

        final AlarmUtil alarm=new AlarmUtil(this);

        mToDoItemsArrayList.remove(position);
        mIndexOfDeletedToDoItem = position;
        Intent i = new Intent(MainActivity.this, TodoNotificationService.class);
        alarm.deleteAlarm(i, choosedItem.getTodoId().hashCode());
        adapter.notifyItemRemoved(position);
        RealmUtils.removeTodo(temp.getTodoId());

        Snackbar.make(mCoordLayout, "مورد حذف شد. ", Snackbar.LENGTH_SHORT)
                .setAction("برگرداندن", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Comment the line below if not using Google Analytics
                        app.send("Undo Button", "UNDO Pressed");
                        mToDoItemsArrayList.add(mIndexOfDeletedToDoItem, choosedItem);
                        if (choosedItem.getToDoDate() != null && choosedItem.hasReminder()) {
                            Intent i = new Intent(MainActivity.this, TodoNotificationService.class);
                            i.putExtra(TodoNotificationService.TODOTEXT, choosedItem.getToDoText());
                            i.putExtra(TodoNotificationService.TODOUUID, choosedItem.getTodoId());
                            alarm.createAlarm(i, choosedItem.getTodoId().hashCode(), choosedItem.getToDoDate().getTime());
                        }
                        adapter.notifyItemInserted(mIndexOfDeletedToDoItem);
                        RealmUtils.addTodo(temp);

                    }
                }).show();
    }

    @Override
    protected void onPause() {
        super.onPause();
//        RealmUtils.addAllTodo(mToDoItemsArrayList);
    }
}


