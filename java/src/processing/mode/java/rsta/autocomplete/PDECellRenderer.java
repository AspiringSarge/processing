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

import javax.swing.Icon;
import javax.swing.JList;

import org.fife.ui.autocomplete.Completion;
import org.fife.ui.autocomplete.CompletionCellRenderer;
import org.fife.ui.autocomplete.FunctionCompletion;
import org.fife.ui.autocomplete.VariableCompletion;


/**
 * The cell renderer used for Processing.
 *
 * @author Robert Futrell
 * @author Joel Moniz
 * @version 1.0
 */
// TODO: Look at JavaCellRenderer for optimizations later on
class PDECellRenderer extends CompletionCellRenderer {

	private Icon variableIcon;
	private Icon functionIcon;


	/**
	 * Constructor.
	 */
	public PDECellRenderer() {
		variableIcon = getIcon("var.png");
		functionIcon = getIcon("function.png");
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void prepareForOtherCompletion(JList list,
			Completion c, int index, boolean selected, boolean hasFocus) {
//    System.out.println("Other " + c.getReplacementText());
	  if (c instanceof PDEBasicCompletionCandidate) {
	    String descrip = ((PDEBasicCompletionCandidate)c).getShortDescription();
	    if (descrip.toLowerCase().contains("<html>")) {
	      setText(descrip);
	    }
	    else {
	      super.prepareForOtherCompletion(list, c, index, selected, hasFocus);
	    }
//	    System.out.println("Other here  " + ((PDEBasicCompletionCandidate)c).getShortDescription());
	  }
	  else {
	    super.prepareForOtherCompletion(list, c, index, selected, hasFocus);
	  }
		setIcon(getEmptyIcon());
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void prepareForVariableCompletion(JList list,
			VariableCompletion vc, int index, boolean selected,
			boolean hasFocus) {
//    System.out.println("Var " + vc.getReplacementText());
		if (vc instanceof PDEOverloadedFunctionCompletionCandidate || 
		    vc instanceof PDEVariableCompletionCandidate) {
		  if (vc instanceof PDEOverloadedFunctionCompletionCandidate) {
		    setIcon(functionIcon);
		  }
		  else {
		    setIcon(variableIcon);
		  }
		  String descrip = vc.getShortDescription();
      if (descrip != null && descrip.toLowerCase().contains("<html>")) {
//        System.out.println("Yep: " + vc.getReplacementText());
        setText(descrip);
      }
      else {
//        System.out.println("Nope: " + vc.getReplacementText());
        super.prepareForVariableCompletion(list, vc, index, selected,
                                           hasFocus);
      }
		}
		else {
	    super.prepareForVariableCompletion(list, vc, index, selected,
	                                       hasFocus);
		  setIcon(variableIcon);
		}
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void prepareForFunctionCompletion(JList list,
			FunctionCompletion fc, int index, boolean selected,
			boolean hasFocus) {
    // TODO: Instead of using this which has no labeling for names of variables, 
	  // comment this out and use things like default after giving variables their proper names in the ctor
	  if (fc instanceof PDEFunctionCompletionCandidate) {
	    String descrip = fc.getShortDescription();
	    if (descrip.toLowerCase().contains("<html>")) {
	      setText(descrip);
	    }
	    else {
	      super.prepareForFunctionCompletion(list, fc, index, selected,
	                                         hasFocus);
	    }
//	    System.out.println("FC inst Func " + fc.getReplacementText());
	  }
	  else {
  		super.prepareForFunctionCompletion(list, fc, index, selected,
  										hasFocus);
//  		System.out.println("Non-FC inst Func " + fc.getReplacementText());
	  }
		setIcon(functionIcon);
	}


}