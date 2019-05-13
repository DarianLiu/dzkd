package com.dzkandian.common.uitls.websockets;

import java.io.IOException;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoWSD;

/**
 *
 */
class MyWebSocket extends NanoWSD.WebSocket {

    public MyWebSocket(NanoHTTPD.IHTTPSession handshakeRequest) {
        super(handshakeRequest);
    }

    @Override
    protected void onOpen() {
    }

    @Override
    protected void onClose(NanoWSD.WebSocketFrame.CloseCode code, String reason, boolean initiatedByRemote) {
    }

    @Override
    protected void onMessage(NanoWSD.WebSocketFrame message) {
    }

    @Override
    protected void onPong(NanoWSD.WebSocketFrame pong) {
    }

    @Override
    protected void onException(IOException exception) {
    }

    @Override
    protected void debugFrameReceived(NanoWSD.WebSocketFrame frame) {
    }

    @Override
    protected void debugFrameSent(NanoWSD.WebSocketFrame frame) {
    }
}