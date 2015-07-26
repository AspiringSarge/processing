/*
 * 03/21/2010
 *
 * Copyright (C) 2010 Robert Futrell
 * robert_futrell at users.sourceforge.net
 * http://fifesoft.com/rsyntaxtextarea
 *
 * This library is distributed under a modified BSD license.  See the included
 * RSTALanguageSupport.License.txt file for details.
 */
package processing.mode.java.rsta.autocomplete;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.swing.text.JTextComponent;

import org.fife.ui.autocomplete.BasicCompletion;
import org.fife.ui.autocomplete.CompletionProvider;
import org.fife.ui.autocomplete.DefaultCompletionProvider;
import org.fife.ui.autocomplete.LanguageAwareCompletionProvider;
import org.fife.ui.autocomplete.ParameterizedCompletion;
import org.fife.ui.autocomplete.ShorthandCompletion;

import processing.app.Base;
import processing.mode.java.JavaEditor;

/**
 * A completion provider tweaked for Processing. It provides code completion 
 * support and parameter assistance.
 *
 * @author Robert Futrell
 * @author Joel Moniz
 * @version 1.0
 */
public class PDECompletionProvider extends LanguageAwareCompletionProvider {

  private JavaEditor editor;
  /**
   * Constructor.
   */
  public PDECompletionProvider(JavaEditor editor) {
    this.editor = editor;
    
    setDefaultCompletionProvider(createCodeCompletionProvider());
    setStringCompletionProvider(createStringCompletionProvider());
    setCommentCompletionProvider(createCommentCompletionProvider());
  }

  /**
   * Adds shorthand completions to the code completion provider.
   *
   * @param codePP
   *          The code completion provider.
   */
  protected void addShorthandCompletions(DefaultCompletionProvider codePP) {
    codePP.addCompletion(new ShorthandCompletion(codePP, "draw",
                                                 "void draw() {\n  "));
    codePP.addCompletion(new ShorthandCompletion(codePP, "setup",
                                                 "void setup() {\n  "));
  }

  /**
   * Returns the provider to use when editing code.
   *
   * @return The provider.
   * @see #createCommentCompletionProvider()
   * @see #createStringCompletionProvider()
   * @see #loadCodeCompletionsFromXml(DefaultCompletionProvider)
   * @see #addShorthandCompletions(DefaultCompletionProvider)
   */
  protected CompletionProvider createCodeCompletionProvider() {
    PDECodeCompletionProvider cp = new PDECodeCompletionProvider(editor);
//    addShorthandCompletions(cp);
    return cp;
  }
  
  public List<ParameterizedCompletion> getParameterizedCompletions(JTextComponent tc) {
    return super.getParameterizedCompletions(tc);
  }

  /**
   * Returns the provider to use when in a comment.
   *
   * @return The provider.
   * @see #createCodeCompletionProvider()
   * @see #createStringCompletionProvider()
   */
  protected CompletionProvider createCommentCompletionProvider() {
    DefaultCompletionProvider cp = new DefaultCompletionProvider();
    cp.addCompletion(new BasicCompletion(cp, "TODO:", "A to-do reminder"));
    cp.addCompletion(new BasicCompletion(cp, "FIXME:",
                                         "A bug that needs to be fixed"));
    cp.addCompletion(new BasicCompletion(cp, "HACK:",
                                         "Something that's hacky, but works"));
    return cp;
  }

  /**
   * Returns the completion provider to use when the caret is in a string.
   *
   * @return The provider.
   * @see #createCodeCompletionProvider()
   * @see #createCommentCompletionProvider()
   */
  protected CompletionProvider createStringCompletionProvider() {
    DefaultCompletionProvider cp = new DefaultCompletionProvider();
    cp.addCompletion(new BasicCompletion(cp, "\\n", "Newline",
                                         "Prints a newline"));
    return cp;
  }

  protected CompletionProvider oldCreateCodeCompletionProvider() {
    PDECodeCompletionProvider cp = new PDECodeCompletionProvider(editor);
//    loadCodeCompletionsFromXml(cp);
//    addShorthandCompletions(cp);
    return cp;
  }

  /**
   * Returns the name of the XML resource to load (on classpath or a file).
   *
   * @return The resource to load.
   */
  protected String getXmlResource() {
    return "c.xml";
  }

  /**
   * Called from {@link #createCodeCompletionProvider()} to actually load the
   * completions from XML. Subclasses that override that method will want to
   * call this one.
   *
   * @param cp
   *          The code completion provider.
   */
  protected void loadCodeCompletionsFromXml(DefaultCompletionProvider cp) {
    // First try loading resource (running from demo jar), then try
    // accessing file (debugging in Eclipse).
    ClassLoader cl = getClass().getClassLoader();
    String res = getXmlResource();
    if (res != null) { // Subclasses may specify a null value
      InputStream in = cl.getResourceAsStream(res);
      try {
        if (in != null) {
          cp.loadFromXML(in);
          in.close();
        } else {
          cp.loadFromXML(Base.getContentFile("modes/java/bin/processing/mode/java/rsta/autocomplete/processing.xml").getAbsolutePath());
        }
      } catch (IOException ioe) {
        ioe.printStackTrace();
      }
    }
  }

}