package org.entur.maven.orb.agent;

/*-
 * #%L
 * agent
 * %%
 * Copyright (C) 2019 - 2020 Entur
 * %%
 * Licensed under the EUPL, Version 1.1 or – as soon they will be
 * approved by the European Commission - subsequent versions of the
 * EUPL (the "Licence");
 * 
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * http://ec.europa.eu/idabc/eupl5
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 * #L%
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.instrument.Instrumentation;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class FileInstrumentationAgent {

	public static void premain(String agentArgs, Instrumentation instrumentation) {
        try {
    		File f = createJarFile();
    		
			instrumentation.appendToBootstrapClassLoaderSearch(new JarFile(f));
		} catch (Throwable e) {
			System.err.println("Problem appending to classpath, unable to capture POM files");
			e.printStackTrace();
			
			return;
		}

        transformClass(FileInputStream.class.getName(), instrumentation);
    }

	private static File createJarFile() throws IOException, FileNotFoundException {
		File f = File.createTempFile("agent", ".jar");
		
		ZipOutputStream out = new ZipOutputStream(new FileOutputStream(f));
		
		copy(out, "/org/entur/maven/orb/agent/MavenCacheFiles.class");

		out.close();
		return f;
	}

	private static void copy(ZipOutputStream out, String name) throws IOException {
		ZipEntry entry = new ZipEntry(name.substring(1));
		out.putNextEntry(entry);
		
		InputStream in = FileTransformer.class.getResourceAsStream(name);
		
		byte[] buffer = new byte[1024];
		int len;
		while ((len = in.read(buffer)) != -1) {
		    out.write(buffer, 0, len);
		}
		out.flush();
		out.closeEntry();
	}

    private static void transformClass(String className, Instrumentation instrumentation) {
        Class<?> targetCls = null;
        ClassLoader targetClassLoader = null;
        // see if we can get the class using forName

        try {
            targetCls = Class.forName(className);
            targetClassLoader = targetCls.getClassLoader();
            transform(targetCls, targetClassLoader, instrumentation);
            return;
        } catch (Exception ex) {
           // ignore
        }
        // otherwise iterate all loaded classes and find what we want
        for(Class<?> clazz: instrumentation.getAllLoadedClasses()) {
            if(clazz.getName().equals(className)) {
                targetCls = clazz;
                targetClassLoader = targetCls.getClassLoader();
                transform(targetCls, targetClassLoader, instrumentation);
                return;
            }
        }
        throw new RuntimeException("Failed to find class [" + className + "]");
    }

    private static void transform(Class<?> clazz, ClassLoader classLoader, Instrumentation instrumentation) {
        FileTransformer dt = new FileTransformer();
        instrumentation.addTransformer(dt, true);
        try {
            instrumentation.retransformClasses(clazz);
        } catch (Exception ex) {
            throw new RuntimeException("Transform failed for class: [" + clazz.getName() + "]", ex);
        }
    }
}
