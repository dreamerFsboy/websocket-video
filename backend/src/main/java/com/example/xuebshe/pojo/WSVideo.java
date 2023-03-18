package com.example.xuebshe.pojo;


import cn.hutool.core.codec.Base64;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ByteUtil;
import cn.hutool.core.util.StrUtil;
import lombok.Data;
import lombok.ToString;

import java.util.Arrays;

import static com.example.xuebshe.websocket.Command.*;

@Data
public class WSVideo {
    int control;
    int roomid;
    int sender;
    int receiver;
    int length;
    byte[] data;

    public WSVideo() {
        control = 0;
        roomid = 0;
        sender = 0;
        receiver = 0;
        length = 0;
        data = null;
    }

    //循环累加、或运算都可以
    public static int byteArrayToIntBest(byte[] bytes){
        return (bytes[3] & 0xff)
                |((bytes[2] & 0xff) << 8)
                |((bytes[1] & 0xff) << 16)
                |((bytes[0] & 0xff) << 24);
    }

    /**
     * 拼接byte数组
     * @param data1
     * @param data2
     * @return 拼接后数组
     */
    public static byte[] addBytes(byte[] data1, byte[] data2) {
        byte[] data3 = new byte[data1.length + data2.length];
        System.arraycopy(data1, 0, data3, 0, data1.length);
        System.arraycopy(data2, 0, data3, data1.length, data2.length);
        return data3;
    }

    public static byte[] intToByteArrayBest(int i){
        return new byte[]{
                (byte)(i >>> 24),
                (byte)(i >>> 16),
                (byte)(i >>> 8),
                (byte)i
        };
    }

    public void decode(byte[] wsdata){
       byte[][] newlist = ArrayUtil.split(wsdata,4);
       this.control = byteArrayToIntBest(newlist[0]);
       this.roomid = byteArrayToIntBest(newlist[1]);
       this.sender = byteArrayToIntBest(newlist[2]);
       this.receiver = byteArrayToIntBest(newlist[3]);
       this.length = byteArrayToIntBest(newlist[4]);
       data = new byte[wsdata.length - 20];
       System.arraycopy(wsdata,20,data,0,wsdata.length - 20);
    }
    public String getStringMessage(){
        return Base64.decodeStr(Base64.encode(data));
    }



    public byte[] encode(){
        return ArrayUtil.addAll(intToByteArrayBest(this.control),
                intToByteArrayBest(this.roomid),
                intToByteArrayBest(this.sender),
                intToByteArrayBest(this.receiver),
                intToByteArrayBest(this.length),
                data
                );
    }

    @Override
    public String toString(){
        String result = "";
        boolean needParse = true;
        switch (control){
            case CREATEROOM:
                result+="创建房间";
                break;
            case JOINASK:
                result+="申请加入";
                break;
            case JOINUNEXIST:
                result+="加入不存在房间";
                break;
            case JOINAGREE:
                result+="同意加入";
                break;
            case JOINREFUSE:
                result+="拒绝加入";
                break;
            case CHANGESOURCE:
                result+="改源";
                break;
            case GETINFO:
                result+="获取信息";
                break;
            case KICK:
                result+="踢出用户";
                break;
            case VS:
                result+="视频流";
                needParse = false;
                break;
            case AS:
                result+="音频流";
                needParse = false;
                break;
            case MESSAGE:
                result+="消息";
                break;
            default:

                result+="尚未定义";
                needParse = false;
        }
        result=result + " 房间号为:" + roomid + "发送者ID:" + sender + "接收者ID:" + receiver + "长度信息(弃用)" + length;
        if(needParse)
        {
            result = result + "解析信息为" + getStringMessage();
        }
        else{
            result = result + "流信息;";// + Arrays.toString(data);
        }
        return result;
    }

}
