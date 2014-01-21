chmod 777 -R database
chmod 777 -R /tmp
java -server -XX:+AggressiveOpts -XX:CompileThreshold=200 -Xmx4g -Dfile.mmap.maxMemory=3gb -Xss5m -Dstorage.keepOpen=false -javaagent:lib/MemoryCounterAgent-1.0.jar -jar lib/fenix-D3S-core-1.0.jar &
