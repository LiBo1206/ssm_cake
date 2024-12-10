package com.dao;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import com.entity.Orders;

public interface OrdersDao {
	// 根据ID删除订单记录
	int deleteById(Integer id);

	// 插入新的订单记录
	int insert(Orders record);

	// 选择性地插入订单记录，只插入非空字段
	int insertSelective(Orders record);

	// 根据ID查询订单记录
	Orders selectById(Integer id);

	// 选择性地更新订单记录，只更新非空字段
	int updateByIdSelective(Orders record);

	// 根据ID更新订单记录
	int updateById(Orders record);

	// 以上为mybatis generator自动生成接口, 具体实现在mapper.xml中
	// ------------------------------------------------------------

	// 以下方法使用mybatis注解实现

	/**
	 * 获取订单列表，支持分页
	 * @param begin 起始记录（通常为页码*每页大小）
	 * @param size 每页大小
	 * @return 返回订单列表
	 */
	@Select("select * from orders order by id desc limit #{begin}, #{size}")
	public List<Orders> getList(@Param("begin")int begin, @Param("size")int size);

	/**
	 * 获取订单总数
	 * @return 返回订单总数
	 */
	@Select("select count(*) from orders")
	public long getTotal();

	/**
	 * 根据状态获取订单列表，支持分页
	 * @param status 订单状态
	 * @param begin 起始记录（通常为页码*每页大小）
	 * @param size 每页大小
	 * @return 返回匹配状态的订单列表
	 */
	@Select("<script> select * from orders where 1=1 "
			+ "<when test='status!=0'>and status=#{status} </when> "
			+ "order by id desc limit #{begin}, #{size} </script>")
	public List<Orders> getListByStatus(@Param("status")byte status, @Param("begin")int begin, @Param("size")int size);

	/**
	 * 根据状态获取订单总数
	 * @param status 订单状态
	 * @return 返回匹配状态的订单总数
	 */
	@Select("select count(*) from orders where status=#{status}")
	public long getTotalByStatus(@Param("status")byte status);

	/**
	 * 通过用户ID获取订单列表
	 * @param userid 用户ID
	 * @return 返回该用户的所有订单列表
	 */
	@Select("select * from orders where user_id=#{userid} order by id desc")
	public List<Orders> getListByUserid(@Param("userid")int userid);
}