/**
* ProcessData.java
*   ProcessData Interface provides one function that takes a String
*   array with variable size & returns void
*
* @author  coltonoppa
* @version 1.41
* @since   2017-04-11 
*/
package com.coltonstack.mgcviewer.runnable.process;

// Interface that InitDatabase, LoadApplication implement
public interface ProcessData {
    public void run(String... input);
}
