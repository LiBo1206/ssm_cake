package com.controller;

import javax.annotation.Resource;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.entity.Goods;
import com.entity.Items;
import com.entity.Orders;
import com.entity.Users;
import com.service.GoodService;
import com.service.OrderService;
import com.service.TypeService;
import com.service.UserService;
import com.util.SafeUtil;

import java.util.List;
import java.util.Objects;

/**
 * 用户相关接口
 */
@Controller
@RequestMapping("/index")
public class UserController{

	private static final String INDENT_KEY = "order"; // 用于存储订单信息的会话键

	@Resource
	private UserService userService; // 用户服务
	@Resource
	private OrderService orderService; // 订单服务
	@Resource
	private GoodService goodService; // 商品服务
	@Resource
	private TypeService typeService; // 类目服务

	/**
	 * 注册用户
	 * 提供用户注册功能，支持表单验证与用户唯一性检查。
	 *
	 * @param flag 页面标志，控制跳转逻辑
	 * @param user 用户对象，包含注册信息
	 * @param model 模型对象，用于传递数据到页面
	 * @return 注册页面或登录页面
	 */
	@RequestMapping("/register")
	public String register(@RequestParam(required=false, defaultValue="0") int flag, Users user, Model model) {
		model.addAttribute("typeList", typeService.getList()); // 获取类目列表
		if (flag == -1) {
			model.addAttribute("flag", 5); // 注册页面标志
			return "/index/register.jsp"; // 跳转到注册页面
		}
		if (user.getUsername().isEmpty()) {
			model.addAttribute("msg", "用户名不能为空!"); // 错误提示：用户名为空
			return "/index/register.jsp";
		} else if (userService.isExist(user.getUsername())) {
			model.addAttribute("msg", "用户名已存在!"); // 错误提示：用户名已存在
			return "/index/register.jsp";
		} else {
			String password = user.getPassword(); // 保存密码以便登录使用
			userService.add(user); // 添加用户
			user.setPassword(password);
			return "redirect:login?flag=-1"; // 注册成功后跳转到登录页面
		}
	}

	/**
	 * 用户登录
	 * 验证用户登录信息，并将用户信息存储到会话中。
	 *
	 * @param flag 页面标志，控制跳转逻辑
	 * @param user 用户对象，包含登录信息
	 * @param session 会话对象，用于存储用户信息
	 * @param model 模型对象，用于传递数据到页面
	 * @return 登录页面或首页
	 */
	@RequestMapping("/login")
	public String login(@RequestParam(required=false, defaultValue="0") int flag, Users user, HttpSession session, Model model) {
		model.addAttribute("typeList", typeService.getList()); // 获取类目列表
		if (flag == -1) {
			flag = 6; // 登录页面标志
			return "/index/login.jsp"; // 跳转到登录页面
		}
		if (userService.checkUser(user.getUsername(), user.getPassword())) {
			session.setAttribute("user", userService.get(user.getUsername())); // 将用户信息存储到会话中
			return "redirect:index"; // 登录成功后跳转到首页
		} else {
			model.addAttribute("msg", "用户名或密码错误!"); // 错误提示：登录失败
			return "/index/login.jsp";
		}
	}

	/**
	 * 注销登录
	 * 移除用户的会话信息并跳转到登录页面。
	 *
	 * @param session 会话对象，用于管理用户状态
	 * @return 登录页面
	 */
	@RequestMapping("/logout")
	public String logout(HttpSession session) {
		session.removeAttribute("user"); // 移除用户信息
		session.removeAttribute("order"); // 移除订单信息
		return "/index/login.jsp"; // 返回登录页面
	}

	/**
	 * 查看购物车
	 * 显示用户的购物车页面。
	 *
	 * @param model 模型对象，用于传递数据到页面
	 * @return 购物车页面
	 */
	@RequestMapping("/cart")
	public String cart(Model model) {
		model.addAttribute("typeList", typeService.getList()); // 获取类目列表
		return "/index/cart.jsp"; // 返回购物车页面
	}

	/**
	 * 购买商品
	 * 将指定商品添加到购物车，若库存不足则返回错误提示。
	 *
	 * @param goodid 商品ID
	 * @param session 会话对象，用于存储订单信息
	 * @return 操作结果："ok" 或 "empty"
	 */
	@RequestMapping("/buy")
	public @ResponseBody String buy(int goodid, HttpSession session) {
		Goods goods = goodService.get(goodid); // 获取商品信息
		if (goods.getStock() <= 0) { // 检查库存
			return "empty"; // 库存不足
		}
		Orders order = (Orders) session.getAttribute(INDENT_KEY); // 获取当前订单
		if (order == null) {
			session.setAttribute(INDENT_KEY, orderService.add(goods)); // 创建新订单
		} else {
			session.setAttribute(INDENT_KEY, orderService.addOrderItem(order, goods)); // 添加商品到现有订单
		}
		return "ok"; // 操作成功
	}

