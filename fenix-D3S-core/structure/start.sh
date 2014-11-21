chmod 777 -R database
chmod 777 -R /tmp
java -server -XX:+AggressiveOpts -XX:CompileThreshold=200 -Xmx4g -Dfile.mmap.maxMemory=3gb -Xss5m -Dstorage.keepOpen=false -jar lib/fenix-D3S-core-2.0-SNAPSHOT.jar &
