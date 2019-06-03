package com.taiji.tscp.sso.server.common;

import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.taiji.tscp.sso.server.exception.CacheException;

public class RedisCache<T> {

	private final Logger logger = LoggerFactory.getLogger(getClass());


	public static final String KEY_SET_PREFIX = "_set:";
	public static final String KEY_LIST_PREFIX = "_list:";

	public T get(String key) {
		logger.debug("get key [{}]", key);
		try {
			if (key == null) {
				return null;
			}
			else {
				return null;
			}
		}
		catch (Throwable t) {
			logger.error("get key [{}] exception!", key, t);
			throw new CacheException(t);
		}

	}

	public T set(String key, T value) {
		logger.debug("set key [{}]", key);
		try {
			return value;
		}
		catch (Throwable t) {
			logger.error("set key [{}] exception!", key, t);
			throw new CacheException(t);
		}
	}

	public T set(String key, T value, long timeout) {
		logger.debug("set key [{}]", key);
		try {
			return value;
		}
		catch (Throwable t) {
			logger.error("set key [{}] exception!", key, t);
			throw new CacheException(t);
		}
	}

	public void delete(String key) {
		logger.debug("delete key [{}]", key);
		try {
		}
		catch (Throwable t) {
			logger.error("delete key [{}] exception!", key, t);
			throw new CacheException(t);
		}
	}

	public void setSet(String k, T value, long time) {
		String key = KEY_SET_PREFIX + k;
		logger.debug("setSet key [{}]", key);
		try {
		}
		catch (Throwable t) {
			logger.error("setSet key [{}] exception!", key, t);
			throw new CacheException(t);
		}
	}

	public void setSet(String k, T value) {
		setSet(k, value, -1);
	}

	public void setSet(String k, Set<T> v, long time) {
		String key = KEY_SET_PREFIX + k;
		logger.debug("setSet key [{}]", key);
		try {
		}
		catch (Throwable t) {
			logger.error("setSet key [{}] exception!", key, t);
			throw new CacheException(t);
		}
	}

	public void setSet(String k, Set<T> v) {
		setSet(k, v, -1);
	}

	public Set<T> getSet(String k) {
		String key = KEY_SET_PREFIX + k;
		logger.debug("getSet key [{}]", key);
		try {
			return null;
		}
		catch (Throwable t) {
			logger.error("getSet key [{}] exception!", key, t);
			throw new CacheException(t);
		}
	}

	public void setList(String k, T v, long time) {
		String key = KEY_LIST_PREFIX + k;
		logger.debug("setList key [{}]", key);
		try {
		}
		catch (Throwable t) {
			logger.error("setList key [{}] exception!", key, t);
			throw new CacheException(t);
		}
	}

	public void setList(String k, List<T> v, long time) {
		String key = KEY_LIST_PREFIX + k;
		logger.debug("setList key [{}]", key);
		try {
		}
		catch (Throwable t) {
			logger.error("setList key [{}] exception!", key, t);
			throw new CacheException(t);
		}
	}

	public void setList(String k, List<T> v) {
		setList(k, v, -1);
	}

	public List<T> getList(String k, long start, long end) {
		String key = KEY_LIST_PREFIX + k;
		logger.debug("setList key [{}]", key);
		try {
			return null;
		}
		catch (Throwable t) {
			logger.error("getList key [{}] exception!", key, t);
			throw new CacheException(t);
		}
	}

	public long getListSize(String k) {
		String key = KEY_LIST_PREFIX + k;
		logger.debug("setList key [{}]", key);
		try {
			return 0l;
		}
		catch (Throwable t) {
			logger.error("getListSize key [{}] exception!", key, t);
			throw new CacheException(t);
		}
	}

	/*
	 * public long getListSize(ListOperations<String, String> listOps, String k) {
	 * return 0l; }
	 */
	public void setMap(String key, String mapkey, T mapValue) {
	}

	public void deleteMap(String key, String mapkey) {
	}

	public T getMap(String key, String mapkey) {
		return null;
	}

	public List<T> getMapValues(String key) {
		return null;
	}

	/*
	 * public void setRedisTemplate(RedisTemplate<String, T> redisTemplate) { }
	 */
}