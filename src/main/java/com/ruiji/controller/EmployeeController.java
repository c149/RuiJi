package com.ruiji.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruiji.daomin.Employee;
import com.ruiji.common.R;
import com.ruiji.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/employee")
@Slf4j
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;
    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest re, @RequestBody Employee employee) {
        String username = employee.getUsername();
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes(StandardCharsets.UTF_8));
        LambdaQueryWrapper<Employee> lw = new LambdaQueryWrapper<Employee>();
        lw.eq(Employee::getUsername,username);
        Employee one = employeeService.getOne(lw);
        if(null == one) {
            return R.error("登录失败");
        }
        if(!one.getPassword().equals(password)) {
            return R.error("密码错误");
        }
        if(one.getStatus() == 0) {
            return R.error("账号已禁用");
        }
        re.getSession().setAttribute("employee", one.getId());
        return R.success(one);
    }
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest re) {
        re.getSession().removeAttribute("employee");
        return R.success("成功");
    }
    @PostMapping
    public R<String> addMember(HttpServletRequest req,@RequestBody Employee employee) {
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes(StandardCharsets.UTF_8)));
//        employee.setCreateTime(LocalDateTime.now());
//        employee.setUpdateTime(LocalDateTime.now());
        Long id = (Long)req.getSession().getAttribute("employee");
//        employee.setCreateUser(id);
//        employee.setUpdateUser(id);
        boolean save = employeeService.save(employee);
        if (save) {
            return R.success("添加成功");
        }else {
            return R.error("添加失败");
        }
    }
    @GetMapping("/page")
    public R<Page> listMember(Integer page, Integer pageSize, String name) {
        LambdaQueryWrapper<Employee> lqw = new LambdaQueryWrapper();
        if(null != name) {
            lqw.like(Employee::getUsername,name);
        }
        lqw.orderByDesc(Employee::getUpdateTime);
        Page info = new Page(page, pageSize);
        employeeService.page(info, lqw);
//        log.info("page:{},pageSize:{},name:{}",page,pageSize,name);
        return R.success(info);
    }
    @PutMapping
    public R<String> editEmployee(HttpServletRequest re, @RequestBody Employee employee) {
        System.out.println(employee);
        String userId = re.getSession().getAttribute("employee").toString();
//        System.out.println(userId.equals("1"));
        if(!"1".equals(userId)) return R.error("error");
        log.info("id={},status={}",employee.getId(),employee.getStatus());
        employee.setUpdateTime(LocalDateTime.now());
        employee.setUpdateUser(Long.parseLong(userId));
        employeeService.updateById(employee);
        return R.success("success");
    }
    @GetMapping("/{id}")
    public R<Employee> getByID(@PathVariable long id) {
        Employee employee = employeeService.getById(id);
        return R.success(employee);
    }

//    @PutMapping
//    public R<String> editMember(HttpServletRequest req,@RequestBody Employee employee) {
//        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes(StandardCharsets.UTF_8)));
////        employee.setCreateTime(LocalDateTime.now());
//        employee.setUpdateTime(LocalDateTime.now());
//        Long id = (Long)req.getSession().getAttribute("employee");
////        employee.setCreateUser(id);
//        employee.setUpdateUser(id);
//        boolean save = employeeService.updateById(employee);
//        if (save) {
//            return R.success("添加成功");
//        }else {
//            return R.error("添加失败");
//        }
//    }

}
