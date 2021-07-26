package edu.buet.net;

import java.io.IOException;
import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

class Message implements Serializable {
    private static final Object sessionIdLock = new Object();
    private static long lastSessionId = 0;
    protected final long sessionId;
    protected final Object body;
    protected Message(long referralID, Object body) {
        this.sessionId = referralID;
        this.body = body;
    }
    protected Message(Object body) {
        synchronized (sessionIdLock) {
            this.sessionId = ++lastSessionId;
        }
        this.body = body;
    }
    public Class<?> getType() {
        return body.getClass();
    }
}
class GenericMessage<R> extends Message {
    protected final Class<R> receiveType;
    protected final CompletableFuture<R> future;
    protected GenericMessage(long sessionId, Object sendObject, Class<R> receiveType) {
        super(sessionId, sendObject);
        this.receiveType = receiveType;
        this.future = new CompletableFuture<>();
    }
    protected GenericMessage(Object sendObject, Class<R> receiveType) {
        super(sendObject);
        this.receiveType = receiveType;
        this.future = new CompletableFuture<>();
    }
    protected void complete(Object obj) throws ClassCastException {
        if (obj != null)
            future.complete(receiveType.cast(obj));
        else
            future.complete(null);
    }
    public Message toMessage() {
        return new Message(sessionId, body);
    }
    public boolean isResponseVoid() {
        return receiveType.equals(Void.class);
    }
    public Class<R> getResponseType() {
        return receiveType;
    }
}

public abstract class NetworkProvider {
    protected Consumer<SocketHandle> connectionHandler = null;
    protected Map<Class<?>, Handler<?>> handlers;
    protected ExecutorService executor;
    protected class Handler<T> {
        Class<T> type;
        BiConsumer<T, SocketHandle> handler;
        public Handler(Class<T> type, BiConsumer<T, SocketHandle> handler) {
            this.type = type;
            this.handler = handler;
        }
        public void handle(Message message, SocketHandle socket) {
            handler.accept(type.cast(message.body), socket);
        }
    }
    protected synchronized Handler<?> getHandler(Class<?> type) {
        if (handlers.containsKey(type)) { //if direct match, great!
            return handlers.get(type);
        } else { //otherwise look for assignable class handlers
            for (var entry : handlers.entrySet()) {
                if (entry.getKey().isAssignableFrom(type)) {
                    return entry.getValue();
                }
            }
        }
        return null;
    }
    protected abstract class Worker implements Runnable {
        SocketHandle socket;
        public Worker(SocketHandle socket) {
            this.socket = socket;
        }
        public void write() throws IOException {
            while (socket.queuedMessageCount() > 0) {
                var messageout = socket.dequeMessage();
                socket.out.writeObject(messageout.toMessage());
                if (messageout.isResponseVoid()) {
                    socket.complete(messageout);
                    socket.removeSession(messageout.sessionId);
                } else {
                    socket.keepSession(messageout);
                }
            }
        }
        public void read() throws IOException, ClassNotFoundException {
            var messagein = (Message)socket.in.readObject();
            if (socket.hasSession(messagein.sessionId)) {
                var session = socket.getSession(messagein.sessionId);
                socket.complete(session, messagein);
            } else {
                var handler = getHandler(messagein.getType());
                if (handler != null) {
                    socket.handle(messagein, handler);
                }
            }
        }
        public void loop() throws IOException, ClassNotFoundException {
            write();
            read();
        }
        public abstract void run();
    }
    public NetworkProvider() {
        handlers = new ConcurrentHashMap<>();
        executor = Executors.newCachedThreadPool();
    }
    public synchronized <T extends Serializable>  NetworkProvider on(Class<T> type, BiConsumer<T, SocketHandle> handler) {
        var h = new Handler<T>(type, handler );
        handlers.put(type, h);
        return this;
    }
}
