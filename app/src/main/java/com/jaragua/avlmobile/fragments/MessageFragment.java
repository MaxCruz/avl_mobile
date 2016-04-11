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
import android.widget.SimpleCursorAdapter;
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

import butterknife.Bind;
import butterknife.ButterKnife;

public class MessageFragment extends Fragment {

    private final Handler handler = new Handler();
    @Bind(R.id.listView)
    protected SwipeMenuListView listView;
    SwipeMenuCreator creator = new SwipeMenuCreator() {

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
    private SimpleCursorAdapter adapter;
    private String[] fromColumns = {Constants.MessageModel.COLUMN_MESSAGE};
    private int[] toViews = {android.R.id.text1};
    private Runnable refreshCallback = new Runnable() {

        @Override
        public void run() {
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
                switch (index) {
                    case 0:
                        Toast.makeText(getActivity(), "REPLY", Toast.LENGTH_LONG).show();
                        break;
                    case 1:
                        Toast.makeText(getActivity(), "DELETE", Toast.LENGTH_LONG).show();
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
        //AppAdapter mAdapter = new AppAdapter();
        //listView.setAdapter(mAdapter);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        SQLiteDatabase db = dataSource.getDb();
        MessageModel model = new MessageModel();
        String[] projection = new String[]{
                Constants.MessageModel.COLUMN_ID,
                Constants.MessageModel.COLUMN_SERVER_ID,
                Constants.MessageModel.COLUMN_MESSAGE
        };
        cursor = db.query(model.getModelName(), projection, null, null, null, null, null);
        adapter = new SimpleCursorAdapter(getActivity(),
                android.R.layout.simple_list_item_1, cursor,
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

}
