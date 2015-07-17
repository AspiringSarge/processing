package processing.mode.java.rsta;

import java.awt.Color;
import java.net.URL;

import javax.swing.text.Element;

import org.fife.ui.rsyntaxtextarea.RSyntaxDocument;
import org.fife.ui.rsyntaxtextarea.parser.DefaultParseResult;
import org.fife.ui.rsyntaxtextarea.parser.DefaultParserNotice;
import org.fife.ui.rsyntaxtextarea.parser.ExtendedHyperlinkListener;
import org.fife.ui.rsyntaxtextarea.parser.ParseResult;
import org.fife.ui.rsyntaxtextarea.parser.Parser;
import org.fife.ui.rsyntaxtextarea.parser.ParserNotice;
import org.fife.ui.rsyntaxtextarea.parser.TaskTagParser.TaskNotice;

import processing.app.Base;
import processing.app.EditorStatus;
import processing.app.Mode;
import processing.mode.java.JavaEditor;
import processing.mode.java.JavaMode;
import processing.mode.java.pdex.ErrorCheckerService;
import processing.mode.java.pdex.ErrorMarker;
import processing.mode.java.pdex.Problem;


public class ProcessingErrorChecker extends ErrorCheckerService implements Parser{

  private DefaultParseResult result;
  private Boolean onlyRepaint;

  private Color warningColor;
  private Color errorColor;

  public ProcessingErrorChecker(JavaEditor debugEditor) {
    super(debugEditor);
    onlyRepaint = false;
    result = new DefaultParseResult(this);
    
    Mode mode = debugEditor.getMode();
    errorColor = mode.getColor("errorbar.errorcolor");
    warningColor = mode.getColor("errorbar.warningcolor");
  }

  @Override
  public ExtendedHyperlinkListener getHyperlinkListener() {
    return null;
  }

  @Override
  public URL getImageBase() {
    return null;
  }

  /**
   * Overriden to always be true. Mwahahaha... 
   * On a more serious note, TODO: permit user to
   * disable syntax checking via this method
   */
  @Override
  public boolean isEnabled() {
    return true;
  }

  @Override
  public ParseResult parse(RSyntaxDocument doc, String style) {
    result.clearNotices();
    Element root = doc.getDefaultRootElement();
    int lineCount = root.getElementCount();
    result.setParsedLines(0, lineCount-1);
    
    if (onlyRepaint) {
      updateEditorStatus();
    }
    else {
      checkCode();    
      // TODO: Something likely needs to be done in there:
      checkForMissingImports();
    }
    for (Problem p: problemsList) {
      if (p.getTabIndex() == editor.getSketch().getCurrentCodeIndex()) {
        DefaultParserNotice pn = 
            new DefaultParserNotice(this,
                                    p.getMessage(),
                                    p.getLineNumber(),
                                    p.getPDEStartOffset() +
                                      p.getPDELineStartOffset(),
                                    p.getPDELineStopOffset() -
                                      p.getPDELineStartOffset() +
                                      1);
        if (p.isError()) {
          pn.setLevel(ParserNotice.Level.ERROR);
          pn.setColor(errorColor);
        }
        else if (p.isWarning()) {
          pn.setLevel(ParserNotice.Level.WARNING);
          pn.setColor(warningColor);
        }
        result.addNotice(pn);
      }
      /*
      System.out.println("Msg: " + p.getMessage() + " Line: " + p.getLineNumber()  + 
                         " Start:" + (p.getPDEStartOffset() +
                         p.getPDELineStartOffset()) + " Len:" + 
                         (p.getPDEStartOffset() + 
                            p.getPDELineStopOffset() -
                            p.getPDELineStartOffset()));
      System.out.println("Start Offset:" + p.getPDEStartOffset() +
                         "\nLine start offset:" + p.getPDELineStartOffset() +  
                         "\nStop Offset:" + p.getPDEStopOffset() +
                         "\nLine stop offset:" + p.getPDELineStopOffset());
                         */
    }
    return result;
  }
  
  /**
   * Convenience method to refresh things- the status notification, the
   * squiggles, the error tabs. 
   */
  public void redraw() {
    synchronized(onlyRepaint) {
      onlyRepaint = true;
      editor.getTextArea().forceReparsing(this);
      onlyRepaint = false;
    }
  }
  
  // TODO: Should this maybe use the RSyntaxDocument instead?
  protected boolean checkCode() {
    try {
      sourceCode = preprocessCode(editor.getSketch().getMainProgram());
      compilationUnitState = 0;
      syntaxCheck();
      // No syntax errors, proceed for compilation check, Stage 2.

      if (!hasSyntaxErrors()) {

      }
      if (problems.length == 0 && !editor.hasJavaTabs()) {
        sourceCode = xqpreproc.doYourThing(sourceCode, programImports);
        prepareCompilerClasspath();
        compileCheck();
      }

      astGenerator.buildAST(cu);
      if (!JavaMode.errorCheckEnabled) {
        problemsList.clear();
        Base.log("Error Check disabled, so not updating UI.");
      }
      calcPDEOffsetsForProbList();
      updateErrorTable();
      
      updateEditorStatus();
      
      editor.updateErrorToggle();
      return true;

    } catch (Exception e) {
      Base.log("Oops! [ErrorCheckerService.checkCode]: " + e);
      e.printStackTrace();
    }
    return false;
  }

  @Override
  public void updateEditorStatus() {
    if (editor.getStatusMode() == EditorStatus.EDIT) return;
    
    if (isEnabled()) {
      synchronized (problemsList) {
        for (Problem problem : problemsList) {
          if (problem.getLineNumber() == editor.getTextArea().getCaretLine() &&
              problem.getTabIndex() == editor.getSketch().getCurrentCodeIndex()) {
            /* TODO:
            if (emarker.getType() == ErrorMarker.Warning) {
              editor.statusMessage(emarker.getProblem().getMessage(),
                                   JavaEditor.STATUS_INFO);
            } else {
              editor.statusMessage(emarker.getProblem().getMessage(),
                                   JavaEditor.STATUS_COMPILER_ERR);
            }
            */
            if (problem.isError()) {
              editor.statusMessage(problem.getMessage(),
                                   JavaEditor.STATUS_COMPILER_ERR);
            }
            else if (problem.isWarning()) {
              editor.statusMessage(problem.getMessage(),
                                   JavaEditor.STATUS_INFO);
            }
            return;
          }
        }
      }
    }

    // This line isn't an error line anymore, so probably just clear it
    if (editor.statusMessageType == JavaEditor.STATUS_COMPILER_ERR) {
      editor.statusEmpty();
      return;
    }
  }
}