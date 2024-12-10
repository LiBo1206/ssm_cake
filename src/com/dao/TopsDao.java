package com.dao;

import java.util.List;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import com.entity.Tops;

public interface TopsDao {
	// 根据ID删除置顶项记录
	int deleteById(Integer id);

	// 插入新的置顶项记录
	int insert(Tops record);

	// 选择性地插入置顶项记录，只插入非空字段
	int insertSelective(Tops record);

	// 根据ID查询置顶项记录
	Tops selectById(Integer id);

	// 选择性地更新置顶项记录，只更新非空字段
	int updateByIdSelective(Tops record);

	// 根据ID更新置顶项记录
	int updateById(Tops record);

	// 以上为mybatis generator自动生成接口, 具体实现在mapper.xml中
	// ------------------------------------------------------------

	// 以下方法使用mybatis注解实现

	/**
	 * 获取置顶项列表，支持分页
	 * @param type 置顶项类型
	 * @param begin 起始记录（通常为页码*每页大小）
	 * @param size 每页大小
	 * @return 返回匹配类型的置顶项列表
	 */
	@Select("select * from tops where type=#{type} order by id desc limit #{begin}, #{size}")
	public List<Tops> getList(@Param("type")byte type, @Param("begin")int begin, @Param("size")int size);

	/**
	 * 获取置顶项总数
	 * @param type 置顶项类型
	 * @return 返回匹配类型的置顶项总数
	 */
	@Select("select count(*) from tops where type=#{type}")
	public long getTotal(byte type);

	/**
	 * 通过商品ID获取置顶项列表
	 * @param goodid 商品ID
	 * @return 返回与该商品ID相关联的置顶项列表
	 */
	@Select("select * from tops where good_id=#{goodid}")
	public List<Tops> getListByGoodid(int goodid);

	/**
	 * 通过商品ID和类型删除置顶项
	 * @param goodid 商品ID
	 * @param type 置顶项类型
	 * @return 操作成功返回true，否则返回false
	 */
	@Delete("delete from tops where good_id=#{goodid} and type=#{type}")
	public boolean deleteByGoodidAndType(@Param("goodid")int goodid, @Param("type")byte type);

	/**
	 * 通过商品ID删除置顶项
	 * @param goodid 商品ID
	 * @return 操作成功返回true，否则返回false
	 */
	@Delete("delete from tops where good_id=#{goodid}")
	public boolean deleteByGoodid(@Param("goodid")int goodid);
}