package org.freecode.irc.event;

import org.freecode.irc.IrcConnection;
import org.freecode.irc.Privmsg;

/**
 * User: Shivam
 * Date: 16/06/13
 * Time: 23:57
 */
public class RawPrivateMessageListener implements DelegatingListener {
    private IrcConnection connection;

    public RawPrivateMessageListener(IrcConnection connection) {
        this.connection = connection;
    }

    public boolean qualifies(String rawLine) {
        final String[] parts = rawLine.split(" ", 4);
        return parts.length == 4 && parts[1].equals("PRIVMSG") && parts[3].startsWith(":");
    }

    public void execute(String rawLine) {
        final Privmsg privmsg = new Privmsg(rawLine, connection);
        for (PrivateMessageListener listener : connection.getDelegates(PrivateMessageListener.class)) {
            listener.onPrivmsg(privmsg);
        }
    }
}
