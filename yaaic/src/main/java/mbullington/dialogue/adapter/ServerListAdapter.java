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
package mbullington.dialogue.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import mbullington.dialogue.Dialogue;
import mbullington.dialogue.R;
import mbullington.dialogue.model.Server;

import android.support.v7.widget.RecyclerView;

public class ServerListAdapter extends RecyclerView.Adapter<ServerListAdapter.ServerListViewHolder> {
    public ArrayList<Server> servers;

    public ServerListAdapter() {
    }

    public void loadServers() {
        this.servers = Dialogue.getInstance().getServersAsArrayList();
        notifyDataSetChanged();
    }

    @Override
    public ServerListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.i("Dialogue", "#1");
        return new ServerListViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.mixin_chanitem, parent, false));
    }

    @Override
    public void onBindViewHolder(ServerListViewHolder holder, int index) {
        Server server = servers.get(index);
        holder.setServer(server);
        holder.setText(server.getTitle());
    }

    @Override
    public int getItemCount() {
        return servers.size();
    }

    public static class ServerListViewHolder extends RecyclerView.ViewHolder {

        private Server server;
        private int notificationCount;

        @InjectView(R.id.messages_status)
        TextView messagesStatus;

        @InjectView(R.id.text)
        TextView text;

        public ServerListViewHolder(View v) {
            super(v);
            ButterKnife.inject(this, v);
        }

        public void setServer(Server server) {
            this.server = server;
        }

        public void setText(String text) {
            this.text.setText(text);
        }

        public void setNotificationCount(int count) {
            this.notificationCount = count;
            if(server != null && server.isConnected()) {
                messagesStatus.setText(String.valueOf(this.notificationCount));
            }
        }
    }
}