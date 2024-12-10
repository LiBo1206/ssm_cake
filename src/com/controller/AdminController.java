package com.controller;

import java.util.Objects;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.entity.Admins;
import com.entity.Goods;
import com.entity.Tops;
import com.entity.Types;
import com.entity.Users;
import com.service.AdminService;
import com.service.GoodService;
import com.service.OrderService;
import com.service.TopService;
import com.service.TypeService;
import com.service.UserService;
import com.util.PageUtil;
import com.util.SafeUtil;
import com.util.UploadUtil;

/**
 * 后台相关接口
 */
@Controller
@RequestMapping("/admin")
public class AdminController {

	private static final int rows = 10; // 每页显示的记录数

	@Autowired
	private AdminService adminService; // 注入管理员服务
	@Autowired
	private OrderService orderService; // 注入订单服务
	@Autowired
	private UserService userService; // 注入用户服务
	@Autowired
	private GoodService goodService; // 注入商品服务
	@Autowired
	private TopService topService; // 注入推荐服务
	@Autowired
	private TypeService typeService; // 注入分类服务

	/**
	 * 管理员登录
	 *
	 * @param admin    管理员实体对象
	 * @param request  请求对象
	 * @param session  会话对象
	 * @return 跳转路径（登录成功重定向到首页，失败返回登录页面）
	 */
	@RequestMapping("/login")
	public String login(Admins admin, HttpServletRequest request, HttpSession session) {
		if (adminService.checkUser(admin.getUsername(), admin.getPassword())) {
			session.setAttribute("username", admin.getUsername()); // 保存用户名到会话
			return "redirect:index"; // 登录成功，跳转到后台首页
		}
		request.setAttribute("msg", "username or password is error!"); // 设置错误信息
		return "/admin/login.jsp"; // 登录失败，返回登录页面
	}

	/**
	 * 退出登录
	 *
	 * @param session 会话对象
	 * @return 登录页面路径
	 */
	@RequestMapping("/logout")
	public String logout(HttpSession session) {
		session.removeAttribute("admin"); // 清除管理员登录信息
		return "/admin/login.jsp";
	}

	/**
	 * 后台首页
	 *
	 * @param request 请求对象
	 * @return 首页页面路径
	 */
	@RequestMapping("/index")
	public String index(HttpServletRequest request) {
		request.setAttribute("msg", "WeCome!!欢迎"); // 设置欢迎信息
		return "/admin/index.jsp";
	}

	/**
	 * 获取订单列表
	 *
	 * @param status  订单状态
	 * @param request 请求对象
	 * @param page    页码
	 * @return 订单列表页面路径
	 */
	@RequestMapping("/orderList")
	public String orderList(@RequestParam(required = false, defaultValue = "0") byte status, HttpServletRequest request,
							@RequestParam(required = false, defaultValue = "1") int page) {
		request.setAttribute("flag", 1); // 设置页面标志
		request.setAttribute("status", status); // 设置订单状态
		request.setAttribute("orderList", orderService.getList(status, page, rows)); // 获取订单列表
		request.setAttribute("pageTool", PageUtil.getPageTool(request, orderService.getTotal(status), page, rows)); // 分页工具
		return "/admin/order_list.jsp";
	}

	/**
	 * 订单发货
	 *
	 * @param id     订单ID
	 * @param status 订单状态
	 * @param page   页码
	 * @return 重定向到订单列表
	 */
	@RequestMapping("/orderDispose")
	public String orderDispose(int id, byte status, @RequestParam(required = false, defaultValue = "1") int page) {
		orderService.dispose(id); // 处理订单
		return "redirect:orderList?flag=1&status=" + status + "&page=" + page;
	}

	/**
	 * 订单完成
	 *
	 * @param id     订单ID
	 * @param status 订单状态
	 * @param page   页码
	 * @return 重定向到订单列表
	 */
	@RequestMapping("/orderFinish")
	public String orderFinish(int id, byte status, @RequestParam(required = false, defaultValue = "1") int page) {
		orderService.finish(id); // 完成订单
		return "redirect:orderList?flag=1&status=" + status + "&page=" + page;
	}

	/**
	 * 删除订单
	 *
	 * @param id     订单ID
	 * @param status 订单状态
	 * @param page   页码
	 * @return 重定向到订单列表
	 */
	@RequestMapping("/orderDelete")
	public String orderDelete(int id, byte status, @RequestParam(required = false, defaultValue = "1") int page) {
		orderService.delete(id); // 删除订单
		return "redirect:orderList?flag=1&status=" + status + "&page=" + page;
	}

