package com.dao;

import java.util.List;
import org.apache.ibatis.annotations.Select;
import com.entity.Types;

public interface TypesDao {
    // 根据ID删除类型记录
    int deleteById(Integer id);

    // 插入新的类型记录
    int insert(Types record);

    // 选择性地插入类型记录，只插入非空字段
    int insertSelective(Types record);

    // 根据ID查询类型记录
    Types selectById(Integer id);

    // 选择性地更新类型记录，只更新非空字段
    int updateByIdSelective(Types record);

    // 根据ID更新类型记录
    int updateById(Types record);

    // 以上为mybatis generator自动生成接口, 具体实现在mapper.xml中
    // ------------------------------------------------------------

    // 以下方法使用mybatis注解实现

    /**
     * 获取类型列表
     * @return 返回所有类型的列表，按ID降序排列
     */
    @Select("select * from types order by id desc")
    public List<Types> getList();
}