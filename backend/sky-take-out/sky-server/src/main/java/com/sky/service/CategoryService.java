package com.sky.service;

import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.result.PageResult;

public interface CategoryService {
    /**
     * 添加类别
     * @param categoryDTO
     */
    void addCategory(CategoryDTO categoryDTO);

    /**
     * 类别分页查询
     * @param categoryPageQueryDTO
     * @return
     */
    PageResult page(CategoryPageQueryDTO categoryPageQueryDTO);

    /**
     * 启用，禁用分类
     * @param status
     * @param id
     */
    void startOrStop(Integer status, Long id);

    /**
     * 根据id 删除分类
     * @param id
     */
    void deleteById(Long id);

    /**
     * 根据id 更新分类
     * @param categoryDTO
     */
    void updateById(CategoryDTO categoryDTO);
}
