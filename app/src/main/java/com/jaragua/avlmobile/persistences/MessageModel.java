package com.jaragua.avlmobile.persistences;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.jaragua.avlmobile.utils.Constants;

public class MessageModel extends ModelInterface {

	public final String createStatement = "CREATE TABLE " + Constants.MessageModel.TABLE + "(" +
            Constants.MessageModel.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            Constants.MessageModel.COLUMN_SERVER_ID + " INTEGER NOT NULL, " +
            Constants.MessageModel.COLUMN_STATUS + " INTEGER NOT NULL, " +
            Constants.MessageModel.COLUMN_MESSAGE + " TEXT NOT NULL, " +
			Constants.MessageModel.COLUMN_RESPONSE + " TEXT NOT NULL);";
	public final String dropStatement = "DROP TABLE IF EXISTS " + Constants.MessageModel.TABLE + ";";
	private int id;
    private int serverId;
	private int status;
	private String message;
	private String response;

	@Override
	public void onCreate(SQLiteDatabase dataBase) {
		dataBase.execSQL(createStatement);
	}

	@Override
	public void onUpdate(SQLiteDatabase dataBase) {
		dataBase.execSQL(dropStatement);
		dataBase.execSQL(createStatement);
	}

	@Override
	public String getModelId() {
		return Constants.MessageModel.COLUMN_ID;
	}

	@Override
	public String getModelName() {
		return Constants.MessageModel.TABLE;
	}

	@Override
	public String[] getColumns() {
		return new String[] {
                Constants.MessageModel.COLUMN_ID,
                Constants.MessageModel.COLUMN_SERVER_ID,
                Constants.MessageModel.COLUMN_STATUS,
                Constants.MessageModel.COLUMN_MESSAGE,
                Constants.MessageModel.COLUMN_RESPONSE
        };
	}

	@Override
	public ContentValues getContentValues() {
		ContentValues content = new ContentValues();
		content.put(Constants.MessageModel.COLUMN_SERVER_ID, serverId);
		content.put(Constants.MessageModel.COLUMN_STATUS, status);
        content.put(Constants.MessageModel.COLUMN_MESSAGE, message);
		content.put(Constants.MessageModel.COLUMN_RESPONSE, response);
		return content;
	}

	@Override
	public void setFromCursor(Cursor cursor) {
		this.id = cursor.getInt(0);
        this.serverId = cursor.getInt(1);
        this.status = cursor.getInt(2);
        this.message = cursor.getString(3);
        this.response = cursor.getString(4);
    }

    public MessageModel(int serverId, int status, String message, String response) {
        this.serverId = serverId;
        this.status = status;
        this.message = message;
        this.response = response;
    }

    public MessageModel() {
    }

    @Override
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getServerId() {
        return serverId;
    }

    public void setServerId(int serverId) {
        this.serverId = serverId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

}
