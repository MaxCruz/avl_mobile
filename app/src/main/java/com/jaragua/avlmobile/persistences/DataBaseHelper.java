package com.jaragua.avlmobile.persistences;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.jaragua.avlmobile.utils.Constants;

public class DataBaseHelper extends SQLiteOpenHelper {

	private final EvacuationModel evacuationModel;
	private final MessageModel messageModel;

	public DataBaseHelper(Context context) {
		super(context, Constants.DataBaseHelper.DATABASE_NAME, null, Constants.DataBaseHelper.DATABASE_VERSION);
		evacuationModel = new EvacuationModel();
		messageModel = new MessageModel();
	}

	@Override
	public void onCreate(SQLiteDatabase dataBase) {
		evacuationModel.onCreate(dataBase);
		messageModel.onCreate(dataBase);
	}

	@Override
	public void onUpgrade(SQLiteDatabase dataBase, int oldVersion, int newVersion) {
		evacuationModel.onUpdate(dataBase);
		messageModel.onUpdate(dataBase);
	}

}
