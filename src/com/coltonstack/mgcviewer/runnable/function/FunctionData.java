/**
* FunctionData.java
*   FunctionData Interface provides one function that takes a String
*   array with variable size & returns List of any Object
* 
* @author  coltonoppa
* @version 1.41
* @since   2017-04-11 
*/
package com.coltonstack.mgcviewer.runnable.function;

import java.util.List;

// Interface that FindRecruits, FindLines implement
public interface FunctionData {
    public List<?> run(String... input);
}
