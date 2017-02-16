# Phonebook Server

## About

This is a small _Java_ application which serves a phonebook over a network. The project includes a 
HTTP server which serves a simple static web interface for sending requests to the server and displaying the response
 and a phonebook server which returns search results. Both servers communicate via a RMI (remote method invocation) 
 and can run on different machines in a network.

## Prerequisites

Java has to be installed on your machine.

## Getting started

To get started, first compile the Java main classes in the `src` directory:

```
javac HttpServer.java && javac PhonebookServer.java
```

> Alternatively, since the servers can be run on departed machines you can move them first.

Now you can start the servers with:

```
java HttpServer
```

By default the server runs at [http://localhost:3000](http://localhost:3000) but you can pass arguments to change that:

* a port number
* `hn` (for host name) or `ha` (for host address) or any other ip address


Next open a new shell (locally or on the other computer) and run:

```
java PhonebookServer
```

Now you can open the interface in the browser and get started.


## License & acknowledgement

MIT

**Please acknowledge:** This project was created as part of a course at [Beuth University](http://www.beuth-hochschule.de/)
