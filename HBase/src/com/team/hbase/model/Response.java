package com.team.hbase.model;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public abstract class Response<T> {
	@SuppressWarnings("unchecked")
	public T getT() throws InstantiationException, IllegalAccessException {
		Type sType = getClass().getGenericSuperclass();
		Type[] generics = ((ParameterizedType) sType).getActualTypeArguments();
		Class<T> mTClass = (Class<T>) (generics[0]);
		return mTClass.newInstance();
	}
}
