package org.openjira.jiraservice;

import org.openjira.jira.JiraDB;
import org.openjira.jira.JiraServersDB;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class JiraContentProvider extends ContentProvider {

	private static final String PATH_SERVERS = "servers";
	private static final String PATH_FILTERS = "filters";
	private static final String AUTHORITY = "org.openjira.jiraservice";
	public static final Uri CONTENT_URI_SERVERS = Uri.parse("content://" + AUTHORITY + "/" + PATH_SERVERS);
	public static final Uri CONTENT_URI_DATA = Uri.parse("content://" + AUTHORITY + "/" + PATH_FILTERS);

	private static final int ALL_ROWS_SERVERS = 1;
	private static final int SINGLE_ROW_SERVERS = 2;
	private static final int ALL_ROWS_FILTERS = 3;
	private static final int SINGLE_ROW_FILTERS = 4;

	private static final UriMatcher uriMatcher;

	private JiraServersDB jiraServersDB;

	static {
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		uriMatcher.addURI(AUTHORITY, PATH_SERVERS, ALL_ROWS_SERVERS);
		uriMatcher.addURI(AUTHORITY, PATH_SERVERS + "/#", SINGLE_ROW_SERVERS);
		uriMatcher.addURI(AUTHORITY, PATH_FILTERS, ALL_ROWS_FILTERS);
		uriMatcher.addURI(AUTHORITY, PATH_FILTERS + "/#", SINGLE_ROW_FILTERS);
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
		final String databaseTable;
		// Switch base on Uri type
		switch (uriMatcher.match(uri)) {
		case ALL_ROWS_SERVERS:
			rowSelection = null;
			databaseTable = JiraServersDB.DATABASE_TABLE;
			break;
		case SINGLE_ROW_SERVERS:
			final String rowId = uri.getPathSegments().get(1);
			rowSelection = JiraServersDB.KEY_ID + "=" + rowId;
			databaseTable = JiraServersDB.DATABASE_TABLE;
			break;
		case ALL_ROWS_FILTERS:
			rowSelection = null;
			databaseTable = JiraDB.TABLE_FILTERS;
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

		final int rowsDeleted = db.delete(databaseTable, finalSelection, selectionArgs);

		return rowsDeleted;
	}

	@Override
	public String getType(final Uri uri) {
		// Return MIME type
		switch (uriMatcher.match(uri)) {
		case ALL_ROWS_SERVERS:
			return "vnd.android.cursor.dir/vnd.openjira.elemental";
		case SINGLE_ROW_SERVERS:
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
			final Uri insertedUri = ContentUris.withAppendedId(CONTENT_URI_SERVERS, id);
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
		case ALL_ROWS_SERVERS:
			// 1 == true == return everything
			rowSelection = "1";
			break;
		case SINGLE_ROW_SERVERS:
			final String rowId = uri.getPathSegments().get(1);
			rowSelection = JiraServersDB.KEY_ID + "=" + rowId;
			break;
		case ALL_ROWS_FILTERS:
			rowSelection = null;
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

		// Return cursor
		return db.query(JiraServersDB.DATABASE_TABLE, projection, finalSelection, selectionArgs, null, null, sortOrder);
	}

	@Override
	public int update(final Uri uri, final ContentValues values, final String selection, final String[] selectionArgs) {
		final SQLiteDatabase db = this.jiraServersDB.getWritableDatabase();

		final String rowSelection;
		// Switch base on Uri type
		switch (uriMatcher.match(uri)) {
		case ALL_ROWS_SERVERS:
			rowSelection = null;
			break;
		case SINGLE_ROW_SERVERS:
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
