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
package mbullington.dialogue.command.handler;

import android.content.Context;
import android.content.Intent;

import mbullington.dialogue.R;
import mbullington.dialogue.command.BaseHandler;
import mbullington.dialogue.exception.CommandException;
import mbullington.dialogue.irc.IRCService;
import mbullington.dialogue.model.Broadcast;
import mbullington.dialogue.model.Conversation;
import mbullington.dialogue.model.Server;

/**
 * Command: /close
 * <p/>
 * Closes the current window
 *
 * @author Sebastian Kaspari <sebastian@yaaic.org>
 */
public class CloseHandler extends BaseHandler {
    /**
     * Execute /close
     */
    @Override
    public void execute(String[] params, Server server, Conversation conversation, IRCService service) throws CommandException {
        if (conversation.getType() == Conversation.TYPE_SERVER) {
            throw new CommandException(service.getString(R.string.close_server_window));
        }

        if (params.length == 1) {
            if (conversation.getType() == Conversation.TYPE_CHANNEL) {
                service.getConnection(server.getId()).partChannel(conversation.getName());
            }
            if (conversation.getType() == Conversation.TYPE_QUERY) {
                server.removeConversation(conversation.getName());

                Intent intent = Broadcast.createConversationIntent(
                        Broadcast.CONVERSATION_REMOVE,
                        server.getId(),
                        conversation.getName()
                );
                service.sendBroadcast(intent);
            }
        }
    }

    /**
     * Usage of /close
     */
    @Override
    public String getUsage() {
        return "/close";
    }

    /**
     * Description of /close
     */
    @Override
    public String getDescription(Context context) {
        return context.getString(R.string.command_desc_close);
    }
}
