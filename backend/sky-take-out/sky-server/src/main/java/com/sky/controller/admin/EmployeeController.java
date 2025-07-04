package com.sky.controller.admin;

import com.sky.constant.JwtClaimsConstant;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.properties.JwtProperties;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.EmployeeService;
import com.sky.utils.JwtUtil;
import com.sky.vo.EmployeeLoginVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 员工管理
 */
@RestController
@RequestMapping("/admin/employee")
@Slf4j
@Api(tags = "员工相关接口")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private JwtProperties jwtProperties;

    /**
     * 登录
     * @param employeeLoginDTO
     * @return
     */
    @PostMapping("/login")
    @ApiOperation(value = "员工登录")   // 效果会取代swagger中的login这样的接口方法，直接显示员工登录
    public Result<EmployeeLoginVO> login(@RequestBody EmployeeLoginDTO employeeLoginDTO) {
        log.info("员工登录：{}", employeeLoginDTO);

        Employee employee = employeeService.login(employeeLoginDTO);

        //登录成功后，生成jwt令牌, 避免访问其他页面也需要登录
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.EMP_ID, employee.getId());
        String token = JwtUtil.createJWT(
                jwtProperties.getAdminSecretKey(),
                jwtProperties.getAdminTtl(),
                claims);

        EmployeeLoginVO employeeLoginVO = EmployeeLoginVO.builder()
                .id(employee.getId())
                .userName(employee.getUsername())
                .name(employee.getName())
                .token(token)
                .build();

        return Result.success(employeeLoginVO);
    }

    /**
     * 退出
     *
     * @return
     */
    @PostMapping("/logout")
    @ApiOperation("员工退出")
    public Result<String> logout() {
        return Result.success();
    }


    /**
     * 新增员工
     * @return
     */
    @PostMapping
    @ApiOperation("新增员工")
    public Result save(@RequestBody EmployeeDTO employeeDTO) {
        // System.out.println("Controller save 方法的线程id: " + Thread.currentThread().getId());

        // 为了方便调试
        log.info("新增员工, {}", employeeDTO);

        // 调用 service 新增方法
        employeeService.save(employeeDTO);

        return Result.success();
    }

    /**
     * 分页查询员工
     * @return
     */
    @GetMapping("/page")
    @ApiOperation("分页查询员工")
    public Result<PageResult> page(EmployeePageQueryDTO employeePageQueryDTO) {
        // 不用使用注解，使用正确的类型，Springmvc会自己封装好注解
        log.info("员工分页查询: {}", employeePageQueryDTO);

        // 调用service对象，输入什么数据，返回什么数据。
        PageResult pageResult = employeeService.pageQuery(employeePageQueryDTO);

        return Result.success(pageResult);
    }

    // 泛型不是强制的, 针对查询类型的操作，因为需要后端返回data类型的数据，因此就需要将泛型添加上。
    // 对于其他类型的，非查询的。操作，就不需要了

    /**
     * /admin/employee/status/1?id=123
     *  由于 status 参数是一个路径参数，需要使用PathVaribale 注解映射。
     *  由于id 是通过地址栏的键值对传递的参数，传递时 id=123。对应参数名即可
     *
     *  由于路径中的参数名字和 传入的参数都是status。
     *  如果不一致，需要在PathVariable后面添加小括号，绑定好
     * @return
     */
    @PostMapping("/status/{status}")
    @ApiOperation("启用，禁用员工账号")
    public Result startOrStop(@PathVariable Integer status, Long id) {
        log.info("账户启用，禁用: status={}, id={}", status, id);

        // 调用service
        employeeService.startOrStop(status, id);

        // 操作完，填入参数
        return Result.success();
    }

    /**
     * 根据主键id查询 员工信息，并返回给前端。
     * 由于employee的不是所有的数据都需要返回给前端，因此可以使用一个自己创建的带有部分关键属性的EmployeeDTO类来返回给前端。
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    @ApiOperation("根据id查询员工信息")
    public Result<Employee> getById(@PathVariable("id") Long id) {
        log.info("根据主键id查询员工信息. id->{}", id);

        // 调用service，获取EmployeeDTO，传入id
        Employee employee = employeeService.getById(id);

        return Result.success(employee);
    }

    @PutMapping("")
    @ApiOperation("编辑员工信息")
    public Result updateEmployeeInfo(@RequestBody EmployeeDTO employeeDTO){
        log.info("编辑员工信息, 新的员工信息->{}", employeeDTO);

        // 调用service的方法，注意参数和返回值。
        employeeService.updateEmployeeInfo(employeeDTO);

        return Result.success();
    }
}
