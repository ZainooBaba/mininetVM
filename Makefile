all: iperfer.class

iperfer.class: iperfer.java
	javac Iperfer.java

run: all
	java Iperfer -c -h Hello -p 12312 -t 10

clean:
	rm -f Iperfer.class
