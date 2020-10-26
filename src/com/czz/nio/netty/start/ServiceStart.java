package com.czz.nio.netty.start;

import com.czz.nio.netty.service.Service;

import java.net.InetSocketAddress;

/**
 * @author czz
 * @version 1.0
 * @date 2020/10/26 21:42
 */
public class ServiceStart {
    public static void main(String[] args) {
        Service service = new Service(new InetSocketAddress(1234));
        service.init();
    }
}
