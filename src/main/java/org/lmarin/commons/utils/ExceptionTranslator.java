package org.lmarin.commons.utils;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.apache.commons.io.IOUtils;

public class ExceptionTranslator {

	public static String convert(Throwable t) {
		if (t != null) {
			StringWriter buffer = new StringWriter();
			PrintWriter writer = null;
			try {
				writer = new PrintWriter(buffer,true);
				t.printStackTrace(writer);
			} finally {
				IOUtils.closeQuietly(writer);
			}
			return buffer.toString();
		} else {
			return null;
		}
	}

}
