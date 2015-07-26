package processing.app.rsta;

import org.fife.ui.rtextarea.RTextScrollPane;

public class PDEScrollBar extends RTextScrollPane {
  
  PDETextArea pdeTextArea;
  
  public PDEScrollBar() {
    super();
  }
  
  public PDEScrollBar(PDETextArea pdeTextArea) {
    super(pdeTextArea);
    
    this.pdeTextArea = pdeTextArea;
//    TODO: Figure out how (and whether) to show horizontal scroll bar 
//    this.getHorizontalScrollBar().setVisible(true);
    pdeTextArea.setScrollbar(this);
  }

  public int getVerticalScrollPosition() {
    return getVerticalScrollBar().getValue();
  }

  public void setVerticalScrollPosition(int pos) {
    getVerticalScrollBar().setValue(pos);
  }
  
  /**
   * Ensures that the specified line and offset is visible by scrolling
   * the text area if necessary.
   * @param line The line to scroll to
   * @param offset The offset in the line to scroll to
   * @return True if scrolling was actually performed, false if the
   * line and offset was already visible
   * @deprecated
   */
  public boolean scrollTo(int line, int offset) {
    return false;
  }
}
