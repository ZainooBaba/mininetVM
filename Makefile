all: Iperfer.class

Iperfer.class: Iperfer.java
	javac Iperfer.java

run: all
	java Iperfer -c -h Hello -p 12312 -t 10

serv: all
	java Iperfer -s -p 5000

client: all
	java Iperfer -c -h 10.0.0.1 -p 5000 -t 10

clean:
	rm -f Iperfer.class
