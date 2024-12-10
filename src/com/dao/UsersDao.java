package com.dao;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import com.entity.Users;

public interface UsersDao {
	// 根据ID删除用户记录
	int deleteById(Integer id);

	// 插入新的用户记录
	int insert(Users record);

	// 选择性地插入用户记录，只插入非空字段
	int insertSelective(Users record);

	// 根据ID查询用户记录
	Users selectById(Integer id);

	// 选择性地更新用户记录，只更新非空字段
	int updateByIdSelective(Users record);

	// 根据ID更新用户记录
	int updateById(Users record);

	// 以上为mybatis generator自动生成接口, 具体实现在mapper.xml中
	// ------------------------------------------------------------

	// 以下方法使用mybatis注解实现

	/**
	 * 通过用户名查找用户
	 * @param username 用户名
	 * @return 返回匹配的Users对象，如果没有找到则返回null
	 */
	@Select("select * from users where username=#{username}")
	public Users getByUsername(String username);

	/**
	 * 通过用户名和密码查找用户
	 * @param username 用户名
	 * @param password 密码
	 * @return 返回匹配的Users对象，如果没有找到则返回null
	 */
	@Select("select * from users where username=#{username} and password=#{password}")
	public Users getByUsernameAndPassword(@Param("username")String username, @Param("password")String password);

	/**
	 * 获取用户列表，支持分页
	 * @param begin 起始记录（通常为页码*每页大小）
	 * @param size 每页大小
	 * @return 返回用户列表，如果没有找到则返回空集合
	 */
	@Select("select * from users order by id desc limit #{begin}, #{size}")
	public List<Users> getList(@Param("begin")int begin, @Param("size")int size);

	/**
	 * 获取用户总数
	 * @return 返回用户总数
	 */
	@Select("select count(*) from users")
	public long getTotal();
}