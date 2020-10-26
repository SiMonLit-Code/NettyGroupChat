package com.czz.nio.netty.start;

import com.czz.nio.netty.client.Client;

import java.net.InetSocketAddress;

/**
 * @author czz
 * @version 1.0
 * @date 2020/10/26 21:42
 */
public class ClientStart2 {
    public static void main(String[] args) {
        Client client = new Client(new InetSocketAddress("127.0.0.1",1234));
        client.init();
    }
}
