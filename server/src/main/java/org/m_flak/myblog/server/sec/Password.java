package org.m_flak.myblog.server.sec;

public interface Password<T> {
    T getPassword();
    int getSize();
}
