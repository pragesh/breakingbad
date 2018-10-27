package com.breakingbad.app;

import org.eclipse.swt.widgets.Display;

/**
 * Main class for running the application.
 * @author prageshjagnani
 *
 */
public class App 
{
    public static void main( String[] args ){
		Display display = new Display();
		new MainWindow(display);
		display.dispose();
	
    }
}
