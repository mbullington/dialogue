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
package mbullington.dialogue.listener;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import mbullington.dialogue.activity.MessageActivity;
import mbullington.dialogue.adapter.MessageListAdapter;
import mbullington.dialogue.model.Extra;

/**
 * Listener for clicks on conversation messages
 *
 * @author Sebastian Kaspari <sebastian@yaaic.org>
 */
public class MessageClickListener implements OnItemClickListener {
    private static MessageClickListener instance;

    /**
     * Private constructor
     */
    private MessageClickListener() {
    }

    /**
     * Get global instance of message click listener
     *
     * @return
     */
    public static synchronized MessageClickListener getInstance() {
        if (instance == null) {
            instance = new MessageClickListener();
        }

        return instance;
    }

    /**
     * On message item clicked
     */
    @Override
    public void onItemClick(AdapterView<?> group, View view, int position, long id) {
        MessageListAdapter adapter = (MessageListAdapter) group.getAdapter();

        Intent intent = new Intent(group.getContext(), MessageActivity.class);
        intent.putExtra(Extra.MESSAGE, adapter.getItem(position).getText().toString());
        group.getContext().startActivity(intent);
    }
}
