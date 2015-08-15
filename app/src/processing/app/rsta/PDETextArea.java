package processing.app.rsta;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.Transferable;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;

import javax.swing.JPopupMenu;
import javax.swing.border.EmptyBorder;
import javax.swing.event.HyperlinkEvent;
import javax.swing.text.BadLocationException;
import javax.swing.text.Segment;
import javax.swing.text.StyleContext;

import org.fife.ui.rsyntaxtextarea.AbstractTokenMakerFactory;
import org.fife.ui.rsyntaxtextarea.RSyntaxDocument;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.Style;
import org.fife.ui.rsyntaxtextarea.TokenMakerFactory;
import org.fife.ui.rsyntaxtextarea.TokenTypes;
import org.fife.ui.rtextarea.RTextAreaBase;

import processing.app.Preferences;
import processing.app.syntax.HtmlSelection;
import processing.app.syntax.PdeTextAreaDefaults;
import processing.app.syntax.SyntaxDocument;
import processing.app.syntax.SyntaxStyle;
import processing.app.syntax.TextAreaDefaults;
import processing.app.syntax.Token;
import processing.app.syntax.TokenMarker;

public class PDETextArea extends RSyntaxTextArea {
  private int horizontalOffset;
  protected PDEScrollBar scrollbar;
  protected TextAreaDefaults defaults;
  
  public PDETextArea(TextAreaDefaults defaults) {
    super(/*defaults.rows, defaults.cols*/);
    this.defaults = defaults;
    setupSyntaxHighlighting();
  }

