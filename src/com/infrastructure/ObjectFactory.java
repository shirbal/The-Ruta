package com.infrastructure;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

public class ObjectFactory {
	
	private static ObjectFactory instance = null;
	
	private HashMap<String, Object> registeredClasses = new HashMap<String, Object>();
	
	private ObjectFactory()
	{
	}
	
	public static ObjectFactory Instance()
	{
		if(instance == null)
		{
			instance = new ObjectFactory();
		}
		return instance;
	}
	

	public Object getInstance(String className) throws IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException
	{
		if(registeredClasses.containsKey(className)	== false)
		{
			registerProduct (className);
		}
		return registeredClasses.get(className);
	}
	
	public void register(String className, Object newInstance)
	{
		registeredClasses.put(className, newInstance);
	}
	
	private void registerProduct (String className) throws IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException
	{
		Object newInstance = createInstance(className);
		registeredClasses.put(className, newInstance);
	}

	private Object createInstance(String className) throws IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException
	{
		Class<?> productClass = (Class<?>)registeredClasses.get(className);
		Constructor<?> productConstructor = null;
		try {
			productConstructor = productClass.getDeclaredConstructor(new Class[] { String.class });
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return productConstructor.newInstance(new Object[] { });
	}
}
