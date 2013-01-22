package org.alexdalton.jira;

import java.util.ArrayList;

import org.alexdalton.jira.JiraConn.LoginListener;
import org.alexdalton.jira.model.JiraFilter;
import org.alexdalton.jira.model.JiraIssue;
import org.alexdalton.jira.model.JiraServer;
import org.xmlrpc.android.XMLRPCClient;
import org.xmlrpc.android.XMLRPCException;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class JiraFilters extends OJActivity implements OnItemClickListener, LoginListener {

    JiraConn conn;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.filters);
        setTheme(android.R.style.Theme_Light);
        

        findViewById(R.id.search).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                EditText et = (EditText) findViewById(R.id.query);
                String query = et.getText().toString();
                if (query != null && query.length() > 0) {
                    Intent i = new Intent(JiraFilters.this, Search.class);
                    i.setData(Uri.parse("jira://search?query=" + query));
                    startActivity(i);
                }
            }
        });
      conn = JiraApp.get().getCurrentConnection(); 
      conn.setOnLoginListener(this);
//      findViewById(R.id.progress).setVisibility(View.VISIBLE);
      getFavouriteFilters();
//      findViewById(R.id.progress).setVisibility(View.GONE);
    }

    XMLRPCClient rpcClient;
    Object loginToken;
    ArrayList<JiraIssue> issueList;

    public void getFavouriteFilters() {
        ArrayList<String> filterNames = new ArrayList<String>();
        ArrayList<JiraFilter> filters = conn.getFilters();
        for (int i = 0; i < filters.size(); i++) {
            filterNames.add(filters.get(i).getName());
        }
        ListView list = (ListView) findViewById(R.id.filterList);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.listitem, R.id.text, filterNames);
        list.setAdapter(adapter);
        list.setOnItemClickListener(this);
    }

    JiraApp app;

    public void logout() throws XMLRPCException {
        Boolean bool = (Boolean) rpcClient.call("jira1.logout", loginToken);
        Log.i("", "Logout successful: " + bool);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, MENU_PREFERENCES, 0, R.string.preferences).setIcon(android.R.drawable.ic_menu_preferences);
        menu.add(0, MENU_NEW, 1, "New").setIcon(android.R.drawable.ic_menu_add);
        menu.add(0, MENU_REFRESH, 2, "Refresh").setIcon(android.R.drawable.ic_menu_upload);
        menu.add(0, MENU_SEARCH, 3, "Search").setIcon(android.R.drawable.ic_menu_search);
        menu.add(0, MENU_ABOUT, 4, "About").setIcon(android.R.drawable.ic_menu_info_details);

        return super.onCreateOptionsMenu(menu);
    }

    private static final int MENU_PREFERENCES = 100;
    private static final int MENU_NEW = 101;
    private static final int MENU_REFRESH = 102;
    private static final int MENU_SEARCH = 103;
    private static final int MENU_ABOUT = 104;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        boolean handled = false;
        switch (item.getItemId()) {
            case MENU_REFRESH:
                setProgressDialog(new ProgressDialog(this));
                getProgressDialog().setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                getProgressDialog().setMax(100);
                getProgressDialog().setTitle("Syncing");
                getProgressDialog().show();
                conn.doRefresh();
                handled = true;
                break;
            case MENU_PREFERENCES:
                startActivity(new Intent(this, JiraPreferences.class));
                handled = true;
                break;
            case MENU_NEW:
                startActivity(new Intent(this, CreateIssue.class));
                handled = true;
                break;
            case MENU_SEARCH:
                startActivity(new Intent(this, Search.class));
                handled = true;
                break;
            case MENU_ABOUT:
                startActivity(new Intent(this, About.class));
                handled = true;
                break;
        }
        return handled;
    }

    public void projectSelected() {

    }

    public void onItemClick(AdapterView< ? > arg0, View arg1, int arg2, long arg3) {
        try {
            if (arg0.equals(findViewById(R.id.filterList))) {
                Intent i = new Intent(this, IssueList.class);
                i.putExtra("filter", conn.getFilters().get(arg2).getId());
                i.putExtra("filterName", conn.getFilters().get(arg2).getName());
                startActivity(i);
                PreferenceManager.getDefaultSharedPreferences(this).edit().putInt(JiraApp.LAST_FILTER, arg2).commit();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onLoginComplete() {
        this.runOnUiThread(new Runnable() {
            public void run() {
 //               findViewById(R.id.progress).setVisibility(View.VISIBLE);
                getFavouriteFilters();
//                findViewById(R.id.progress).setVisibility(View.GONE);
//                if (getProgressDialog() != null && getProgressDialog().isShowing())
//                    getProgressDialog().dismiss();
            }
        });
    }

    public void onLoginError(final Exception e) {
//        this.runOnUiThread(new Runnable() {
//            public void run() {
//                Toast.makeText(JiraFilters.this, "Connection error: " + e, Toast.LENGTH_SHORT).show();
//            }
//        });
    }

    

    public void onSyncProgress(final String message, final int progress, final int max) {
//        runOnUiThread(new Runnable() {
//            public void run() {
//                if (getProgressDialog() != null && getProgressDialog().isShowing()) {
//                    if (message != null)
//                        getProgressDialog().setTitle(message);
//                    getProgressDialog().setMax(max);
//                    getProgressDialog().setProgress(progress);
//                }
//            }
//        });
    }

}