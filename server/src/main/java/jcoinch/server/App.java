package jcoinch.server;

import java.net.InetAddress;
import java.util.Scanner;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;

public final class App
{
	static final boolean SSL = System.getProperty("ssl") != null;

	public App(int port) throws Exception
	{
		final SslContext sslCtx;
		if (SSL)
		{
			SelfSignedCertificate ssc = new SelfSignedCertificate();
			sslCtx = SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey()).build();
		} else
			sslCtx = null;
		EventLoopGroup bossGroup = new NioEventLoopGroup();
	    EventLoopGroup workerGroup = new NioEventLoopGroup();
	    try
	    {
	       ServerBootstrap b = new ServerBootstrap();
	       b.group(bossGroup, workerGroup)
	       	.channel(NioServerSocketChannel.class)
	       	.childHandler(new ChannelInitializer<SocketChannel>()
	       		{
	       			@Override
	       			public void initChannel(SocketChannel ch) throws Exception
	       			{
	       				ChannelPipeline pipeline = ch.pipeline();
	       				if (sslCtx != null)
	       					pipeline.addLast(sslCtx.newHandler(ch.alloc()));
	       				pipeline.addLast(new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter()));
	       				pipeline.addLast(new StringDecoder());
	       				pipeline.addLast(new StringEncoder());
	       				pipeline.addLast(new ServerHandler());
	       			}
	       		})
	       	.option(ChannelOption.SO_BACKLOG, 128)
	       	.childOption(ChannelOption.SO_KEEPALIVE, true);
	        ChannelFuture f = b.bind(port).sync();
	        boolean tmp = true;
	        System.out.println("Port : " + port);
	        Scanner nc = new Scanner(System.in);
	        while (tmp)
	        {
	        	System.out.print(":");
	        	System.out.flush();
	        	if (nc.nextLine().equals("exit"))
	        		tmp = false;
	        }
	        nc.close();
	        System.out.println("Exiting");
	        f.channel().close().sync();
	    }
	    finally
	    {
	       bossGroup.shutdownGracefully();
	       workerGroup.shutdownGracefully();
	    }
	}
	
    public static void main(String[] args) throws Exception
    {
    	try
    	{
    		if (args.length == 1)
    			new App(Integer.parseInt(args[0]));
    		else
    			new App(4242);
    	}
    	catch (Exception e)
    	{
	    e.printStackTrace();
    		System.out.println("An error occured");
    	}
    }
}
