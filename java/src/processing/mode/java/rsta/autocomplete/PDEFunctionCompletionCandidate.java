package processing.mode.java.rsta.autocomplete;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import javax.swing.text.Position;

import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.fife.ui.autocomplete.Completion;
import org.fife.ui.autocomplete.CompletionProvider;
import org.fife.ui.autocomplete.FunctionCompletion;
import org.fife.ui.autocomplete.ParameterizedCompletion.Parameter;

import processing.mode.java.pdex.CompletionCandidate;

public class PDEFunctionCompletionCandidate extends FunctionCompletion {

  private String summary;
  private List<Parameter> params;
  CompletionCandidate cc;

//TODO: Check the overridden funcitons and ensure they're returning 
//what they're supposed to be
  public PDEFunctionCompletionCandidate(CompletionProvider provider, CompletionCandidate cc) {
    super(provider, cc.getElementName(), cc.isLocal()?((MethodDeclaration)cc.getWrappedObject()).getReturnType2().toString():((Method)cc.getWrappedObject()).getReturnType().getSimpleName());
    ArrayList<Parameter> p = new ArrayList<>();
    if (cc.isLocal()) {
      MethodDeclaration m = (MethodDeclaration)cc.getWrappedObject();
      List<SingleVariableDeclaration> l = m.parameters();
      for (SingleVariableDeclaration par : l) {
  //      System.out.println(actPar.getClass().getSimpleName() + actPar.getName() + actPar.getParameterizedType().getClass().getSimpleName() + actPar.getAnnotatedType());
        p.add(new Parameter(par.getType().toString(), par.getName().toString()));
      }
    }
    else {
      Method m = (Method)cc.getWrappedObject();
      for (java.lang.reflect.Parameter actPar : m.getParameters()) {
  //      System.out.println(actPar.getClass().getSimpleName() + actPar.getName() + actPar.getParameterizedType().getClass().getSimpleName() + actPar.getAnnotatedType());
        p.add(new Parameter(actPar.getType().getSimpleName(), actPar.getName()));
      }
    }
    this.setParams(p);
    this.summary = null;
    this.cc = cc;
//    System.out.println(cc.getElementName());
  }
  
  @Override
  public String getSummary() {
    // Shows docs
    if (this.summary == null) {
      return super.getSummary();
    }
    else {
      return this.summary;
    }
  }
  
  public void setSummary(String summary) {
    this.summary = summary;
  }
  

  @Override
  public int compareTo(Completion other) {
    if(getRelevance() != other.getRelevance()){
      return other.getRelevance() - getRelevance();
    }
    return (getReplacementText().compareTo(other.getReplacementText()));
  }

  @Override
  /**
   * This now return the type. 
   */
  public int getRelevance() {
    return cc.getType();
  }

  @Override
  public String getReplacementText() {
    // Text to replace
    return cc.getCompletionString().substring(0, cc.getCompletionString().indexOf('('));
  }

  @Override
  public String getToolTipText() {
    return getSummary();
  }
  
  /**
   * Returns the "definition string" for this function completion.  For
   * example, for the C "<code>printf</code>" function, this would return
   * "<code>int printf(const char *, ...)</code>".
   * 
   * @return The definition string.
   */
  @Override
  public String getDefinitionString() {
    // Used in first part of getSummary()
/*
    StringBuilder sb = new StringBuilder();

    // Add the return type if applicable (C macros like NULL have no type).
    String type = getType();
    if (type!=null) {
      sb.append(type).append(' ');
    }

    // Add the item being described's name.
    sb.append(getName());

    // Add parameters for functions.
    CompletionProvider provider = getProvider();
    char start = provider.getParameterListStart();
    if (start!=0) {
      sb.append(start);
    }
    for (int i=0; i<getParamCount(); i++) {
      Parameter param = getParam(i);
      type = param.getType();
      String name = param.getName();
      if (type!=null) {
        sb.append(type);
        if (name!=null) {
          sb.append(' ');
        }
      }
      if (name!=null) {
        sb.append(name);
      }
      if (i<params.size()-1) {
        sb.append(provider.getParameterListSeparator());
      }
    }
    char end = provider.getParameterListEnd();
    if (end!=0) {
      sb.append(end);
    }

    return sb.toString();
*/
    return cc.getLabel();
  }

  /*
  @Override
  public ParameterizedCompletionInsertionInfo getInsertionInfo(
      JTextComponent tc, boolean replaceTabsWithSpaces) {


  }
   */
  
  @Override
  public String getShortDescription() {
    return cc.getLabel();
  }
}
