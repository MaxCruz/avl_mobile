package com.jaragua.avlmobile.persistences;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.jaragua.avlmobile.utils.Constants;

public class EvacuationModel extends ModelInterface {

	public final String createStatement = "CREATE TABLE " + Constants.EvacuationModel.TABLE + "(" +
            Constants.EvacuationModel.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            Constants.EvacuationModel.COLUMN_URL + " TEXT NOT NULL, " +
            Constants.EvacuationModel.COLUMN_DATA + " TEXT NOT NULL, " +
			Constants.EvacuationModel.COLUMN_COUNT + " INTEGER NOT NULL, " +
            Constants.EvacuationModel.COLUMN_PRIORITY + " INTEGER NOT NULL);";
	public final String dropStatement = "DROP TABLE IF EXISTS " + Constants.EvacuationModel.TABLE + ";";
	private int id;
	private String url;
	private String data;
    private int count;
	private int priority;

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
		return Constants.EvacuationModel.COLUMN_ID;
	}
	
	@Override
	public String getModelName() {
		return Constants.EvacuationModel.TABLE;
	}

	@Override
	public String[] getColumns() {
		return new String[] {
                Constants.EvacuationModel.COLUMN_ID,
                Constants.EvacuationModel.COLUMN_URL,
                Constants.EvacuationModel.COLUMN_DATA,
                Constants.EvacuationModel.COLUMN_COUNT,
                Constants.EvacuationModel.COLUMN_PRIORITY
        };
	}
	
	@Override 
	public ContentValues getContentValues() {
		ContentValues content = new ContentValues();
		content.put(Constants.EvacuationModel.COLUMN_URL, url);
		content.put(Constants.EvacuationModel.COLUMN_DATA, data);
        content.put(Constants.EvacuationModel.COLUMN_COUNT, count);
		content.put(Constants.EvacuationModel.COLUMN_PRIORITY, priority);
		return content;
	}
	
	@Override
	public void setFromCursor(Cursor cursor) {
		this.id = cursor.getInt(0);
		this.url = cursor.getString(1);
		this.data = cursor.getString(2);
        this.count = cursor.getInt(3);
		this.priority = cursor.getInt(4);
	}
	
	public EvacuationModel(String url, String data, int count, int priority) {
		this.url = url;
		this.data = data;
        this.count = count;
		this.priority = priority;
	}
	
	public EvacuationModel() {
	}

	@Override
	public int getId() {
		return id;
	}

	public String getUrl() {
		return url;
	}

    public String getData() {
        return data;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

}
