package com.hlframe.modules.dc.utils;

import java.net.URL;
import java.net.URLClassLoader;

public class MyClassLoader extends URLClassLoader {

	public MyClassLoader(URL[] urls) {
		super(urls);
	}

	public MyClassLoader(URL[] urls, ClassLoader parent) {
		super(urls, parent);
	}

	public void addJar(URL url) {
		this.addURL(url);
	}

}
