package com.jaragua.avlmobile.fragments;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog;
import com.jaragua.avlmobile.R;
import com.jaragua.avlmobile.entities.MessageResponse;
import com.jaragua.avlmobile.persistences.DataSource;
import com.jaragua.avlmobile.persistences.MessageModel;
import com.jaragua.avlmobile.utils.ConnectionManager;
import com.jaragua.avlmobile.utils.Constants;
import com.jaragua.avlmobile.utils.DeviceProperties;
import com.jaragua.avlmobile.utils.Graph;
import com.jaragua.avlmobile.utils.MessageCursorAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

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
    private ConnectionManager connectionManager;
    private MessageCursorAdapter adapter;
    private String[] fromColumns = {
            Constants.MessageModel.COLUMN_MESSAGE,
            Constants.MessageModel.COLUMN_RECEIVED,
            Constants.MessageModel.COLUMN_STATUS,
            Constants.MessageModel.COLUMN_SERVER_ID
    };
    private int[] toViews = {
            R.id.textViewMessage,
            R.id.textViewReceived,
            R.id.imageViewMessageIcon,
            R.id.textViewID
    };
    private Runnable refreshCallback = new Runnable() {

        @Override
        public void run() {
            reload();
            handler.postDelayed(this, Constants.MainActivity.REFRESH_INTERVAL);
        }

    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_message, container, false);
        ButterKnife.bind(this, rootView);
        dataSource = DataSource.getInstance(getActivity());
        connectionManager = new ConnectionManager(getContext());
        listView.setMenuCreator(creator);
        listView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(final int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:
                        replyMessageDialog(position);
                        break;
                    case 1:
                        deleteDialog(position);
                        break;
                }
                return false;
            }
        });
        listView.setSwipeDirection(SwipeMenuListView.DIRECTION_LEFT);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                TextView message = (TextView) view.findViewById(R.id.textViewMessage);
                readMessageDialog(position, message.getText().toString());
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

    private void readMessageDialog(final int position, String message) {
        String close = getContext().getString(R.string.close);
        new MaterialStyledDialog(getActivity())
                .setTitle(getContext().getString(R.string.title_message))
                .setDescription(message)
                .setIcon(R.drawable.ic_drafts_white_36dp)
                .setPositive(close, new MaterialDialog.SingleButtonCallback() {

                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        cursor.moveToPosition(position);
                        MessageModel dataModel = new MessageModel();
                        dataModel.setFromCursor(cursor);
                        if (dataModel.getStatus() == 0) {
                            dataModel.setStatus(1);
                            dataSource.update(dataModel, dataModel.getModelId() + " = ?",
                                    new String[]{String.valueOf(dataModel.getId())});
                            reload();
                        }
                    }

                })
                .show();
    }

    private void deleteDialog(final int position) {
        String yes = getContext().getString(R.string.yes);
        String no = getContext().getString(R.string.no);
        cursor.moveToPosition(position);
        final MessageModel dataModel = new MessageModel();
        dataModel.setFromCursor(cursor);
        String title = getContext().getString(R.string.title_delete);
        String description = getContext().getString(R.string.message_delete);
        new MaterialStyledDialog(getActivity())
                .setTitle(String.format(title, dataModel.getServerId()))
                .setDescription(description)
                .setIcon(R.drawable.ic_delete_white_36dp)
                .setPositive(yes, new MaterialDialog.SingleButtonCallback() {

                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dataSource.delete(dataModel, dataModel.getModelId() + " = ?",
                                new String[]{String.valueOf(dataModel.getId())});
                        reload();
                    }

                })
                .setNegative(no, new MaterialDialog.SingleButtonCallback() {

                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }

                })
                .show();
    }

    private void replyMessageDialog(final int position) {
        final String reply = getContext().getString(R.string.reply);
        String cancel = getContext().getString(R.string.cancel);
        cursor.moveToPosition(position);
        final MessageModel dataModel = new MessageModel();
        dataModel.setFromCursor(cursor);
        if (dataModel.getStatus() == 2) {
            Toast.makeText(getContext(), R.string.message_already_replied, Toast.LENGTH_LONG).show();
            return;
        }
        final String possibleResponses = dataModel.getResponse();
        View customView;
        if (!possibleResponses.isEmpty()) {
            customView = View.inflate(getContext(), R.layout.message_reply_list, null);
            String[] spinnerArray = possibleResponses.split("\\|");
            List<String> responseList = Arrays.asList(spinnerArray);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    getContext(), android.R.layout.simple_spinner_item, responseList);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            Spinner items = (Spinner) customView.findViewById(R.id.spinnerReply);
            items.setAdapter(adapter);
        } else {
            customView = View.inflate(getContext(), R.layout.message_reply_text, null);
        }
        String title = getContext().getString(R.string.title_reply);
        String description = getContext().getString(R.string.message_reply);
        new MaterialStyledDialog(getActivity())
                .setTitle(String.format(title, dataModel.getServerId()))
                .setDescription(description)
                .setIcon(R.drawable.ic_reply_white_36dp)
                .setCustomView(customView)
                .setPositive(reply, new MaterialDialog.SingleButtonCallback() {

                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        String response;
                        if (possibleResponses.isEmpty()) {
                            EditText reply = (EditText) dialog.findViewById(R.id.editTextReply);
                            response = reply.getText().toString();
                        } else {
                            Spinner reply = (Spinner) dialog.findViewById(R.id.spinnerReply);
                            response = reply.getSelectedItem().toString();
                        }
                        sendToDriver(dataModel.getServerId(), response);
                        dataModel.setStatus(2);
                        dataSource.update(dataModel, dataModel.getModelId() + " = ?",
                                new String[]{String.valueOf(dataModel.getId())});
                        reload();
                        Toast.makeText(getContext(), R.string.message_replied, Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                    }

                })
                .setNegative(cancel, new MaterialDialog.SingleButtonCallback() {

                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }

                })
                .show();
    }

    private void setCursor() {
        SQLiteDatabase db = dataSource.getDb();
        MessageModel model = new MessageModel();
        String[] projection = model.getColumns();
        String order = Constants.MessageModel.COLUMN_RECEIVED + " DESC";
        cursor = db.query(model.getModelName(), projection, null, null, null, null, order);
    }

    private void reload() {
        setCursor();
        adapter.swapCursor(cursor);
        adapter.notifyDataSetChanged();
    }

    private void sendToDriver(long id, String response) {
        DeviceProperties deviceProperties = new DeviceProperties(getContext());
        MessageResponse message = new MessageResponse();
        message.setMotive(91);
        message.setMessageId(id);
        message.setDate(Constants.LocationService.FORMAT_DATE.format(new Date()));
        message.setMessageResponse(response);
        ArrayList<MessageResponse> messages = new ArrayList<>();
        messages.add(message);
        connectionManager.sendEventToDriverHttp(
                Constants.SERVER_URL,
                deviceProperties.getImei(),
                Constants.ConnectionManager.PRODUCT,
                messages
        );
    }

}
