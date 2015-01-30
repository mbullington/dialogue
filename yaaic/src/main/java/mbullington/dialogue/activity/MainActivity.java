/*
Yaaic - Yet Another Android IRC Client

Copyright 2009-2013 Sebastian Kaspari

This file is part of Yaaic.

Yaaic is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

Yaaic is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with Yaaic.  If not, see <http://www.gnu.org/licenses/>.
 */
package mbullington.dialogue.activity;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

import butterknife.OnItemClick;
import butterknife.OnItemLongClick;
import mbullington.dialogue.Dialogue;
import mbullington.dialogue.R;
import mbullington.dialogue.adapter.ServerListAdapter;
import mbullington.dialogue.db.Database;
import mbullington.dialogue.irc.IRCBinder;
import mbullington.dialogue.irc.IRCService;
import mbullington.dialogue.listener.ServerListener;
import mbullington.dialogue.model.Broadcast;
import mbullington.dialogue.model.Extra;
import mbullington.dialogue.model.Server;
import mbullington.dialogue.model.Status;
import mbullington.dialogue.receiver.ServerReceiver;

public class MainActivity extends ActionBarActivity implements ServiceConnection, ServerListener {
    private static int instanceCount = 0;
    private IRCBinder binder;
    private ServerReceiver receiver;
    private ServerListAdapter adapter;

    @InjectView(android.R.id.list)
    ListView list;

    @InjectView(R.id.toolbar)
    Toolbar toolbar;

    @InjectView(R.id.edit_fab)
    ImageButton fab;

    /**
     * On create
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // avoid duplicate activities
        if (instanceCount > 0) {
            finish();
        }
        instanceCount++;
        setContentView(R.layout.main);
        ButterKnife.inject(this);

        setSupportActionBar(toolbar);

        adapter = new ServerListAdapter();

        list.setAdapter(adapter);
    }

    /**
     * On Destroy
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        instanceCount--;
    }

    /**
     * On resume
     */
    @Override
    public void onResume() {
        super.onResume();

        // Start and connect to service
        Intent intent = new Intent(this, IRCService.class);
        intent.setAction(IRCService.ACTION_BACKGROUND);
        startService(intent);
        bindService(intent, this, 0);

        receiver = new ServerReceiver(this);
        registerReceiver(receiver, new IntentFilter(Broadcast.SERVER_UPDATE));

        adapter.loadServers();
    }

    /**
     * On pause
     */
    @Override
    public void onPause() {
        super.onPause();

        if (binder != null && binder.getService() != null) {
            binder.getService().checkServiceStatus();
        }

        unbindService(this);
        unregisterReceiver(receiver);
    }

    /**
     * Service connected to Activity
     */
    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        binder = (IRCBinder) service;
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        binder = null;
    }

    @OnItemClick(android.R.id.list)
    public void onServerSelected(AdapterView<?> parent, View view, int position, long id) {
        Server server = adapter.getItem(position);

        if (server == null) {
            // "Add server" was selected
            startActivityForResult(new Intent(this, AddServerActivity.class), 0);
            return;
        }

        Intent intent = new Intent(this, ConversationActivity.class);

        if (server.getStatus() == Status.DISCONNECTED && !server.mayReconnect()) {
            server.setStatus(Status.PRE_CONNECTING);
            intent.putExtra("connect", true);
        }

        intent.putExtra("serverId", server.getId());
        startActivity(intent);
    }

    @OnItemLongClick(android.R.id.list)
    public boolean onServerOptions(AdapterView<?> l, View v, int position, long id) {
        final Server server = adapter.getItem(position);

        if (server == null) {
            // "Add server" view selected
            return true;
        }

        final CharSequence[] items = {
                getString(R.string.connect),
                getString(R.string.disconnect),
                getString(R.string.edit),
                getString(R.string.delete)
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(server.getTitle());
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                switch (item) {
                    case 0: // Connect
                        if (server.getStatus() == Status.DISCONNECTED) {
                            binder.connect(server);
                            server.setStatus(Status.CONNECTING);
                            adapter.notifyDataSetChanged();
                        }
                        break;
                    case 1: // Disconnect
                        server.clearConversations();
                        server.setStatus(Status.DISCONNECTED);
                        server.setMayReconnect(false);
                        binder.getService().getConnection(server.getId()).quitServer();
                        break;
                    case 2: // Edit
                        editServer(server.getId());
                        break;
                    case 3: // Delete
                        binder.getService().getConnection(server.getId()).quitServer();
                        deleteServer(server.getId());
                        break;
                }
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
        return true;
    }

    /**
     * Start activity to edit server with given id
     *
     * @param serverId The id of the server
     */
    private void editServer(int serverId) {
        Server server = Dialogue.getInstance().getServerById(serverId);

        if (server.getStatus() != Status.DISCONNECTED) {
            Toast.makeText(this, getResources().getString(R.string.disconnect_before_editing), Toast.LENGTH_SHORT).show();
        } else {
            Intent intent = new Intent(this, AddServerActivity.class);
            intent.putExtra(Extra.SERVER, serverId);
            startActivityForResult(intent, 0);
        }
    }

    /**
     * Options Menu (Menu Button pressed)
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        // inflate from xml
        MenuInflater inflater = new MenuInflater(this);
        inflater.inflate(R.menu.main, menu);

        return true;
    }

    /**
     * On menu item selected
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.about:
                startActivity(new Intent(this, AboutActivity.class));
                break;
            case R.id.settings:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
            case R.id.disconnect_all:
                ArrayList<Server> mServers = Dialogue.getInstance().getServersAsArrayList();
                for (Server server : mServers) {
                    if (binder.getService().hasConnection(server.getId())) {
                        server.setStatus(Status.DISCONNECTED);
                        server.setMayReconnect(false);
                        binder.getService().getConnection(server.getId()).quitServer();
                    }
                }
                // ugly
                binder.getService().stopForegroundCompat(R.string.app_name);
        }

        return true;
    }

    /**
     * On activity result
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            // Refresh list from database
            adapter.loadServers();
        }
    }

    /**
     * Delete server
     *
     * @param serverId
     */
    public void deleteServer(int serverId) {
        Database db = new Database(this);
        db.removeServerById(serverId);
        db.close();

        Dialogue.getInstance().removeServerById(serverId);
        adapter.loadServers();
    }

    /**
     * On server status update
     */
    @Override
    public void onStatusUpdate() {
        adapter.loadServers();
    }

    @OnClick(R.id.edit_fab)
    public void onAddServer(View v) {
        this.startActivityForResult(new Intent(this, AddServerActivity.class), 0);
    }
}
