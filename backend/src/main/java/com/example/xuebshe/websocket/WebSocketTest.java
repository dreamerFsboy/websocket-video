package com.example.xuebshe.websocket;

import cn.dev33.satoken.exception.SaTokenException;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.*;
import cn.hutool.extra.spring.SpringUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.xuebshe.common.result.CommonResult;
import com.example.xuebshe.pojo.Login.LoginBean;
import com.example.xuebshe.pojo.Roomnum;
import com.example.xuebshe.pojo.Sys.SysUser;
import com.example.xuebshe.pojo.WSVideo;
import com.example.xuebshe.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static com.example.xuebshe.websocket.Command.*;

/**
 * @ServerEndpoint 注解是一个类层次的注解，它的功能主要是将目前的类定义成一个websocket服务器端,
 * 注解的值将被用于监听用户连接的终端访问URL地址,客户端可以通过这个URL来连接到WebSocket服务器端
 */
@ServerEndpoint(value = "/websocket/{satoken}")
@Component
@Slf4j
public class WebSocketTest {

    private static final String USER_ID = "user_id";
    //借助USER和ROOM进行分区
    private static ConcurrentHashMap<Roomnum, Session> roomnummap = new ConcurrentHashMap<>();
    //房间号 + 房间内的人
    private static ConcurrentMap<Integer, List<Roomnum>> roommap = new ConcurrentHashMap<>();
    //请求列表,谁在请求,请求去哪儿
    private static ConcurrentMap<String, Integer> inviteList = new ConcurrentHashMap<>();

    private UserService userService = SpringUtil.getBean(UserService.class);

    public SysUser getSysUser(String loginjson) {
        CommonResult token = JSON.toJavaObject((JSON) JSON.parse(loginjson), CommonResult.class);
        JSONObject object = (JSONObject) token.getData();
        return userService.findByEmail((String) object.get("email"));
    }

    public void getSize() {
        //显示所有的大小
        log.info("用户-WS SESSION大小" + roomnummap.size() + "\n" + "房间号-房间 列表大小" + roommap.size() + "\n" + "请求列表大小" + inviteList.size());
    }

    public void sendAllInfomation() {
        log.info("关闭发送");
        for (int roomid : roommap.keySet()) {
            sendAllInformation(roomid);
        }
    }

    public void sendAllInformationAndKillThem() throws IOException {
        log.info("关闭发送");
        for (int getInfoRoomid : roommap.keySet()) {
            WSVideo reply = new WSVideo();
            //传输房间内所有消息
            if (roommap.containsKey(getInfoRoomid)) {
                //根据sender我可以反查出来你是哪个小臂崽子
                //反查你麻痹,谁访问就是谁的SESSION
                for (Roomnum roomnum : roommap.get(getInfoRoomid)) {
                    Session session = roomnummap.get(roomnum);
                    session.close();
                    log.info("直接扬了" + roomnum);
                }
            } else {
                log.info("发送出错找不到对应的房间号");
            }
        }
    }

    public void sendAllInformation(int getInfoRoomid) {
        WSVideo reply = new WSVideo();
        //传输房间内所有消息
        if (roommap.containsKey(getInfoRoomid)) {
            byte[] information = JSON.toJSONBytes(roommap.get(getInfoRoomid));
            reply.setControl(GETINFO);
            reply.setRoomid(getInfoRoomid);
            reply.setSender(0);
            reply.setReceiver(0);
            reply.setData(information);

            //根据sender我可以反查出来你是哪个小臂崽子
            //反查你麻痹,谁访问就是谁的SESSION
            for (Roomnum roomnum : roommap.get(getInfoRoomid)) {
                Session session = roomnummap.get(roomnum);
                sendMessage(session, reply.encode());
                log.info("发送全体消息到" + roomnum);
            }
        } else {
            log.info("发送出错找不到对应的房间号");
        }
    }

