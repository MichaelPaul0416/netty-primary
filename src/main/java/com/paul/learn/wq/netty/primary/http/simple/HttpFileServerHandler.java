package com.paul.learn.wq.netty.primary.http.simple;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.handler.stream.ChunkedFile;
import org.apache.log4j.Logger;

import javax.activation.MimetypesFileTypeMap;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.regex.Pattern;

/**
 * @Author:wangqiang20995
 * @description:
 * @Date:2017/9/3
 */
public class HttpFileServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    private Logger logger = Logger.getLogger(HttpFileServerHandler.class);

    private String url;

    public HttpFileServerHandler(String url){
        this.url = url;
    }
    @Override
    protected void messageReceived(ChannelHandlerContext channelHandlerContext, FullHttpRequest fullHttpRequest) throws Exception {
        if(!fullHttpRequest.getDecoderResult().isSuccess()){
            sendError(channelHandlerContext,HttpResponseStatus.BAD_REQUEST);
            return ;
        }

        if(fullHttpRequest.getMethod() != HttpMethod.GET){
            sendError(channelHandlerContext,HttpResponseStatus.METHOD_NOT_ALLOWED);
            return ;
        }

        final String uri = fullHttpRequest.getUri();
        final String path = sanitizeUri(uri);
        if(path == null){
            sendError(channelHandlerContext,HttpResponseStatus.FORBIDDEN);
            return ;
        }
        File file = new File(path);
        if(file.isHidden() || !file.exists()){
            sendError(channelHandlerContext,HttpResponseStatus.NOT_FOUND);
            return ;
        }

        if(file.isDirectory()){
            if(uri.endsWith("/")){
                listFile(channelHandlerContext,file);
            }else{
                redirect(channelHandlerContext,uri + "/");
            }

            return ;
        }

        if(!file.isFile()){
            sendError(channelHandlerContext,HttpResponseStatus.FORBIDDEN);
            return ;
        }
        RandomAccessFile randomAccessFile ;
        try {
            randomAccessFile = new RandomAccessFile(file, "r");

        }catch (FileNotFoundException e){
            sendError(channelHandlerContext,HttpResponseStatus.FORBIDDEN);
            logger.error(String.format("文件【%s】不存在",file.getName()));
            e.printStackTrace();
            return ;
        }

        long fileLength = randomAccessFile.length();
        HttpResponse response = new DefaultHttpResponse(HttpVersion.HTTP_1_1,HttpResponseStatus.OK);
        HttpHeaders.setContentLength(response,fileLength);
        setContentTypeHeader(response,file);
        if(HttpHeaders.isKeepAlive(fullHttpRequest)){
            response.headers().set(HttpHeaders.Names.CONNECTION,HttpHeaders.Values.KEEP_ALIVE);
        }
        channelHandlerContext.write(response);
        ChannelFuture channelFuture = channelHandlerContext.write(new ChunkedFile(randomAccessFile,0,fileLength,8192),
                channelHandlerContext.newProgressivePromise());
        channelFuture.addListener(new ChannelProgressiveFutureListener() {
            @Override
            public void operationProgressed(ChannelProgressiveFuture channelProgressiveFuture, long progress, long total) throws Exception {
                if(total < 0){
                    logger.info("操作总文件数目："+progress);
                }else{
                    logger.info(String.format("操作文件【已操作/总文件】：%s/%s",progress,total));
                }
            }

            @Override
            public void operationComplete(ChannelProgressiveFuture channelProgressiveFuture) throws Exception {
                logger.info("文件操作完毕，全部写入到管道中");
            }
        });

        ChannelFuture lastFuture = channelHandlerContext.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
        if(!HttpHeaders.isKeepAlive(fullHttpRequest)){
            lastFuture.addListener(ChannelFutureListener.CLOSE);
        }

    }

    private void setContentTypeHeader(HttpResponse response , File file){
        MimetypesFileTypeMap mimetypesFileTypeMap = new MimetypesFileTypeMap();
        response.headers().set(HttpHeaders.Names.CONTENT_TYPE,mimetypesFileTypeMap.getContentType(file.getPath()));
    }
    private void redirect(ChannelHandlerContext context,String newUri){
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,HttpResponseStatus.FOUND);
        response.headers().set(HttpHeaders.Names.LOCATION,newUri);
        context.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);

    }

    private static final Pattern ALLOWED_FILE_NAME = Pattern.compile("[A-Za-z0-9][-_A-Za-z0-9.]*");
    private void listFile(ChannelHandlerContext context,File file){
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,HttpResponseStatus.OK);
        StringBuilder stringBuilder = new StringBuilder();
        response.headers().set(HttpHeaders.Names.CONTENT_TYPE,"text/html; charset=UTF-8");
        stringBuilder.append("<!DOCTYPE html\r\n>");
        stringBuilder.append("<html><head><title>");
        stringBuilder.append(file.getPath());
        stringBuilder.append("</title></head><body>\r\n");
        stringBuilder.append("<h3>");
        stringBuilder.append(file.getPath()).append(" 目录:");
        stringBuilder.append("</h3>");
        stringBuilder.append("<ul><li>");
        stringBuilder.append("链接：<a href=\"../\">..</a></li>\r\n");
        for(File single : file.listFiles()){
            if(single.isHidden() || !file.exists()){
                continue;
            }
            if(!ALLOWED_FILE_NAME.matcher(file.getName()).matches()){
                continue;
            }
            stringBuilder.append("<li>链接：<a href=\""+single.getName()+"\">"+single.getName()+"</a></li>\r\n");
        }
        stringBuilder.append("<ul></body></html>\r\n");
        ByteBuf byteBuf = Unpooled.copiedBuffer(stringBuilder, Charset.forName("UTF-8"));
        response.content().writeBytes(byteBuf);
        byteBuf.release();
        context.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
        logger.info(String.format("目录【%s】下的文件打印完毕",file.getAbsolutePath()));
    }

    private static final Pattern INSECURE_URI = Pattern.compile(".*[<>&\"].*");

    private String sanitizeUri(String uri){
        try{
            uri = URLDecoder.decode(uri,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            logger.error("目前暂不支持utf-8编码，转为iso-8859-1编码...");
            try {
                uri = URLDecoder.decode(uri,"iso-8859-1");
            } catch (UnsupportedEncodingException e1) {
                e1.printStackTrace();
            }
        }
        if(!uri.startsWith(url)){
            return null;
        }
        if(!uri.startsWith("/")){
            return null;
        }
        uri = uri.replace('/', File.separatorChar);
        if(uri.contains("."+ File.separator) || uri.startsWith(".") || uri.endsWith(".") || INSECURE_URI.matcher(uri).matches()){
            return null;
        }

        return System.getProperty("user.dir") + File.separator + uri;
    }


    private void sendError(ChannelHandlerContext context, HttpResponseStatus status){
        String info = "failed --> " + status.toString() + "\r\n";
        FullHttpResponse response;
        try {
            response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,status,
                    Unpooled.copiedBuffer(Unpooled.copiedBuffer(info.getBytes("UTF-8"))));
            response.headers().set(HttpHeaders.Names.CONTENT_TYPE,"text/plain; charset=UTF-8");
            context.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}
