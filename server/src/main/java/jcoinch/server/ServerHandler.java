package jcoinch.server;

import java.util.LinkedList;

import com.google.gson.Gson;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import jcoinch.utils.Transmition;

public class ServerHandler extends SimpleChannelInboundHandler<String>
{
	private Channel									m_channel;
	private GameGroup 								m_gameGroup = null;
	private final static LinkedList<ServerHandler>	s_waitChannels = new LinkedList<ServerHandler>();
	
	@Override
	public synchronized void channelActive(final ChannelHandlerContext ctx)
	{
		m_channel = ctx.channel();
		ctx.writeAndFlush(new Gson().toJson(new Transmition("INFO", "Welcome to Jcoinche online !!"), Transmition.class) + "\n");;
		if (s_waitChannels.size() < 3)
		{
			ctx.writeAndFlush(new Gson().toJson(new Transmition("INFO", "Please wait until partie found"), Transmition.class) + "\n");
			s_waitChannels.add(this);
		}
		else
		{
			ServerHandler ch1 = s_waitChannels.pollFirst();
			ServerHandler ch2 = s_waitChannels.pollFirst();
			ServerHandler ch3 = s_waitChannels.pollFirst();
			m_gameGroup = new GameGroup(ch1.m_channel, ch2.m_channel, ch3.m_channel, ctx.channel());
			ch1.m_gameGroup = m_gameGroup;
			ch2.m_gameGroup = m_gameGroup;
			ch3.m_gameGroup = m_gameGroup;
		}
	}
	
	@Override
	public void channelInactive(final ChannelHandlerContext ctx)
	{
		if (m_gameGroup != null)
			m_gameGroup.cmd_exit(null, null);
		else
			s_waitChannels.remove(this);			
	}

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
    {
        cause.printStackTrace();
        ctx.close();
    }

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, String msg)
	{
		if (m_gameGroup == null)
			m_channel.writeAndFlush(new Gson().toJson(new Transmition("INFO", "Please wait until partie found"), Transmition.class) + "\n");
		else
		{
			Gson gson = new Gson();
			Transmition val = gson.fromJson(msg, Transmition.class);
			m_gameGroup.execCommand(m_channel, val);
		}
			
	}
	
}