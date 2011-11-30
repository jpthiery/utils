package org.lmarin.commons.utils.l18n;

import java.util.ResourceBundle;

import org.junit.Test;
import org.lmarin.commons.utils.i18n.ResourceBundleProxy;

public class ResourceBundleProxyTest {

	@Test
	public void test() {
		QuesterImperialRb instance = ResourceBundleProxy.getInstance(ResourceBundle.getBundle("QuesterImperial"), QuesterImperialRb.class);
		System.out.println(instance.title("1.0.0"));
		System.out.println(instance.getName());
//		System.out.println(instance.getAnnee());
	}
	
	private interface QuesterImperialRb {
		
		String title( String version);		
		
		String getName();
		Integer getAnnee();
	}

}
