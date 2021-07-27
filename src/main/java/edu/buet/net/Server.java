package edu.buet.net;

import java.io.IOException;
import java.io.Serializable;
import java.net.ServerSocket;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class Server extends NetworkProvider {
    private class Worker extends NetworkProvider.Worker {
        public Worker(SocketHandle socket) {
            super(socket);
        }
        public void run() {
            connectionHandler.accept(socket);
            try {
                while (!Thread.interrupted()) {
                    try {
                        super.loop();
                    } catch (ClassNotFoundException e) {
                        System.out.println("Invalid message from client: " + e.getMessage());
                    } catch (ClassCastException e) {
                        System.out.println("Invalid message from client: " + e.getMessage());
                    }
                }
            } catch (IOException e) {
                System.out.println("Connection rejected closing: " + e.getMessage());
                closeHandler.accept(socket);
                try {
                    socket.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
    public Server() {
        super();
    }
    public <T extends Serializable>  Server on(Class<T> type, BiConsumer<T, SocketHandle> handler) {
        super.on(type, handler);
        return this;
    }
    public Server onAccept(Consumer<SocketHandle> handler) {
        this.connectionHandler = this.connectionHandler.andThen(handler);
        return this;
    }
    public Server onClose(Consumer<SocketHandle> handler) {
        this.closeHandler = this.closeHandler.andThen(handler);
        return this;
    }
    public void run(int port) throws IOException {
        ServerSocket listener = new ServerSocket(port);
        executor.submit(()-> {
            try {
                while (!Thread.interrupted()) {
                    var socket = new SocketHandle(this, listener.accept());
                    executor.submit(new Worker(socket));
                }
            } catch (IOException e) {
                System.out.println("Socket prematurely closed: " + e.getMessage());
            } finally {
                try {
                    listener.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
