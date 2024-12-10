package com.dao;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import com.entity.Goods;

public interface GoodsDao {
	// 根据ID删除商品记录
	int deleteById(Integer id);

	// 插入新的商品记录
	int insert(Goods record);

	// 选择性地插入商品记录，只插入非空字段
	int insertSelective(Goods record);

	// 根据ID查询商品记录
	Goods selectById(Integer id);

	// 选择性地更新商品记录，只更新非空字段
	int updateByIdSelective(Goods record);

	// 根据ID更新商品记录
	int updateById(Goods record);

	// 以上为mybatis generator自动生成接口, 具体实现在mapper.xml中
	// ------------------------------------------------------------

	// 以下方法使用mybatis注解实现

	/**
	 * 获取商品列表，支持分页
	 * @param begin 起始记录（通常为页码*每页大小）
	 * @param size 每页大小
	 * @return 返回商品列表
	 */
	@Select("select * from goods order by id desc limit #{begin}, #{size}")
	public List<Goods> getList(@Param("begin")int begin, @Param("size")int size);

	/**
	 * 获取商品总数
	 * @return 返回商品总数
	 */
	@Select("select count(*) from goods")
	public long getTotal();

	/**
	 * 通过类型获取商品列表，支持分页
	 * @param typeid 商品类型ID
	 * @param begin 起始记录（通常为页码*每页大小）
	 * @param size 每页大小
	 * @return 返回指定类型商品的列表
	 */
	@Select("select * from goods where type_id=#{typeid} order by id desc limit #{begin}, #{size}")
	public List<Goods> getListByType(@Param("typeid")int typeid, @Param("begin")int begin, @Param("size")int size);

	/**
	 * 通过类型获取商品总数
	 * @param typeid 商品类型ID
	 * @return 返回指定类型商品的总数
	 */
	@Select("select count(*) from goods where type_id=#{typeid}")
	public long getTotalByType(@Param("typeid")int typeid);

	/**
	 * 通过名称获取商品列表，支持分页
	 * @param name 商品名称关键词
	 * @param begin 起始记录（通常为页码*每页大小）
	 * @param size 每页大小
	 * @return 返回包含关键词的商品列表
	 */
	@Select("select * from goods where name like concat('%',#{name},'%') order by id desc limit #{begin}, #{size}")
	public List<Goods> getListByName(@Param("name")String name, @Param("begin")int begin, @Param("size")int size);

	/**
	 * 通过名称获取商品总数
	 * @param name 商品名称关键词
	 * @return 返回包含关键词的商品总数
	 */
	@Select("select count(*) from goods where name like concat('%',#{name},'%')")
	public long getTotalByName(@Param("name")String name);
}