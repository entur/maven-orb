# agent
Maven agent (as in instrumentation) for recording access of `pom.xml` files within the `.m2`folder.

Results are written to `/tmp/poms.txt`.

## Usage
```
export MAVEN_OPTS="$MAVEN_OPTS -javaagent:/path/to/agent.jar"
```

The agent modifies the source code of the `java.io.FileInputStream` so that all calls to open is recorded.  

## Shaded contents

 * ASM
 
