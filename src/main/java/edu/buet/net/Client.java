package edu.buet.net;

import java.io.IOException;
import java.io.Serializable;
import java.net.InetAddress;
import java.util.concurrent.CompletableFuture;
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
    public synchronized boolean isConnected() {
        return !sockets.isEmpty();
    }
    public SocketHandle connect(InetAddress address, int port) throws IOException {
        var socket = new SocketHandle(this, address, port);
        executor.submit(new Worker(socket));
        return socket;
    }
    public CompletableFuture<SocketHandle> connectAsync(InetAddress address, int port, boolean newSocket, boolean retry) {
        System.out.println("trying to connect");
        var future = new CompletableFuture<SocketHandle>();
        executor.submit(()-> {
            if (!newSocket) {
                for (var socket : sockets.values()) {
                    if (socket.hasSameAddress(address, port)) {
                        System.out.println("prev socket found");
                        future.complete(socket);
                        return;
                    }
                }
            }
            do {
                try {
                    var socket = connect(address, port);
                    System.out.println("Connected");
                    future.complete(socket);
                    break;
                } catch (IOException e) {
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException err) {}
                }
            } while (!Thread.interrupted() && retry);
        });
        return future;
    }
    public CompletableFuture<SocketHandle> connectAsync(InetAddress address, int port) {
        return connectAsync(address, port, false, true);
    }
    public void shutdown() {
        executor.shutdownNow();
    }
}
