package processing.mode.java.rsta.autocomplete;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JTextArea;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.JTextComponent;
import javax.swing.text.Segment;
import javax.swing.text.Utilities;

import org.fife.ui.autocomplete.Completion;
import org.fife.ui.autocomplete.DefaultCompletionProvider;
import org.fife.ui.autocomplete.LanguageAwareCompletionProvider;
import org.fife.ui.autocomplete.ParameterizedCompletion;

import processing.mode.java.JavaEditor;
import processing.mode.java.pdex.CompletionCandidate;

public class PDECodeCompletionProvider extends DefaultCompletionProvider {

  JavaEditor editor;
  
  public PDECodeCompletionProvider(JavaEditor editor) {
    this.editor = editor;
  }
  /*
  @Override
  public List<Completion> getCompletionsAt(JTextComponent tc, Point p) {

    int offset = tc.viewToModel(p);
    if (offset<0 || offset>=tc.getDocument().getLength()) {
      return null;
    }

    Segment s = new Segment();
    Document doc = tc.getDocument();
    Element root = doc.getDefaultRootElement();
    int line = root.getElementIndex(offset);
    Element elem = root.getElement(line);
    int start = elem.getStartOffset();
    int end = elem.getEndOffset() - 1;

    try {

      doc.getText(start, end-start, s);

      // Get the valid chars before the specified offset.
      int startOffs = s.offset + (offset-start) - 1;
      while (startOffs>=s.offset && isValidChar(s.array[startOffs])) {
        startOffs--;
      }

      // Get the valid chars at and after the specified offset.
      int endOffs = s.offset + (offset-start);
      while (endOffs<s.offset+s.count && isValidChar(s.array[endOffs])) {
        endOffs++;
      }

      int len = endOffs - startOffs - 1;
      if (len<=0) {
        return null;
      }
      String text = new String(s.array, startOffs+1, len);

      int pdeLine = line + editor.getErrorChecker().mainClassOffset;
      
      DefaultListModel<CompletionCandidate> list = 
          editor.getErrorChecker().getASTGenerator().getPredictions(text, pdeLine,0);
      ArrayList<Completion> l = new ArrayList<>();
      for (int i=0; i<list.getSize(); i++) {
        l.add(new PDERSTACompletionCandidate(this, list.getElementAt(i)));
      }
      return l;
    } catch (BadLocationException ble) {
      ble.printStackTrace(); // Never happens
    }

    return null;

  }
  */
  
  @Override
  public List<ParameterizedCompletion> getParameterizedCompletions(JTextComponent tc) {
//    String text = getAlreadyEnteredText(tc);
//    int pdeLine = line + editor.getErrorChecker().mainClassOffset;
//    
//    DefaultListModel<CompletionCandidate> list = 
//        editor.getErrorChecker().getASTGenerator().getPredictions(text, pdeLine,0);
//    ArrayList<Completion> l = new ArrayList<>();
//    for (int i=0; i<list.getSize(); i++) {
//      l.add(new PDERSTACompletionCandidate(this, list.getElementAt(i)));
//    }
//    return l;
//  } catch (BadLocationException ble) {
//    ble.printStackTrace(); // Never happens
//  }
//
    
    return null;
  }

  @Override
  protected List<Completion> getCompletionsImpl(JTextComponent comp) {
    String text = getAlreadyEnteredText(comp);
    int line = getRow(comp);
    int pdeLine = line + editor.getErrorChecker().mainClassOffset;

    DefaultListModel<CompletionCandidate> list =
        editor.getErrorChecker().getASTGenerator().getPredictions(text,
                                                                  pdeLine, 0);
    ArrayList<Completion> l = new ArrayList<>();
    for (int i = 0; i < list.getSize(); i++) {
      l.add(new PDERSTACompletionCandidate(this, list.getElementAt(i)));
    }
    return l;
  }
  
  /**
   * Use to get line number at which caret is placed.
   * 
   * Code adapted from http://java-sl.com/tip_row_column.html
   * 
   * @param comp
   *          : The JTextArea console
   * @return Row number
   */
  public static int getRow(JTextComponent comp) {
    int pos = comp.getCaretPosition();
    int rn = (pos == 0) ? 1 : 0;
    try {
      int offs = pos;
      while (offs > 0) {
        offs = Utilities.getRowStart(comp, offs) - 1;
        rn++;
      }
    } catch (BadLocationException e) {
      e.printStackTrace();
    }
    return rn;
  }
  
}