	/**
	 * 顾客列表
	 *
	 * @param request 请求对象
	 * @param page    页码
	 * @return 顾客列表页面路径
	 */
	@RequestMapping("/userList")
	public String userList(HttpServletRequest request, @RequestParam(required = false, defaultValue = "1") int page) {
		request.setAttribute("flag", 2); // 设置页面标志
		request.setAttribute("userList", userService.getList(page, rows)); // 获取用户列表
		request.setAttribute("pageTool", PageUtil.getPageTool(request, userService.getTotal(), page, rows)); // 分页工具
		return "/admin/user_list.jsp";
	}

	/**
	 * 顾客添加页面
	 *
	 * @param request 请求对象
	 * @return 顾客添加页面路径
	 */
	@RequestMapping("/userAdd")
	public String userAdd(HttpServletRequest request) {
		request.setAttribute("flag", 2); // 设置页面标志
		return "/admin/user_add.jsp";
	}

	/**
	 * 顾客添加保存
	 *
	 * @param user    用户实体对象
	 * @param request 请求对象
	 * @param page    页码
	 * @return 重定向到顾客列表
	 */
	@RequestMapping("/userSave")
	public String userSave(Users user, HttpServletRequest request, @RequestParam(required = false, defaultValue = "1") int page) {
		if (userService.isExist(user.getUsername())) { // 判断用户名是否已存在
			request.setAttribute("msg", "用户名已存在!"); // 设置错误信息
			return "/admin/user_add.jsp"; // 返回添加页面
		}
		userService.add(user); // 添加用户
		return "redirect:userList?flag=2&page=" + page;
	}

	/**
	 * 顾客密码重置页面
	 *
	 * @param id      用户ID
	 * @param request 请求对象
	 * @return 顾客密码重置页面路径
	 */
	@RequestMapping("/userRe")
	public String userRe(int id, HttpServletRequest request) {
		request.setAttribute("flag", 2); // 设置页面标志
		request.setAttribute("user", userService.get(id)); // 获取用户信息
		return "/admin/user_reset.jsp";
	}

	/**
	 * 顾客密码重置
	 *
	 * @param user 用户实体对象
	 * @param page 页码
	 * @return 重定向到顾客列表
	 */
	@RequestMapping("/userReset")
	public String userReset(Users user, @RequestParam(required = false, defaultValue = "1") int page) {
		String password = SafeUtil.encode(user.getPassword()); // 加密密码
		user = userService.get(user.getId()); // 获取用户信息
		user.setPassword(password); // 更新密码
		userService.update(user); // 保存用户信息
		return "redirect:userList?flag=2&page=" + page;
	}

	/**
	 * 顾客编辑页面
	 *
	 * @param id      用户ID
	 * @param request 请求对象
	 * @return 顾客编辑页面路径
	 */
	@RequestMapping("/userEdit")
	public String userEdit(int id, HttpServletRequest request) {
		request.setAttribute("flag", 2); // 设置页面标志
		request.setAttribute("user", userService.get(id)); // 获取用户信息
		return "/admin/user_edit.jsp";
	}

	/**
	 * 顾客更新
	 *
	 * @param user 用户实体对象
	 * @param page 页码
	 * @return 重定向到顾客列表
	 */
	@RequestMapping("/userUpdate")
	public String userUpdate(Users user, @RequestParam(required = false, defaultValue = "1") int page) {
		userService.update(user); // 更新用户信息
		return "redirect:userList?flag=2&page=" + page;
	}


	/**
	 * 添加推荐
	 * 保存推荐信息到数据库，并返回操作结果。
	 *
	 * @param tops 推荐对象，包含推荐的详细信息
	 * @param status 推荐状态，默认为0
	 * @param page 当前页码，默认为1
	 * @return 返回操作结果，"ok"表示成功，null表示失败
	 */
	@RequestMapping("/topSave")
	public @ResponseBody String topSave(Tops tops,
										@RequestParam(required=false, defaultValue="0") byte status,
										@RequestParam(required=false, defaultValue="1") int page) {
		int id = topService.add(tops); // 保存推荐信息到数据库
		return id > 0 ? "ok" : null; // 返回操作结果
	}

