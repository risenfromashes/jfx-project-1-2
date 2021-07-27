package edu.buet.net;

import java.io.IOException;
import java.io.Serializable;
import java.net.InetAddress;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class Client extends NetworkProvider {
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
                        System.out.println("Invalid message from server: " + e.getMessage());
                        e.printStackTrace();
                    } catch (ClassCastException e) {
                        System.out.println("Invalid message from server: " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            } catch (IOException e) {
                System.out.println("Connection rejected. Closing: " + e.getMessage());
                closeHandler.accept(socket);
                try {
                    socket.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
    public Client() {
        super();
    }
    public <T extends Serializable>  Client on(Class<T> type, BiConsumer<T, SocketHandle> handler) {
        super.on(type, handler);
        return this;
    }
    public Client onConnect(Consumer<SocketHandle> handler) {
        this.connectionHandler = this.connectionHandler.andThen(handler);
        return this;
    }
    public Client onClose(Consumer<SocketHandle> handler) {
        this.closeHandler = this.closeHandler.andThen(handler);
        return this;
    }
    public SocketHandle connect(InetAddress address, int port) throws IOException {
        var socket = new SocketHandle(address, port);
        executor.submit(new Worker(socket));
        return socket;
    }
}
