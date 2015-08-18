package processing.mode.java.rsta;

import java.awt.Color;
import java.awt.Font;

import processing.app.rsta.PDEScrollBar;
import processing.app.rsta.PDETextArea;
import processing.mode.java.JavaEditor;

public class PDEDebugScrollPane extends PDEScrollBar {
  
  public PDEDebugScrollPane() {
    super();
  }
  
  public PDEDebugScrollPane(PDETextArea pdeTextArea) {
    super(pdeTextArea);
//    this.gutter.setLineNumberColor(Color.BLUE);
  }
  
  public PDEDebugScrollPane(JavaEditor javaeditor) {
    this(javaeditor.getTextArea());
    gutter = new PDEGutter(javaeditor);
    
    // TODO: Workaround, discovered after much effort: set the new gutter into
    // the scrollpane manually to get it to work- done to enable debug symbols
    // to be toggled on click
    this.setRowHeaderView(gutter);
    
    // sets the text area's scrollpane instance as this object. Also handles
    // all the UI, etc. of the gutter
    javaeditor.getTextArea().setScrollbar(this);
  }

}
