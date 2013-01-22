package org.alexdalton.jira;

import java.util.ArrayList;

import org.alexdalton.jira.model.JiraServer;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class ServerList extends OJActivity implements OnItemClickListener {
	// ****************** Main menu ID's ****************** //
	private static final int MENU_NEW = 101;
	private static final int MENU_PREFERENCES = 102;
	// ****************** Context menu ID's ****************** //
	private static final int CONTEXTMENU_DELETEITEM = ContextMenu.FIRST;
	private static final int CONTEXTMENU_EDITITEM = ContextMenu.FIRST + 2;
	

	ListView lst;
	ArrayList<JiraServer> servers;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.serverlist);
		lst = (ListView) findViewById(R.id.serverList);
		loadServers();
		lst.setOnItemClickListener(this);
		/* Add Context-Menu listener to the ListView. */
		lst.setOnCreateContextMenuListener(this);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.setHeaderTitle(R.string.cm_serverlist_title);
		menu.add(Menu.NONE, CONTEXTMENU_DELETEITEM, 0, R.string.delete_server);
		menu.add(Menu.NONE, CONTEXTMENU_EDITITEM, 0, R.string.edit_server);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		// get servername from user selection
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item
				.getMenuInfo();

		String serverName = (String) lst.getAdapter().getItem(info.position); // get
		// servername

		/* Switch on the ID of the item, to get what the user selected. */
		switch (item.getItemId()) {
		case CONTEXTMENU_DELETEITEM:
			JiraApp.get().deleteServer(serverName);
			loadServers(); // reload view
			break;
		case CONTEXTMENU_EDITITEM:
			Intent i = new Intent(this, EditServer.class);
			Bundle bundle = new Bundle();
			JiraServer jiraServer = JiraApp.get().getServerFromName(serverName);

			bundle.putInt(JiraServersDB.KEY_ID, jiraServer.get_id());
			bundle.putString(JiraServersDB.KEY_NAME, jiraServer.getName());
			bundle.putString(JiraServersDB.KEY_URL, jiraServer.getUrl());
			bundle.putString(JiraServersDB.KEY_USER, jiraServer.getUser());
			bundle.putString(JiraServersDB.KEY_PASSWORD,
					jiraServer.getPassword());

			i.putExtras(bundle);

			startActivityForResult(i, ACTIVITY_EDIT);
			break;
		}
		return super.onContextItemSelected(item);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		loadServers(); // reload view
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, MENU_NEW, 1, R.string.m_serverlist_addserver).setIcon(
				android.R.drawable.ic_menu_add);
		menu.add(0, MENU_PREFERENCES, 2, R.string.preferences).setIcon(
				android.R.drawable.ic_menu_preferences);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case MENU_NEW:
			addServer();
			return true;
		case MENU_PREFERENCES:
			startActivity(new Intent(this, JiraPreferences.class));
			return true;
		}
		return false;
	}

	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		Intent i = new Intent(this, JiraFilters.class);
		final String name = servers.get(arg2).getName();
		i.setData(Uri.parse("jira://showFilters?server="
				+ name));
		startActivity(i);
		PreferenceManager.getDefaultSharedPreferences(this).edit().putString(JiraApp.SELECTED_SERVER, name).commit();
	}

	public void loadServers() {
		servers = JiraApp.get().getServerList();
		String[] labels = new String[servers.size()];
		for (int i = 0; i < servers.size(); i++) {
			labels[i] = servers.get(i).getName();
		}
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				R.layout.listitem, R.id.text, labels);
		lst.setAdapter(adapter);
	}
}