  void setupSyntaxHighlighting() {
    AbstractTokenMakerFactory atmf = (AbstractTokenMakerFactory)TokenMakerFactory.getDefaultInstance();
    atmf.putMapping("text/processing", "processing.app.rsta.PDESyntaxHighlight");
    Thread t = new Thread(new Runnable() {
      
      @Override
      public void run() {
        try {
          Thread.sleep(2000);
          System.out.println("Starting");
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
        setSyntaxEditingStyle("text/processing");
      }
    });
    t.start();
  }


  private static Font baseFont;
  private static Font boldFont;
  private static Font italicFont;
  public void setupHighlightingStyle(Font baseFont) {

    // Colors used by tokens.
    Color comment     = new Color(0,128,0);
    Color docComment    = new Color(164,0,0);
    Color markupComment   = new Color(0, 96, 0);
    Color keyword     = Color.RED;
    Color dataType      = new Color(0,128,128);
    Color function      = new Color(173,128,0);
    Color preprocessor    = new Color(128,128,128);
    Color operator      = new Color(128, 64, 64);
    Color regex       = new Color(0,128,164);
    Color variable      = new Color(255,153,0);
    Color literalNumber   = new Color(100,0,200);
    Color literalString   = new Color(220,0,156);
    Color error     = new Color(148,148,0);

    // (Possible) special font styles for keywords and comments.
    if (baseFont==null) {
      PDETextArea.baseFont = RTextAreaBase.getDefaultFont();
    }
    Font commentFont = PDETextArea.baseFont;
    Font keywordFont = PDETextArea.baseFont;
    // WORKAROUND for Sun JRE bug 6282887 (Asian font bug in 1.4/1.5)
    // That bug seems to be hidden now, see 6289072 instead.
    StyleContext sc = StyleContext.getDefaultStyleContext();
    PDETextArea.boldFont = sc.getFont(baseFont.getFamily(), Font.BOLD,
        baseFont.getSize());
    PDETextArea.italicFont = sc.getFont(baseFont.getFamily(), Font.ITALIC,
        baseFont.getSize());
//    commentFont = PDETextArea.italicFont;
//    keywordFont = PDETextArea.boldFont;
    Style[] styles = new Style[TokenTypes.DEFAULT_NUM_TOKEN_TYPES];
    styles[TokenTypes.COMMENT_EOL] = syntaxStyleToStyle(defaults.styles[Token.COMMENT1]);
    styles[TokenTypes.COMMENT_MULTILINE] = syntaxStyleToStyle(defaults.styles[Token.COMMENT2]);
    styles[TokenTypes.COMMENT_DOCUMENTATION] = syntaxStyleToStyle(defaults.styles[Token.COMMENT2]);
    styles[TokenTypes.RESERVED_WORD] = syntaxStyleToStyle(defaults.styles[Token.KEYWORD1]);
    styles[TokenTypes.RESERVED_WORD_2] = syntaxStyleToStyle(defaults.styles[Token.KEYWORD2]);
    styles[TokenTypes.RESERVED_WORD_3] = syntaxStyleToStyle(defaults.styles[Token.KEYWORD3]);
    styles[TokenTypes.RESERVED_WORD_4] = syntaxStyleToStyle(defaults.styles[Token.KEYWORD4]);
    styles[TokenTypes.DATA_TYPE] = syntaxStyleToStyle(defaults.styles[Token.KEYWORD5]);
    styles[TokenTypes.RESERVED_WORD_5] = syntaxStyleToStyle(defaults.styles[Token.KEYWORD6]);
    styles[TokenTypes.FUNCTION1] = syntaxStyleToStyle(defaults.styles[Token.FUNCTION1]);
    styles[TokenTypes.FUNCTION2] = syntaxStyleToStyle(defaults.styles[Token.FUNCTION2]);
    styles[TokenTypes.FUNCTION3] = syntaxStyleToStyle(defaults.styles[Token.FUNCTION3]);
    styles[TokenTypes.FUNCTION4] = syntaxStyleToStyle(defaults.styles[Token.FUNCTION4]);
    styles[TokenTypes.LITERAL_CHAR] = syntaxStyleToStyle(defaults.styles[Token.LITERAL1]);
    styles[TokenTypes.LITERAL_BACKQUOTE] = syntaxStyleToStyle(defaults.styles[Token.LITERAL1]);
    styles[TokenTypes.LITERAL_STRING_DOUBLE_QUOTE] = syntaxStyleToStyle(defaults.styles[Token.LITERAL1]);
    styles[TokenTypes.CONSTANTS] = syntaxStyleToStyle(defaults.styles[Token.LITERAL2]);
    styles[TokenTypes.OPERATOR] = syntaxStyleToStyle(defaults.styles[Token.OPERATOR]);
    styles[TokenTypes.LABEL] = syntaxStyleToStyle(defaults.styles[Token.LABEL]);

    // TODO: When handling boolean, ensure that it is removed from keywords1 list- it's now included
    // as both LITERAL_BOOLEAN and keyword1
    styles[TokenTypes.LITERAL_BOOLEAN] = syntaxStyleToStyle(defaults.styles[Token.KEYWORD1]);

    // TODO: Definitely add in more options for each of these later on in preferences file
    styles[TokenTypes.LITERAL_NUMBER_DECIMAL_INT] = syntaxStyleToStyle(defaults.styles[Token.KEYWORD2]);
    styles[TokenTypes.LITERAL_NUMBER_FLOAT] = syntaxStyleToStyle(defaults.styles[Token.KEYWORD2]);
    styles[TokenTypes.LITERAL_NUMBER_HEXADECIMAL] = syntaxStyleToStyle(defaults.styles[Token.KEYWORD2]);
    styles[TokenTypes.COMMENT_KEYWORD] = syntaxStyleToStyle(defaults.styles[Token.COMMENT1]);
    SyntaxStyle defaultStyle = new SyntaxStyle(defaults.fgcolor, false);
    styles[TokenTypes.SEPARATOR] = syntaxStyleToStyle(defaultStyle);

    // TODO: Possibly add in more options for each of these later on
    styles[TokenTypes.FUNCTION] = syntaxStyleToStyle(defaultStyle);
    styles[TokenTypes.VARIABLE] = syntaxStyleToStyle(defaultStyle);
    styles[TokenTypes.IDENTIFIER] = syntaxStyleToStyle(defaultStyle);
    styles[TokenTypes.WHITESPACE] = new Style();

    // TODO: Just leaving this as default for now- this probably has to be removed later
    styles[TokenTypes.COMMENT_MARKUP] = syntaxStyleToStyle(defaults.styles[Token.COMMENT1]);
    styles[TokenTypes.REGEX] = new Style(regex);
    styles[TokenTypes.ANNOTATION] = new Style(Color.gray);
    styles[TokenTypes.PREPROCESSOR] = new Style(preprocessor);
    styles[TokenTypes.MARKUP_TAG_DELIMITER] = new Style(Color.RED);
    styles[TokenTypes.MARKUP_TAG_NAME] = new Style(Color.BLUE);
    styles[TokenTypes.MARKUP_TAG_ATTRIBUTE] = new Style(new Color(63, 127, 127));
    styles[TokenTypes.MARKUP_TAG_ATTRIBUTE_VALUE] = new Style(literalString);
    styles[TokenTypes.MARKUP_COMMENT] = new Style(markupComment, null,
                                                  commentFont);
    styles[TokenTypes.MARKUP_DTD] = new Style(function);
    styles[TokenTypes.MARKUP_PROCESSING_INSTRUCTION] = new Style(preprocessor);
    styles[TokenTypes.MARKUP_CDATA] = new Style(new Color(0xcc6600));
    styles[TokenTypes.MARKUP_CDATA_DELIMITER] = new Style(new Color(0x008080));
    styles[TokenTypes.MARKUP_ENTITY_REFERENCE] = new Style(dataType);
    styles[TokenTypes.ERROR_IDENTIFIER] = new Style(error);
    styles[TokenTypes.ERROR_NUMBER_FORMAT] = new Style(error);
    styles[TokenTypes.ERROR_STRING_DOUBLE] = new Style(error);
    styles[TokenTypes.ERROR_CHAR] = new Style(error);

    // Issue #34: If an application modifies TokenTypes to add new built-in
    // token types, we'll get NPEs if not all styles are initialized.
    for (int i=0; i<styles.length; i++) {
      if (styles[i]==null) {
        styles[i] = new Style();
      }
    }
    getSyntaxScheme().setStyles(styles);

    setTabsEmulated(Preferences.getBoolean("editor.tabs.expand"));
    setTabSize(Preferences.getInteger("editor.tabs.size")+4);
    System.out.println(getTabSize());
  }


  public static Style syntaxStyleToStyle(SyntaxStyle s) {
    if (s.isBold()) {
      return new Style(s.getColor(), null, boldFont);
    }
    else {
      return new Style(s.getColor(), null, baseFont);
    }
  }
  

  public void setScrollbar(PDEScrollBar scrollbar) {
    this.scrollbar = scrollbar;
  }
  
  public PDEScrollBar getScrollbar() {
    return this.scrollbar;
  }

  public boolean isSelectionActive() {
    return (getSelectionEnd()!= getSelectionStart());
  }
  
  public void setSelectedText(String what) {
    replaceSelection(what);
  }
  
  public int getDocumentLength() {
    return getText().length();
  }
  
  /**
   * Returns the text on the specified line.
   * @param lineIndex The line
   * @return The text, or null if the line is invalid
   */
  public final String getLineText(int lineIndex) {
    int start = getLineStartOffset(lineIndex);
    int len = getLineStopOffset(lineIndex) - start - 1;
    String str = getText(start, len);
    return str;
  }

  /**
   * Copies the text on the specified line into a segment. If the line
   * is invalid, the segment will contain a null string.
   * @param lineIndex The line
   */
  public final void getLineText(int lineIndex, Segment segment) {
      int start = getLineStartOffset(lineIndex);
      int end = getLineStopOffset(lineIndex);
      int offset = end - start - 1;
      try {
        getDocument().getText(start,offset,segment);
      } catch (BadLocationException e) {
        e.printStackTrace();
        segment.offset = segment.count = 0;
      }
  }  

  /**
   * Set document with a twist, includes the old caret
   * and scroll positions, added for p5. [fry]
   */
  public void setDocument(RSyntaxDocument document,
                          int start, int stop, int scroll) {
    if (this.getDocument() == document)
      return;
    // TODO:
//    if (this.getDocument() != null)
//      this.getDocument().removeDocumentListener(documentHandler);
    
    this.setDocument(document);
    // TODO:
//    document.addDocumentListener(documentHandler);

    select(start, stop);
    updateScrollBars();
    setVerticalScrollPosition(scroll);
    
    repaint();
    // TODO: Check.
//    painter.repaint();
  }
  
  public void setVerticalScrollPosition(int scroll) {
    scrollbar.getVerticalScrollBar().setValue(scroll);
  }


  /**
   * Updates the state of the scroll bars. This should be called
   * if the number of lines in the document changes, or when the
   * size of the text area changes.
   */
  public void updateScrollBars() {
    /*
    TODO: Is this really required? Does RScrollBar handle automatically? 
    if (vertical != null && visibleLines != 0) {
      vertical.setValues(firstLine,visibleLines,0,getLineCount());
      vertical.setUnitIncrement(2);
      vertical.setBlockIncrement(visibleLines);
    }

    //if (horizontal != null && width != 0) {
    if ((horizontal != null) && (painter.getWidth() != 0)) {
      //int value = horizontal.getValue();
      //System.out.println("updateScrollBars");
      //int width = painter.getWidth();
      int lineCount = getLineCount();
      int maxLineLength = 0;
      for (int i = 0; i < lineCount; i++) {
        int lineLength = getLineLength(i);
        if (lineLength > maxLineLength) {
          maxLineLength = lineLength;
        }
      }
      int charWidth = painter.getFontMetrics().charWidth('w');
      int width = maxLineLength * charWidth;
      int painterWidth = painter.getWidth();

      // Update to how horizontal scrolling is handled
      // http://code.google.com/p/processing/issues/detail?id=280
      // http://code.google.com/p/processing/issues/detail?id=316
      //setValues(int newValue, int newExtent, int newMin, int newMax)
      if (horizontalOffset < 0) {
        horizontal.setValues(-horizontalOffset, painterWidth, -leftHandGutter, width);
      } else {
        horizontal.setValues(-leftHandGutter, painterWidth, -leftHandGutter, width);
      }

      //horizontal.setUnitIncrement(painter.getFontMetrics().charWidth('w'));
      horizontal.setUnitIncrement(charWidth);
      horizontal.setBlockIncrement(width / 2);
    }*/
  }
  

  /**
   * Returns the horizontal offset of drawn lines.
   */
  public final int getHorizontalOffset() {
    return horizontalOffset;
  }


  /**
   * Sets the horizontal offset of drawn lines. This can be used to
   * implement horizontal scrolling.
   * @param horizontalOffset offset The new horizontal offset
   */
  public void setHorizontalOffset(int horizontalOffset) {
    if (horizontalOffset == this.horizontalOffset) {
      return;
    }
    this.horizontalOffset = horizontalOffset;
    if (horizontalOffset != scrollbar.getHorizontalScrollBar().getValue()) {
      updateScrollBars();
    }
    
    repaint();
    // TODO: Check.
//    painter.repaint();
  }

  public int getLineStartNonWhiteSpaceOffset(int line) {
    int offset, length;
    String str;
    offset = getLineStartOffset(line);
    length = getLineLength(line);
    str = getText(offset, length);

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

    int selStart = getSelectionStart();
    int selStop = getSelectionEnd();

    int startLine = getSelectionStartLine();
    int stopLine = getSelectionStopLine();

    // If no selection, convert all the lines
    if (selStart == selStop) {
      startLine = 0;
      stopLine = getLineCount() - 1;
    } else {
      // Make sure the selection doesn't end at the beginning of the last line
      int lineStart = getLineStartOffset(stopLine);
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
    int start = getSelectionStart();
    int line = getLineOfOffset(start);
    return line;
  }

  public int getSelectionStopLine() {
    int line = -1;
    int end = getSelectionEnd();
    line = getLineOfOffset(end);
    return line;
  }

  /**
   * Returns the line containing the specified offset.
   * @param offset The offset
   */
  public final int getLineOfOffset(int offset) {
    return getDocument().getDefaultRootElement().getElementIndex(offset);
  }

  // TODO: This...
  private void emitAsHTML(StringBuilder cf, int line) {
    Segment segment = new Segment();
    getLineText(line, segment);

    char[] segmentArray = segment.array;
    int limit = segment.getEndIndex();
    int segmentOffset = segment.offset;
    int segmentCount = segment.count;
    // TODO: TokenMarker tokenMarker = getTokenMarker();
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
   * Returns the length of the specified line.
   * @param line The line
   */
  public int getLineLength(int line) {
    int len = -1;
    try {
      len = getLineEndOffset(line) - 
      getLineStartOffset(line) - 1;
    } catch (BadLocationException e) {
      e.printStackTrace();
    }
    return len;
  }

  public void setRightClickPopup(JPopupMenu menu) {
    // TODO: Need configurePopupMenu() ?
    setPopupMenu(menu);
  }
  
  public JPopupMenu getRightClickPopup() {
    return getPopupMenu();
  }
  
  @Override
  public String getText(int offset, int length) {
    try {
      return super.getText(offset, length);
    } catch (BadLocationException e) {
      e.printStackTrace();
      return null;
    }
  }
  
  public int getSelectionStop() {
    return getSelectionEnd();
  }

  /**
   * Returns the caret line.
   */
  public final int getCaretLine()
  {
    return getLineOfOffset(getCaretPosition());
  }

  public int getLineStartOffset(int line) {
    try {
      return super.getLineStartOffset(line);
    } catch (BadLocationException e) {
      e.printStackTrace();
      return -1;
    }
  }
  
  public int getLineStopOffset(int line) {
    try {
      return super.getLineEndOffset(line);
    } catch (BadLocationException e) {
      e.printStackTrace();
      return -1;
    }
  }
  
  public void updateAppearance() {
    setForeground(defaults.fgcolor);
    setBackground(defaults.bgcolor);

    String fontFamily = Preferences.get("editor.font.family");
    int fontSize = Preferences.getInteger("editor.font.size");
    Font plainFont = new Font(fontFamily, Font.PLAIN, fontSize);
    if (!fontFamily.equals(plainFont.getFamily())) {
      System.err
        .println(fontFamily + " not available, resetting to monospaced");
      fontFamily = "Monospaced";
      Preferences.set("editor.font.family", fontFamily);
      plainFont = new Font(fontFamily, Font.PLAIN, fontSize);
    }
    
    setFont(plainFont);
    Font boldFont = new Font(fontFamily, Font.BOLD, fontSize);
    boolean antialias = Preferences.getBoolean("editor.smooth");
    setAntiAliasingEnabled(antialias);

    FontMetrics fm = super.getFontMetrics(plainFont);

    // TODO: This is done automatically, right?
//    textArea.recalculateVisibleLines();
    
    setupSyntaxHighlighting();
    setupHighlightingStyle(plainFont);
  }
  
  // TODO: Look at commented out lines
  public Printable getPrintable() {
    return new Printable() {
      
      @Override
      public int print(Graphics graphics, PageFormat pageFormat, 
                       int pageIndex) throws PrinterException {
        int lineHeight = getFontMetrics(getFont()).getHeight();
        int linesPerPage = (int) (pageFormat.getImageableHeight() / lineHeight);
        int lineCount = getLineCount();
        int lastPage = lineCount / linesPerPage;

        if (pageIndex > lastPage) {
          return NO_SUCH_PAGE;

        } else {
          Graphics2D g2 = (Graphics2D) graphics;
//          TokenMarker tokenMarker = getDocument().getTokenMarker();
          int firstLine = pageIndex*linesPerPage;
          g2.translate(Math.max(54, pageFormat.getImageableX()),
                        pageFormat.getImageableY() - firstLine*lineHeight);
//          printing = true;
//          for (int line = firstLine; line < firstLine + linesPerPage; line++) {
//            paintLine(g2, line, 0, tokenMarker);
//          }
//          printing = false;
          return PAGE_EXISTS;
        }
      }
    };
  }
}
