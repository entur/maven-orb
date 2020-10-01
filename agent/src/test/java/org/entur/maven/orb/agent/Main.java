package org.entur.maven.orb.agent;

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
