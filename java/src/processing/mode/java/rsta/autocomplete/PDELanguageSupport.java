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

import javax.swing.ListCellRenderer;
import javax.swing.text.Caret;
import javax.swing.text.JTextComponent;

import org.fife.rsta.ac.AbstractLanguageSupport;
import org.fife.ui.autocomplete.AutoCompletion;
import org.fife.ui.autocomplete.Completion;
import org.fife.ui.autocomplete.CompletionProvider;
import org.fife.ui.autocomplete.ParameterizedCompletion;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;

import processing.mode.java.JavaEditor;


/**
 * Language support for Processing.
 *
 * @author Robert Futrell
 * @author Joel Moniz
 * @version 1.0
 */
public class PDELanguageSupport extends AbstractLanguageSupport {

	/**
	 * The completion provider, shared amongst all text areas editing Processing.
	 */
	private PDECompletionProvider provider;

	private JavaEditor editor;

	/**
	 * Constructor.
	 */
	public PDELanguageSupport(JavaEditor editor) {
	  this.editor = editor;
		setParameterAssistanceEnabled(true);
		setShowDescWindow(true);
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	protected ListCellRenderer createDefaultCompletionCellRenderer() {
		return new PDECellRenderer();
	}


	private PDECompletionProvider getProvider() {
		if (provider==null) {
			provider = new PDECompletionProvider(editor);
		}
		return provider;
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public void install(RSyntaxTextArea textArea) {

		PDECompletionProvider provider = getProvider();
		PDEAutoCompletion ac = createAutoCompletion(provider);
		ac.install(textArea);
		installImpl(textArea, ac);

		textArea.setToolTipSupplier(provider);

	}
	
  @Override
  protected PDEAutoCompletion createAutoCompletion(CompletionProvider p) {
    PDEAutoCompletion ac = new PDEAutoCompletion(p);
    ac.setListCellRenderer(getDefaultCompletionCellRenderer());
    ac.setAutoCompleteEnabled(isAutoCompleteEnabled());
    ac.setAutoActivationEnabled(isAutoActivationEnabled());
    ac.setAutoActivationDelay(getAutoActivationDelay());
    ac.setParameterAssistanceEnabled(isParameterAssistanceEnabled());
    ac.setShowDescWindow(getShowDescWindow());
    return ac;
  }


	/**
	 * {@inheritDoc}
	 */
	public void uninstall(RSyntaxTextArea textArea) {
		uninstallImpl(textArea);
		textArea.setToolTipSupplier(null);
	}

	/**
	 * Derived from AutoComplete, solely for the purpose of making the process of
	 * using Autocomplete with overridden methods smoother
	 * @author Joel Moniz
	 *
	 */
	public class PDEAutoCompletion extends AutoCompletion {

    public PDEAutoCompletion(CompletionProvider provider) {
      super(provider);
    }
    
    @Override
    protected void insertCompletion(Completion c,
                                    boolean typedParamListStartChar) {
      System.out.println(c.getReplacementText() + "  " + c.getClass().getSimpleName());
      if (c.getReplacementText().trim().endsWith("(")) {
        System.out.println("HERE!");
        JTextComponent textComp = getTextComponent();
        String alreadyEntered = c.getAlreadyEntered(textComp);
//        hidePopupWindow();
        refreshPopupWindow();
        Caret caret = textComp.getCaret();

        int dot = caret.getDot();
        int len = alreadyEntered.length();
        int start = dot - len;
        String replacement = getReplacementText(c, textComp.getDocument(),
            start, len);

        caret.setDot(start);
        caret.moveDot(dot);
        textComp.replaceSelection(replacement);
        refreshPopupWindow();
      }
      else {
        super.insertCompletion(c, typedParamListStartChar);
      }
    }
	  
	}

}