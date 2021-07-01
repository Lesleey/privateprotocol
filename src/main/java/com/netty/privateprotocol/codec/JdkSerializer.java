package com.netty.privateprotocol.codec;

import io.netty.buffer.ByteBuf;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * @author Lesleey
 * @date 2021/7/1-21:50
 * @function 用于序列化和反序列化对象
 */
public class JdkSerializer {

    /**
     *  序列化对象， 并写入到 byteBuf中
     * @param byteBuf
     * @param value
     */
    public void writeObject(ByteBuf byteBuf, Object value) {
        try(ByteArrayOutputStream byteOs = new ByteArrayOutputStream();
            ObjectOutputStream os =  new ObjectOutputStream(byteOs);){
            os.writeObject(value);
            byte[] bytes = byteOs.toByteArray();
            byteBuf.writeInt(bytes.length);
            byteBuf.writeBytes(bytes);
        }catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException("序列化出现异常！" + e.getMessage());
        }
    }

    public Object readObject(ByteBuf byteBuf){
        int objectBytesLenth = byteBuf.readInt();
        if(objectBytesLenth == 0)
            return null;
        byte[] byteArr = new byte[objectBytesLenth];
        byteBuf.readBytes(byteArr);
        try(ByteArrayInputStream byteIs = new ByteArrayInputStream(byteArr);
            ObjectInputStream is =  new ObjectInputStream(byteIs);){
            return is.readObject();
        }catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException("序列化出现异常！" + e.getMessage());
        }
    }
}
