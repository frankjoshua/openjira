package org.openjira.jiraservice;

import org.openjira.jira.JiraServersDB;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;

public class JiraContentProvider extends ContentProvider {

	private static final String PATH = "servers";
	private static final String AUTHORITY = "org.openjira.jiraservice";
	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
			+ "/" + PATH);

	private static final int ALL_ROWS = 1;
	private static final int SINGLE_ROW = 2;

	private static final UriMatcher uriMatcher;

	private JiraServersDB jiraServersDB;

	static {
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		uriMatcher.addURI(AUTHORITY, PATH, ALL_ROWS);
		uriMatcher.addURI(AUTHORITY, PATH + "/#", SINGLE_ROW);
	}

	@Override
	public boolean onCreate() {
		// Create database connection
		jiraServersDB = new JiraServersDB(getContext());
		return jiraServersDB != null;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		throw new UnsupportedOperationException("Not Implemented");
	}

	@Override
	public String getType(Uri uri) {
		throw new UnsupportedOperationException("Not Implemented");
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		throw new UnsupportedOperationException("Not Implemented");
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		final int match = uriMatcher.match(uri);
		switch (match) {
		case ALL_ROWS:
			return jiraServersDB.getServerListCursor();
		default:
			break;
		}
		throw new UnsupportedOperationException("Unkown Uri");
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		throw new UnsupportedOperationException("Not Implemented");
	}

}
