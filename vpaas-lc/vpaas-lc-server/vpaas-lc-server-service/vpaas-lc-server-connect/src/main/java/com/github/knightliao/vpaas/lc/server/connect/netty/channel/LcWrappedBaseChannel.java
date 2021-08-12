package com.github.knightliao.vpaas.lc.server.connect.netty.channel;

import java.net.SocketAddress;
import java.util.concurrent.ConcurrentHashMap;

import com.github.knightliao.middle.lang.future.IInvokeFuture;

import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelId;
import io.netty.channel.ChannelMetadata;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelProgressivePromise;
import io.netty.channel.ChannelPromise;
import io.netty.channel.EventLoop;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import lombok.Getter;

/**
 * 重写channel
 *
 * @author knightliao
 * @date 2021/8/4 15:13
 */
public abstract class LcWrappedBaseChannel implements Channel {

    // 原指针
    protected Channel channel;

    // 记录所有发送的指针
    @Getter
    protected ConcurrentHashMap<Long, IInvokeFuture> futures = new ConcurrentHashMap<>();

    @Override
    public ChannelId id() {
        return channel.id();
    }

    @Override
    public EventLoop eventLoop() {
        return channel.eventLoop();
    }

    @Override
    public Channel parent() {
        return channel.parent();
    }

    @Override
    public ChannelConfig config() {
        return channel.config();
    }

    @Override
    public boolean isOpen() {
        return channel.isOpen();
    }

    @Override
    public boolean isRegistered() {
        return channel.isRegistered();
    }

    @Override
    public boolean isActive() {
        return channel.isActive();
    }

    @Override
    public ChannelMetadata metadata() {
        return channel.metadata();
    }

    @Override
    public SocketAddress localAddress() {
        return channel.localAddress();
    }

    @Override
    public SocketAddress remoteAddress() {
        return channel.remoteAddress();
    }

    @Override
    public ChannelFuture closeFuture() {
        return channel.closeFuture();
    }

    @Override
    public boolean isWritable() {
        return channel.isWritable();
    }

    @Override
    public long bytesBeforeUnwritable() {
        return channel.bytesBeforeUnwritable();
    }

    @Override
    public long bytesBeforeWritable() {
        return channel.bytesBeforeWritable();
    }

    @Override
    public Unsafe unsafe() {
        return channel.unsafe();
    }

    @Override
    public ChannelPipeline pipeline() {
        return channel.pipeline();
    }

    @Override
    public ByteBufAllocator alloc() {
        return channel.alloc();
    }

    @Override
    public ChannelFuture bind(SocketAddress socketAddress) {
        return channel.bind(socketAddress);
    }

    @Override
    public ChannelFuture connect(SocketAddress socketAddress) {
        return channel.connect(socketAddress);
    }

    @Override
    public ChannelFuture connect(SocketAddress socketAddress, SocketAddress socketAddress1) {
        return channel.connect(socketAddress, socketAddress1);
    }

    @Override
    public ChannelFuture disconnect() {
        return channel.disconnect();
    }

    @Override
    public ChannelFuture deregister() {
        return channel.deregister();
    }

    @Override
    public ChannelFuture bind(SocketAddress socketAddress, ChannelPromise channelPromise) {
        return channel.bind(socketAddress, channelPromise);
    }

    @Override
    public ChannelFuture connect(SocketAddress socketAddress, ChannelPromise channelPromise) {
        return channel.connect(socketAddress, channelPromise);
    }

    @Override
    public ChannelFuture connect(SocketAddress socketAddress, SocketAddress socketAddress1,
                                 ChannelPromise channelPromise) {
        return channel.connect(socketAddress, socketAddress1, channelPromise);
    }

    @Override
    public ChannelFuture disconnect(ChannelPromise channelPromise) {
        return channel.disconnect(channelPromise);
    }

    @Override
    public ChannelFuture close(ChannelPromise channelPromise) {
        return channel.close(channelPromise);
    }

    @Override
    public ChannelFuture deregister(ChannelPromise channelPromise) {
        return channel.deregister(channelPromise);
    }

    @Override
    public Channel read() {
        return channel.read();
    }

    @Override
    public Channel flush() {
        return channel.flush();
    }

    @Override
    public ChannelPromise newPromise() {
        return channel.newPromise();
    }

    @Override
    public ChannelProgressivePromise newProgressivePromise() {
        return channel.newProgressivePromise();
    }

    @Override
    public ChannelFuture newSucceededFuture() {
        return channel.newSucceededFuture();
    }

    @Override
    public ChannelFuture newFailedFuture(Throwable throwable) {
        return channel.newFailedFuture(throwable);
    }

    @Override
    public ChannelPromise voidPromise() {
        return channel.voidPromise();
    }

    @Override
    public <T> Attribute<T> attr(AttributeKey<T> attributeKey) {
        return channel.attr(attributeKey);
    }

    @Override
    public <T> boolean hasAttr(AttributeKey<T> attributeKey) {
        return channel.hasAttr(attributeKey);
    }

    @Override
    public int compareTo(Channel o) {
        return channel.compareTo(o);
    }
}
