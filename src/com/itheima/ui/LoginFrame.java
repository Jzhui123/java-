package com.itheima.ui;

import javax.swing.*;
import java.awt.event.*;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class LoginFrame extends JFrame {
    private JTextField nicknameField;
    private JButton loginButton;
    private Socket socket; //记录当前客户端系统的通信管道

    public LoginFrame() {
        // 设置窗口标题
        setTitle("聊天登录");
        setSize(300, 150);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // 居中显示

        // 创建面板
        JPanel panel = new JPanel();
        panel.setLayout(null);

        // 创建组件
        JLabel label = new JLabel("请输入昵称:");
        nicknameField = new JTextField(20);
        loginButton = new JButton("登录");

        // 设置组件位置和大小
        label.setBounds(30, 20, 80, 25);
        nicknameField.setBounds(110, 20, 150, 25);
        loginButton.setBounds(100, 60, 100, 30);

        // 添加组件到面板
        panel.add(label);
        panel.add(nicknameField);
        panel.add(loginButton);

        // 添加动作监听器
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String nickname = nicknameField.getText().trim();
                if (nickname.isEmpty()) {
                    JOptionPane.showMessageDialog(LoginFrame.this, "昵称不能为空！", "错误", JOptionPane.ERROR_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(LoginFrame.this, "欢迎你，" + nickname + "！", "登录成功", JOptionPane.INFORMATION_MESSAGE);
                    try {
                        Login(nickname);
                        //进入聊天室逻辑
                        new ChatFrame(nickname,socket);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    dispose(); // 关闭登录窗口
                }
            }
        });

        // 添加面板到窗口
        add(panel);
        setVisible(true);
    }

    private void Login(String nickname) throws Exception {
        //立即发送登录消息给服务端程序
        //1.创建socket管道，请求与服务端socket连接
        socket = new Socket(Constant.SERVER_IP, Constant.PORT);
        //2.立即发送消息1和昵称
        DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
        dos.writeInt(1);
        dos.writeUTF(nickname);
        dos.flush();
    }

    public static void main(String[] args) {
        // 启动图形界面
        SwingUtilities.invokeLater(() -> {
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
        });
    }
}