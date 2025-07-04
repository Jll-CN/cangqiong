package com.sky.mapper;

import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CategoryMapper {

    /**
     * 插入新的分类
     * @param category
     */
    @Insert("INSERT INTO category (id, type, name, sort, status, create_time, update_time, create_user, update_user)" +
            "VALUES (#{id}, #{type}, #{name}, #{sort}, #{status}, #{createTime}, #{updateTime}, #{createUser}, #{updateUser})")
    public void insert(Category category);

    /**
     * 分页查询
     * @param pageStart
     * @param pageSize
     * @param name
     * @param type
     * @return
     */
    List<Category> queryByPage(@Param("pageStart") int pageStart, @Param("pageSize") int pageSize, @Param("name") String name, @Param("type") Integer type);

    /**
     * 总记录查询
     * @param categoryPageQueryDTO
     * @return
     */
    int countByPage(CategoryPageQueryDTO categoryPageQueryDTO);

    /**
     * 更新category的属性
     * @param category
     */
    void update(Category category);

    /**
     * 根据id 删除记录
     * @param id
     */
    @Delete("DELETE FROM category WHERE id = #{id}")
    void delete(Long id);
}
