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

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import mbullington.dialogue.Dialogue;
import mbullington.dialogue.R;
import mbullington.dialogue.model.Server;

import android.support.v7.widget.RecyclerView;

import com.squareup.otto.Bus;

public class ServerListAdapter extends RecyclerView.Adapter<ServerListAdapter.ServerListViewHolder> {
    public Bus bus;
    private ArrayList<Server> servers;

    public ServerListAdapter() {
        bus = new Bus();
    }

    public void loadServers() {
        this.servers = Dialogue.getInstance().getServersAsArrayList();
        notifyDataSetChanged();
    }

    @Override
    public ServerListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ServerListViewHolder(this, LayoutInflater.from(parent.getContext()).inflate(R.layout.mixin_chanitem, parent, false));
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

    public static class OnClickEvent {
        public Server server;
        public boolean isLongClick;

        public OnClickEvent(Server server) {
            this(server, false);
        }

        public OnClickEvent(Server server, boolean isLongClick) {
            this.server = server;
            this.isLongClick = isLongClick;
        }
    }

    public static class ServerListViewHolder extends RecyclerView.ViewHolder {

        private final Drawable statusDisconnected;
        private final Drawable statusConnected;
        private final Drawable statusNotify;

        private ServerListAdapter adapter;

        private Context context;
        private Server server;
        private int notificationCount;

        @InjectView(R.id.messages_status)
        TextView messagesStatus;

        @InjectView(R.id.text)
        TextView text;

        public ServerListViewHolder(ServerListAdapter adapter, View v) {
            super(v);
            ButterKnife.inject(this, v);

            this.adapter = adapter;
            this.context = v.getContext();

            this.statusDisconnected = context.getResources().getDrawable(R.drawable.status_disconnected);
            this.statusConnected = context.getResources().getDrawable(R.drawable.status_connected);
            this.statusNotify = context.getResources().getDrawable(R.drawable.status_notify);


            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ServerListViewHolder.this.adapter.bus.post(new OnClickEvent(ServerListViewHolder.this.server));
                }
            });

            v.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    ServerListViewHolder.this.adapter.bus.post(new OnClickEvent(ServerListViewHolder.this.server, true));
                    return true;
                }
            });
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