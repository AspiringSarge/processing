package processing.mode.java.rsta;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashMap;

import org.fife.ui.rtextarea.Gutter;
import org.fife.ui.rtextarea.LineNumberList;
import org.fife.ui.rtextarea.RTextArea;

import processing.mode.java.JavaEditor;

public class PDEGutter extends Gutter {
  
  public PDEGutter(final JavaEditor javaeditor) {
    super(javaeditor.getTextArea());
//    lineNumberList = null;
    this.lineNumberList.addMouseListener(new MouseListener() {

      @Override
      public void mouseReleased(MouseEvent e) {
      }

      @Override
      public void mousePressed(MouseEvent e) {
      }

      @Override
      public void mouseExited(MouseEvent e) {
      }

      @Override
      public void mouseEntered(MouseEvent e) {
      }

      @Override
      public void mouseClicked(MouseEvent e) {
        int pos = javaeditor.getTextArea().viewToModel(new Point(0, e.getY()));
        int line = javaeditor.getTextArea().getLineOfOffset(pos);
        javaeditor.toggleBreakpoint(line);
      }
    });
  }



public void addDebugSymbol(int line, String symbol) {
  lineNumberList.addDebugSymbol(line+1, symbol);
}


public void removeDebugSymbol(int line) {
  lineNumberList.removeDebugSymbol(line+1);
}


public void clearDebugSymbols() {
  lineNumberList.clearDebugSymbols();
}


public String getDebugSymbol(int line) {
  String s = lineNumberList.getDebugSymbol(line+1);
  if (s!=null) {
    return s;
  }
  else {
    return ((line+1) + "");
  }
}
}
