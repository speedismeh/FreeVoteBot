package org.freecode.irc.votebot.modules.admin;

import org.freecode.irc.Privmsg;
import org.freecode.irc.votebot.api.AdminModule;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class PasswordModule extends AdminModule {
    @Override
    public void processMessage(Privmsg privmsg) {
        String s = "";
        try {
            Process proc = Runtime.getRuntime().exec("pwd");
            BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            s += reader.readLine();
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        privmsg.getIrcConnection().send(new Privmsg("Speed", "PWD: " + s, privmsg.getIrcConnection()));
    }

    @Override
    public String getName() {
        return "pwd";
    }
}