package org.freecode.irc.votebot.modules.admin;

import com.speed.irc.types.Privmsg;
import org.freecode.irc.votebot.api.AdminModule;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created with IntelliJ IDEA.
 * User: Deprecated
 * Date: 11/22/13
 * Time: 10:52 PM
 */

@Component
public class RebuildModule extends AdminModule {

    @Value("${git.commit.id.abbrev}")
	private String idAbbrev;
    @Value("${git.commit.id.describe}")
	private String idDescribe;

	public static final String LAST_ID = "pre-rebuild.commit.id.abbrev";

    @Override
	public void onConnect() {
		String last = getStringProperty(LAST_ID);
		if (idAbbrev.equalsIgnoreCase(last)) {
            fvb.sendMsg("Greets");
            return;
        }

		int commits = countCommitsSince(last);

        fvb.sendMsg("Running " + idDescribe + ", " + commits + " new commits since last run (" + last + ")");

		store(LAST_ID, idAbbrev);
	}

	@Override
	public void processMessage(Privmsg privmsg) {
		privmsg.getConversable().getServer().quit("REBUILDING");
		try (BufferedReader reader = executeRebuild()) {
			String line;
			while ((line = reader.readLine()) != null) {
				System.out.println(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static int countCommitsSince(String idAbbrev) {
		try {
			Process p = Runtime.getRuntime().exec(new String[]{
					"/bin/sh", "-c", "git rev-list " + idAbbrev + "..HEAD | wc -l"
			});
			String line = new BufferedReader(new InputStreamReader(p.getInputStream())).readLine();
			return Integer.parseInt(line);
		} catch (IOException | NumberFormatException e) {
			e.printStackTrace();
		}

		return -1;
	}

	private static BufferedReader executeRebuild() throws IOException {
		Process p = Runtime.getRuntime().exec("./run.sh &");
		return new BufferedReader(new InputStreamReader(p.getInputStream()));
	}

	@Override
	public String getName() {
		return "rebuild";
	}

	protected Right[] getRights() {
		return new Right[]{Right.FOUNDER};
	}

	public void setIdAbbrev(String idAbbrev) {
		this.idAbbrev = idAbbrev;
	}

	public void setIdDescribe(String idDescribe) {
		this.idDescribe = idDescribe;
	}

}
