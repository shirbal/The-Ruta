package com.infrastructure;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

public class ObjectFactory {
	
	private static ObjectFactory mInstance = null;
	
	/**
	 * 
	 */
	private ObjectFactory() { }
	
	/**
	 * 
	 * @return
	 */
	public static synchronized ObjectFactory instance()
	{
		if(mInstance == null)
		{
			mInstance = new ObjectFactory();
		}
		return mInstance;
	}
	/**
	 * 
	 */
	private HashMap<String, Object> registeredClasses = new HashMap<String, Object>();
	
	/**
	 * 
	 * @param className
	 * @return
	 */
	public Object getInstance(String className)
	{
		Object newInstance = null;
		if(registeredClasses.containsKey(className)	== false)
		{
			try {
				newInstance = createInstance(className);
				registeredClasses.put(className, newInstance);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
		else
		{
			newInstance = registeredClasses.get(className);
		}
		return newInstance;
	}
	
	/**
	 * 
	 * @param className
	 * @param newInstance
	 */
	public void register(String className, Object newInstance)
	{
		registeredClasses.put(className, newInstance);
	}
	
	/**
	 * 
	 * @param className
	 * @return
	 * @throws IllegalArgumentException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	private Object createInstance(String className) throws IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException
	{
		Class<?> productClass = (Class<?>)registeredClasses.get(className);
		Constructor<?> productConstructor = null;
		try {
			productConstructor = productClass.getDeclaredConstructor(new Class[] { String.class });
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
		return productConstructor.newInstance(new Object[] { });
	}
}