	/**
	 * 删除推荐
	 * 删除指定的推荐信息，并返回操作结果。
	 *
	 * @param tops 推荐对象，包含需要删除的推荐信息
	 * @param status 推荐状态，默认为0
	 * @param page 当前页码，默认为1
	 * @return 返回操作结果，"ok"表示成功，null表示失败
	 */
	@RequestMapping("/topDelete")
	public @ResponseBody String topDelete(Tops tops,
										  @RequestParam(required=false, defaultValue="0") byte status,
										  @RequestParam(required=false, defaultValue="1") int page) {
		boolean flag = topService.delete(tops); // 删除推荐信息
		return flag ? "ok" : null; // 返回操作结果
	}

	/**
	 * 类目列表
	 * 获取类目列表数据，并将相关信息设置到请求中。
	 *
	 * @param request HttpServletRequest对象，用于传递页面相关数据
	 * @return 跳转到类目列表页面
	 */
	@RequestMapping("/typeList")
	public String typeList(HttpServletRequest request) {
		request.setAttribute("flag", 4); // 设置页面标志
		request.setAttribute("typeList", typeService.getList()); // 设置类目列表数据
		return "/admin/type_list.jsp"; // 返回类目列表页面
	}

	/**
	 * 类目添加
	 * 保存用户提交的类目信息，并重定向到类目列表页面。
	 *
	 * @param type 类目对象，包含需要保存的类目信息
	 * @param page 当前页码，默认为1
	 * @return 重定向到类目列表页面
	 */
	@RequestMapping("/typeSave")
	public String typeSave(Types type,
						   @RequestParam(required=false, defaultValue="1") int page) {
		typeService.add(type); // 保存类目信息到数据库
		return "redirect:typeList?flag=4&page=" + page; // 返回类目列表页面
	}


	/**
	 * 类目更新
	 * 显示类目更新页面，提供需要编辑的类目信息。
	 *
	 * @param id 类目ID
	 * @param request HttpServletRequest对象，用于传递页面相关数据
	 * @return 跳转到类目更新页面
	 */
	@RequestMapping("/typeEdit")
	public String typeUp(int id, HttpServletRequest request) {
		request.setAttribute("flag", 4); // 设置页面标志
		request.setAttribute("type", typeService.get(id)); // 获取类目信息
		return "/admin/type_edit.jsp"; // 返回类目编辑页面
	}

	/**
	 * 类目更新
	 * 保存用户提交的类目信息，并重定向到类目列表页面。
	 *
	 * @param type 类目对象，包含需要更新的类目信息
	 * @param page 当前页码，默认为1
	 * @return 重定向到类目列表页面
	 */
	@RequestMapping("/typeUpdate")
	public String typeUpdate(Types type,
							 @RequestParam(required=false, defaultValue="1") int page) {
		typeService.update(type); // 更新类目信息到数据库
		return "redirect:typeList?flag=4&page=" + page; // 返回类目列表页面
	}

	/**
	 * 类目删除
	 * 删除指定的类目信息，并重定向到类目列表页面。
	 *
	 * @param type 类目对象，包含需要删除的类目信息
	 * @param page 当前页码，默认为1
	 * @return 重定向到类目列表页面
	 */
	@RequestMapping("/typeDelete")
	public String typeDelete(Types type,
							 @RequestParam(required=false, defaultValue="1") int page) {
		typeService.delete(type); // 删除类目信息
		return "redirect:typeList?flag=4&page=" + page; // 返回类目列表页面
	}

	/**
	 * 管理员列表
	 * 获取管理员列表数据，并将相关信息设置到请求中。
	 *
	 * @param request HttpServletRequest对象，用于传递页面相关数据
	 * @param page 当前页码，默认为1
	 * @return 跳转到管理员列表页面
	 */
	@RequestMapping("/adminList")
	public String adminList(HttpServletRequest request,
							@RequestParam(required=false, defaultValue="1") int page) {
		request.setAttribute("flag", 5); // 设置页面标志
		request.setAttribute("adminList", adminService.getList(page, rows)); // 获取管理员列表
		request.setAttribute("pageTool", PageUtil.getPageTool(request, adminService.getTotal(), page, rows)); // 设置分页工具
		return "/admin/admin_list.jsp"; // 返回管理员列表页面
	}

