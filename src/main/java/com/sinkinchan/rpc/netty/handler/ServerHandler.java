package com.sinkinchan.rpc.netty.handler;

import com.sinkinchan.rpc.netty.proto.RichManProto;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.List;

/**
 * Created by apple on 2017/5/21.
 */
public class ServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        RichManProto.RichMan req = (RichManProto.RichMan) msg;
        System.out.println(req.getName() + "他有" + req.getCarsCount() + "量车");
        List<RichManProto.RichMan.Car> lists = req.getCarsList();
        if (null != lists) {

            for (RichManProto.RichMan.Car car : lists) {
                System.out.println(car.getName());
            }
        }
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

}
