package ir.beigirad.todoapp.CustomUI;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;

import java.util.ArrayList;
import java.util.Collections;

import ir.beigirad.todoapp.Activities.AddToDoActivity;
import ir.beigirad.todoapp.Utils.Constants;
import ir.beigirad.todoapp.MyApplication;
import ir.beigirad.todoapp.R;
import ir.beigirad.todoapp.Models.ToDoItem;

import static ir.beigirad.todoapp.Utils.Constants.Theme.LightTheme;

/**
 * Created by farhad-mbp on 6/13/17.
 */

public class BasicListAdapter extends RecyclerView.Adapter<BasicListAdapter.ViewHolder> implements ItemTouchHelperClass.ItemTouchHelperAdapter{
    private ArrayList<ToDoItem> items;
    private Context mContext;
    private Listener mListener;

    public BasicListAdapter(Context context,ArrayList<ToDoItem> items, Listener mListener) {
        this.mContext=context;
        this.items = items;
        this.mListener = mListener;
    }


    @Override
    public void onItemMoved(int fromPosition, int toPosition) {
        if(fromPosition<toPosition){
            for(int i=fromPosition; i<toPosition; i++){
                Collections.swap(items, i, i+1);
            }
        }
        else{
            for(int i=fromPosition; i > toPosition; i--){
                Collections.swap(items, i, i-1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public void onItemRemoved(final int position) {
        mListener.onTochRemovedItem(position);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_circle_try, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }


    public void updateList(ArrayList<ToDoItem> mToDoItemsArrayList) {
        this.items=mToDoItemsArrayList;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        RelativeLayout container;
        TextView mToDoTextview;
        ImageView mColorImageView;
        TextView mTimeTextView;

        public ViewHolder(View v){
            super(v);
            mToDoTextview = (TextView)v.findViewById(R.id.toDoListItemTextview);
            mTimeTextView = (TextView)v.findViewById(R.id.todoListItemTimeTextView);
            mColorImageView = (ImageView)v.findViewById(R.id.toDoListItemColorImageView);
            container= (RelativeLayout) v.findViewById(R.id.listItemLinearLayout);


        }

        public void bind(final int position) {
            ToDoItem item = items.get(position);
            //Background color for each to-do item. Necessary for night/day mode
            int bgColor;
            //color of title text in our to-do item. White for night mode, dark gray for day mode
            int todoTextColor;
            if(MyApplication.getPref().getTheme().equals(LightTheme)){
                bgColor = Color.WHITE;
                todoTextColor = mContext.getResources().getColor(R.color.secondary_text);
            }
            else{
                bgColor = Color.DKGRAY;
                todoTextColor = Color.WHITE;
            }
            container.setBackgroundColor(bgColor);

            if(item.hasReminder() && item.getToDoDate()!=null){
                mToDoTextview.setMaxLines(1);
                mTimeTextView.setVisibility(View.VISIBLE);
            }
            else{
                mTimeTextView.setVisibility(View.GONE);
                mToDoTextview.setMaxLines(2);
            }
            mToDoTextview.setText(item.getToDoText());
            mToDoTextview.setTextColor(todoTextColor);

            TextDrawable myDrawable = TextDrawable.builder().beginConfig()
                    .textColor(Color.WHITE)
                    .useFont(MyApplication.getDefaultTypeface())
                    .toUpperCase()
                    .endConfig()
                    .buildRound(item.getToDoText().substring(0,1),item.getTodoColor());

            mColorImageView.setImageDrawable(myDrawable);
            if(item.getToDoDate()!=null){
                String timeToShow;
                if(android.text.format.DateFormat.is24HourFormat(mContext)){
                    timeToShow = AddToDoActivity.formatDate(Constants.DATE_TIME_FORMAT_24_HOUR, item.getToDoDate());
                }
                else{
                    timeToShow = AddToDoActivity.formatDate(Constants.DATE_TIME_FORMAT_12_HOUR, item.getToDoDate());
                }
                mTimeTextView.setText(timeToShow);
            }

            itemView.setTag(item);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ToDoItem item1= (ToDoItem) v.getTag();
                    mListener.onSelectItem(position,item1);
                }
            });
        }
    }

    public interface Listener{
        public void onSelectItem(int position,ToDoItem item);
        public void onTochRemovedItem(int position);
    }
}
