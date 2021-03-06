/**
 *
 */
package com.lafaspot.pop.session;

import java.util.concurrent.ExecutionException;

import org.mockito.Mockito;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.lafaspot.logfast.logging.Logger;
import com.lafaspot.pop.command.PopCommand;
import com.lafaspot.pop.command.PopCommandResponse;
import com.lafaspot.pop.exception.PopException;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.handler.ssl.SslContext;

/**
 * Unit tests for PopSession.
 * @author kraman
 *
 */
public class PopSessionTest {

    private SslContext globalSslContext;
    private Bootstrap globalBootstrap;
    private Logger globalLogger;
    private ChannelFuture globalConnectFuture;
    private Channel globalChannel;

    @BeforeClass
    public void init() throws InterruptedException {

        globalSslContext = Mockito.mock(SslContext.class);
        globalBootstrap = Mockito.mock(Bootstrap.class);
        globalLogger = Mockito.mock(Logger.class);
        globalConnectFuture = Mockito.mock(ChannelFuture.class);
        globalChannel = Mockito.mock(Channel.class);

        Mockito.when(globalConnectFuture.channel()).thenReturn(globalChannel);
        Mockito.when(globalConnectFuture.sync()).thenReturn(globalConnectFuture);
    }

    @Test
    public void testConnect() throws PopException, InterruptedException, ExecutionException {
        final String server = "localhost";
        final int port = 933;
        final int timeout = 1000;

        final PopFuture future = Mockito.mock(PopFuture.class);
        final PopCommand command = Mockito.mock(PopCommand.class);

        Mockito.when(command.getType()).thenReturn(PopCommand.Type.INVALID);
        final PopCommandResponse resp = new PopCommandResponse(command);

        final PopSession testSession = new PopSession(globalSslContext, globalBootstrap, globalLogger);

        Mockito.when(globalBootstrap.connect(Mockito.any(String.class), Mockito.anyInt())).thenReturn(globalConnectFuture);
        Mockito.when(future.isDone()).thenReturn(true);
        Mockito.when(future.get()).thenReturn(resp);

        PopFuture f = testSession.connect(server, port, timeout, timeout);
        Assert.assertNotNull(f);
    }

    @Test
    public void testGetChannel() throws PopException, InterruptedException, ExecutionException {

        final String server = "localhost";
        final int port = 933;
        final int timeout = 1000;

        final PopFuture future = Mockito.mock(PopFuture.class);
        final PopCommand command = Mockito.mock(PopCommand.class);

        Mockito.when(command.getType()).thenReturn(PopCommand.Type.INVALID);
        final PopCommandResponse resp = new PopCommandResponse(command);

        final PopSession testSession = new PopSession(globalSslContext, globalBootstrap, globalLogger);

        Mockito.when(globalBootstrap.connect(Mockito.any(String.class), Mockito.anyInt())).thenReturn(globalConnectFuture);
        Mockito.when(future.isDone()).thenReturn(true);
        Mockito.when(future.get()).thenReturn(resp);

        Assert.assertNull(testSession.getChannel());
        testSession.connect(server, port, timeout, timeout);
        Assert.assertNotNull(testSession.getChannel());
    }
}
