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

import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.ASM8;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;

import java.io.FileInputStream;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;

public class FileTransformer implements ClassFileTransformer {

	private final String targetClassName;
	
	public FileTransformer() {
		this.targetClassName = FileInputStream.class.getName().replaceAll("\\.", "/");
	}
	
	/*
	public byte[] transform(Module module, ClassLoader loader, String className, Class<?> classBeingRedefined,
			ProtectionDomain protectionDomain, byte[] byteCode) throws IllegalClassFormatException {
		return transform(loader, className, classBeingRedefined, protectionDomain, byteCode);
	}
	*/
	
	public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] byteCode) throws IllegalClassFormatException {
		if (!className.equals(targetClassName)) {
			return byteCode;
		}

        ClassReader classReader = new ClassReader(byteCode);
        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);

        InputStreamClassVisitor exceptionThrower = new InputStreamClassVisitor(classWriter);

       	classReader.accept(exceptionThrower, 0);
       	
		return classWriter.toByteArray();
	}
	
    public static class InputStreamClassVisitor extends ClassVisitor {
        public InputStreamClassVisitor(ClassVisitor classVisitor) {
            super(ASM8, classVisitor);
        }
        
        @Override
        public MethodVisitor visitMethod(
            int access, String name, String desc, String signature,
            String[] exceptions) {
            
            MethodVisitor mv = cv.visitMethod(access, name, desc, signature, exceptions);
            
            if(name.equals("open")) {
            	return new OpenMethodVisitor(mv);
            }

            return mv;
        }
    }
	
	public static class OpenMethodVisitor extends MethodVisitor {

	    public OpenMethodVisitor(MethodVisitor methodVisitor) {
	        super(ASM8, methodVisitor);
	    }

	    @Override
	    public void visitCode() {
	    	mv.visitVarInsn(ALOAD, 1);
	    	mv.visitMethodInsn(INVOKESTATIC, "org/entur/maven/orb/agent/MavenCacheFiles", "open", "(Ljava/lang/String;)V", false);	    	
	    	
	    	super.visitCode();
	    }
	    
	}	
}
