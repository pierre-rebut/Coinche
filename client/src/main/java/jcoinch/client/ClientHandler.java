package jcoinch.client;

import com.google.gson.Gson;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import jcoinch.utils.Card;
import jcoinch.utils.Transmition;

public class ClientHandler extends SimpleChannelInboundHandler<String>
{
	private App m_core;
	
	public ClientHandler(App tmp)
	{
		m_core = tmp;
	}
	
	@Override
	public void channelInactive(final ChannelHandlerContext ctx)
	{
		System.out.println("Disconnected from server\nPress enter to continue");
		m_core.m_loopGame = false;
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
	{
		cause.printStackTrace();
        ctx.close();
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception
	{
		Gson gson = new Gson();
		Transmition val = gson.fromJson(msg, Transmition.class);
		System.out.println(val.m_type + " -> " + val.m_cmd);
		int i = 0;
		if (val.m_cards != null)
			for (Card tmp : val.m_cards)
			{
				System.out.println(i + ": " + tmp.color + " " + tmp.value);
				i++;
			}
	}
}
