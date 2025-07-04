package com.sky.service.impl;

import com.fasterxml.jackson.databind.ser.Serializers;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.mapper.CategoryMapper;
import com.sky.result.PageResult;
import com.sky.service.CategoryService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {
    // 自动注入mapper
    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    public void addCategory(CategoryDTO categoryDTO) {

        // 将CategoryDTO 属性复制到 Category中
        Category category = new Category();
        BeanUtils.copyProperties(categoryDTO, category);            // DTO --> category

        // Status 默认为 0
        category.setStatus(StatusConstant.DISABLE);

        // 设置创建和更新时间
        category.setCreateTime(LocalDateTime.now());
        category.setUpdateTime(LocalDateTime.now());

        // 设置创建和更新人
        Long userId = BaseContext.getCurrentId();                       // 使用封装LocalThread的BaseContext类获取用户id
        category.setCreateUser(userId);
        category.setUpdateUser(userId);

        // name 和 id 是unique的，可能会出现SQL异常
        // TODO

        // 调用mapper 注入数据库
        categoryMapper.insert(category);
    }

    /**
     * 类别分页查询实现
     * @param categoryPageQueryDTO
     * @return
     */
    @Override
    public PageResult page(CategoryPageQueryDTO categoryPageQueryDTO) {
        // name, page, pageSize, type(0, 1)
        // page 当前第几页，pageSize 就是一页有多少

        // 计算起始行
        int pageStart = (categoryPageQueryDTO.getPage() - 1) * categoryPageQueryDTO.getPageSize();
        String name = categoryPageQueryDTO.getName(); // get name
        Integer type = categoryPageQueryDTO.getType();  // get type
        int pageSize = categoryPageQueryDTO.getPageSize();

        // 查询当前页数据, 分页查询需要传入多个数据
        List<Category> list = categoryMapper.queryByPage(pageStart, pageSize, name, type);

        // 查询总的记录数
        int total = categoryMapper.countByPage(categoryPageQueryDTO);

        // 封装分页结果
        PageResult pageResult = new PageResult();
        pageResult.setTotal(total);
        pageResult.setRecords(list);

        return pageResult;
    }

    /**
     * 启用，禁用分类
     * @param status
     * @param id
     */
    @Override
    public void startOrStop(Integer status, Long id) {

        // 将数据包装成一个 category 对象
        // 更新时间 和 user
        Category category = Category.builder()
                .status(status)
                .id(id)
                .updateTime(LocalDateTime.now())
                .updateUser(BaseContext.getCurrentId())
                .build();

        // 使用update方法
        categoryMapper.update(category);
    }

    /**
     * 根据id删除分类 实现
     * @param id
     */
    @Override
    public void deleteById(Long id) {

        // 如何删除？
        categoryMapper.delete(id);
    }

    @Override
    public void updateById(CategoryDTO categoryDTO) {
        // 复制categoryDTO 到 category中

        Category category = new Category();
        BeanUtils.copyProperties(categoryDTO, category);

        // 更新时间，user
        category.setUpdateTime(LocalDateTime.now());
        category.setUpdateUser(BaseContext.getCurrentId());

        // 调用update方法
        categoryMapper.update(category);
    }
}
