package io.chengguo.streaming.rtsp.header;

import android.support.annotation.NonNull;
import android.support.v4.util.ObjectsCompat;

import java.util.Objects;

import static io.chengguo.streaming.utils.Utils.trimSafely;

/**
 * Header
 * Created by fingerart on 2018-07-14.
 */
public abstract class Header<V> {
    private String name;
    private V value;

    public Header(String name, V value) {
        setName(name);
        setRawValue(value);
    }

    public Header(@NonNull String nameOrRawHeader) {
        int iSeparator = nameOrRawHeader.indexOf(":");
        if (iSeparator == -1) {
            setName(nameOrRawHeader);
        } else {
            setName(nameOrRawHeader.substring(0, iSeparator));
            setRawValue(parseValue(nameOrRawHeader.substring(++iSeparator)));
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = trimSafely(name);
    }

    public V getRawValue() {
        return value;
    }

    public void setRawValue(V value) {
        this.value = value;
    }

    /**
     * 解析值
     *
     * @param value
     * @return
     */
    protected abstract V parseValue(String value);

    @Override
    public boolean equals(Object otherHeader) {
        if (otherHeader instanceof Header) {
            return this == otherHeader || (Objects.equals(this.name, ((Header) otherHeader).name) && Objects.equals(this.value, ((Header) otherHeader).value));
        }
        return false;
    }

    @Override
    public String toString() {
        return name + ": " + value;
    }
}