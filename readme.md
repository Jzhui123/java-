# 《综合项目实战：局域网内的沟通软件》

## 需求：

展示一个用户的登录界面，这个界面只要求用户输入自己聊天的昵称就可以了

登录进入后，展示一个群聊的窗口，这个窗口，展示展现人数，展示消息展示框，消息输入框，发送按钮，可以实现群聊、私聊、实现实时展示在线人数，完全做到及时通讯功能

## 技术选型

1. GUI编程技术：Swing
2. 网络编程
3. 面向对象设计
4. 常用API



# 如何启动

1. 先跑server，再跑app，app支持多开，需要手动在运行下拉框中edit config->modify options->allow multiple
2. 输入昵称，即可进入聊天界面



# 效果展示

![image-20250619140043315](C:\Users\lenovo\AppData\Roaming\Typora\typora-user-images\image-20250619140043315.png)



![image-20250619140255436](C:\Users\lenovo\AppData\Roaming\Typora\typora-user-images\image-20250619140255436.png)

=== 双击进行私聊 === 

![image-20250619140417833](C:\Users\lenovo\AppData\Roaming\Typora\typora-user-images\image-20250619140417833.png)



![image-20250619140603661](C:\Users\lenovo\AppData\Roaming\Typora\typora-user-images\image-20250619140603661.png)

## 思路分析

## 1.创建一个模块，代表我们的项目：itheima-chat-system



## 2.拿到系统需要的界面

​	**-- 登录界面：这个界面只要求用户输入自己聊天的昵称就可以了**

```java
package com.itheima.ui;

import javax.swing.*;
import java.awt.event.*;

public class LoginFrame extends JFrame {
    private JTextField nicknameField;
    private JButton loginButton;

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
                    // 可以在这里打开主聊天窗口
                    dispose(); // 关闭登录窗口
                }
            }
        });

        // 添加面板到窗口
        add(panel);
    }

    public static void main(String[] args) {
        // 启动图形界面
        SwingUtilities.invokeLater(() -> {
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
        });
    }
}
```

​		**-- 聊天界面** 

```java
package com.itheima.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashSet;
import java.util.Set;

public class ChatFrame extends JFrame {
    private JTextArea messageArea;
    private JTextArea inputArea;  // 多行输入框
    private JButton sendButton;
    private JLabel onlineCountLabel;
    private JList<String> onlineUserList;
    private DefaultListModel<String> userListModel;

    // 模拟在线用户集合
    private Set<String> onlineUsers = new HashSet<>();

    public ChatFrame(String nickname) {
        setTitle("群聊窗口 - " + nickname);
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // 居中显示

        // 主面板使用 BorderLayout 布局
        setLayout(new BorderLayout());

        // 左侧消息展示区
        messageArea = new JTextArea();
        messageArea.setEditable(false);
        messageArea.setLineWrap(true);
        JScrollPane messageScrollPane = new JScrollPane(messageArea);

        // 右侧面板：在线人数 + 在线用户列表
        JPanel rightPanel = new JPanel(new BorderLayout());
        onlineCountLabel = new JLabel("在线人数: 1");
        onlineCountLabel.setFont(new Font("楷体", Font.BOLD, 16));
        JPanel topRightPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topRightPanel.add(onlineCountLabel);
        topRightPanel.setBorder(BorderFactory.createTitledBorder("在线用户"));

        // 在线用户列表
        userListModel = new DefaultListModel<>();
        userListModel.addElement(nickname); // 添加自己
        onlineUserList = new JList<>(userListModel);
        onlineUserList.setVisibleRowCount(15);
        JScrollPane userScrollPane = new JScrollPane(onlineUserList);

        rightPanel.add(topRightPanel, BorderLayout.NORTH);
        rightPanel.add(userScrollPane, BorderLayout.CENTER);
        rightPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // 分割窗格：左右结构
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, messageScrollPane, rightPanel);
        splitPane.setDividerLocation(600);

        // 底部发送区域
        JPanel bottomPanel = new JPanel(new BorderLayout());
        inputArea = new JTextArea(3, 20); // 多行输入框
        inputArea.setLineWrap(true);
        inputArea.setWrapStyleWord(true);
        JScrollPane inputScrollPane = new JScrollPane(inputArea);

        sendButton = new JButton("发送");
        sendButton.setPreferredSize(new Dimension(80, 30));

        bottomPanel.add(inputScrollPane, BorderLayout.CENTER);
        bottomPanel.add(sendButton, BorderLayout.EAST);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // 将组件添加到主窗口
        add(splitPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        // 发送按钮监听
        sendButton.addActionListener(e -> sendMessage(nickname));

        // 回车发送消息
        inputArea.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER && !e.isShiftDown()) {
                    e.consume(); // 阻止换行
                    sendMessage(nickname);
                }
            }
        });

        // 初始化在线用户（模拟）
        simulateOnlineUsers(nickname);
    }

    // 模拟其他用户上线
    private void simulateOnlineUsers(String selfNickname) {
        onlineUsers.add(selfNickname);
        onlineUsers.add("小明");
        onlineUsers.add("小红");

        updateOnlineUsers();
    }

    // 更新在线用户列表和人数
    private void updateOnlineUsers() {
        userListModel.clear();
        for (String user : onlineUsers) {
            userListModel.addElement(user);
        }
        onlineCountLabel.setText("在线人数: " + onlineUsers.size());
    }

    // 发送消息方法
    private void sendMessage(String nickname) {
        String message = inputArea.getText().trim();
        if (!message.isEmpty()) {
            messageArea.append(nickname + ": " + message + "\n");
            inputArea.setText("");
            // 自动滚动到底部
            messageArea.setCaretPosition(messageArea.getDocument().getLength());
        }
    }

    // 测试入口
    public static void main(String[] args) {
            ChatFrame chatWindow = new ChatFrame("用户A");
            chatWindow.setVisible(true);
    }
}
```