    public void sendAllInformation(int getInfoRoomid, WSVideo reply) {
        //传输房间内所有消息
        if (roommap.containsKey(getInfoRoomid)) {
            //根据sender我可以反查出来你是哪个小臂崽子
            //反查你麻痹,谁访问就是谁的SESSION
            for (Roomnum roomnum : roommap.get(getInfoRoomid)) {
                Session session = roomnummap.get(roomnum);
                sendMessage(session, reply.encode());
                log.info("发送全体消息到" + roomnum);
            }
        } else {
            log.info("发送出错找不到对应的房间号");
        }
    }

    public void sendAllInformationButExceptMe(int getInfoRoomid, WSVideo reply, Session exceptSession) {
        //传输房间内所有消息
        if (roommap.containsKey(getInfoRoomid)) {
            //根据sender我可以反查出来你是哪个小臂崽子
            //反查你麻痹,谁访问就是谁的SESSION
            for (Roomnum roomnum : roommap.get(getInfoRoomid)) {
                Session session = roomnummap.get(roomnum);
                if (!session.equals(exceptSession)) {
                    sendMessage(session, reply.encode());
                }
                log.info("发送全体消息到" + roomnum);
            }
        } else {
            log.info("发送出错找不到对应的房间号");
        }

    }


    @OnOpen
    public void onOpen(Session session, @PathParam("satoken") String satoken) throws IOException {
        log.debug("收到新连接,连接者TOKEN为" + satoken);
        String loginId = (String) StpUtil.getLoginIdByToken(satoken.replace(" ", ""));
        if (loginId == null) {
            session.close();
            throw new SaTokenException("连接失败无效token");
        }
        //转换loginid为用户对象
        SysUser user = getSysUser(loginId);
        log.info("收到新用户加入连接,用户信息:" + user);
        //创建一个空用户状态
        Roomnum roomnum = new Roomnum();
        roomnum.setRoomid(null);
        roomnum.setAvatar(user.getPicture());
        roomnum.setEmail(user.getEmail());
        roomnum.setNickname(user.getNick());
        //用户默认加入的时候不是主人
        roomnum.setOwner(false);
        roomnummap.put(roomnum, session);
        getSize();
    }

    @OnClose
    public void onClose(Session session) throws IOException {
        System.out.println("关闭连接");
        boolean deadOwner = false;
        if (getRoomNumFromSession(session).isOwner()) {
            System.out.println("房主死了,删掉房主");
            //告诉其他的人让他们去讨生活
            WSVideo reply = new WSVideo();
            reply.setControl(KICK);
            reply.setReceiver(0);
            reply.setSender(0);
            sendAllInformation(getRoomNumFromSession(session).getRoomid(), reply);
            session.close();
            roommap.remove(getRoomNumFromSession(session).getRoomid());
            deadOwner = true;
        }
        if (getRoomNumFromSession(session).getRoomid() != null && !deadOwner) {
            System.out.println("房间里的人死了,收尸");
            roommap.get(getRoomNumFromSession(session).getRoomid()).remove(getRoomNumFromSession(session).getNum() - 1);
        }
        System.out.println(getRoomNumFromSession(session).getEmail() + "这人滚了");
        roomnummap.remove(getRoomNumFromSession(session));
        if (!deadOwner) {
            log.info("死人广播");
            sendAllInformationAndKillThem();
            sendAllInfomation();
        }
        getSize();
    }

    @OnError
    public void onError(Session session, Throwable error) throws IOException {
        System.out.println("发生错误");
        session.close();
        sendAllInfomation();
        error.printStackTrace();
    }

    public void updateAllRoomnum(Roomnum joinNum) {
        Roomnum user2 = getRoomNumFromSession(roomnummap.get(joinNum));
        user2.setEmail(joinNum.getEmail());
        user2.setNickname(joinNum.getNickname());
        user2.setAvatar(joinNum.getAvatar());
        user2.setRoomid(joinNum.getRoomid());
        user2.setOwner(joinNum.isOwner());
        user2.setNum(joinNum.getNum());
        log.info("更新为" + user2);
    }