	/**
	 * 管理员修改自己密码
	 * 显示管理员密码修改页面，并提供当前管理员信息。
	 *
	 * @param request HttpServletRequest对象，用于传递页面相关数据
	 * @param session HttpSession对象，用于获取当前管理员的用户名
	 * @return 跳转到密码修改页面
	 */
	@RequestMapping("/adminRe")
	public String adminRe(HttpServletRequest request, HttpSession session) {
		request.setAttribute("flag", 5); // 设置页面标志
		request.setAttribute("admin", adminService.getByUsername(String.valueOf(session.getAttribute("username")))); // 获取管理员信息
		return "/admin/admin_reset.jsp"; // 返回密码修改页面
	}

	/**
	 * 管理员修改自己密码
	 * 保存管理员新密码，若原密码错误，返回错误提示。
	 *
	 * @param admin 管理员对象，包含密码修改信息
	 * @param request HttpServletRequest对象，用于传递页面相关数据
	 * @return 跳转到密码修改页面
	 */
	@RequestMapping("/adminReset")
	public String adminReset(Admins admin, HttpServletRequest request) {
		request.setAttribute("flag", 5); // 设置页面标志
		if (adminService.get(admin.getId()).getPassword().equals(SafeUtil.encode(admin.getPassword()))) {
			admin.setPassword(SafeUtil.encode(admin.getPasswordNew())); // 更新密码
			adminService.update(admin); // 保存新密码到数据库
			request.setAttribute("admin", admin);
			request.setAttribute("msg", "修改成功!");
		} else {
			request.setAttribute("msg", "原密码错误!"); // 返回错误提示
		}
		return "/admin/admin_reset.jsp"; // 返回密码修改页面
	}

	/**
	 * 管理员添加
	 * 保存管理员信息到数据库，若用户名已存在，返回错误提示。
	 *
	 * @param admin 管理员对象，包含需要添加的管理员信息
	 * @param request HttpServletRequest对象，用于传递页面相关数据
	 * @param page 当前页码，默认为1
	 * @return 跳转到管理员列表页面或返回添加页面
	 */
	@RequestMapping("/adminSave")
	public String adminSave(Admins admin, HttpServletRequest request,
							@RequestParam(required=false, defaultValue="1") int page) {
		if (adminService.isExist(admin.getUsername())) {
			request.setAttribute("msg", "用户名已存在!"); // 返回错误提示
			return "/admin/admin_add.jsp"; // 返回添加页面
		}
		adminService.add(admin); // 保存管理员信息
		return "redirect:adminList?flag=5&page=" + page; // 返回管理员列表页面
	}

	/**
	 * 管理员修改
	 * 显示管理员信息修改页面，并提供需要编辑的管理员数据。
	 *
	 * @param id 管理员ID
	 * @param request HttpServletRequest对象，用于传递页面相关数据
	 * @return 跳转到管理员信息修改页面
	 */
	@RequestMapping("/adminEdit")
	public String adminEdit(int id, HttpServletRequest request) {
		request.setAttribute("flag", 5); // 设置页面标志
		request.setAttribute("admin", adminService.get(id)); // 获取管理员信息
		return "/admin/admin_edit.jsp"; // 返回管理员编辑页面
	}

	/**
	 * 管理员更新
	 * 保存管理员修改后的信息到数据库，并重定向到管理员列表页面。
	 *
	 * @param admin 管理员对象，包含需要更新的信息
	 * @param page 当前页码，默认为1
	 * @return 重定向到管理员列表页面
	 */
	@RequestMapping("/adminUpdate")
	public String adminUpdate(Admins admin,
							  @RequestParam(required=false, defaultValue="1") int page) {
		admin.setPassword(SafeUtil.encode(admin.getPassword())); // 加密密码
		adminService.update(admin); // 更新管理员信息
		return "redirect:adminList?flag=5&page=" + page; // 返回管理员列表页面
	}

	/**
	 * 管理员删除
	 * 删除指定的管理员信息，并重定向到管理员列表页面。
	 *
	 * @param admin 管理员对象，包含需要删除的信息
	 * @param page 当前页码，默认为1
	 * @return 重定向到管理员列表页面
	 */
	@RequestMapping("/adminDelete")
	public String adminDelete(Admins admin,
							  @RequestParam(required=false, defaultValue="1") int page) {
		adminService.delete(admin); // 删除管理员信息
		return "redirect:adminList?flag=5&page=" + page; // 返回管理员列表页面
	}


}