	/**
	 * 减少商品数量
	 * 减少购物车中指定商品的数量。
	 *
	 * @param goodid 商品ID
	 * @param session 会话对象，用于存储订单信息
	 * @return 操作结果："ok"
	 */
	@RequestMapping("/lessen")
	public @ResponseBody String lessen(int goodid, HttpSession session) {
		Orders order = (Orders) session.getAttribute(INDENT_KEY); // 获取当前订单
		if (order != null) {
			session.setAttribute(INDENT_KEY, orderService.lessenIndentItem(order, goodService.get(goodid))); // 减少商品数量
		}
		return "ok"; // 操作成功
	}

	/**
	 * 删除商品
	 * 从购物车中移除指定商品。
	 *
	 * @param goodid 商品ID
	 * @param session 会话对象，用于存储订单信息
	 * @return 操作结果："ok"
	 */
	@RequestMapping("/delete")
	public @ResponseBody String delete(int goodid, HttpSession session) {
		Orders order = (Orders) session.getAttribute(INDENT_KEY); // 获取当前订单
		if (order != null) {
			session.setAttribute(INDENT_KEY, orderService.deleteIndentItem(order, goodService.get(goodid))); // 删除商品
		}
		return "ok"; // 操作成功
	}



	/**
	 * 提交订单
	 * 用户提交购物车中的订单，首先检查用户是否登录，然后验证商品库存情况，最后保存订单并跳转到支付页面。
	 *
	 * @param request 请求对象，用于设置消息提示
	 * @param session 会话对象，存储用户和订单信息
	 * @param model 模型对象，用于传递数据到页面
	 * @return 页面路径
	 */
	@RequestMapping("/save")
	public String save(ServletRequest request, HttpSession session, Model model) {
		model.addAttribute("typeList", typeService.getList()); // 获取类目列表
		Users user = (Users) session.getAttribute("user"); // 获取当前登录用户
		if (user == null) {
			request.setAttribute("msg", "请登录后提交订单!"); // 提示用户登录
			return "/index/login.jsp"; // 如果未登录，跳转到登录页面
		}

		Orders sessionOrder = (Orders) session.getAttribute(INDENT_KEY); // 获取会话中的订单
		if (sessionOrder != null) {
			// 检查订单中的商品库存
			for (Items item : sessionOrder.getItemList()) {
				Goods product = goodService.get(item.getGoodId()); // 获取商品信息
				if (item.getAmount() > product.getStock()) { // 库存不足
					request.setAttribute("msg", "商品 [" + product.getName() + "] 库存不足! 当前库存数量: " + product.getStock());
					return "/index/cart.jsp"; // 返回购物车页面
				}
			}

			sessionOrder.setUserId(user.getId()); // 设置订单的用户ID
			sessionOrder.setUser(userService.get(user.getId())); // 设置订单的用户信息
			int orderid = orderService.save(sessionOrder); // 保存订单
			session.removeAttribute(INDENT_KEY); // 清除购物车信息
			return "redirect:topay?orderid=" + orderid; // 跳转到支付页面
		}

		request.setAttribute("msg", "处理失败!"); // 提示订单处理失败
		return "/index/cart.jsp"; // 返回购物车页面
	}

	/**
	 * 支付页面
	 * 显示支付页面，用户可以选择支付方式。
	 *
	 * @param orderid 订单ID
	 * @param request 请求对象，用于设置数据
	 * @param model 模型对象，用于传递数据到页面
	 * @return 支付页面
	 */
	@RequestMapping("/topay")
	public String topay(int orderid, ServletRequest request, Model model) {
		model.addAttribute("typeList", typeService.getList()); // 获取类目列表
		request.setAttribute("order", orderService.get(orderid)); // 设置订单信息
		return "/index/pay.jsp"; // 跳转到支付页面
	}

	/**
	 * 支付（模拟）
	 * 模拟支付操作，更新订单支付状态并跳转到支付成功页面。
	 *
	 * @param order 订单对象，包含支付信息
	 * @param model 模型对象，用于传递数据到页面
	 * @return 支付成功页面
	 */
	@RequestMapping("/pay")
	public String pay(Orders order, Model model) {
		model.addAttribute("typeList", typeService.getList()); // 获取类目列表
		orderService.pay(order); // 模拟支付操作，更新支付状态
		return "redirect:payok?orderid=" + order.getId(); // 跳转到支付成功页面
	}