## 3.定义一个App启动类：创建进入界面对象并展示

## 4.分析系统的整体架构



![image-20250618111618877](C:\Users\lenovo\AppData\Roaming\Typora\typora-user-images\image-20250618111618877.png)

1.开发服务端：

	- 接受客户端的管道链接
	- 接收登录消息，接收昵称信息
	- 服务端也可能是接收客户端发送过来的群聊消息
	- 服务端存储全部在线的socket管道，以便到时候知道哪些客户端在线，以便为这些客户端转发消息
	- 如果服务端收到了登录消息，接收昵称，然后更新所有客户端的在线人数列表
	- 如果服务端收到了群聊消息，接收这个人的消息，再转发给所有客户端展示这个消息

2.客户端界面已经做好了



## 5.先开发完整的服务端

- 第一步：创建一个服务端的项目：itheima-chat-server
- 第二部：创建一个服务端启动类，启动服务器等待客户端的链接

```java
package com.itheima;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public static void main(String[] args) {
        System.out.println("服务器启动...");
        //1.注册端口
        try {
            ServerSocket serverSocket = new ServerSocket(Constant.PORT);
            //2.主线程负责接受客户端的连接请求
            while (true) {
                System.out.println("等待客户端连接...");
                Socket socket = serverSocket.accept();
                System.out.println("一个客户端连接了...");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
```

- 第三步：把这个管道交给一个独立的线程来处理：以便支持很多客户端可以同时进行通信

```java
public class Server {
    public static void main(String[] args) {
        System.out.println("服务器启动...");
        //1.注册端口
        try {
            ServerSocket serverSocket = new ServerSocket(Constant.PORT);
            //2.主线程负责接受客户端的连接请求
            while (true) {
                System.out.println("等待客户端连接...");
                Socket socket = serverSocket.accept();
                new ServerReaderThread(socket).start();
                System.out.println("一个客户端连接了...");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
```



- 第四步：定义一个集合容器存储手游登录进来的客户端管道，以便将来群发消息给他们

​	-- 这个集合只需要一个记住所有在线的客户端的socket

```
//定义一个map集合，键是socket，值是客户端的昵称
public static final Map<Socket, String> onlineSockets = new HashMap<>();
```



6.服务端线程开始接收登录消息/群聊消息

- 先接收一个整数，再判断消息类型，制定动作

