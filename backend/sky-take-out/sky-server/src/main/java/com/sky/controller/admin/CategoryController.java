package com.sky.controller.admin;

import com.github.pagehelper.Page;
import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.CategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@Api(tags = "分类相关接口")
@RequestMapping("/admin/category")
public class CategoryController {

    // 注入Service
    @Autowired
    private CategoryService categoryService;

    /**
     * 添加类别
     * @param categoryDTO
     * @return
     */
    @PostMapping("")
    @ApiOperation("类别添加")
    public Result addCategory(@RequestBody CategoryDTO categoryDTO) {
        log.info("添加新的类别: Category = {}", categoryDTO);

        // 调用service，注意参数和返回值
        categoryService.addCategory(categoryDTO);

        return Result.success();
    }

    /**
     * 分页查询类别
     * @param categoryPageQueryDTO
     * @return
     */
    @GetMapping("/page")
    @ApiOperation("分页查询类别")
    public Result<PageResult> page(CategoryPageQueryDTO categoryPageQueryDTO) {
        // 打印日志
        log.info("分页查询类别, categoryDTO = {}", categoryPageQueryDTO);

        // 调用service, 考虑参数，考虑返回值
        PageResult pageResult = categoryService.page(categoryPageQueryDTO);

        // 封装后返回前端
        return Result.success(pageResult);
    }

    /**
     *  启用，禁用分类
     *  Query 参数如何获取呢？
     * @return
     */
    @PostMapping("/status/{status}")
    @ApiOperation("启用，禁用分类")
    public Result startOrStop(@PathVariable Integer status, Long id){
        log.info("启用，禁用分类: status = {}, id = {}", status, id);

        // 调用service，参数，返回值
        categoryService.startOrStop(status, id);


        return Result.success();
    }

    /**
     * 根据id 删除分类
     * @param id
     * @return
     */
    @DeleteMapping("")
    @ApiOperation("根据id 删除分类")
    public Result deleteById(Long id){
        // 打印日志
        log.info("打印需要删除的id: id = {}", id);

        // 使用 service 删除id
        categoryService.deleteById(id);

        return Result.success();
    }

    /**
     * 根据id 修改属性
     * @param categoryDTO
     * @return
     */
    @PutMapping("")
    @ApiOperation("修改分类")
    public Result updateById(@RequestBody CategoryDTO categoryDTO){
        // 打印日志
        log.info("根据id 更新 category, categoryDTO = {}", categoryDTO);

        categoryService.updateById(categoryDTO);

        return Result.success();
    }
}
