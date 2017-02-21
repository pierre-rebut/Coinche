package jcoinch.client;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import com.google.gson.Gson;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import jcoinch.utils.Transmition;

public final class App
{
	static final boolean SSL = System.getProperty("ssl") != null;
	private final String m_host;
	private final int m_port;
	public boolean 	m_loopGame;

	public App(String host, int port) throws Exception
	{
		m_host = host;
		m_port = port;
		System.out.println("Connect to " + host + " on port " + port);
		final SslContext sslCtx;
		if (SSL)
			sslCtx = SslContextBuilder.forClient().trustManager(InsecureTrustManagerFactory.INSTANCE).build();
		else
			sslCtx = null;
		
	    EventLoopGroup group = new NioEventLoopGroup();
	    m_loopGame = true;
	    try
	    {
	    	final App tmpThis = this;
	       Bootstrap b = new Bootstrap();
	       b.group(group)
	       	.channel(NioSocketChannel.class)
	       	.handler(new ChannelInitializer<SocketChannel>()
	       			{
						@Override
						protected void initChannel(SocketChannel ch) throws Exception {
							ChannelPipeline pipeline = ch.pipeline();
							if (sslCtx != null)
								pipeline.addLast(sslCtx.newHandler(ch.alloc(), m_host, m_port));
							pipeline.addLast(new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter()));
							pipeline.addLast(new StringDecoder());
							pipeline.addLast(new StringEncoder());
		       				pipeline.addLast(new ClientHandler(tmpThis));
							
						}
	       			});
	       Channel ch = b.connect(host, port).sync().channel();
	       ChannelFuture lastWriteFuture = null;
	       BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
	       while (m_loopGame == true)
	       {
	    	   String line = in.readLine().trim();
	    	   if (line == null || m_loopGame == false)
	    		   break;
	    	   String[] parts = line.trim().replaceAll(" +", " ").split(" ", 2);
	    	   Transmition msg;
	    	   if (parts.length == 2)
	    		   msg = new Transmition(parts[0], parts[1]);
	    	   else
	    		   msg = new Transmition(parts[0], null);
	    	   lastWriteFuture = ch.writeAndFlush(new Gson().toJson(msg, Transmition.class) + "\n");
	    	   
	    	   if ("bye".equals(line))
	    	   {
	    		   ch.closeFuture().sync();
	    		   break;
	    	   }
	       }
	       if (lastWriteFuture != null)
	    	   lastWriteFuture.sync();
	    }
	    finally
	    {
	       group.shutdownGracefully();
	    }
	}
	
    public static void main(String[] args) throws Exception
    {
    	try
    	{
    		if (args.length == 2)
    			new App(args[0], Integer.parseInt(args[1]));
    		else
    			new App("127.0.0.1", 4242);
    	}
    	catch (Exception e)
    	{
    		System.out.println("An error occured");
    	}
    }
}