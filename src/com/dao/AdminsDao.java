package com.dao;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import com.entity.Admins;

public interface AdminsDao {
	// 根据ID删除管理员记录
	int deleteById(Integer id);

	// 插入新的管理员记录
	int insert(Admins record);

	// 选择性地插入管理员记录，只插入非空字段
	int insertSelective(Admins record);

	// 根据ID查询管理员记录
	Admins selectById(Integer id);

	// 选择性地更新管理员记录，只更新非空字段
	int updateByIdSelective(Admins record);

	// 根据ID更新管理员记录
	int updateById(Admins record);

	// 以上为mybatis generator自动生成接口, 具体实现在mapper.xml中
	// ------------------------------------------------------------

	// 以下方法使用mybatis注解实现

	/**
	 * 通过用户名查找管理员
	 * @param username 用户名
	 * @return 返回匹配的Admins对象，如果没有找到则返回null
	 */
	@Select("select * from admins where username=#{username}")
	public Admins getByUsername(String username);

	/**
	 * 通过用户名和密码查找管理员
	 * @param username 用户名
	 * @param password 密码
	 * @return 返回匹配的Admins对象，如果没有找到则返回null
	 */
	@Select("select * from admins where username=#{username} and password=#{password}")
	public Admins getByUsernameAndPassword(@Param("username")String username, @Param("password")String password);

	/**
	 * 获取管理员列表，支持分页
	 * @param page 页码（通常为begin/rows）
	 * @param rows 每页记录数
	 * @return 返回管理员列表，如果没有找到则返回空集合
	 */
	@Select("select * from admins order by id desc limit #{begin}, #{size}")
	public List<Admins> getList(@Param("begin")int begin, @Param("size")int size);

	/**
	 * 获取管理员总数
	 * @return 返回管理员总数
	 */
	@Select("select count(*) from admins")
	public long getTotal();
}