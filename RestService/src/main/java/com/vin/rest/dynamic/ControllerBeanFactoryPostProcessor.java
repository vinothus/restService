package com.vin.rest.dynamic;

import java.io.File;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import javassist.CannotCompileException;
import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.NotFoundException;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ClassFile;
import javassist.bytecode.ConstPool;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.StringMemberValue;
import javassist.util.HotSwapper;

//@Configuration
public class ControllerBeanFactoryPostProcessor implements BeanDefinitionRegistryPostProcessor {

	static Logger log = Logger.getLogger(ControllerBeanFactoryPostProcessor.class.getName());

	public ControllerBeanFactoryPostProcessor(Environment springEnvironment) {

		log.info(springEnvironment.toString());
	}

	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

		BeanDefinitionRegistry factory = (BeanDefinitionRegistry) beanFactory;
		// BeanDefinitionBuilder beanDefinitionBuilder;
		GenericBeanDefinition gbd = new GenericBeanDefinition();
		ConstructorArgumentValues ts = new ConstructorArgumentValues();
		ts.addGenericArgumentValue("Dynamic Bean", "String");
		gbd.setConstructorArgumentValues(ts);
		gbd.setBeanClass(String.class);
		String s = "public void sayHello(){ System.out.println(\"Hello\"); }";
		try {
			Class c = Class.forName("com.dynamic.GenericController");

			ClassPool pool = ClassPool.getDefault();
			List<String> methods = new ArrayList<String>();
			methods.add(s);
			// extracting the class
			createClass(getClass(), "GenericControllers", methods, new ArrayList<String>(), "D:\\");// pool.getCtClass("com.dynamic.GenericController");

			CtClass cc = pool.getCtClass("GenericControllers");
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
			// Class<?> clazz = pool.toClass(cc);
			// Object instance = cd.newInstance();
			// Object instance=c.newInstance();
			Class cd = loadClass("GenericControllers", "D:\\", this.getClass().getClassLoader());

			GenericBeanDefinition gbdctrl = new GenericBeanDefinition();
			gbdctrl.setBeanClass(cd.newInstance().getClass());
			factory.registerBeanDefinition("gbdctrl", gbdctrl);
			/*
			 * Annotation annotRestController = new Annotation() {
			 * 
			 * @Override public Class<? extends Annotation> annotationType() {
			 * 
			 * return org.springframework.web.bind.annotation.RestController.class; } };
			 * Annotation annotRequestPath = new Annotation() {
			 * 
			 * @Override public Class<? extends Annotation> annotationType() {
			 * 
			 * return org.springframework.web.bind.annotation.RequestMapping.class; } };
			 */

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		factory.registerBeanDefinition("YearDataSource", gbd);
	}

	@Override
	public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
		// TODO Auto-generated method stub

	}

	public static CtMethod write(CtClass target, String format, Object... args) throws CannotCompileException {
		String body = String.format(format, args);
		// log.info( "writing method into [%s]:%n%s%n", target.getName(), body );
		log.info(("writing method into [%s]:%n%s" + target.getName() + body));
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
			// TODO throw
			log.info(temp);
			e.printStackTrace();
		}

		return cc;
	}

	public static Class loadClass(String className, String directory, ClassLoader loader) throws Exception {
		File f = new File(directory);
		java.net.URL[] urls = new java.net.URL[] { f.toURI().toURL() };
		ClassLoader cl = new URLClassLoader(urls, loader);
		Class cls = cl.loadClass(className);
		return cls;
	}

	public com.vin.rest.dynamic.GenericController getClassGM() throws Exception {
		ClassPool pool = ClassPool.getDefault();
		CtClass cc = pool.getCtClass("com.dynamic.GenericController");
		// cc.defrost();
		ClassFile ccFile = cc.getClassFile();
		ConstPool constpool = ccFile.getConstPool();
		AnnotationsAttribute attr = getAnnotationsAttribute(ccFile);
		Annotation annot = new Annotation("org.springframework.web.bind.annotation.RequestMapping", constpool);
		annot.addMemberValue("name", new StringMemberValue("/testrest", ccFile.getConstPool()));
		attr.addAnnotation(annot);
		Annotation annotrest = new Annotation("org.springframework.web.bind.annotation.RestController", constpool);
		attr.addAnnotation(annotrest);
		// byte[] classFile = cc.toBytecode();
		/*
		 * HotSwapper hs=new HotSwapper(8000) ;//= new HostSwapper(); // 8000 is a port
		 * number. hs.reload("com.dynamic.GenericController", classFile);
		 */
		cc.debugWriteFile();
		// Class<?> clazz = pool.toClass(cc);

		// clazz.
		return (new GenericController());
	}
}
