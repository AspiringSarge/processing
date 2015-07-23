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

import org.fife.rsta.ac.AbstractLanguageSupport;
import org.fife.ui.autocomplete.AutoCompletion;
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
		AutoCompletion ac = createAutoCompletion(provider);
		ac.install(textArea);
		installImpl(textArea, ac);

		textArea.setToolTipSupplier(provider);

	}


	/**
	 * {@inheritDoc}
	 */
	public void uninstall(RSyntaxTextArea textArea) {
		uninstallImpl(textArea);
		textArea.setToolTipSupplier(null);
	}


}