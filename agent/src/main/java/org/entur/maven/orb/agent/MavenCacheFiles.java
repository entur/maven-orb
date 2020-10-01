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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Set;

public class MavenCacheFiles extends Thread {

	public static final MavenCacheFiles instance = new MavenCacheFiles();

	private static final String M2 = System.getProperty("user.home") + "/.m2/repository";
	
	public static void open(String name) {
    	if(name.endsWith(".pom") && name.startsWith(M2)) {
    		instance.add(name);
    	}
	}

	private final Set<String> poms = new HashSet<>(1024);

	public MavenCacheFiles() {
		Runtime.getRuntime().addShutdownHook(this);
	}
	
	public void add(String name) {
		synchronized(poms) {
			poms.add(name);
		}
	}
	
	public void close() {
		File file = new File("/tmp/poms.txt");
		
		FileOutputStream fout = null;
		PrintWriter writer = null;
		try {
			fout = new FileOutputStream(file, true); // i.e. append
		    writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(fout)));
			
			synchronized(poms) {
				for(String pom : poms) {
					writer.println(pom);
				}
			}
		} catch (IOException e) {
			System.err.println("Problem writing POM files to " + file + " : " + e);
		} finally {
			if(writer != null) {
				writer.close();
			}
			if(fout != null) {
				try {
					fout.close();
				} catch (IOException e) {
				}
			}
		}
	}

	@Override
	public void run() {
		close();
	}
}
