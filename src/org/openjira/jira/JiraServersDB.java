/*******************************************************************************
 * Copyright 2012 Alexandre d'Alton
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package org.openjira.jira;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class JiraServersDB extends SQLiteOpenHelper {

	private static final int VERSION = 1;
	public static final String DATABASE_TABLE = "servers";
	public static final String KEY_ID = "_id";
	public static final String KEY_NAME = "name";
	public static final String KEY_URL = "url";
	public static final String KEY_USER = "user";
	public static final String KEY_PASSWORD = "password";

	public JiraServersDB(final Context context) {
		super(context, DATABASE_TABLE, null, VERSION);
	}

	@Override
	public void onCreate(final SQLiteDatabase db) {
		db.execSQL("CREATE TABLE IF NOT EXISTS servers (_id integer PRIMARY KEY, name varchar(50), url varchar(256), user varchar(50), password varchar(50));");
	}

	/**
	 * Called when the database needs to be upgraded.
	 * 
	 */
	@Override
	public void onUpgrade(final SQLiteDatabase db, final int oldVersion, final int newVersion) {
		throw new UnsupportedOperationException("Not implemeted");
	}

}
