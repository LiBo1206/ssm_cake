package com.dao;

import java.util.List;
import org.apache.ibatis.annotations.Select;
import com.entity.Items;

public interface ItemsDao {
	// 根据ID删除订单项记录
	int deleteById(Integer id);

	// 插入新的订单项记录
	int insert(Items record);

	// 选择性地插入订单项记录，只插入非空字段
	int insertSelective(Items record);

	// 根据ID查询订单项记录
	Items selectById(Integer id);

	// 选择性地更新订单项记录，只更新非空字段
	int updateByIdSelective(Items record);

	// 根据ID更新订单项记录
	int updateById(Items record);

	// 以上为mybatis generator自动生成接口, 具体实现在mapper.xml中
	// ------------------------------------------------------------

	// 以下方法使用mybatis注解实现

	/**
	 * 获取订单项列表
	 * @param orderid 订单ID
	 * @return 返回指定订单ID的所有订单项列表
	 */
	@Select("select * from items where order_id=#{orderid}")
	public List<Items> getItemList(int orderid);
}