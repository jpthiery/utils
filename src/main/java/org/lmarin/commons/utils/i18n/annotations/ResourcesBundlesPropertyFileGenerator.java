package org.lmarin.commons.utils.i18n.annotations;

import java.util.LinkedList;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;

import org.lmarin.commons.utils.apt.TypeUtils;

@SupportedAnnotationTypes({"org.lmarin.commons.utils.l18n.annotations.ResourceBundlePropertiesGenerable", 
		"org.lmarin.commons.utils.l18n.annotations.ResourcesBundleKey", "org.lmarin.commons.utils.l18n.annotations.ResourcesBundleValue"})
@SupportedSourceVersion(SourceVersion.RELEASE_6)
public class ResourcesBundlesPropertyFileGenerator extends AbstractProcessor {
	
//	private Types types;
	//	private Elements elements;
	private Messager messager;
	private Filer filer;  
	
	@Override
	public synchronized void init(ProcessingEnvironment processingEnv) {
		super.init(processingEnv);
		//		types = processingEnv.getTypeUtils();
		//		elements = processingEnv.getElementUtils();
		messager = processingEnv.getMessager();
		filer = processingEnv.getFiler();
	}

	@Override
	public boolean process(Set<? extends TypeElement> annotations,
			RoundEnvironment roundEnv) {
		for (TypeElement annotation : annotations) {
			for (Element element : roundEnv.getElementsAnnotatedWith(annotation)) {
				
				ResourceBundlePropertiesGenerable resourceBundlePropertiesGenerable = element.getAnnotation(ResourceBundlePropertiesGenerable.class);
				
				if (resourceBundlePropertiesGenerable != null) {
					String bundleName = resourceBundlePropertiesGenerable.bundleName().length() > 0 ? resourceBundlePropertiesGenerable.bundleName() : TypeUtils.getPackageName((TypeElement) element) + "." + element.toString();
					LinkedList<String> outputList = new LinkedList<String>();
					outputList.add("# Generate By " + ResourcesBundlesPropertyFileGenerator.class.getName());
					outputList.add(resourceBundlePropertiesGenerable.comment());
					
					for (Element innerElement : element.getEnclosedElements()){
						if (innerElement.getModifiers().contains(Modifier.PUBLIC) && innerElement.getKind() == ElementKind.METHOD && innerElement instanceof ExecutableElement) {
							ExecutableElement methodeElement = (ExecutableElement) innerElement;
							ResourcesBundleKey keyAnnotation = methodeElement.getAnnotation(ResourcesBundleKey.class);
							
						}
					}
				}
			}
			
			
			
		}
		return true;
	}

}
