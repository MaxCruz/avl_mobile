package com.jaragua.avlmobile.fragments;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.jaragua.avlmobile.R;
import com.jaragua.avlmobile.persistences.DataSource;
import com.jaragua.avlmobile.persistences.MessageModel;
import com.jaragua.avlmobile.utils.Constants;
import com.jaragua.avlmobile.utils.Graph;
import com.jaragua.avlmobile.utils.MessageCursorAdapter;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MessageFragment extends Fragment {

    private final Handler handler = new Handler();
    @Bind(R.id.listView)
    protected SwipeMenuListView listView;
    protected SwipeMenuCreator creator = new SwipeMenuCreator() {

        @Override
        public void create(SwipeMenu menu) {
            SwipeMenuItem openItem = new SwipeMenuItem(getActivity().getApplicationContext());
            openItem.setBackground(new ColorDrawable(Color.rgb(0xC9, 0xC9, 0xCE)));
            openItem.setWidth(Graph.dp2px(getActivity(), 90));
            openItem.setIcon(R.drawable.ic_reply_white_36dp);
            menu.addMenuItem(openItem);
            SwipeMenuItem deleteItem = new SwipeMenuItem(getActivity().getApplicationContext());
            deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9, 0x3F, 0x25)));
            deleteItem.setWidth(Graph.dp2px(getActivity(), 90));
            deleteItem.setIcon(R.drawable.ic_delete_white_36dp);
            menu.addMenuItem(deleteItem);
        }

    };
    private DataSource dataSource;
    private Cursor cursor;
    private MessageCursorAdapter adapter;
    private String[] fromColumns = {
            Constants.MessageModel.COLUMN_MESSAGE,
            Constants.MessageModel.COLUMN_RECEIVED,
            Constants.MessageModel.COLUMN_STATUS
    };
    private int[] toViews = {
            R.id.textViewMessage,
            R.id.textViewReceived,
            R.id.imageViewMessageIcon
    };
    private Runnable refreshCallback = new Runnable() {

        @Override
        public void run() {
            setCursor();
            adapter.swapCursor(cursor);
            adapter.notifyDataSetChanged();
            handler.postDelayed(this, Constants.MainActivity.REFRESH_INTERVAL);
        }

    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_message, container, false);
        ButterKnife.bind(this, rootView);
        dataSource = DataSource.getInstance(getActivity());
        listView.setMenuCreator(creator);
        listView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                cursor.moveToPosition(position);
                long id = cursor.getLong(cursor.getColumnIndex(Constants.MessageModel.COLUMN_ID));
                switch (index) {
                    case 0:
                        Toast.makeText(getActivity(), "REPLY: " + id, Toast.LENGTH_LONG).show();
                        break;
                    case 1:
                        Toast.makeText(getActivity(), "DELETE: " + id, Toast.LENGTH_LONG).show();
                        break;
                }
                return false;
            }
        });
        listView.setSwipeDirection(SwipeMenuListView.DIRECTION_LEFT);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getActivity(), "ITEM : " + id, Toast.LENGTH_LONG).show();
            }

        });
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        setCursor();
        adapter = new MessageCursorAdapter(getActivity(),
                R.layout.message_item, cursor,
                fromColumns, toViews, 0);
        listView.setAdapter(adapter);
        handler.postDelayed(refreshCallback, Constants.MainActivity.REFRESH_INTERVAL);
    }

    @Override
    public void onPause() {
        super.onPause();
        handler.removeCallbacks(refreshCallback);
        cursor.close();
    }

    private void setCursor() {
        SQLiteDatabase db = dataSource.getDb();
        MessageModel model = new MessageModel();
        String[] projection = new String[]{
                Constants.MessageModel.COLUMN_ID,
                Constants.MessageModel.COLUMN_STATUS,
                Constants.MessageModel.COLUMN_MESSAGE,
                Constants.MessageModel.COLUMN_RECEIVED,
                Constants.MessageModel.COLUMN_RESPONSE
        };
        String order = Constants.MessageModel.COLUMN_RECEIVED + " DESC";
        cursor = db.query(model.getModelName(), projection, null, null, null, null, order);
    }

}
