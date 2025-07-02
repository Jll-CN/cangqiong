package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.PasswordConstant;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.exception.AccountLockedException;
import com.sky.exception.AccountNotFoundException;
import com.sky.exception.PasswordErrorException;
import com.sky.mapper.EmployeeMapper;
import com.sky.result.PageResult;
import com.sky.service.EmployeeService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.prefs.BackingStoreException;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeMapper employeeMapper;

    /**
     * 员工登录
     *
     * @param employeeLoginDTO
     * @return
     */
    public Employee login(EmployeeLoginDTO employeeLoginDTO) {
        String username = employeeLoginDTO.getUsername();
        String password = employeeLoginDTO.getPassword();

        // 1、根据用户名查询数据库中的数据
        Employee employee = employeeMapper.getByUsername(username);

        //2、处理各种异常情况（用户名不存在、密码不对、账号被锁定）
        if (employee == null) {
            //账号不存在
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }

        //密码比对
        // TODO 后期需要进行md5加密，然后再进行比对
        // 明文 md5 加密，然后进行对比
        String encryptPassword = DigestUtils.md5DigestAsHex(password.getBytes());
        if (!encryptPassword.equals(employee.getPassword())) {
            //密码错误
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }

        if (employee.getStatus() == StatusConstant.DISABLE) {
            //账号被锁定
            throw new AccountLockedException(MessageConstant.ACCOUNT_LOCKED);
        }

        //3、返回实体对象
        return employee;
    }

    /**
     * 新增员工方法的实现
     * 调用mapper层将数据插入数据库
     * @param employeeDTO
     */
    @Override
    public void save(EmployeeDTO employeeDTO) {
        // System.out.println("Service save 方法的线程id: " + Thread.currentThread().getId());
        // DTO 对象只是方便接收传递过来的数据，到Mapper层还是需要使用实体类。能够更好的和表对应。
        // 对象转换 将DTO 转换为 员工对象。
//        Employee employee = new Employee();
//        employee.setUsername(employeeDTO.getUsername());  // 一个一个设置太麻烦了

        // 对象属性拷贝
        Employee employee = new Employee();
        BeanUtils.copyProperties(employeeDTO, employee);    // 将employeeDTO 的属性拷贝给 employee

        // 由于DTO类中的属性少于employee中的属性，剩下的属性需要自己设置
        // status, password, createTIme, updateTime, createUser, updateUser,

        employee.setStatus(StatusConstant.ENABLE);  // 默认正常状态, 本来想直接设置1的，但是有点硬编码，可以考虑设计一个常量类。

        // 设置密码，默认密码，需要加密
//        String encryptPassword = DigestUtils.md5DigestAsHex("123456".getBytes());   // 又是硬编码
        String encryptPassword = DigestUtils.md5DigestAsHex(PasswordConstant.DEFAULT_PASSWORD.getBytes());  // 使用常量避免硬编码
        employee.setPassword(encryptPassword);

        // 设置当前记录的创建时间 和 修改时间
        employee.setCreateTime(LocalDateTime.now());
        employee.setUpdateTime(LocalDateTime.now());

        // 设置当前记录创建人id和修改人id --> 就是登录账户的这个人的id
        // 通过 ThreadLocal 取出id
        Long tempId = BaseContext.getCurrentId();
        employee.setCreateUser(tempId);
        employee.setUpdateUser(tempId);

        // remove 掉 ThreadLocal 中的Id
        BaseContext.removeCurrentId();

        // 封装好之后，调用持久层 mapper 注入数据库
        employeeMapper.insert(employee);
    }

    /**
     * 员工分页查询实现
     * @param employeePageQueryDTO
     * @return
     */
    @Override
    public PageResult pageQuery(EmployeePageQueryDTO employeePageQueryDTO) {
        // 1. 数据库层面的分页查询。 select * from employee limit 0，10

        // 2. mybatis 提供的pagehelper 可以简化分页查询
        // pom 中引入 pagehelper
        // 开始分页查询
        PageHelper.startPage(employeePageQueryDTO.getPage(), employeePageQueryDTO.getPageSize());       // 类似于动态SQL

        Page<Employee> page = employeeMapper.pageQuery(employeePageQueryDTO);
        long total = page.getTotal();
        List<Employee> records = page.getResult();

        return new PageResult(total, records);
    }
}
