package edu.buet.net;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import edu.buet.net.NetworkProvider.Handler;
import edu.buet.util.Test;

//MUST NOT BE ACCESSED BY THREADS SIMULTANEOUSLY
public class SocketHandle {
    private Object idLock = new Object();
    private Object sessionLock = new Object();
    private static long lastId = 0;
    private final long id;
    private long currentSession;
    private int currentSessionMessageCount;
    protected final Socket socket;
    protected ObjectOutputStream out;
    protected ObjectInputStream in;
    private Queue<GenericMessage<?>> sendQueue;
    private Map<Long, GenericMessage<?>> sessions;
    private Object attachment;
    private InetAddress address;
    private int port;
    private NetworkProvider provider;
    public boolean hasSameAddress(InetAddress address, int port) {
        return this.address.equals(address) && this.port == port;
    }
    protected void setAddress(InetAddress address, int port) {
        this.address = address;
        this.port = port;
    }
    public Object getAttachment() {
        return attachment;
    }
    public void setAttachment(Object obj) {
        this.attachment = obj;
    }
    public SocketHandle(NetworkProvider provider, InetAddress address, int port) throws IOException {
        this(provider, new Socket(address, port));
        setAddress(address, port);
    }
    public SocketHandle(NetworkProvider provider, Socket socket) throws IOException {
        synchronized (idLock) {
            this.id = ++lastId;
        }
        this.provider = provider;
        this.currentSession = 0;
        this.socket = socket;
        this.out = new ObjectOutputStream(socket.getOutputStream());
        this.in = new ObjectInputStream(socket.getInputStream());
        this.sendQueue = new ConcurrentLinkedQueue<>();
        this.sessions = new ConcurrentHashMap<>();
        this.attachment = null;
    }
    public int queuedMessageCount() {
        return sendQueue.size();
    }
    public void enqueMessage(GenericMessage<?> message) {
        sendQueue.add(message);
    }
    public GenericMessage<?> dequeMessage() {
        return sendQueue.remove();
    }
    public boolean hasSession() {
        return !sessions.isEmpty();
    }
    public boolean hasSession(long sessionId) {
        return sessions.containsKey(sessionId);
    }
    public void keepSession(GenericMessage<?> message) {
        sessions.put(message.sessionId, message);
    }
    public GenericMessage<?> getSession(long sessionId) {
        return sessions.get(sessionId);
    }
    public void removeSession(long sessionId) {
        sessions.remove(sessionId);
    }
    public long getId() {
        return id;
    }
    public <R extends Serializable> CompletableFuture<R> send(Object sendObject, Class<R> receiveType) {
        var m = currentSession == 0 ? new GenericMessage<R>(sendObject, receiveType) : new GenericMessage<R>(currentSession, sendObject, receiveType);
        sendQueue.add(m);
        return m.future;
    }
    public CompletableFuture<Void> send(Object sendObject) {
        var m = currentSession == 0 ? new GenericMessage<Void>(sendObject, Void.class) : new GenericMessage<Void>(currentSession, sendObject, Void.class);
        sendQueue.add(m);
        return m.future;
    }
    public void writeNow() {
        provider.executor.submit(()-> {
            try {
                write();
            } catch (IOException e) {
                System.out.println("Write exception: " + e.getMessage());
            }
        });
    }
    //send without first waiting for read to finish
    public <R extends Serializable> CompletableFuture<R> sendNow(Object sendObject, Class<R> receiveType) {
        var m = currentSession == 0 ? new GenericMessage<R>(sendObject, receiveType) : new GenericMessage<R>(currentSession, sendObject, receiveType);
        sendQueue.add(m);
        writeNow();
        return m.future;
    }
    //send without first waiting for read to finish
    public CompletableFuture<Void> sendNow(Object sendObject) {
        var m = currentSession == 0 ? new GenericMessage<Void>(sendObject, Void.class) : new GenericMessage<Void>(currentSession, sendObject, Void.class);
        sendQueue.add(m);
        writeNow();
        return m.future;
    }
    //send but do not use session
    public CompletableFuture<Void> notify(Object sendObject) {
        var m = new GenericMessage<Void>(sendObject, Void.class);
        sendQueue.add(m);
        writeNow();
        return m.future;
    }
    //must be accessed by just one thread at a time for this to work
    protected boolean complete(GenericMessage<?> message, Message with) {
        Test.assertEquals(message.sessionId, message.sessionId);
        synchronized (sessionLock) {
            currentSession = message.sessionId;
            currentSessionMessageCount = 0;
            message.complete(with != null ? with.body : null);
            currentSession = 0;
            return currentSessionMessageCount > 0;
        }
    }
    protected void complete(GenericMessage<?> message) {
        complete(message, null);
    }
    // returns true if socket queues up message
    protected boolean handle(Message message, Handler<?> handler) {
        synchronized (sessionLock) {
            currentSessionMessageCount = 0;
            currentSession = message.sessionId;
            handler.handle(message, this);
            currentSession = 0;
            return currentSessionMessageCount > 0;
        }
    }
    protected synchronized boolean hasWrite() {
        return !sendQueue.isEmpty();
    }
    public void close() throws IOException {
        out.close();
        in.close();
        socket.close();
    }
    public synchronized void write() throws IOException {
        while (queuedMessageCount() > 0) {
            var messageout = dequeMessage();
            out.writeUnshared(messageout.toMessage());
            out.flush();
            out.reset();
            if (messageout.isResponseVoid()) {
                complete(messageout);
                removeSession(messageout.sessionId);
            } else {
                keepSession(messageout);
            }
        }
    }
    public void read() throws IOException, ClassNotFoundException {
        if (!provider.handlers.isEmpty() || hasSession()) {
            var messagein = (Message)in.readObject();
            if (hasSession(messagein.sessionId)) {
                var session = getSession(messagein.sessionId);
                if (!complete(session, messagein)) //remove session if no more write
                    removeSession(messagein.sessionId);
            } else {
                var handler = provider.getHandler(messagein.getType());
                if (handler != null) {
                    handle(messagein, handler);
                }
            }
        } else {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {}
        }
    }
}
