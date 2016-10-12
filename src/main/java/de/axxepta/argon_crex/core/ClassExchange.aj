package de.axxepta.argon_crex.core;

import java.net.MalformedURLException;

import de.axxepta.oxygen.api.Connection;

import de.axxepta.argon_crex.api.CRexRestConnection;


public aspect ClassExchange {

	pointcut execGetRestConnection(String host, int port, String user, String password) :
		execution(public Connection de.axxepta.oxygen.core.ClassFactory.getRestConnection(String, int, String, String)) && args(host, port, user, password);

	Connection around(String host, int port, String user, String password) throws MalformedURLException :
			execGetRestConnection(host, port, user, password) {
		return new CRexRestConnection(host, port, user, password);
	}

}