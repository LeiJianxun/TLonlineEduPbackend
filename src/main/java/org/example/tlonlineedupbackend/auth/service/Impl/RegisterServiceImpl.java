package org.example.tlonlineedupbackend.auth.service.Impl;

import org.example.tlonlineedupbackend.auth.entity.User;
import org.example.tlonlineedupbackend.auth.repository.UserRepository;
import org.example.tlonlineedupbackend.auth.service.RegisterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.apache.commons.codec.digest.DigestUtils;

import java.util.Calendar;
import java.util.Date;
import java.util.Optional;
import java.util.Random;

@Service
public class RegisterServiceImpl implements RegisterService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Override
    public boolean registerUser(String phone, String password, String name, Date birthday, String identifiers) {
        String id = generateRandomId();
        Optional<User> userOptionalId = userRepository.findById(id);
        Optional<User> userOptional = userRepository.findByPhone(phone);

        if (userOptionalId.isPresent() || userOptional.isPresent()) {
            throw new RuntimeException("用户已存在");
        }

        // 计算年龄
        int age = calculateAge(birthday, new Date());

        User newUser = new User();
        String encodedPassword = passwordEncoder.encode(password);
        newUser.setId(id);
        newUser.setPhone(phone);
        newUser.setPassword(encodedPassword); // 密码应加密
        newUser.setUserName(name);
        newUser.setBirthday(birthday);
        newUser.setAge(age);
        newUser.setIdentifiers(identifiers);
        newUser.setCreate_time(new Date());

        userRepository.save(newUser);
        return true;
    }

    public static String generateRandomId() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 10; i++) {
            int index = random.nextInt(characters.length());
            sb.append(characters.charAt(index));
        }
        return sb.toString();
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
