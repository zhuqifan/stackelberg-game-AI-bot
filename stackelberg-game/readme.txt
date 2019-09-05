our method is based on linear regression with a Forgetting Model
the idea is to periodically train the model with partial data by forgetting factor


***********
how to run
1.unzip the source code file under project directory
2.javac Leader.java
3.run /usr/java/latest/bin/rmiregistry &
4.run java -classpath poi-3.7-20101029.jar: -Djava.rmi.server.hostname=127.0.0.1 comp34120.ex2.Main &
5.run java -Djava.rmi.server.hostname=127.0.0.1 Leader &
