package processing.app.rsyntax;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.Transferable;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;

import javax.swing.JPopupMenu;
import javax.swing.text.BadLocationException;
import javax.swing.text.Segment;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;

import processing.app.syntax.HtmlSelection;
import processing.app.syntax.SyntaxDocument;
import processing.app.syntax.TokenMarker;

public class PDESyntaxTextArea {
  RSyntaxTextArea rsTextArea;
  RTextScrollPane rtScrollPane;
  
  public PDESyntaxTextArea() {
    rsTextArea = new RSyntaxTextArea(); // TODO: Add a SyntaxDocument?
    rtScrollPane = new RTextScrollPane(rsTextArea, true);
    rsTextArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA);
    rsTextArea.setCodeFoldingEnabled(true);
    rsTextArea.setAntiAliasingEnabled(true);
  }
  
  public RTextScrollPane getRTScrollPane() {
    return rtScrollPane;
  }
  
  public RSyntaxTextArea getRSTextArea() {
    return rsTextArea;
  }
  
  public boolean isSelectionActive() {
    return (rsTextArea.getSelectionEnd()-rsTextArea.getSelectionStart()) != 0;
  }
  
  public void setSelectedText(String what) {
    rsTextArea.replaceSelection(what);
  }
  
  public int getDocumentLength() {
    return rsTextArea.getText().length();
  }
  /**
   * Returns the text on the specified line.
   * @param lineIndex The line
   * @return The text, or null if the line is invalid
   */
  public final String getLineText(int lineIndex) {
    int start;
    String str = null;
    try {
      start = rsTextArea.getLineStartOffset(lineIndex);
      int end = rsTextArea.getLineEndOffset(lineIndex) - start - 1;
      str = rsTextArea.getText(start,end);
    } catch (BadLocationException e) {
      e.printStackTrace();
    }
    return str;
  }

  /**
   * Copies the text on the specified line into a segment. If the line
   * is invalid, the segment will contain a null string.
   * @param lineIndex The line
   */
  public final void getLineText(int lineIndex, Segment segment) {
    try {
      int start = rsTextArea.getLineStartOffset(lineIndex);
      int end = rsTextArea.getLineEndOffset(lineIndex);
      int offset = end - start - 1;
      rsTextArea.getDocument().getText(start,offset,segment);
    } catch (BadLocationException e) {
      e.printStackTrace();
    }
  }

  public int getLineStartNonWhiteSpaceOffset(int line) {
    int offset, length;
    String str;
    try {
      offset = rsTextArea.getLineStartOffset(line);
      length = getLineLength(line);
      str = rsTextArea.getText(offset, length);
    } catch (BadLocationException e) {
      e.printStackTrace();
      return -1;
    }

    for(int i = 0; i < str.length(); i++) {
      if(!Character.isWhitespace(str.charAt(i))) {
        return offset + i;
      }
    }
    return offset + length;
  }

  /**
   * Copy the current selection as HTML, formerly "Format for Discourse".
   * <p/>
   * Original code by <A HREF="http://usuarios.iponet.es/imoreta">owd</A>.
   * <p/>
   * Revised and updated for revision 0108 by Ben Fry (10 March 2006).
   * <p/>
   * Updated for 0122 to simply copy the code directly to the clipboard,
   * rather than opening a new window.
   * <p/>
   * Updated for 0144 to only format the selected lines.
   * <p/>
   * Updated for 0185 to incorporate the HTML changes from the Arduino project,
   * and set the formatter to always use HTML (disabling, but not removing the
   * YaBB version of the code) and also fixing it for the Tools API.
   * <p/>
   * Updated for 0190 to simply be part of JEditTextArea, removed YaBB code.
   * Simplest and most sensible to have it live here, since it's no longer
   * specific to any language or version of the PDE.
   */
  public void copyAsHTML() {
    StringBuilder cf = new StringBuilder("<html><body><pre>\n");

    int selStart = rsTextArea.getSelectionStart();
    int selStop = rsTextArea.getSelectionEnd();

    int startLine = getSelectionStartLine();
    int stopLine = getSelectionStopLine();

    // If no selection, convert all the lines
    if (selStart == selStop) {
      startLine = 0;
      stopLine = rsTextArea.getLineCount() - 1;
    } else {
      // Make sure the selection doesn't end at the beginning of the last line
      int lineStart = -1;
      try {
        lineStart = rsTextArea.getLineStartOffset(stopLine);
      } catch (BadLocationException e) {
        e.printStackTrace();
      }
      if (lineStart == selStop) {
        stopLine--;
      }
    }

    // Read the code line by line
    for (int i = startLine; i <= stopLine; i++) {
      emitAsHTML(cf, i);
    }

    cf.append("\n</pre></body></html>");

    HtmlSelection formatted = new HtmlSelection(cf.toString());

    Clipboard clipboard = processing.app.Toolkit.getSystemClipboard();
    clipboard.setContents(formatted, new ClipboardOwner() {
      public void lostOwnership(Clipboard clipboard, Transferable contents) {
        // i don't care about ownership
      }
    });
  }

  public int getSelectionStartLine() {
    int line = -1;
    int start = rsTextArea.getSelectionStart();
    try {
      line = rsTextArea.getLineOfOffset(start);
    } catch (BadLocationException e) {
      e.printStackTrace();
    }
    return line;
  }

  public int getSelectionStopLine() {
    int line = -1;
    int end = rsTextArea.getSelectionEnd();
    try {
      line = rsTextArea.getLineOfOffset(end);
    } catch (BadLocationException e) {
      e.printStackTrace();
    }
    return line;
  }

  /**
   * Returns the line containing the specified offset.
   * @param offset The offset
   */
  public final int getLineOfOffset(int offset) {
    return rsTextArea.getDocument().getDefaultRootElement().getElementIndex(offset);
  }

  private void emitAsHTML(StringBuilder cf, int line) {
    Segment segment = new Segment();
    getLineText(line, segment);

    char[] segmentArray = segment.array;
    int limit = segment.getEndIndex();
    int segmentOffset = segment.offset;
    int segmentCount = segment.count;
    // TODO: TokenMarker tokenMarker = rsTextArea.getTokenMarker();
    TokenMarker tokenMarker = null;
    // If syntax coloring is disabled, do simple translation
    if (tokenMarker == null) {
      for (int j = 0; j < segmentCount; j++) {
        char c = segmentArray[j + segmentOffset];
        //cf = cf.append(c);
        appendAsHTML(cf, c);
      }
    }/* TODO: 
    else {
      // If syntax coloring is enabled, we have to do this
      // because tokens can vary in width
      Token tokens;
      if ((painter.getCurrentLineIndex() == line) &&
          (painter.getCurrentLineTokens() != null)) {
        tokens = painter.getCurrentLineTokens();

      } else {
        painter.setCurrentLineIndex(line);
        painter.setCurrentLineTokens(tokenMarker.markTokens(segment, line));
        tokens = painter.getCurrentLineTokens();
      }

      int offset = 0;
      SyntaxStyle[] styles = painter.getStyles();

      for (;;) {
        byte id = tokens.id;
        if (id == Token.END) {
          char c = segmentArray[segmentOffset + offset];
          if (segmentOffset + offset < limit) {
            //cf.append(c);
            appendAsHTML(cf, c);
          } else {
            cf.append('\n');
          }
          return; // cf.toString();
        }
        if (id != Token.NULL) {
          cf.append("<span style=\"color: #");
          cf.append(PApplet.hex(styles[id].getColor().getRGB() & 0xFFFFFF, 6));
          cf.append(";\">");

          if (styles[id].isBold())
            cf.append("<b>");
        }
        int length = tokens.length;

        for (int j = 0; j < length; j++) {
          char c = segmentArray[segmentOffset + offset + j];
          if (offset == 0 && c == ' ') {
            // Force spaces at the beginning of the line
            cf.append("&nbsp;");
          } else {
            appendAsHTML(cf, c);
            //cf.append(c);
          }
          // Place close tags [/]
          if (j == (length - 1) && id != Token.NULL && styles[id].isBold())
            cf.append("</b>");
          if (j == (length - 1) && id != Token.NULL)
            cf.append("</span>");
        }
        offset += length;
        tokens = tokens.next;
      }
    } */
  }

  /**
   * Handle encoding HTML entities for lt, gt, and anything non-ASCII.
   */
  private void appendAsHTML(StringBuilder buffer, char c) {
    if (c == '<') {
      buffer.append("&lt;");
    } else if (c == '>') {
      buffer.append("&gt;");
    } else if (c == '&') {
      buffer.append("&amp;");
    } else if (c == '\'') {
      buffer.append("&apos;");
    } else if (c == '"') {
      buffer.append("&quot;");
    } else if (c > 127) {
      buffer.append("&#" + ((int) c) + ";");  // use unicode entity
    } else {
      buffer.append(c);  // normal character
    }
  }

  /**
   * Set document with a twist, includes the old caret
   * and scroll positions, added for p5. [fry]
   */
  public void setDocument(SyntaxDocument document,
                          int start, int stop, int scroll) {
   /* TODO:
    if (rsTextArea.getDocument() == document)
      return;
    if (rsTextArea.getDocument() != null)
      rsTextArea.getDocument().removeDocumentListener(documentHandler);
    this.document = document;

    document.addDocumentListener(documentHandler);

    select(start, stop);
    updateScrollBars();
    setVerticalScrollPosition(scroll);
    painter.repaint();
    */
  }


  /**
   * Returns the length of the specified line.
   * @param line The line
   */
  public int getLineLength(int line) {
    int len = -1;
    try {
      len = rsTextArea.getLineEndOffset(line) - 
      rsTextArea.getLineStartOffset(line) - 1;
    } catch (BadLocationException e) {
      e.printStackTrace();
    }
    return len;
  }
  
  public void setRightClickPopup(JPopupMenu menu) {
    rsTextArea.setPopupMenu(menu);
  }

  public int getVerticalScrollPosition() {
    return getRTScrollPane().getVerticalScrollBar().getValue();
  }

  public void setVerticalScrollPosition(int pos) {
    getRTScrollPane().getVerticalScrollBar().setValue(pos);
  }
  
  public Printable getPrintable() {
    return new Printable() {
      
      @Override
      public int print(Graphics graphics, PageFormat pageFormat, 
                       int pageIndex) throws PrinterException {
        int lineHeight = rtScrollPane.getHeight();
        int linesPerPage = (int) (pageFormat.getImageableHeight() / lineHeight);
        int lineCount = rsTextArea.getLineCount();
        int lastPage = lineCount / linesPerPage;

        if (pageIndex > lastPage) {
          return NO_SUCH_PAGE;

        } else {
          Graphics2D g2 = (Graphics2D) graphics;
//   TODO:TokenMarker tokenMarker = textArea.getDocument().getTokenMarker();
          int firstLine = pageIndex*linesPerPage;
          g2.translate(Math.max(54, pageFormat.getImageableX()),
                        pageFormat.getImageableY() - firstLine*lineHeight);
          /* TODO: What dis?
          printing = true;
          for (int line = firstLine; line < firstLine + linesPerPage; line++) {
            paintLine(g2, line, 0, tokenMarker);
          }
          printing = false; */
          return PAGE_EXISTS;
        }
      }
    };
  }
}
