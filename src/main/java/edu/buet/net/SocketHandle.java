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
    private static long lastId = 0;
    private final long id;
    private long currentSession;
    protected final Socket socket;
    protected ObjectOutputStream out;
    protected ObjectInputStream in;
    private Queue<GenericMessage<?>> sendQueue;
    private Map<Long, GenericMessage<?>> sessions;
    private Object attachment;
    public Object getAttachment(){
        return attachment;
    }
    public void setAttachment(Object obj){
        this.attachment = obj;
    }
    public SocketHandle(InetAddress address, int port) throws IOException {
        this(new Socket(address, port));
    }
    public SocketHandle(Socket socket) throws IOException {
        synchronized (idLock) {
            this.id = ++lastId;
        }
        this.currentSession = 0;
        this.socket = socket;
        this.out = new ObjectOutputStream(socket.getOutputStream());
        this.in = new ObjectInputStream(socket.getInputStream());
        this.sendQueue = new ConcurrentLinkedQueue<>();
        this.sessions = new ConcurrentHashMap<>();
        this.attachment = null;
        this.socket.setSoTimeout(1000);
    }
    public synchronized int queuedMessageCount() {
        return sendQueue.size();
    }
    public void enqueMessage(GenericMessage<?> message) {
        sendQueue.add(message);
    }
    public GenericMessage<?> dequeMessage() {
        return sendQueue.remove();
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
    //must be accessed by just one thread at a time for this to work
    protected void complete(GenericMessage<?> message, Message with) {
        Test.assertEquals(message.sessionId, message.sessionId);
        currentSession = message.sessionId;
        message.complete(with != null ? with.body : null);
        currentSession = 0;
    }
    protected void complete(GenericMessage<?> message) {
        complete(message, null);
    }
    protected void handle(Message message, Handler<?> handler) {
        currentSession = message.sessionId;
        handler.handle(message, this);
        currentSession = 0;
    }
    public void close() throws IOException {
        out.close();
        in.close();
        socket.close();
    }
}