    public Roomnum getRoomNumFromSession(Session session) {
        for (Roomnum roomnum : roomnummap.keySet()) {
            String id = roomnummap.get(roomnum).getId();
            String id2 = session.getId();
            if (id.equals(id2)) {
                return roomnum;
            }
        }
        return null;
    }

    public void sendSource(int roomid) {
        WSVideo reply = new WSVideo();
        reply.setControl(CHANGESOURCE);
        reply.setSender(0);
        reply.setReceiver(0);
        byte[] c = RandomUtil.randomBytes(1);
        reply.setData(c);
        sendAllInformation(roomid, reply);
    }


    /**
     * 收到客户端消息后调用的方法
     *
     * @param messages 客户端发送过来的消息
     * @param session  可选的参数
     */
    @OnMessage
    public void onMessage(byte[] messages, Session session) {

        //首先对消息进行解码
        WSVideo video = new WSVideo();
        WSVideo reply = new WSVideo();
        video.decode(messages);
        System.out.println("收到消息:" + video);
        //获取这是哪个用户
        Roomnum user = getRoomNumFromSession(session);
        int control = video.getControl();
        switch (control) {
            case CREATEROOM:
                System.out.println(video.getStringMessage());
                //创建房间
                int roomid = 0;
                //生成ID直到ID不在房间号内
                do {
                    roomid = NumberUtil.generateRandomNumber(100, 999, 1)[0];
                } while (roommap.containsKey(roomid));
                //初始化房间
                roommap.put(roomid, new ArrayList<>());
                //设置用户所在会议号
                user.setRoomid(roomid);
                //将用户添加到会议之内
                roommap.get(roomid).add(user);
                //获取用户是第几个人
                user.setOwner(true);
                //static
                user.setNum(roommap.get(roomid).indexOf(user) + 1);
                //给发送者一点小小的回传震撼
                //设置创建房间
                reply.setControl(CREATEROOM);
                //设置房间ID
                reply.setRoomid(roomid);
                //设置发送者为服务器
                reply.setSender(0);
                //设置接收者为用户的会议第几位
                reply.setReceiver(user.getNum());
                reply.setData(null);
                //返回
                sendMessage(session, reply.encode());
                sendAllInformation(roomid);
                break;
            case JOINASK:
                if (!roommap.containsKey(video.getRoomid())) {
                    reply.setControl(JOINUNEXIST);
                    sendMessage(session, reply.encode());
                    break;
                }
                //否则给里面的OWNER发消息,问让不让进
                for (Roomnum roomnum : roommap.get(video.getRoomid())) {
                    if (roomnum.isOwner()) {
                        //找到正主,拿它获取Session
                        Session targetuser = roomnummap.get(roomnum);
                        //给它发送一个JOINASK请求,携带参数为JSON字符串化之后的用户Email和昵称.
                        Map<String, String> userinformation = new HashMap<>();
                        userinformation.put("email", user.getEmail());
                        userinformation.put("nickname", user.getNickname());
                        userinformation.put("avatar", user.getAvatar());
                        reply.setControl(JOINASK);
                        reply.setRoomid(video.getRoomid());
                        reply.setData(JSON.toJSONBytes(userinformation));
                        //将这个消息放在等待处理的invite队列中
                        inviteList.put(user.getEmail(), roomnum.getRoomid());
                        sendMessage(targetuser, reply.encode());
                    }
                }
                break;
            case JOINAGREE:
                //读取消息String,并搜索是否有这个请求
                SysUser joiner = userService.findByEmail(video.getStringMessage());
                if (inviteList.containsKey(video.getStringMessage())) {//
                    //我们收到的时候,USER是同意的房主.我们需要根据email反查到申请的用户,将他塞进房间
                    Roomnum joinNum = new Roomnum();
                    //用户不是主人
                    joinNum.setOwner(false);
                    //用户设置Email
                    joinNum.setEmail(joiner.getEmail());
                    joinNum.setNickname(joiner.getNick());
                    joinNum.setAvatar(joiner.getPicture());
                    joinNum.setRoomid(inviteList.get(video.getStringMessage()));
                    //用户加入到房间内
                    roommap.get(joinNum.getRoomid()).add(joinNum);
                    //分配给用户一个ID
                    joinNum.setNum(roommap.get(joinNum.getRoomid()).indexOf(joinNum) + 1);
                    //销毁掉请求
                    inviteList.remove(joinNum.getEmail());
                    //更新Session表
                    updateAllRoomnum(joinNum);
                    //给它发送JOINAGREE
                    reply.setControl(JOINAGREE);
                    reply.setSender(0);
                    reply.setRoomid(joinNum.getRoomid());
                    //设置他是第几位
                    reply.setReceiver(joinNum.getNum());
                    //发送消息
                    sendMessage(roomnummap.get(joinNum), reply.encode());
                    //全局通告
                    sendAllInformation(video.getRoomid());
                    //发送CHANGESOURCE
                    sendSource(video.getRoomid());
                    break;
                } else {
                    //请求已经过期,让他滚蛋
                    reply.setControl(JOINREFUSE);
                    Roomnum joinNum = new Roomnum();
                    joinNum.setEmail(joiner.getEmail());
                    sendMessage(roomnummap.get(joinNum), reply.encode());
                    break;
                }
            case JOINREFUSE:
                //我们收到的时候,USER是拒绝的OWNER.我们需要根据email反查到申请的用户,让他滚蛋
                SysUser joiner2 = userService.findByEmail(video.getStringMessage());
                //如果能找到就销毁对应的请求,找不到就是他应该直接滚蛋
                if (inviteList.containsKey(video.getStringMessage())) {
                    inviteList.remove(joiner2.getEmail());
                }
                reply.setControl(JOINREFUSE);
                //由于我们存储的是用户对象,要用我们构造的去获取真实的
                Roomnum joinNum = new Roomnum();
                joinNum.setEmail(joiner2.getEmail());
                //这样就能找到发送给谁了
                sendMessage(roomnummap.get(joinNum), reply.encode());
                break;
            case GETINFO:
                //传输房间内所有消息
                int getInfoRoomid = video.getRoomid();
                if (roommap.containsKey(getInfoRoomid)) {
                    byte[] information = JSON.toJSONBytes(roommap.get(getInfoRoomid));
                    reply.setControl(GETINFO);
                    reply.setRoomid(video.getRoomid());
                    reply.setSender(0);
                    reply.setReceiver(video.getReceiver());
                    reply.setData(information);
                    //根据sender我可以反查出来你是哪个小臂崽子
                    //反查你麻痹,谁访问就是谁的SESSION

                    sendMessage(session, reply.encode());
                    break;
                } else {
                    //房间号不存在,你还好意思给我GETINFO?
                    reply.setControl(GETINFO);
                    reply.setSender(0);
                    sendMessage(session, reply.encode());
                    break;
                }
            case KICK:
                //妈的滚啊!
                //奶奶滴我都忘了前面获取了当前的USER了
                if (!user.isOwner() && !roommap.get(video.getRoomid()).get(video.getReceiver() - 1).equals(user)) {
                    //不是房主是黑客,什么傻逼我不说
                    reply.setControl(KICK);
                    reply.setSender(0);
                    reply.setRoomid(user.getRoomid());
                    reply.setData(StrUtil.bytes("NOTOWNER"));
                    sendMessage(session, reply.encode());
                    //用户应该不知道发生了什么才对
                    //sendAllInformation(video.getRoomid());
                } else {
                    //判断这B踹谁,我们目前先认为是要踹会议内,也就是receiver代表踹的是谁
                    try {
                        if (video.getReceiver() == 0) {
                            //代表踹出全体
                            reply.setControl(KICK);
                            reply.setReceiver(0);
                            reply.setSender(0);
                            //告知你们被解雇了(不是
                            sendAllInformation(video.getRoomid(), reply);
                            //去掉那个room
                            roommap.remove(video.getRoomid());
                            break;
                        }
                        //由于0号位被用于服务器,所以要 - 1
                        Roomnum beKickedPeople = new Roomnum();
                        //roommap.get(user.getRoomid()).get(video.getReceiver() - 1);
                        beKickedPeople.setEmail(video.getStringMessage());
                        Session beKickedPeopleSession = roomnummap.get(beKickedPeople);
                        roommap.get(user.getRoomid()).remove(beKickedPeople);
                        //踹出之后告知用户被踹出
                        reply.setSender(0);
                        reply.setReceiver(0);
                        reply.setControl(KICK);
                        sendMessage(beKickedPeopleSession, reply.encode());
                        //更新INFORMATION
                        sendAllInformation(video.getRoomid());
                        break;
                    } catch (Exception e) {
                        e.printStackTrace();
                        sendAllInformation(video.getRoomid());
                        break;
                    }

                }
            case AS:
                reply.setRoomid(video.getRoomid());
                reply.setControl(AS);
                reply.setData(video.getData());
                reply.setReceiver(0);
                reply.setSender(video.getSender());
                sendAllInformationButExceptMe(reply.getRoomid(), reply, session);
                break;
            case VS:
                reply.setRoomid(video.getRoomid());
                reply.setControl(VS);
                reply.setData(video.getData());
                reply.setReceiver(0);
                reply.setSender(video.getSender());
                sendAllInformationButExceptMe(reply.getRoomid(), reply, session);
                break;
            case MESSAGE:
                if(video.getReceiver()==0)
                {
                    //直接消息
                    reply.setRoomid(video.getRoomid());
                    reply.setSender(video.getSender());
                    reply.setReceiver(0);
                    reply.setControl(MESSAGE);
                    reply.setData(video.getData());
                    sendAllInformation(reply.getRoomid(),reply);
                }
                else{
                    //私聊消息
                    reply.setRoomid(video.getRoomid());
                    reply.setSender(video.getSender());
                    reply.setReceiver(video.getReceiver());
                    reply.setControl(MESSAGE);
                    reply.setData(video.getData());
                    try{
                        Roomnum receiver = roommap.get(video.getRoomid()).get(video.getReceiver());
                        Session receiverSession =  roomnummap.get(receiver);
                        sendMessage(receiverSession,reply.encode());
                    }
                    catch(Exception e)
                    {
                        log.info("似乎没获取到这个人");
                    }

                }
            default:
                log.info("我草发生了什么事");
        }
    }

