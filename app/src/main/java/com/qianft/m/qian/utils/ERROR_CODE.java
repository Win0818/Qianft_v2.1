package com.qianft.m.qian.utils;


public enum ERROR_CODE {

	//  枚举错误代码
	/**
	 * 操作成功
	 */
	X0000("X0000", "操作成功", "操作成功"),

	/**
	 * 分享失败！
	 */
	X0001("X0001", "分享失败！", "分享失败！"),

	/**
	 * 该版本不支持改功能！
	 */
	X0002("X0002", "该版本不支持改功能！", "该版本不支持改功能！"),

	/**
	 * 授权失败！
	 */
	X0003("X0003", "授权失败！", "授权失败！"),

	/**
	  * 登录失败！
	 */
	X0004("0004", "登录失败！", "登录失败！"),

	/**
	 * 网络错误！
	 */
	X0005("0005", "网络错误！", "网络错误！"),
	/**
	 * 该版本不支持改功能！
	 */
	X0006("X0006", "保存失败！", "保存失败！"),
	/**
	 * 该存储不足！
	 */
	X0007("X0007", "存储不足！", "存储不足！"),
	/**
	 * 路径错误！
	 */
	X0008("X0008", "路径错误！", "路径错误！");

	private String index;
	private String name;
	private String msg;

	// 构造方法，注意：构造方法不能为public，因为enum并不可以被实例化
	private ERROR_CODE(String index, String Msg, String name) {
		this.name = name;
		this.index = index;
		msg = Msg;
	}

	// 构造方法，注意：构造方法不能为public，因为enum并不可以被实例化
	private ERROR_CODE(String index, String Msg) {
		this.index = index;
		msg = Msg;
	}

	// 普通方法
	public static String getName(String index) {
		for (ERROR_CODE c : ERROR_CODE.values()) {
			if (c.getIndex() == index) {
				return c.name;
			}
		}
		return null;
	}

	// get set 方法

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIndex() {
		return index;
	}

	public void setIndex(String index) {
		this.index = index;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

}