	/**
	 * 支付成功
	 * 显示支付成功页面，告知用户支付结果。
	 *
	 * @param orderid 订单ID
	 * @param request 请求对象，用于设置数据
	 * @param model 模型对象，用于传递数据到页面
	 * @return 支付成功页面
	 */
	@RequestMapping("/payok")
	public String payok(int orderid, ServletRequest request, Model model) {
		model.addAttribute("typeList", typeService.getList()); // 获取类目列表
		Orders order = orderService.get(orderid); // 获取订单信息
		int paytype = order.getPaytype(); // 获取支付方式
		if (paytype == Orders.PAYTYPE_WECHAT || paytype == Orders.PAYTYPE_ALIPAY) {
			request.setAttribute("msg", "订单[" + orderid + "]支付成功"); // 微信或支付宝支付成功
		} else {
			request.setAttribute("msg", "订单[" + orderid + "]货到付款"); // 货到付款
		}
		return "/index/payok.jsp"; // 跳转到支付成功页面
	}

	/**
	 * 查看订单
	 * 显示用户的历史订单列表。
	 *
	 * @param session 会话对象，用于获取当前用户信息
	 * @param model 模型对象，用于传递数据到页面
	 * @return 订单页面
	 */
	@RequestMapping("/order")
	public String order(HttpSession session, Model model) {
		model.addAttribute("flag", 3); // 设置页面标志
		model.addAttribute("typeList", typeService.getList()); // 获取类目列表
		Users user = (Users) session.getAttribute("user"); // 获取当前登录用户
		if (user == null) {
			model.addAttribute("msg", "请登录后查看订单!"); // 提示用户登录
			return "/index/login.jsp"; // 如果未登录，跳转到登录页面
		}

		List<Orders> orderList = orderService.getListByUserid(user.getId()); // 获取用户的订单列表
		if (orderList != null && !orderList.isEmpty()) {
			for (Orders order : orderList) {
				order.setItemList(orderService.getItemList(order.getId())); // 设置每个订单的商品列表
			}
		}
		model.addAttribute("orderList", orderList); // 将订单列表传递到页面
		return "/index/order.jsp"; // 跳转到订单页面
	}

	/**
	 * 个人信息
	 * 允许用户查看和修改个人信息，支持修改姓名、电话、地址和密码。
	 *
	 * @param user 用户对象，包含修改的个人信息
	 * @param session 会话对象，用于获取和更新当前用户信息
	 * @param model 模型对象，用于传递数据到页面
	 * @return 个人信息页面
	 */
	@RequestMapping("/my")
	public String my(Users user, HttpSession session, Model model) {
		model.addAttribute("flag", 4); // 设置页面标志
		model.addAttribute("typeList", typeService.getList()); // 获取类目列表
		Users userLogin = (Users) session.getAttribute("user"); // 获取当前登录用户
		if (userLogin == null) {
			model.addAttribute("msg", "请先登录!"); // 提示用户登录
			return "/index/login.jsp"; // 如果未登录，跳转到登录页面
		}

		// 进入个人中心页面
		if (Objects.isNull(user) || Objects.isNull(user.getId())) {
			return "/index/my.jsp"; // 跳转到个人中心页面
		}

		Users u = userService.get(user.getId()); // 获取用户信息
		// 修改个人信息
		u.setName(user.getName());
		u.setPhone(user.getPhone());
		u.setAddress(user.getAddress());
		userService.update(u); // 更新数据库
		session.setAttribute("user", u); // 更新会话中的用户信息
		model.addAttribute("msg", "信息修改成功!"); // 提示信息修改成功

		// 修改密码
		if (user.getPasswordNew() != null && !user.getPasswordNew().trim().isEmpty()) {
			if (user.getPassword() != null && !user.getPassword().trim().isEmpty()
					&& SafeUtil.encode(user.getPassword()).equals(u.getPassword())) {
				if (user.getPasswordNew() != null && !user.getPasswordNew().trim().isEmpty()) {
					u.setPassword(SafeUtil.encode(user.getPasswordNew())); // 设置新密码
				}
				userService.update(u); // 更新数据库
				session.setAttribute("user", u); // 更新会话中的用户信息
				model.addAttribute("msg", "密码修改成功!"); // 提示密码修改成功
			} else {
				model.addAttribute("msg", "原密码错误!"); // 提示原密码错误
			}
		}
		return "/index/my.jsp"; // 跳转到个人中心页面
	}


}