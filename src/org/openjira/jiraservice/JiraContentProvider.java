package org.openjira.jiraservice;

import org.openjira.jira.JiraServersDB;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class JiraContentProvider extends ContentProvider {

	private static final String PATH = "servers";
	private static final String AUTHORITY = "org.openjira.jiraservice";
	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + PATH);

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
		this.jiraServersDB = new JiraServersDB(getContext());
		return this.jiraServersDB != null;
	}

	@Override
	public int delete(final Uri uri, final String selection, final String[] selectionArgs) {
		final SQLiteDatabase db = this.jiraServersDB.getWritableDatabase();

		final String rowSelection;
		// Switch base on Uri type
		switch (uriMatcher.match(uri)) {
		case ALL_ROWS:
			rowSelection = null;
			break;
		case SINGLE_ROW:
			final String rowId = uri.getPathSegments().get(1);
			rowSelection = JiraServersDB.KEY_ID + "=" + rowId;
			break;
		default:
			throw new IllegalArgumentException("Unsupported URI:  " + uri);
		}

		// Add passed selection arguments
		final String finalSelection;
		if (selection != null) {
			if (rowSelection == null) {
				finalSelection = selection;
			} else {
				finalSelection = rowSelection + " AND (" + selection + ")";
			}
		} else {
			finalSelection = rowSelection;
		}

		// Delete select rows
		final int rowsDeleted = db.delete(JiraServersDB.DATABASE_TABLE, finalSelection, selectionArgs);

		return rowsDeleted;
	}

	@Override
	public String getType(final Uri uri) {
		// Return MIME type
		switch (uriMatcher.match(uri)) {
		case ALL_ROWS:
			return "vnd.android.cursor.dir/vnd.openjira.elemental";
		case SINGLE_ROW:
			return "vnd.android.cursor.item/vnd.openjira.elemental";
		default:
			throw new IllegalArgumentException("Unsupported URI:  " + uri);
		}

	}

	@Override
	public Uri insert(final Uri uri, final ContentValues values) {
		final SQLiteDatabase db = this.jiraServersDB.getWritableDatabase();

		final long id = db.insert(JiraServersDB.DATABASE_TABLE, null, values);

		if (id > -1) {
			// Create Uri pointing to created row
			final Uri insertedUri = ContentUris.withAppendedId(CONTENT_URI, id);
			// Notify observers about data set change
			getContext().getContentResolver().notifyChange(uri, null);
			return insertedUri;
		} else {
			return null;
		}
	}

	@Override
	public Cursor query(final Uri uri, final String[] projection, final String selection, final String[] selectionArgs, final String sortOrder) {
		final SQLiteDatabase db = this.jiraServersDB.getWritableDatabase();

		final String rowSelection;
		// Switch base on Uri type
		switch (uriMatcher.match(uri)) {
		case ALL_ROWS:
			// 1 == true == return everything
			rowSelection = "1";
			break;
		case SINGLE_ROW:
			final String rowId = uri.getPathSegments().get(1);
			rowSelection = JiraServersDB.KEY_ID + "=" + rowId;
			break;
		default:
			throw new IllegalArgumentException("Unsupported URI:  " + uri);
		}

		// Add passed selection arguments
		final String finalSelection;
		if (selection != null) {
			finalSelection = rowSelection + " AND (" + selection + ")";
		} else {
			finalSelection = rowSelection;
		}
		// Default return columns
		final String[] cols = { JiraServersDB.KEY_ID, JiraServersDB.KEY_NAME, JiraServersDB.KEY_URL, JiraServersDB.KEY_USER, JiraServersDB.KEY_PASSWORD };
		// Return cursor
		return db.query(JiraServersDB.DATABASE_TABLE, cols, finalSelection, selectionArgs, null, null, sortOrder);
	}

	@Override
	public int update(final Uri uri, final ContentValues values, final String selection, final String[] selectionArgs) {
		final SQLiteDatabase db = this.jiraServersDB.getWritableDatabase();

		final String rowSelection;
		// Switch base on Uri type
		switch (uriMatcher.match(uri)) {
		case ALL_ROWS:
			rowSelection = null;
			break;
		case SINGLE_ROW:
			final String rowId = uri.getPathSegments().get(1);
			rowSelection = JiraServersDB.KEY_ID + "=" + rowId;
			break;
		default:
			throw new IllegalArgumentException("Unsupported URI:  " + uri);
		}

		// Add passed selection arguments
		final String finalSelection;
		if (selection != null) {
			if (rowSelection == null) {
				finalSelection = selection;
			} else {
				finalSelection = rowSelection + " AND (" + selection + ")";
			}
		} else {
			finalSelection = rowSelection;
		}

		// Delete select rows
		final int rowsUpdated = db.update(JiraServersDB.DATABASE_TABLE, values, finalSelection, selectionArgs);

		// Notify observers about data set change
		getContext().getContentResolver().notifyChange(uri, null);

		return rowsUpdated;
	}

}
