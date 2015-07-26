package processing.mode.java.rsta.autocomplete;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import javax.swing.text.Position;

import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.fife.ui.autocomplete.Completion;
import org.fife.ui.autocomplete.CompletionProvider;
import org.fife.ui.autocomplete.FunctionCompletion;
import org.fife.ui.autocomplete.ParameterizedCompletion.Parameter;
import org.fife.ui.autocomplete.VariableCompletion;

import processing.mode.java.pdex.CompletionCandidate;

public class PDEOverloadedFunctionCompletionCandidate extends VariableCompletion {

  private String summary;
//  private List<Parameter> params;
  CompletionCandidate cc;

  public PDEOverloadedFunctionCompletionCandidate(CompletionProvider provider, CompletionCandidate cc) {
    super(provider, cc.getElementName(), cc.isLocal()?((MethodDeclaration)cc.getWrappedObject()).getReturnType2().toString():((Method)cc.getWrappedObject()).getReturnType().getSimpleName());
    Method m = (Method)cc.getWrappedObject();
    ArrayList<Parameter> p = new ArrayList<>();
    for (java.lang.reflect.Parameter actPar : m.getParameters()) {
      p.add(new Parameter(actPar.getClass().getSimpleName(), actPar.getName()));
    }
//    this.setParams(p);
    this.summary = null;
    this.cc = cc;
  }
  
  @Override
  public String getSummary() {
    // Shows docs
    if (this.summary == null) {
//      System.out.println("Summary: " + super.getSummary());
      return super.getSummary();
    }
    else {
//      System.out.println("Summary: " + this.getSummary());
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
//    System.out.println("Replacement Text: " + cc.getCompletionString());
    return cc.getCompletionString();
  }

  @Override
  public String getToolTipText() {
    return getSummary();
  }

  @Override
  public String getDefinitionString() {
    // shown in first part of getSummary()
//    System.out.println("Label: " + cc.getLabel());
    return cc.getLabel();
  }
  
  @Override
  public String getShortDescription() {
//    System.out.println("Short: " + cc.getLabel());
    return cc.getLabel();
  }
  
  @Override
  public String getName() {
    // Shown in popup autocomplete menu
//    System.out.println("Name: " + cc.getLabel());
    return cc.getLabel();
  }
}
