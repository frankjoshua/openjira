package org.alexdalton.jira;

import java.util.ArrayList;
import java.util.HashMap;

import org.alexdalton.jira.model.JiraFilter;
import org.alexdalton.jira.model.JiraPriority;
import org.alexdalton.jira.model.JiraProject;
import org.alexdalton.jira.model.JiraResolution;
import org.alexdalton.jira.model.JiraStatus;
import org.alexdalton.jira.model.JiraType;
import org.alexdalton.jira.model.JiraVersion;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class JiraDB extends SQLiteOpenHelper {
    private static final int VERSION = 3;
    private static final String LOGTAG = "JiraDB";

    SQLiteDatabase mDB;

    public JiraDB(Context context, String dbName) {
        super(context, dbName, null, VERSION);
        mDB = getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS filters (id varchar(30) PRIMARY KEY, name varchar(50));");
        db.execSQL("CREATE TABLE IF NOT EXISTS resolutions (id varchar(30) PRIMARY KEY, name varchar(50), description varchar(2048));");
        db.execSQL("CREATE TABLE IF NOT EXISTS priorities (id varchar(30) PRIMARY KEY, name varchar(50), icon varchar(1024), description varchar(2048));");
        db.execSQL("CREATE TABLE IF NOT EXISTS statuses (id varchar(30) PRIMARY KEY, name varchar(50), icon varchar(1024), description varchar(2048));");
        db.execSQL("CREATE TABLE IF NOT EXISTS types (id varchar(30) PRIMARY KEY, name varchar(50), icon varchar(1024), description varchar(2048));");
        db.execSQL("CREATE TABLE IF NOT EXISTS projects (id varchar(30) PRIMARY KEY, name varchar(50), lead varchar(50));");
        db.execSQL("CREATE TABLE IF NOT EXISTS versions (id varchar(30) PRIMARY KEY, project varchar(30), name varchar(50), releasedate varchar(200), released boolean, archived boolean, sequence int);");
    }

    /**
     * Called when the database needs to be upgraded.
     * 
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion == 2 && newVersion == 3) {
            db.execSQL("DELETE FROM projects WHERE id=lead");
        } else {
            db.execSQL("DROP TABLE IF EXISTS filters");
            db.execSQL("DROP TABLE IF EXISTS resolutions");
            db.execSQL("DROP TABLE IF EXISTS priorities");
            db.execSQL("DROP TABLE IF EXISTS statuses");
            db.execSQL("DROP TABLE IF EXISTS types");
            db.execSQL("DROP TABLE IF EXISTS projects");
            db.execSQL("DROP TABLE IF EXISTS versions");
            onCreate(db);
        }
    }



    public void updateFilter(JiraFilter filter) {
        ContentValues cv = new ContentValues();
        cv.put("name", filter.getName());
        cv.put("id", filter.getId());

        mDB.replace("filters", "null", cv);

    }

    public void updateResolution(JiraResolution j) {
        ContentValues cv = new ContentValues();
        cv.put("name", j.getName());
        cv.put("id", j.getId());
        cv.put("description", j.getDescription());

        mDB.replace("resolutions", "null", cv);
    }

    public void updatePriority(JiraPriority j) {
        ContentValues cv = new ContentValues();
        cv.put("icon", j.getIcon());
        cv.put("name", j.getName());
        cv.put("id", j.getId());
        cv.put("description", j.getDescription());

        mDB.replace("priorities", "null", cv);
    }

    public void updateType(JiraType j) {
        ContentValues cv = new ContentValues();
        cv.put("icon", j.getIcon());
        cv.put("name", j.getName());
        cv.put("id", j.getId());
        cv.put("description", j.getDescription());

        mDB.replace("types", "null", cv);
    }

    public void updateStatus(JiraStatus j) {
        ContentValues cv = new ContentValues();
        cv.put("icon", j.getIcon());
        cv.put("name", j.getName());
        cv.put("id", j.getId());
        cv.put("description", j.getDescription());

        mDB.replace("statuses", "null", cv);
    }

    public void updateProject(JiraProject j) {
        ContentValues cv = new ContentValues();
        cv.put("name", j.getName());
        cv.put("id", j.getId());
        cv.put("lead", j.getLead());
        long ret = mDB.replace("projects", "null", cv);
        // Log.v("DB", "updating project " + j.getId() + " name: " + j.getName()
        // + "lead: " + j.getLead() + "result: " + ret);

        ArrayList<JiraVersion> versions = j.getVersions();
        if (versions != null) {
            for (int i = 0; i < versions.size(); i++) {
                updateVersion(j, versions.get(i));
            }
        }
    }

    public void updateVersion(JiraProject p, JiraVersion j) {
        ContentValues cv = new ContentValues();
        cv.put("name", j.getName());
        cv.put("id", j.getId());
        cv.put("releasedate", j.getReleaseDate());
        cv.put("released", j.isReleased());
        cv.put("archived", j.isArchived());
        cv.put("sequence", j.getSequence());
        cv.put("project", p.getTag());

        mDB.replace("versions", "null", cv);
    }

    public ArrayList<JiraFilter> getFilters() {
        ArrayList<JiraFilter> filters = new ArrayList<JiraFilter>();
        String[] cols = {"id", "name"};
        mDB = getWritableDatabase(); // TODO: Workaround to prevent "attempt to acquire a reference on a close SQLiteCloseable", but why is the con closed?
        Cursor res = mDB.query("filters", cols, "1", null, null, null, null);
        for (int i = 0; i < res.getCount(); i++) {
            res.moveToPosition(i);
            filters.add(new JiraFilter(res.getString(0), res.getString(1)));
        }
        res.close();
        return filters;
    }

    public ArrayList<JiraVersion> getVersions(JiraProject p) {
        ArrayList<JiraVersion> versions = new ArrayList<JiraVersion>();
        String[] cols = {"id", "name", "releasedate", "sequence", "released", "archived"};
        String[] params = {p.getTag()};
        Cursor res = mDB.query("versions", cols, "project=?", params, null, null, null);
        for (int i = 0; i < res.getCount(); i++) {
            res.moveToPosition(i);
            versions.add(new JiraVersion(Integer.parseInt(res.getString(0)), res.getString(1), res.getString(2), Integer.parseInt(res.getString(3)), Boolean.parseBoolean(res.getString(4)),
                    Boolean.parseBoolean(res.getString(5))));
        }
        res.close();
        return versions;
    }

    public ArrayList<JiraProject> getProjects() {
        ArrayList<JiraProject> projects = new ArrayList<JiraProject>();
        String[] cols = {"id", "name", "lead"};
        Cursor res = mDB.query("projects", cols, "1", null, null, null, null);
        for (int i = 0; i < res.getCount(); i++) {
            res.moveToPosition(i);
            JiraProject p = new JiraProject(res.getString(0), res.getString(1), res.getString(2));
            projects.add(p);
            p.setVersions(getVersions(p));
        }
        res.close();

        return projects;
    }

    public HashMap<Integer, JiraType> getTypes() {
        HashMap<Integer, JiraType> types = new HashMap<Integer, JiraType>();
        String[] cols = {"id", "name", "description", "icon"};
        Cursor res = mDB.query("types", cols, "1", null, null, null, null);
        for (int i = 0; i < res.getCount(); i++) {
            res.moveToPosition(i);
            JiraType t = new JiraType(res.getString(0), res.getString(1), res.getString(2), res.getString(3));
            types.put(Integer.parseInt(res.getString(0)), t);
            Log.v(LOGTAG, "added type: " + t.toString());
        }
        res.close();

        return types;
    }

    public HashMap<Integer, JiraPriority> getPriorities() {
        HashMap<Integer, JiraPriority> priorities = new HashMap<Integer, JiraPriority>();
        String[] cols = {"id", "name", "description", "icon"};
        Cursor res = mDB.query("priorities", cols, "1", null, null, null, null);
        for (int i = 0; i < res.getCount(); i++) {
            res.moveToPosition(i);
            JiraPriority p = new JiraPriority(res.getString(0), res.getString(1), res.getString(2), res.getString(3));
            priorities.put(Integer.parseInt(res.getString(0)), p);
            Log.v(LOGTAG, "added priority: " + p.toString());
        }
        res.close();

        return priorities;
    }

    public HashMap<Integer, JiraStatus> getStatuses() {
        HashMap<Integer, JiraStatus> statuses = new HashMap<Integer, JiraStatus>();
        String[] cols = {"id", "name", "description", "icon"};
        Cursor res = mDB.query("statuses", cols, "1", null, null, null, null);
        for (int i = 0; i < res.getCount(); i++) {
            res.moveToPosition(i);
            JiraStatus s = new JiraStatus(res.getString(0), res.getString(1), res.getString(2), res.getString(3));
            statuses.put(Integer.parseInt(res.getString(0)), s);
            Log.v(LOGTAG, "added status: " + s.toString());
        }
        res.close();

        return statuses;
    }

}
