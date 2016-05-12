package com.bitdubai.android_core.app.common.version_1.communication.client_system_broker;

import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.io.StreamCorruptedException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by MAtias Furszyfer on 2016.04.27..
 */
public class BufferChannelAIDL {


    private static final String TAG = "BufferChannelAIDL";
    private ConcurrentMap<String,ByteArrayOutputStream> objects;
    private ConcurrentMap<String,Object> buffer;
    private ConcurrentMap<String,Lock> locks1;


    public BufferChannelAIDL() {
        this.objects = new ConcurrentHashMap<>();
        locks1 = new ConcurrentHashMap<>();
        buffer = new ConcurrentHashMap<>();
    }

    public void addFullData(String id,Serializable data){
        Log.i(TAG, "Notification object arrived");
        if(data!=null) Log.i(TAG, data.toString());
        Lock lock = locks1.get(id);
        if(lock!=null) {
            synchronized (lock) {
                buffer.put(id, (data != null) ? data : new EmptyObject());
                //locks.get(id).release();
                lock.unblock();
                lock.notify();
            }
        }else{
            Log.e(TAG,"lOCK IS NULL, PLEASE CHECK THIS.class: "+getClass().getName()+" line:"+new Throwable().getStackTrace()[0].getLineNumber());
        }
    }

    public void addChunkedData(String id,byte[] chunkedBytes,boolean isFinish){
        Log.i(TAG,"addChunkedData");
        try {
            if (objects.containsKey(id)) {
                objects.get(id).write(chunkedBytes);
            } else {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                byteArrayOutputStream.write(chunkedBytes);
                objects.put(id,byteArrayOutputStream);
            }
        }catch (IOException e){
            e.printStackTrace();
        }

        if(isFinish){
            Log.i(TAG, "Notification object is finish");
            try {
                notificateObject(id);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else{
            Log.i(TAG, "Notification object is not finish");
        }

    }
    public void notificateObject(String id) throws Exception{
        Log.i(TAG, "Notification object arrived");
        Lock lock = locks1.get(id);
        synchronized (lock){
            //locks.get(id).release();
            lock.unblock();
            lock.notify();
        }

    }

    public boolean lockObject(String id) throws InterruptedException {
        if(!objects.containsKey(id)){
            Log.i(TAG,"waiting for object");
            //Semaphore semaphore = new Semaphore(1);
            //locks.put(id, semaphore);
            Lock lock = new Lock();
            synchronized (lock){
                lock.block();
                locks1.put(id, lock);
                Log.i(TAG, "wainting queue quantity: " + locks1.size());
                while(lock.getIsBlock()){
                    lock.wait();
                    Log.i(TAG, "thread wake up");
                    Log.i(TAG, "Lock is: "+lock.getIsBlock());
                }
            }
            //semaphore.acquire();
        }
        return true;
    }

    public Object getBufferObject(String id){
        try {
            lockObject(id);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }
        Log.i(TAG,"getBufferObject");

        if(buffer.containsKey(id)){
            return buffer.get(id);
        }else {
            ByteArrayOutputStream byteArrayOutputStream = objects.get(id);
            ByteArrayInputStream bis = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
            ObjectInput in = null;
            try {
                in = new ObjectInputStream(bis);
                Object o = in.readObject();
                Log.i(TAG, (o != null) ? o.toString() : "");
                return o;
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (StreamCorruptedException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    bis.close();
                } catch (IOException ex) {
                    // ignore close exception
                }
                try {
                    if (in != null) {
                        in.close();
                    }
                } catch (IOException ex) {
                    // ignore close exception
                }


                //Clean resources
                try {
                    byteArrayOutputStream.close();
                    objects.remove(id);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;

    }



}
