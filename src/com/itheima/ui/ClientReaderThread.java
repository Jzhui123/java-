package com.itheima.ui;

import javax.swing.*;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ClientReaderThread extends Thread{
    private Socket socket;
    private DataInputStream dis;
    private ChatFrame win;
    public ClientReaderThread(Socket socket, ChatFrame win) {
        this.socket = socket;
        this.win = win;
    }

    @Override
    public void run() {
        try {
            //接收的消息可能有很多种类型：1.登录消息 2. 群聊消息 3. 私聊消息
            dis = new DataInputStream(socket.getInputStream());
            while (true) {
                int type = dis.readInt();
                switch (type) {
                    case 1:
                        //  服务端发来的在线人数更新消息
                        updateClientOnlineUserList(dis);
                        break;
                    case 2:
                        //  服务端发的群聊消息
                        getMsgToWin();
                        break;
                    case 3:
                        getMsgToWin();
                        break;
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getMsgToWin() throws Exception {
        //获取群聊消息
        String message = dis.readUTF();
        win.setMsgToWin(message);
    }


    private void updateClientOnlineUserList(DataInputStream dis) throws Exception {
        //1.读取有多少个在线用户
        int count = dis.readInt();
        List< String> onLineNames =new ArrayList<>();
        //2.循环控制读取多少个用户信息
        for(int i = 0; i < count; i++){
            //3.读取用户信息
            String nickname = dis.readUTF();
            //4.将昵称装入到集合中
            onLineNames.add(nickname);
        }
        //5.更新到窗口界面上的右侧展示出来
        win.updateOnlineUserList(onLineNames);
    }


}
