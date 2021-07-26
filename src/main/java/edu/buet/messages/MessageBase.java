package edu.buet.messages;

import java.io.Serializable;

abstract class MessageBase<T> implements Serializable {

    protected final boolean success;
    protected final T body;
    protected final String message;

    protected MessageBase(boolean success, String message, T body) {
        this.success = success;
        this.message = message;
        this.body = body;
    }
    protected MessageBase() {
        this.success = false;
        this.message = null;
        this.body = null;
    }

    T get() {
        return body;
    }

    boolean success() {
        return success;
    }
}
