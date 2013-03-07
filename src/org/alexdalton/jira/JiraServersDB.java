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
package org.alexdalton.jira;

import java.util.ArrayList;

import org.alexdalton.jira.model.JiraServer;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class JiraServersDB extends SQLiteOpenHelper {
    SQLiteDatabase mDB;
    private static final int VERSION = 1;
    public static final String DATABASE_TABLE = "servers";
    public static final String KEY_ID = "_id";
    public static final String KEY_NAME = "name";
    public static final String KEY_URL = "url";
    public static final String KEY_USER = "user";
    public static final String KEY_PASSWORD = "password";
    
    public JiraServersDB(Context context) {
        super(context, DATABASE_TABLE, null, VERSION);
        mDB = getWritableDatabase();

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS servers (_id integer PRIMARY KEY, name varchar(50), url varchar(256), user varchar(50), password varchar(50));");
    }

    /**
     * Called when the database needs to be upgraded.
     * 
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void addServer(JiraServer server) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(KEY_NAME, server.getName());
        cv.put(KEY_URL, server.getUrl());
        cv.put(KEY_USER, server.getUser());
        cv.put(KEY_PASSWORD, server.getPassword());

        mDB.insert(DATABASE_TABLE, "null", cv);
        //TODO: DB close for exception-fix on Filter-Update? Current workaround: changed db. to mDB.
    }

    public void deleteServer(JiraServer server) {
	if(server == null) return;
        mDB.delete(DATABASE_TABLE, KEY_NAME+"='"+server.get_id()+"'", null);
        // TODO: delete useless database too 
    }
    
    public ArrayList<JiraServer> getServerList() {
        ArrayList<JiraServer> servers = new ArrayList<JiraServer>();
        SQLiteDatabase db = getReadableDatabase();
        String[] cols = {KEY_ID, KEY_NAME, KEY_URL, KEY_USER, KEY_PASSWORD};
        Cursor res = db.query(DATABASE_TABLE, cols, "1", null, null, null, null);
        for (int i = 0; i < res.getCount(); i++) {
            res.moveToPosition(i);
            servers.add(new JiraServer(res.getInt(0), res.getString(1), res.getString(2), res.getString(3), res.getString(4)));
        }
        res.close();
        return servers;
    }

    /**
     * Update the server using the details provided. The server to be updated is
     * specified using the oldServerName
     * 
     * @param oldServerName
     * @param serverName
     * @param password 
     * @param username 
     * @param serverUrl 
     */
    public boolean updateServer(JiraServer server) {
	if(server == null) return false;
	
        ContentValues args = new ContentValues();
        args.put(KEY_NAME, server.getName());
        args.put(KEY_URL, server.getUrl());
        args.put(KEY_USER, server.getUser());
        args.put(KEY_PASSWORD, server.getPassword());

        return mDB.update(DATABASE_TABLE, args, KEY_ID + "='" + server.get_id() +"'", null) > 0;
    }
}
