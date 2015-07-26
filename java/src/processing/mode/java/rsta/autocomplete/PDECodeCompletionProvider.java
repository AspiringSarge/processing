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
import org.fife.ui.autocomplete.FunctionCompletion;
import org.fife.ui.autocomplete.LanguageAwareCompletionProvider;
import org.fife.ui.autocomplete.ParameterizedCompletion;
import org.fife.ui.autocomplete.ParameterizedCompletion.Parameter;

import processing.mode.java.JavaEditor;
import processing.mode.java.pdex.CompletionCandidate;

public class PDECodeCompletionProvider extends DefaultCompletionProvider {

  JavaEditor editor;
  
  public PDECodeCompletionProvider(JavaEditor editor) {
    this.editor = editor;
    setParameterizedCompletionParams('(', ", ", ')');
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
        l.add(new PDEBasicCompletionCandidate(this, list.getElementAt(i)));
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
//      l.add(new PDEBasicCompletionCandidate(this, list.getElementAt(i)));
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
      l.add(PDECompletionCandidateFactory.getCandidate(this, list.getElementAt(i)));
    }
    /*
    PDEFunctionCompletionCandidate f = new PDEFunctionCompletionCandidate(this, "dummy", "int");
    ArrayList<Parameter> p = new ArrayList<>();
    p.add(new Parameter("int", "test"));
    p.add(new Parameter("int", "test2"));
    f.setParams(p);*/
//    f.setShortDescription("Here ye, here ye.");
//    f.setSummary("<html><body>Here is a man that <b>really</b> was gone</body></html>");
//    f.setSummary("<html><table cellpadding=\"0\" cellspacing=\"0\" border=\"0\" class=\"ref-item\"> <tbody><tr class=\"name-row\">    <th scope=\"row\">Name</th>   <td><h3>rect()</h3></td>  </tr> <tr class=\"\"> </tr><tr class=\"\"><th scope=\"row\">Examples</th><td><div class=\"example\"><img src=\"images/rect_0.png\" alt=\"example pic\"><pre class=\"margin\">rect(30, 20, 55, 55);</pre></div><div class=\"example\"><img src=\"images/rect_1.png\" alt=\"example pic\"><pre class=\"margin\">rect(30, 20, 55, 55, 7);</pre></div><div class=\"example\"><img src=\"images/rect_2.png\" alt=\"example pic\"><pre class=\"margin\">rect(30, 20, 55, 55, 3, 6, 12, 18);</pre></div></td></tr>   <tr class=\"\">   <th scope=\"row\">Description</th>    <td>Draws a rectangle to the screen. A rectangle is a four-sided shape with every angle at ninety degrees. By default, the first two parameters set the location of the upper-left corner, the third sets the width, and the fourth sets the height. The way these parameters are interpreted, however, may be changed with the <b>rectMode()</b> function.<br><br>To draw a rounded rectangle, add a fifth parameter, which is used as the radius value for all four corners.<br><br>To use a different radius value for each corner, include eight parameters. When using eight parameters, the latter four set the radius of the arc at each corner separately, starting with the top-left corner and moving clockwise around the rectangle.</td>  </tr> <tr class=\"\"><th scope=\"row\">Syntax</th><td><pre>rect(<kbd>a</kbd>, <kbd>b</kbd>, <kbd>c</kbd>, <kbd>d</kbd>)rect(<kbd>a</kbd>, <kbd>b</kbd>, <kbd>c</kbd>, <kbd>d</kbd>, <kbd>r</kbd>)rect(<kbd>a</kbd>, <kbd>b</kbd>, <kbd>c</kbd>, <kbd>d</kbd>, <kbd>tl</kbd>, <kbd>tr</kbd>, <kbd>br</kbd>, <kbd>bl</kbd>)</pre></td></tr> <tr class=\"\"> <th scope=\"row\">Parameters</th><td><table cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tbody><tr class=\"\"><th scope=\"row\" class=\"code\">a</th><td>float: x-coordinate of the rectangle by default</td></tr><tr class=\"\"><th scope=\"row\" class=\"code\">b</th><td>float: y-coordinate of the rectangle by default</td></tr><tr class=\"\"><th scope=\"row\" class=\"code\">c</th><td>float: width of the rectangle by default</td></tr><tr class=\"\"><th scope=\"row\" class=\"code\">d</th><td>float: height of the rectangle by default</td></tr><tr class=\"\"><th scope=\"row\" class=\"code\">r</th><td>float: radii for all four corners</td></tr><tr class=\"\"><th scope=\"row\" class=\"code\">tl</th><td>float: radius for top-left corner</td></tr><tr class=\"\"><th scope=\"row\" class=\"code\">tr</th><td>float: radius for top-right corner</td></tr><tr class=\"\"><th scope=\"row\" class=\"code\">br</th><td>float: radius for bottom-right corner</td></tr><tr class=\"\"><th scope=\"row\" class=\"code\">bl</th><td>float: radius for bottom-left corner</td></tr></tbody></table></td> </tr> <tr class=\"\"><th scope=\"row\">Returns</th><td class=\"code\">void</td></tr>  <tr class=\"\"><th scope=\"row\">Related</th><td><a class=\"code\" href=\"rectMode_.html\">rectMode()</a><br><a class=\"code\" href=\"quad_.html\">quad()</a><br></td></tr></tbody></table></html");
//    f.setSummary(org.fife.rsta.ac.java.Util.docCommentToHtml("/**La di bla\n\n@param comp: Lah*/"));
//    l.add(f);
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
