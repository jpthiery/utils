package org.lmarin.commons.utils.apt;

import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;

public class TypeUtils {

	public static final String getPackageName(TypeElement typeElement) {
		String res = "";
		String className = typeElement.getSimpleName().toString();
		String fullClassName = typeElement.getQualifiedName().toString();
		int end = fullClassName.length() - (className.length() + 1);
		res = fullClassName.substring(0,end);
		return res;
	}

	public static final Class<?> getClassFromType(TypeMirror typeMirror) throws ClassNotFoundException {
		return Class.forName(typeMirror.toString());
	}
	
	public static String dumpException(Exception e) {
		StringBuilder sb = new StringBuilder();
		sb.append(e.getMessage()).append('\n');
		for (StackTraceElement stack : e.getStackTrace()) {
			sb.append(stack.toString()).append('\n');
		}
		return sb.toString();
	}
	
}
