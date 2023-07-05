package com.ruiji.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ruiji.common.R;
import com.ruiji.daomin.AddressBook;
import com.ruiji.service.AddressBookService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.server.Session;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/addressBook")
public class AddressBookController {
    @Autowired
    private AddressBookService addressBookService;
    @GetMapping("/list")
    public R<List<AddressBook>> addressListApi() {
        LambdaQueryWrapper<AddressBook> lqw = new LambdaQueryWrapper<>();
        lqw.orderByDesc(AddressBook::getCreateTime);
        List<AddressBook> list = addressBookService.list(lqw);
        return R.success(list);
    }
    @GetMapping("/{id}")
    public R<AddressBook> addressFindOneApi(@PathVariable long id) {
        AddressBook addressBook = addressBookService.getById(id);
        return R.success(addressBook);
    }
    @PostMapping
    public R<String> addAddressApi(@RequestBody AddressBook addressBook, HttpSession session) {
        Object user = session.getAttribute("user");
        Long id = Long.parseLong(user.toString());
        addressBook.setUserId(id);
        addressBookService.save(addressBook);
        return R.success("success");
    }
    @PutMapping
    public R<String> updateAddressApi(@RequestBody AddressBook addressBook) {
        addressBookService.updateById(addressBook);
        return R.success("success");
    }
    @DeleteMapping
    public R<String> deleteAddressApi(long ids) {
        addressBookService.removeById(ids);
        return R.success("success");
    }
    @PutMapping("/default")
    public R<String> setDefaultAddressApi(@RequestBody AddressBook addressBook) {
        QueryWrapper<AddressBook> qw = new QueryWrapper<>();
        qw.eq("is_default",1);
        AddressBook defaultAddress = addressBookService.getOne(qw);
        defaultAddress.setIsDefault(0);
        addressBook.setIsDefault(1);
        addressBookService.updateById(defaultAddress);
        addressBookService.updateById(addressBook);
        return R.success("修改成功");
    }
    @GetMapping("/default")
    public R<AddressBook> getDefaultAddress() {
        QueryWrapper<AddressBook> qw = new QueryWrapper<>();
        qw.eq("is_default",1);
        AddressBook defaultAddress = addressBookService.getOne(qw);
        return R.success(defaultAddress);
    }


}
