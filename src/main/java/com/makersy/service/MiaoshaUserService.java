package com.makersy.service;


import com.makersy.dao.MiaoshaUserDao;
import com.makersy.domain.MiaoshaUser;
import com.makersy.exception.GlobalException;
import com.makersy.redis.MiaoshaUserKey;
import com.makersy.redis.RedisService;
import com.makersy.result.CodeMsg;
import com.makersy.utils.MD5Util;
import com.makersy.utils.UUIDUtil;
import com.makersy.vo.LoginVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

@Service
public class MiaoshaUserService {
	
	
	public static final String COOKIE_NAME_TOKEN = "token";

	@Autowired
	private MiaoshaUserDao miaoshaUserDao;
	
	@Autowired
	private RedisService redisService;

	/**
	 * 通过id从缓存或数据库获取user对象
	 * @param id
	 * @return
	 */
	public MiaoshaUser getById(long id) {
		MiaoshaUser user = redisService.get(MiaoshaUserKey.getById, "" + id, MiaoshaUser.class);
		if (user != null) {
			return user;
		}
		//取数据库
		user = miaoshaUserDao.getById(id);
		if (user != null) {
			//对象缓存
			redisService.set(MiaoshaUserKey.getById, "" + id, user);
		}
		return user;
	}

	//更新密码
	public boolean updatePassword(String token, long id, String formPass) {
		//取user
		MiaoshaUser user = getById(id);
		if (user == null) {
			//密码不存在，取出的user为null
			throw new GlobalException(CodeMsg.MOBILE_NOT_EXIST);
		}
		//更新数据库
		//此处新建一个对象，修改哪个字段就修改哪个字段。为了提高sql效率
		MiaoshaUser toBeUpdate = new MiaoshaUser();
		toBeUpdate.setId(id);
		toBeUpdate.setPassword(MD5Util.formPass2DBPass(formPass, user.getSalt()));
		miaoshaUserDao.update(toBeUpdate);
		//处理缓存
		redisService.delete(MiaoshaUserKey.getById, "" + id);
		redisService.set(MiaoshaUserKey.token, token, user);  //更新token，而不是删除，否则用户无法登录
		return true;
	}

	public MiaoshaUser getByToken(HttpServletResponse response, String token) {
		if(StringUtils.isEmpty(token)) {
			return null;
		}
		MiaoshaUser user = redisService.get(MiaoshaUserKey.token, token, MiaoshaUser.class);
		//当前存在有效操作，延长session有效期
		if(user != null) {
			addCookie(response, token, user);
		}
		return user;
	}
	

	public String  login(HttpServletResponse response, LoginVo loginVo) {
		if(loginVo == null) {
			throw new GlobalException(CodeMsg.SERVER_ERROR);
		}
		String mobile = loginVo.getMobile();
		String formPass = loginVo.getPassword();
		//判断手机号是否存在
		MiaoshaUser user = getById(Long.parseLong(mobile));
		if(user == null) {
			throw new GlobalException(CodeMsg.MOBILE_NOT_EXIST);
		}
		//验证密码
		String dbPass = user.getPassword();
		String saltDB = user.getSalt();
		String calcPass = MD5Util.formPass2DBPass(formPass, saltDB);
		if(!calcPass.equals(dbPass)) {
			throw new GlobalException(CodeMsg.PASSWORD_ERROR);
		}
		//登陆成功后生成cookie
		String token = UUIDUtil.uuid();
		addCookie(response, token, user);
		return token;
	}

	/**
	 * 添加cookie方法。即可以新生成一个cookie，也可以为已存在的cookie延长有效期。
     * 如果token已存在，那么只需要更新token的有效时间，不用生成一个新的token
	 * @param response
	 * @param token
	 * @param user
	 */
	private void addCookie(HttpServletResponse response, String token, MiaoshaUser user) {
		redisService.set(MiaoshaUserKey.token, token, user);
		Cookie cookie = new Cookie(COOKIE_NAME_TOKEN, token);
		cookie.setMaxAge(MiaoshaUserKey.token.expireSeconds());  //设置最大有效时间
		cookie.setPath("/");
		response.addCookie(cookie);
	}

}
