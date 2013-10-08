package org.lmarin.commons.utils.neo4j;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.apache.commons.beanutils.PropertyUtils;
import org.lmarin.commons.utils.neo4j.annotation.Identifier;

public class AnnotedBeanIdentfiableHandler implements InvocationHandler {

	private static final String PROPERTYNAME = "Identifier";
	
	private static final String GET_IDENTIFIERMETHODNAME = "get" + PROPERTYNAME;

	private static final String SET_IDENTIFIERMETHODNAME = "set" + PROPERTYNAME;

	private final Object target;

	private Field idenfitierField;

	public AnnotedBeanIdentfiableHandler(Object target) {
		super();
		this.target = target;
		Class<? extends Object> targetClass = this.target.getClass();
		Field[] fields = targetClass.getDeclaredFields();
		for (int i = 0; i < fields.length && idenfitierField == null; i++) {
			Field field = fields[i];
			Identifier annotation = field.getAnnotation(Identifier.class);
			if (annotation != null) {
				idenfitierField = field;
			}
		}
		if (idenfitierField == null) {
			throw new IllegalArgumentException(targetClass.getName()
							+ " class need to get an annotation @Identifier on one field");
		}
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

		if (method.getName().equals(GET_IDENTIFIERMETHODNAME)) {

			return PropertyUtils.getProperty(target, idenfitierField.getName());

		} else if (method.getName().equals(SET_IDENTIFIERMETHODNAME)) {

			PropertyUtils.setProperty(target, idenfitierField.getName(), args[0]);
			return null;

		} else {

			return method.invoke(proxy, args);

		}
	}

	@SuppressWarnings("unchecked")
	public final static <T> Identifiable<T> createIdentifiable(Object bean) {
		return (Identifiable<T>) Proxy.newProxyInstance(bean.getClass().getClassLoader(),
						new Class[] { Identifiable.class }, new AnnotedBeanIdentfiableHandler(bean));
	}

}
