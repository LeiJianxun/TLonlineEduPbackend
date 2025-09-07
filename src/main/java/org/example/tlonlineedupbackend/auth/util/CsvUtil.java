package org.example.tlonlineedupbackend.auth.util;

import org.example.tlonlineedupbackend.auth.entity.User;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.stream.Collectors;

public class CsvUtil {
    public static List<User> parseUsers(InputStream is, Long departmentId) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            return reader.lines()
                    .skip(1) // 跳过标题行
                    .map(line -> {
                        String[] fields = line.split(",");
                        User user = new User();
                        user.setUserName(fields[0].trim());
                        user.setPhone(fields[1].trim());
                        user.setSex(fields[2].trim());
                        user.setAscription(departmentId);
                        return user;
                    })
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException("CSV解析失败", e);
        }
    }
}
