package com.example.jjt_ssd.streetlamp.Tools;

import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Vector;

/**
 * Created by JJT-ssd on 2016/8/30.
 */
public  class MyDictionary<K,V> extends Dictionary<K,V> {

    private Vector<K> keys;
    private Vector<V> elements;
    public MyDictionary() {
        this.keys = new Vector<K>();
        this.elements = new Vector<V>();
    }

    public Enumeration<K> keys() {
        return this.keys.elements();
    }

    public Enumeration<V> elements() {
        return this.elements.elements();
    }

    public V get(Object key) {
        int i = keys.indexOf(key);
        return this.elements.get(i);
    }

    public boolean isEmpty() {
        return this.keys.size() == 0;
    }

    public V put(Object key, Object value) {
        int i = keys.indexOf(key);
        if (i != -1) {
            V oldElement = this.elements.elementAt(i);
            this.elements.setElementAt((V) value, i);
            return oldElement;
        } else {
            this.keys.addElement((K) key);
            this.elements.addElement((V) value);
            return null;
        }
    }

    public V remove(Object key) {
        int i = this.keys.indexOf(key);
        if (i != -1) {
            V oldElement = this.elements.elementAt(i);
            this.keys.removeElementAt(i);
            this.elements.removeElementAt(i);
            return oldElement;
        } else {
            return null;
        }

    }
    public int size() {
        return this.keys.size();
    }
}
