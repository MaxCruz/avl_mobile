package com.jaragua.avlmobile.activities;

import android.app.ListActivity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.jaragua.avlmobile.R;
import com.jaragua.avlmobile.persistences.DataSource;
import com.jaragua.avlmobile.persistences.MessageModel;
import com.jaragua.avlmobile.utils.Constants;

public class MainOldActivity extends ListActivity {

    private final Handler handler = new Handler();
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataSource = DataSource.getInstance(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        SQLiteDatabase db = dataSource.getDb();
        MessageModel model = new MessageModel();
        String[] projection = new String[]{
                Constants.MessageModel.COLUMN_ID,
                Constants.MessageModel.COLUMN_SERVER_ID,
                Constants.MessageModel.COLUMN_MESSAGE
        };
        cursor = db.query(model.getModelName(), projection, null, null,
                null, null, null);
        adapter = new SimpleCursorAdapter(this,
                android.R.layout.simple_list_item_1, cursor,
                fromColumns, toViews, 0);
        setListAdapter(adapter);
        setContentView(R.layout.activity_main);
        handler.postDelayed(refreshCallback, Constants.MainActivity.REFRESH_INTERVAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(refreshCallback);
        cursor.close();
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        // TODO: DISPLAY POSSIBLE RESPONSES WHEN AN OPTION IS CLICKED
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            this.moveTaskToBack(true);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
