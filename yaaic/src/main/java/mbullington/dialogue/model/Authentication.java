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
package mbullington.dialogue.model;

/**
 * Authentication credentials for a server.
 *
 * @author Sebastian Kaspari <sebastian@yaaic.org>
 */
public class Authentication {
    private String saslUsername;
    private String saslPassword;
    private String nickservPassword;

    /**
     * Does this instance have credentials for Nickserv authentication?
     *
     * @return True if nickserv credentials are present, false otherwise.
     */
    public boolean hasNickservCredentials() {
        return nickservPassword != null && nickservPassword.length() > 0;
    }

    /**
     * Does this instance have credentials for SASL authentication?
     *
     * @return True if nickserv credentials are present, false otherwise.
     */
    public boolean hasSaslCredentials() {
        return saslUsername != null && saslUsername.length() > 0;
    }

    /**
     * Get the username for SASL authentication.
     *
     * @return
     */
    public String getSaslUsername() {
        return saslUsername;
    }

    /**
     * Set the username for SASL authentication.
     *
     * @param saslUsername
     */
    public void setSaslUsername(String saslUsername) {
        if (saslUsername == "") {
            saslUsername = null;
        }

        this.saslUsername = saslUsername;
    }

    /**
     * Get the password for SASL authentication.
     *
     * @return
     */
    public String getSaslPassword() {
        return saslPassword;
    }

    /**
     * Set the password for SASL authentication.
     *
     * @param saslPassword
     */
    public void setSaslPassword(String saslPassword) {
        if (saslPassword == "") {
            saslPassword = null;
        }

        this.saslPassword = saslPassword;
    }

    /**
     * Get the password for Nickserv authentication.
     *
     * @return
     */
    public String getNickservPassword() {
        return nickservPassword;
    }

    /**
     * Set the password for Nickserv authentication.
     *
     * @param nickservPassword
     */
    public void setNickservPassword(String nickservPassword) {
        if (nickservPassword == "") {
            nickservPassword = null;
        }

        this.nickservPassword = nickservPassword;
    }
}
