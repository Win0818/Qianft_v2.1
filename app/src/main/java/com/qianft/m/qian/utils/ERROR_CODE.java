package com.qianft.m.qian.utils;


public enum ERROR_CODE {

	// /<summary>
	// / 枚举错误代码
	// /</summary>
	/**
	 * 操作成功
	 */
	X0000("X0000", "操作成功", "操作成功"),
	/**
	 * 无效请求，非POST方法
	 */
	X0001("X0001", "无效请求，非POST方法", ""),
	/**
	 * 无效请求，方法名不能为空
	 */
	X0002("X0002", "无效请求，方法名不能为空", ""),
	/**
	 * 无效请求，请求数据内容不能为空
	 */
	X0003("X0003", "无效请求，请求数据内容不能为空", ""),
	/**
	 * 无效请求，请求内容不允许为非JSON对象
	 */
	X0004("X0004", "无效请求，请求内容不允许为非JSON对象", "");

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
