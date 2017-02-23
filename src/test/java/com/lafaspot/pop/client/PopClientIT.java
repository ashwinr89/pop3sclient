/**
 *
 */
package com.lafaspot.pop.client;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.lafaspot.logfast.logging.LogContext;
import com.lafaspot.logfast.logging.LogManager;
import com.lafaspot.logfast.logging.Logger;
import com.lafaspot.logfast.logging.Logger.Level;
import com.lafaspot.pop.command.PopCommand;
import com.lafaspot.pop.command.PopCommand.Type;
import com.lafaspot.pop.command.PopCommandResponse;
import com.lafaspot.pop.exception.PopException;
import com.lafaspot.pop.session.PopSession;

/**
 * @author kraman
 *
 */
public class PopClientIT {

    private PopClient client;
    private LogManager logManager;
    private Logger logger;
    private final String server = "jpop.pop.mail.yahoo.com";
    private final int port = 995;

    @BeforeClass
    public void beforeClass() throws PopException {

        logManager = new LogManager(Level.DEBUG, 5);
        logManager.setLegacy(true);
        logger = logManager.getLogger(new LogContext(PopClientIT.class.getName()) {
        });
        client = new PopClient(10, logManager);
    }

    @Test
	public void testConnect() throws PopException, InterruptedException, ExecutionException {
		PopSession session = client.createSession();
		Future<PopCommandResponse> f = session.connect(server, port, 30000, 60000);

		f.get();
		System.out.println("conntect is complete, sending capa");
		Future<PopCommandResponse> f2 = session.execute(new PopCommand(PopCommand.Type.CAPA));
		System.out.println("sent capa, waiting for resp.");
		System.out.println("capa " + f2.get().getLines());

		System.out.println("DONE ");
	}

    @Test
    public void testUidl() throws PopException, InterruptedException, ExecutionException {
    	final PopSession session = client.createSession();

		Future<PopCommandResponse> f = session.connect(server, port, 30000, 60000);
		Assert.assertNotNull(f.get());

		PopCommand user = new PopCommand(Type.USER);
		user.addArgs("krinteg1@yahoo.com");
		f = session.execute(user);
		Assert.assertNotNull(f.get());

		PopCommand pass = new PopCommand(Type.PASS);
		pass.addArgs("**");
		f = session.execute(pass);
		Assert.assertNotNull(f.get());

		f = session.execute(new PopCommand(Type.UIDL));
		System.out.println(f.get().getLines());

		session.disconnect();
    }


    @Test (enabled=true)
	public void testUidlListRetr() throws PopException, InterruptedException, ExecutionException {
		final PopSession session = client.createSession();

		Future<PopCommandResponse> f = session.connect(server, port, 30000, 60000);
		Assert.assertNotNull(f.get());

		PopCommand user = new PopCommand(Type.USER);
		user.addArgs("krinteg1@yahoo.com");
		f = session.execute(user);
		Assert.assertNotNull(f.get());

		PopCommand pass = new PopCommand(Type.PASS);
		pass.addArgs("password");
		f = session.execute(pass);
		Assert.assertNotNull(f.get());

		f = session.execute(new PopCommand(Type.UIDL));
		Assert.assertTrue(f.get().getLines().size() > 0);


		f = session.execute(new PopCommand(Type.LIST));
		Assert.assertTrue(f.get().getLines().size() > 0);


		PopCommand retrCmd = new PopCommand(Type.RETR);
		retrCmd.addArgs("1");
		f = session.execute(retrCmd);
		Assert.assertTrue(f.get().getLines().size() > 0);
		System.out.println(f.get().getLines());

		Assert.assertNotNull(session.disconnect());
	}

}
