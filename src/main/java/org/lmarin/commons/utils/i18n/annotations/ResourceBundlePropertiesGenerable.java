/**
 * 
 */
package org.lmarin.commons.utils.i18n.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation marker.
 * @author Jean-Pascal THIERY
 *
 */
@Retention(RetentionPolicy.SOURCE)
@Target(value={ElementType.TYPE})
public @interface ResourceBundlePropertiesGenerable {
	
	String bundleName() default "";
	
	String comment() default "";
	
}
