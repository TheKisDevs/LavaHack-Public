/*
 * This program was written by Tobias Schmidradler (schmidi000)
 * and published under the MIT License.
 * 
 * What kind of program is this?
 * This is a program for performing a Distributed Denial of Serivce (DDOS).
 * With a DDOS you could make a host in the network unreachable.
 *
 * 
 * All rights are reserved.
 * 
 * NOTE: 
 * ------------------------------------------------------------------------------------------
 * This program is only for educational purposes!
 * You use this on your own risk. 
 * Distributed Denial of Service attacks are illegal, you could go to jail for this.
 * ------------------------------------------------------------------------------------------
 * 
 * I am liable for nothing!
 *
 * Link: https://github.com/schmidi000/JDOS
 * E-Mail: tobias.schmidradler@gmail.com
 * Website: www.straim.com
 * Copyright (c) 2015
 */
package com.kisman.cc.util.process.web;

import java.net.SocketAddress;

/**
 * Abstract super class of all different protocols.
 * Every protocol implementation must inherit from this abstract class.
 * 
 * @author Tobias Schmidradler
 * 
 * Last changed: 13.05.2015
 */
public abstract class DDOS implements Runnable {
    private SocketAddress address;

    /**
     * This method writes something (Protocol dependent) to the socket.
     */
    public abstract void writeLineToSocket(String message);

    /**
     * Creates the socket. (Protocol dependent)
     */
    protected abstract void createSocket();

    /**
     * Connect to the socket. (Protocol dependent)
     */
    protected abstract void connectToSocket();

    /**
     * Close the socket (Protocol dependent)
     */
    protected abstract void closeSocket();

    /**
     * Get the address of the victim
     */
    public SocketAddress getAddress() {
        return address;
    }

    /**
     * Set the address of the victim
     */
    public void setAddress(SocketAddress address) {
        this.address = address;
    }
}