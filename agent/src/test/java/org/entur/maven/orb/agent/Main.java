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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 
 * Simple test program. Add "-javaagent:/path/to/agent/target/agent-1.0.0-SNAPSHOT.jar" to JVM parameters. 
 *
 */

public class Main {

	private static final String M2 = System.getProperty("user.home") + "/.m2/repository";

	public static void main(String[] args) throws IOException {
		System.out.println("Start");
		File file = new File(M2, "test.pom");
		FileOutputStream fout = new FileOutputStream(file);
		fout.write("TEST".getBytes());
		FileInputStream fin = new FileInputStream(file);
		fin.read();
		System.out.println("End");
	}
}