```java
public class ServerReaderThread extends Thread{
    private Socket s;
    public ServerReaderThread(Socket s) {
        this.s = s;
    }

    @Override
    public void run() {
        try {
            //接收的消息可能有很多种类型：1.登录消息 2. 群聊消息 3. 私聊消息
            //客户端必须声明协议发送消息
            //比如客户端先发1，表示登录；2，表示群聊；3，表示私聊
            //先从socket管道中接收类型编号
            DataInputStream dis = new DataInputStream(s.getInputStream());
            int type = dis.readInt();
            switch (type) {
                case 1:
                    //客户端发来了登录消息，接下来要接收昵称数据，更新全部在线客户端的在线人数列表
                    break;
                case 2:
                    //客户端发来了群聊消息，接下来要接收群聊内容，群聊内容发送给所有在线客户端
                    break;
                case 3:
                    //客户端发来了私聊消息，接下来要接收私聊内容，私聊内容发送给指定的在线客户端
                    break;
            }


        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("客户端下线"+s.getInetAddress().getHostAddress()+":"+s.getPort());
        }
    }
}
```



7. 开始实现服务端的登录消息接收

```java
private void updateClientLineUserList() {
    //更新全部客户端的在线人数列表
    //拿到全部在线客户端的用户昵称，把这些名称转发给全部在线socket管道
    //1. 拿到全部在线socket用户名称
    Collection<String> onLineUsers = Server.onlineSockets.values();
    //2. 把这个集合中的所有用户都发送给全部在线socket管道
    for (Socket socket : Server.onlineSockets.keySet()) {
        try {
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
            dos.writeInt(1);    //告诉客户端，这是在线人数列表 1：在线人数列表  2：群聊消息  3：私聊消息
            dos.writeInt(onLineUsers.size());
            for (String nickname : onLineUsers) {
                dos.writeUTF(nickname);
            }
            dos.flush();
        } catch (Exception e) {
            System.out.println("客户端下线了"+socket.getInetAddress().getHostAddress()+":"+socket.getPort());
            Server.onlineSockets.remove(socket);
        }
    }
}
```



**第七步：接收客户端的群聊消息**

线程每收到一个客户端的群聊消息，就应该把这个消息转发给全部在线的客户端对应的socket管道

```java
private void sendMsgToAll(String msg) {
    //群聊消息发送给全部在线客户端
    //一定要拼装好这个消息再发送给全部在线客户端
    StringBuilder sb = new StringBuilder();
    String name = Server.onlineSockets.get(socket);
    LocalDateTime now = LocalDateTime.now();
    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss EEE a");
    String nowStr = dtf.format(now);

    String msgResult = sb.append(name).append(" ").append(nowStr).append("\r\n").
            append(msg).append("\r\n").toString();
    for (Socket socket : Server.onlineSockets.keySet()) {
        try {
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
            dos.writeInt(2);    //告诉客户端，这是在线人数列表 1：在线人数列表  2：群聊消息  3：私聊消息
           dos.writeUTF(msgResult);
            dos.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
```

## 6.完善整个客户端程序的代码

第一步：从登录界面开始，完成了登录，完成了socket管道传给消息聊天界面

给进入按钮绑定一个事件监听器，让他可以点击，一旦点击了，获取到昵称，然后立即请求与服务器端socket连接，并立即发送登录信息：发送1，发送昵称

再展示客户端的聊天界面：接收到了昵称，接收到了属于自己客户端的socket通信管道

第二步：立即再消息聊天界面，立即读取客户端socket管道从服务端发来的在线人数更新消息/群聊消息

- 交给一个独立的线程专门读取客户端socket从服务端收到的在线人数更新数据和群聊数据
- 一旦受到消息，先判断消息的类型，判断是在线人数更新消息还是群聊消息，分开处理

第三步：接收群聊消息

- 接收消息类型：2，接收到群聊消息，展示到界面的面板上即可

第四步：发送群聊消息

- 给发送按钮绑定一个点击事件，获取输入框的消息内容，先发送2号类型，再把消息发给服务端

##  7.扩展，增加私聊功能

有注释
