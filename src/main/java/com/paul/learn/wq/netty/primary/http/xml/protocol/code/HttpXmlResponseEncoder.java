package com.paul.learn.wq.netty.primary.http.xml.protocol.code;

import com.paul.learn.wq.netty.primary.http.xml.bean.HttpXmlResponse;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import org.apache.log4j.Logger;

import java.util.List;

/**
 * @Author:wangqiang20995
 * @description:
 * @Date:2017/9/9
 */
public class HttpXmlResponseEncoder extends AbstractHttpXmlEncoder<HttpXmlResponse> {
    /**
     * 对于泛型的指定
     * 服务端
     * 1 请求的时候netty处理将原生的http处理成netty的http【FullHttpRequest】对象给我们，然后我们将其中的内容转换为自己定义的http对象【HttpXmlRequest】----decode
     * 2 响应会客户端的时候，我们需要先构造自己的http对象【HttpXmlResponse】，然后将其交给netty，netty将其处理为netty的http对象【FullHttpResponse】 ---- encode
     *
     * 客户端
     * 客户端其实也是类似，角色互换而已，将客户端看成是服务端即可
     */

    private Logger logger = Logger.getLogger(HttpXmlResponseEncoder.class);


    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, HttpXmlResponse httpXmlResponse, List<Object> list) throws Exception {

        ByteBuf byteBuf = encode0(channelHandlerContext,httpXmlResponse.getObject());//将javabean转换成字节码
        FullHttpResponse response = httpXmlResponse.getResponse();
        if(response == null){
            response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK,byteBuf);
        }else{
            response = new DefaultFullHttpResponse(response.getProtocolVersion(),response.getStatus(),byteBuf);
        }
        response.headers().set(HttpHeaders.Names.CONTENT_TYPE,"text/xml");
        HttpHeaders.setContentLength(response,byteBuf.readableBytes());
        list.add(response);

    }
}
