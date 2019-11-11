package org.restheart.security.exceptions;

public class TapisException
 extends Exception
{
	private static final long serialVersionUID = -2996383526058549742L;

	// AloeUtils.aloeify() requires that all subclasses implement constructors
	// with these exact signatures or guarantee that aloeify() is never called 
	// with subclasses that don't implement these two constructors.  See 
	// JobUtils.aloeify() for an example of how to implement non-conforming
	// exceptions that still can be aloeified.
	public TapisException(String message) {super(message);}
	public TapisException(String message, Throwable cause) {super(message, cause);}
}
