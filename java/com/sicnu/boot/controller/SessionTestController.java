package com.sicnu.boot.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * description:
 *
 * @author 蔡名展
 * @version 1.0
 * @date 2022/11/20 11:20
 */

@Slf4j
@RequestMapping
@RestController
public class SessionTestController {
    @RequestMapping("/session")
    public Object springSession(@RequestParam("username") String username, HttpServletRequest request, HttpSession session) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null && cookies.length > 0) {
            for (Cookie cookie : cookies) {
                log.warn(cookie.getName() + "=" + cookie.getValue());
            }
        }
        Object value = session.getAttribute("username");
        if (value == null) {
            log.warn("用户不存在");
            //保存session
            session.setAttribute("username", "{username: '" + username + "', age: 30}");
        } else {
            log.warn("用户存在");
        }
        return "这是8080端口,username=" + value;
    }
}
