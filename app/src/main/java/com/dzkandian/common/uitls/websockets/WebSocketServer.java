package com.dzkandian.common.uitls.websockets;

import fi.iki.elonen.NanoWSD;

/**
 * 开启APP自身服务器
 */
public class WebSocketServer extends NanoWSD {

    public WebSocketServer(int port) {
        super(port);
    }

    @Override
    protected WebSocket openWebSocket(IHTTPSession handshake) {
        return new MyWebSocket(handshake);
    }
}