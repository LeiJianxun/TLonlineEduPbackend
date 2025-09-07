package org.example.tlonlineedupbackend.auth.service;

import jakarta.transaction.Transactional;
import org.example.tlonlineedupbackend.auth.controller.UserInfoController.UserChangeRequest;
import org.example.tlonlineedupbackend.auth.entity.User;
import org.example.tlonlineedupbackend.auth.repository.UserRepository;
import org.example.tlonlineedupbackend.auth.security.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Transactional
    public CustomUserDetails updateUserInfo(String phone, UserChangeRequest userChangeRequest) {
        // 查找现有用户
        Optional<User> optionalUser = userRepository.findByPhone(phone);

        // 如果用户不存在，抛出异常
        if (!optionalUser.isPresent()) {
            throw new RuntimeException("User not found");
        }

        // 获取用户
        User user = optionalUser.get();

        // 更新用户信息
        if (userChangeRequest.getUserName() != null) {
            user.setUserName(userChangeRequest.getUserName());
        }
        if (userChangeRequest.getSex() != null) {
            user.setSex(userChangeRequest.getSex());
        }
        if (userChangeRequest.getPhone() != null) {
            user.setPhone(userChangeRequest.getPhone());
        }
        if (userChangeRequest.getBirthday() != null) {
            Date NewBirthday = userChangeRequest.getBirthday();
            user.setBirthday(NewBirthday);
            int newAge = calculateAge(NewBirthday, new Date());
            user.setAge(newAge);
        }

        // 保存更新后的用户
        userRepository.save(user);

        // 使用更新后的字段构造 CustomUserDetails 并返回
        return new CustomUserDetails(
                user.getId(),
                user.getUserName(),
                user.getPhone(),
                user.getAge(),
                user.getSex(),
                user.getIdentifiers(),
                user.getAscription(),
                user.getBirthday()
        );
    }

    public boolean updatePassword(String phone, String newPassword) {
        // 在这里执行数据库操作来更新密码
        // 例如，使用 JPA 更新密码
        Optional<User> userOptional = userRepository.findByPhone(phone);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setPassword(newPassword); // 更新密码
            userRepository.save(user); // 保存更新后的用户信息
            return true;
        }
        return false;
    }

    /**
     * 根据生日和当前日期计算年龄
     * @param birthday 用户生日
     * @param currentDate 当前日期
     * @return 年龄
     */
    public static int calculateAge(Date birthday, Date currentDate) {
        if (birthday == null || currentDate == null) {
            throw new IllegalArgumentException("日期不能为空");
        }

        // 使用 Calendar 获取年份、月份、日期
        Calendar birthCal = Calendar.getInstance();
        birthCal.setTime(birthday);

        Calendar currentCal = Calendar.getInstance();
        currentCal.setTime(currentDate);

        int age = currentCal.get(Calendar.YEAR) - birthCal.get(Calendar.YEAR);

        // 如果当前日期的月份和日小于生日的月份和日，年龄减 1
        if (currentCal.get(Calendar.MONTH) < birthCal.get(Calendar.MONTH) ||
                (currentCal.get(Calendar.MONTH) == birthCal.get(Calendar.MONTH) &&
                        currentCal.get(Calendar.DAY_OF_MONTH) < birthCal.get(Calendar.DAY_OF_MONTH))) {
            age--;
        }

        return age;
    }

}
