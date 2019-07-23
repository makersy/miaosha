package com.makersy.redis;

public class AccessKey extends BasePrefix{

	private AccessKey(int expireSeconds, String prefix) {
		super(expireSeconds, prefix);
	}

	/**
	 * 这样写可以使同一个key有不同的有效期
	 */
	public static AccessKey withExpire(int expireSeconds) {
		return new AccessKey(expireSeconds, "access");
	}
}
