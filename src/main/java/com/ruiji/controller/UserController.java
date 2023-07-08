package com.ruiji.controller;


import ch.qos.logback.core.util.TimeUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ruiji.common.R;
import com.ruiji.daomin.User;
import com.ruiji.service.UserService;
import com.ruiji.util.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.TimeoutUtils;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.management.Query;
import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@Slf4j
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private RedisTemplate redisTemplate;
    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpSession session) {
        String phone = user.getPhone();
        if(phone != null) {
            String code = ValidateCodeUtils.generateValidateCode(4).toString();
//            log.info("code={}",code);
            session.setAttribute("code", code);
//            session.setAttribute("code", 1234);
            log.info("{}",phone);
            redisTemplate.opsForValue().set(phone, "1234", 5, TimeUnit.MINUTES);
            return R.success("发送成功");
        }
        return R.success("发送失败");
    }
    @PostMapping("/login")
    public R<String> login(@RequestBody Map user, HttpSession session) {
        String phone = user.get("phone").toString();
        String inputCode = user.get("code").toString();
        if(phone == null || inputCode == null || session.getAttribute("code") == null) {
            return R.error("请输入完整");
        }
//        String code = session.getAttribute("code").toString();
        String code = (String)redisTemplate.opsForValue().get(phone);
        log.info("inputCode={},code={}",inputCode,code);
        if(inputCode.equals(code)) {
            QueryWrapper<User> qw = new QueryWrapper<>();
            qw.eq("phone", phone);
            User queryUser = userService.getOne(qw);
            Long userId;
            if(queryUser == null) {
                User user1 = new User();
                user1.setPhone(phone);
                userService.save(user1);
                userId = user1.getId();
            }else {
                userId = queryUser.getId();
            }
            session.setAttribute("user",userId);
            redisTemplate.delete(phone);
            return R.success("登录成功");
        }
        return R.error("验证码错误");
    }
    @PostMapping("/logout")
    public R<String> logout(HttpSession session) {
        session.removeAttribute("code");
        session.removeAttribute("user");
        return R.success("已退出登录");
    }

}
