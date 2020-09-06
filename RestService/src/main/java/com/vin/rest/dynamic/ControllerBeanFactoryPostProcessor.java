package com.vin.rest.dynamic;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.core.env.Environment;

import javassist.CannotCompileException;
import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ClassFile;
import javassist.bytecode.ConstPool;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.StringMemberValue;

//@Configuration
public class ControllerBeanFactoryPostProcessor implements BeanDefinitionRegistryPostProcessor {

	static Logger log = Logger.getLogger(ControllerBeanFactoryPostProcessor.class.getName());
    String gnc="GenericControllers";
	public ControllerBeanFactoryPostProcessor(Environment springEnvironment) {

	}

	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory)   {

		BeanDefinitionRegistry factory = (BeanDefinitionRegistry) beanFactory;
		GenericBeanDefinition gbd = new GenericBeanDefinition();
		ConstructorArgumentValues ts = new ConstructorArgumentValues();
		ts.addGenericArgumentValue("Dynamic Bean", "String");
		gbd.setConstructorArgumentValues(ts);
		gbd.setBeanClass(String.class);
		String s = "public void sayHello(){ System.out.println(\"Hello\"); }";
		try {

			ClassPool pool = ClassPool.getDefault();
			List<String> methods = new ArrayList<>();
			methods.add(s);
			// extracting the class
			createClass(getClass(), gnc, methods, new ArrayList<>(), "D:\\");

			CtClass cc = pool.getCtClass(gnc);
			cc.defrost();
			ClassFile ccFile = cc.getClassFile();
			ConstPool constpool = ccFile.getConstPool();
			AnnotationsAttribute attr = getAnnotationsAttribute(ccFile);
			Annotation annot = new Annotation("org.springframework.web.bind.annotation.RequestMapping", constpool);
			annot.addMemberValue("name", new StringMemberValue("/testrest", ccFile.getConstPool()));
			attr.addAnnotation(annot);
			Annotation annotrest = new Annotation("org.springframework.web.bind.annotation.RestController", constpool);
			attr.addAnnotation(annotrest);
			cc.writeFile("D:\\");
			 
			Class cd = loadClass(gnc, "D:\\", this.getClass().getClassLoader());

			GenericBeanDefinition gbdctrl = new GenericBeanDefinition();
			gbdctrl.setBeanClass(cd.newInstance().getClass());
			factory.registerBeanDefinition("gbdctrl", gbdctrl);
			 

		} catch (Exception e) {
			e.printStackTrace();
		}
		factory.registerBeanDefinition("YearDataSource", gbd);
	}

	@Override
	public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) {
		 // Do nothing because of this
	}

	public static CtMethod write(CtClass target, String format, Object... args) throws CannotCompileException {
		String body = String.format(format, args);
		CtMethod method = CtNewMethod.make(body, target);
		target.addMethod(method);
		return method;
	}

	public static AnnotationsAttribute getAnnotationsAttribute(ClassFile ccFile) {
		AnnotationsAttribute attr = (AnnotationsAttribute) ccFile.getAttribute(AnnotationsAttribute.visibleTag);
		if (attr == null) {
			attr = new AnnotationsAttribute(ccFile.getConstPool(), AnnotationsAttribute.visibleTag);
			ccFile.addAttribute(attr);
		}
		return attr;
	}

	public static CtClass createClass(Class<?> c, String name, List<String> methods, List<String> interfaces,
			String directory) {
		String temp = null;
		CtClass cc = null;
		try {
			ClassPool pool = ClassPool.getDefault();
			pool.insertClassPath(new ClassClassPath(c));
			cc = pool.makeClass(name);
			if (interfaces != null) {
				for (String s : interfaces) {
					CtClass anInterface = pool.get(s);
					cc.addInterface(anInterface);
				}
			}
			for (String s : methods) {
				temp = s;
				CtMethod m = CtNewMethod.make(s, cc);
				cc.addMethod(m);
			}
			cc.writeFile(directory);
		} catch (Exception e) {
			log.info(temp);
			e.printStackTrace();
		}

		return cc;
	}

	public static Class loadClass(String className, String directory, ClassLoader loader) {
		File f = new File(directory);
		java.net.URL[] urls;
		ClassLoader cl = null;
		try {
			urls = new java.net.URL[] { f.toURI().toURL() };
			cl = new URLClassLoader(urls, loader);
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		}
		Class cls = null;
		try {
			cls = cl.loadClass(className);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return cls;
	}

	public com.vin.rest.dynamic.GenericController getClassGM()  {
		ClassPool pool = ClassPool.getDefault();
		CtClass cc = null;
		ClassFile ccFile =null;
		try {
			cc = pool.getCtClass("com.dynamic.GenericController");
			 ccFile = cc.getClassFile();
			 ConstPool constpool = ccFile.getConstPool();
				AnnotationsAttribute attr = getAnnotationsAttribute(ccFile);
				Annotation annot = new Annotation("org.springframework.web.bind.annotation.RequestMapping", constpool);
				annot.addMemberValue("name", new StringMemberValue("/testrest", ccFile.getConstPool()));
				attr.addAnnotation(annot);
				Annotation annotrest = new Annotation("org.springframework.web.bind.annotation.RestController", constpool);
				attr.addAnnotation(annotrest);
				cc.debugWriteFile();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		

		return (new GenericController());
	}
}
