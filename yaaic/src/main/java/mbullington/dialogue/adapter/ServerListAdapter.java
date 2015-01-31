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
import android.graphics.Interpolator;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.PathInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import mbullington.dialogue.Dialogue;
import mbullington.dialogue.R;
import mbullington.dialogue.model.Server;

import android.support.v7.widget.RecyclerView;

import com.squareup.otto.Bus;

public class ServerListAdapter extends RecyclerView.Adapter<ServerListAdapter.ServerItem> {
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
    public ServerItem onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ServerCategory(this, LayoutInflater.from(parent.getContext()).inflate(R.layout.mixin_chancategory, parent, false));
    }

    @Override
    public void onBindViewHolder(ServerItem holder, int index) {
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

    public static class ServerCategory extends ServerItem {

        private boolean rotation = true;

        @InjectView(R.id.action)
        ImageView action;

        public ServerCategory(ServerListAdapter adapter, View v) {
            super(adapter, v);
        }

        @Override
        protected void onClick(View v) {
            RotateAnimation animation = new RotateAnimation(rotation ? 0 : 180, rotation ? 180 : 360, action.getPivotX(), action.getPivotY());
            animation.setInterpolator(AnimationUtils.loadInterpolator(this.context, android.R.interpolator.fast_out_slow_in));
            animation.setDuration(600);
            animation.setFillAfter(true);

            rotation = !rotation;
            action.startAnimation(animation);
        }
    }

    public static class ServerItem extends RecyclerView.ViewHolder {

        protected final Drawable statusDisconnected;
        protected final Drawable statusConnected;
        protected final Drawable statusNotify;

        protected ServerListAdapter adapter;

        protected Context context;
        protected Server server;
        protected int notificationCount;

        @InjectView(R.id.messages_status)
        TextView messagesStatus;

        @InjectView(R.id.text)
        TextView text;

        public ServerItem(ServerListAdapter adapter, View v) {
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
                    ServerItem.this.onClick(v);
                }
            });

            v.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    return ServerItem.this.onLongClick(v);
                }
            });
        }

        protected void onClick(View v) {
            adapter.bus.post(new OnClickEvent(ServerItem.this.server));
        }

        protected boolean onLongClick(View v) {
            ServerItem.this.adapter.bus.post(new OnClickEvent(ServerItem.this.server, true));
            return true;
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