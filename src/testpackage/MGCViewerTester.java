/**
* MGCViewerTester.java
*   MGCViewerTester object instantiates MGCViewer class to run.
*
* @author  coltonoppa
* @version 1.41
* @since   2017-04-03 
*/
package testpackage;

import com.coltonstack.mgcviewer.MGCViewer;

public class MGCViewerTester {
    public static void main(String[] args) {
        MGCViewer mgcViewer = new MGCViewer();
        mgcViewer.run();
    }
}