    public static void sendMessageAsync(Session session, byte[] message) {
        try {
            WSVideo video = new WSVideo();
            video.decode(message);
            if(video.getControl() == AS || video.getControl() == VS)
            {
                log.info("[异步]向SID为:" + session.getId() + "发送Byte:" + Arrays.toString(video.getData()));
            }
            else{
                log.info("[异步]向SID为:" + session.getId() + "发送" + video.getStringMessage());
            }
            session.getAsyncRemote().sendBinary(ByteBuffer.wrap(message));
        } catch (Exception e) {
            e.printStackTrace();
            log.info("什么傻逼打我服务器");
        }
    }

    // 向指定客户端推送消息
    public static void sendMessage(Session session, byte[] message) {
        boolean test = false;
        if (test) {
            sendMessageAsync(session, message);
            return;
        }
        synchronized(session){
        try {
            WSVideo video = new WSVideo();
            video.decode(message);
            if(video.getControl() == AS || video.getControl() == VS)
            {
                log.info("[同步]向SID为:" + session.getId() + "发送Byte:");// Arrays.toString(video.getData()));
            }
            else{
                log.info("[同步]向SID为:" + session.getId() + "发送" + video.getStringMessage());
            }
//            System.out.println("向sid为：" + session.getId() + "，发送：" + Arrays.toString(message));
            OutputStream os = session.getBasicRemote().getSendStream();
            os.write(message);
            os.flush();
            os.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        }
    }


}
