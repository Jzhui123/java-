package com.itheima.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.List;

public class ChatFrame extends JFrame {
    private JTextArea messageArea;
    private JTextArea inputArea;  // 多行输入框
    private JButton sendButton;
    private JLabel onlineCountLabel;
    private JList<String> onlineUserList;
    private Socket socket;
    private String nickname;

    public ChatFrame(String nickname) {
        setTitle("群聊窗口 - " + nickname);
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // 居中显示

        // 使用自定义的背景面板
        BackgroundPanel mainPanel = new BackgroundPanel();
        mainPanel.setLayout(new BorderLayout());

        // 左侧消息展示区
        messageArea = new JTextArea();
        messageArea.setEditable(false);
        messageArea.setLineWrap(true);
        messageArea.setOpaque(false);
        messageArea.setForeground(Color.BLUE);
        messageArea.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        JScrollPane messageScrollPane = new JScrollPane(messageArea);
        messageScrollPane.setOpaque(false);
        messageScrollPane.getViewport().setOpaque(false);

        // 右侧面板：在线人数 + 在线用户列表
        JPanel rightPanel = new JPanel(new BorderLayout());
        onlineCountLabel = new JLabel("在线人数: 1");
        onlineCountLabel.setFont(new Font("楷体", Font.BOLD, 16));
        onlineCountLabel.setForeground(Color.RED);
        JPanel topRightPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topRightPanel.add(onlineCountLabel);
        topRightPanel.setBorder(BorderFactory.createTitledBorder("在线用户"));
        topRightPanel.setOpaque(false);

        onlineUserList = new JList<>();
        onlineUserList.setVisibleRowCount(15);
        onlineUserList.setOpaque(false);
        onlineUserList.setForeground(Color.RED);
        // 设置自定义的ListCellRenderer以确保每个列表项背景透明
        onlineUserList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setOpaque(false); // 使组件背景透明
                return this;
            }
        });
        JScrollPane userScrollPane = new JScrollPane(onlineUserList);
        userScrollPane.setOpaque(false);
        userScrollPane.getViewport().setOpaque(false);

        rightPanel.add(topRightPanel, BorderLayout.NORTH);
        rightPanel.add(userScrollPane, BorderLayout.CENTER);
        rightPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        rightPanel.setOpaque(false);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, messageScrollPane, rightPanel);
        splitPane.setDividerLocation(600);
        splitPane.setOpaque(false);

        // 底部发送区域
        JPanel bottomPanel = new JPanel(new BorderLayout());
        inputArea = new JTextArea(3, 20);
        inputArea.setLineWrap(true);
        inputArea.setWrapStyleWord(true);
        inputArea.setOpaque(false);
        inputArea.setBackground(new Color(0, 0, 0, 0));
        JScrollPane inputScrollPane = new JScrollPane(inputArea);
        inputScrollPane.setOpaque(false);
        inputScrollPane.getViewport().setOpaque(false);

        sendButton = new JButton("发送");
        sendButton.setPreferredSize(new Dimension(80, 30));
        sendButton.setOpaque(false);
        sendButton.setContentAreaFilled(false);
        sendButton.setBorderPainted(false);
        sendButton.setForeground(Color.GREEN);
        // 调大字体：使用你喜欢的字体和字号，比如：
        sendButton.setFont(new Font("微软雅黑", Font.BOLD, 16)); // 字号设为16，加粗显示

        bottomPanel.add(inputScrollPane, BorderLayout.CENTER);
        bottomPanel.add(sendButton, BorderLayout.EAST);
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // 添加组件到主面板
        mainPanel.add(splitPane, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        // 设置内容面板为背景面板
        setContentPane(mainPanel);

        // 发送按钮监听
        sendButton.addActionListener(e -> sendMessage());

        // 回车发送消息
        inputArea.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER && !e.isShiftDown()) {
                    e.consume(); // 阻止换行
                    sendMessage();
                }
            }
        });

        // 双击用户列表项发送私聊消息
        onlineUserList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e) && e.getClickCount() == 2) {
                    String selectedNickname = onlineUserList.getSelectedValue();
                    if (selectedNickname != null && !selectedNickname.equals(nickname)) {
                        sendPrivateMessage(selectedNickname);
                    }
                }
            }
        });

        setVisible(true);
    }

    public ChatFrame(String nickname, Socket socket) {
        this(nickname);
        this.nickname = nickname;
        this.socket = socket;
        new ClientReaderThread(socket, this).start();
    }

    // 发送消息方法
    private void sendMessage() {
        String message = inputArea.getText().trim();
        inputArea.setText("");
        if (!message.isEmpty()) {
            try {
                DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
                dos.writeInt(2);
                dos.writeUTF(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //更新在线人数
    public void updateOnlineUserList(List<String> onLineNames) {
        onlineUserList.setListData(onLineNames.toArray(new String[0]));
        onlineCountLabel.setText("在线人数: " + onLineNames.size());
    }

    public void setMsgToWin(String msg) {
        messageArea.append(msg);
    }
    //自己展示私聊消息
    public void setPrivateMsgToWin(String toNickname, String msg) {
        StringBuilder sb=new StringBuilder();
        String nowStr=TimeUtils.getCurrentTime();
        String Resultmsg=sb.append("【私聊】To ").append(toNickname).append(" ").append(nowStr).append("\r\n").
                append(msg).toString();
        messageArea.append(Resultmsg);
    }


    // 发送私聊消息
    private void sendPrivateMessage(String toNickname) {
        String message = JOptionPane.showInputDialog(this, "请输入私聊内容：", "私聊给 " + toNickname, JOptionPane.PLAIN_MESSAGE);
        if (message != null && !message.trim().isEmpty()) {
            try {
                DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
                dos.writeInt(3); // 私聊类型
                dos.writeUTF(toNickname); // 发送给谁
                dos.writeUTF(message); // 发送的内容
                dos.flush();
                setPrivateMsgToWin(toNickname,message);
            } catch (IOException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "发送私聊消息失败！", "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }


    // 自定义带背景图的面板（支持透明度）
    static class BackgroundPanel extends JPanel {
        private Image backgroundImage;

        public BackgroundPanel() {
            // 从资源路径加载背景图
            backgroundImage = new ImageIcon(ChatFrame.class.getResource("/images/quannai.png")).getImage();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            if (backgroundImage != null) {
                // 设置透明度：0.6f 表示 60% 不透明度（即 40% 透明）
                float transparency = 0.5f; // 范围：0.0f（全透明）到 1.0f（不透明）

                // 创建 Graphics2D 对象并设置透明度
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, transparency));

                // 绘制背景图并铺满整个面板
                g2d.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);

                // 释放资源
                g2d.dispose();
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ChatFrame("张三"));
    }
}