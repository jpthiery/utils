package org.lmarin.commons.utils.i18n;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.text.MessageFormat;
import java.util.ResourceBundle;

public class ResourceBundleProxy implements InvocationHandler {

	private final ResourceBundle delegate;

	private final Class<?> interfaces;

	public ResourceBundleProxy(ResourceBundle delegate, Class<?> interfaces) {
		super();
		if (interfaces == null) {
			throw new IllegalArgumentException("interfaces is null.");
		}
		if (delegate == null) {
			throw new IllegalArgumentException("delegate is null.");
		}
		this.delegate = delegate;
		this.interfaces = interfaces;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		if (interfaceContainMethode(method.getName()) == null) {
			throw new IllegalStateException("Interface " + interfaces.getCanonicalName() + " doesn't get method with name " + method.getName());
		}
		if (!String.class.isAssignableFrom(method.getReturnType())) {
			throw new IllegalStateException(method.getName() + " doesn't return a String.");
		}
		String methodeName = method.getName().startsWith("get") ? method.getName().substring(3) : method.getName();
		return MessageFormat.format(delegate.getString(methodeName), args);
	}

	private Method interfaceContainMethode(String methodeName) {
		for (Method m : interfaces.getMethods()) {
			if (m.getName().equals(methodeName))
				return m;
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public static <T> T getInstance(ResourceBundle rb, Class<T> interfaces) {
		return (T) Proxy.newProxyInstance(
			interfaces.getClassLoader(),
			new Class[] {interfaces},
			new ResourceBundleProxy(rb, interfaces)
			);
	}

}
