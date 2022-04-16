package com.telus.services.ejbtester;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws Exception
    {
        System.out.println( "Hello World!" );
        for (int i = 0; i < 8; i++) {
        	switch (i) {
        	case 0: System.out.println("i=" + i); break;
        	case 1: throw new RuntimeException("i=" + i);
        	default: System.out.println("i=" + i); break;
        	}
        }
    }
}
