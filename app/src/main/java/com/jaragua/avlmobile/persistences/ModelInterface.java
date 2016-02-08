package com.jaragua.avlmobile.persistences;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public abstract class ModelInterface {

	public abstract String getModelId();
	public abstract String getModelName();
	public abstract String[] getColumns();
	public abstract int getId();
	public abstract ContentValues getContentValues();
	public abstract void setFromCursor(Cursor cursor);
	public abstract void onCreate(SQLiteDatabase dataBase);
	public abstract void onUpdate(SQLiteDatabase dataBase);
	
	public ModelInterface() {
	}
	
}
