// 文件路径：src/com/itheima/utils/TimeUtils.java

package com.itheima.ui;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TimeUtils {

    // 支持的格式可以根据项目需求扩展
    private static final DateTimeFormatter dtf = 
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss EEE a");

    /**
     * 获取当前时间字符串，格式为：2025-06-19 14:30:00 星期四 下午
     */
    public static String getCurrentTime() {
        return dtf.format(LocalDateTime.now());
    }
}