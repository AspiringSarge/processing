package org.fife.rsta.ac.java;

import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.JTextComponent;

import org.fife.ui.autocomplete.DefaultCompletionProvider;

public class PDEDefaultCompletionProvider extends DefaultCompletionProvider {
  
  @Override
  public String getAlreadyEnteredText(JTextComponent comp) {
    Document doc = comp.getDocument();

    int dot = comp.getCaretPosition();
    Element root = doc.getDefaultRootElement();
    int index = root.getElementIndex(dot);
    Element elem = root.getElement(index);
    int start = elem.getStartOffset();
    int len = dot-start;
    try {
      doc.getText(start, len, seg);
    } catch (BadLocationException ble) {
      ble.printStackTrace();
      return EMPTY_STRING;
    }

    int segEnd = seg.offset + len;
    start = segEnd - 1;
    while (start>=seg.offset && isValidChar(seg.array[start])) {
      start--;
    }
    start++;

    len = segEnd - start;
    return len==0 ? EMPTY_STRING : new String(seg.array, start, len);
  }
}
