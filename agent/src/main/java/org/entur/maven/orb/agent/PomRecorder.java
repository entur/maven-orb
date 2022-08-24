package org.entur.maven.orb.agent;

/*-
 * #%L
 * Maven POM agent
 * %%
 * Copyright (C) 2020 - 2022 Entur
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
import java.util.Collections;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class PomRecorder extends Thread {

	public static final PomRecorder instance = new PomRecorder();

	protected static final String M2_DIRECTORY = System.getProperty("user.home") + "/.m2";
	protected static final String M2_REPOSITORY_DIRECTORY = M2_DIRECTORY + "/repository";

	public static void record(File file) {
		String name = file.getAbsolutePath();
		if(name.endsWith(".pom") && name.startsWith(M2_REPOSITORY_DIRECTORY)) {
			instance.add(name);
		}
	}

	private final Set<String> poms = Collections.newSetFromMap(new ConcurrentHashMap<>());

	public PomRecorder() {
		Runtime.getRuntime().addShutdownHook(this);
	}

	public void add(String name) {
		poms.add(name);
	}

	public void close() {
		File file;
		do {
			file = new File(M2_DIRECTORY, "maven-pom-recorder-poms-" + randomId() + ".txt");
		} while(file.exists());

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
			System.err.println("Instrumentation problem writing POM files to " + file + " : " + e);
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

	public static String randomId() {
		int leftLimit = 48; // numeral '0'
		int rightLimit = 122; // letter 'z'
		int targetStringLength = 16;
		Random random = new Random();

		String generatedString = random.ints(leftLimit, rightLimit + 1)
				.filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
				.limit(targetStringLength)
				.collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
				.toString();

		return generatedString;
	}

	@Override
	public void run() {
		close();
	}
}
