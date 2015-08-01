package processing.mode.java.rsta.autocomplete;

import java.lang.reflect.Field;

import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.fife.ui.autocomplete.Completion;
import org.fife.ui.autocomplete.CompletionProvider;
import org.fife.ui.autocomplete.VariableCompletion;

import processing.mode.java.pdex.ASTGenerator;
import processing.mode.java.pdex.CompletionCandidate;

// TODO: Check the overridden funcitons and ensure they're returning 
// what they're supposed to be 
public class PDEVariableCompletionCandidate extends VariableCompletion {

  private String summary;
  //private List<Parameter> params;
  CompletionCandidate cc;
  
  public PDEVariableCompletionCandidate(CompletionProvider provider,
                                        CompletionCandidate cc) {
    super(provider, 
          cc.getElementName(), 
          (cc.getWrappedObject() instanceof SingleVariableDeclaration)
          ?((SingleVariableDeclaration)cc.getWrappedObject()).getType().toString()
          :((cc.getWrappedObject() instanceof VariableDeclarationFragment)
            ?ASTGenerator.extracTypeInfo2(((VariableDeclarationFragment)cc.getWrappedObject())).toString()
            :((Field)cc.getWrappedObject()).getType().getSimpleName()));
    this.cc = cc;
    if (provider instanceof PDECodeCompletionProvider) {
      this.summary = 
          ((PDECodeCompletionProvider)provider).getDocsMap().getVariableReference(this.getName());
    }
    else {
      this.summary = null;
    }
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
  
//  @Override
//  public String getShortDescription() {
//    System.out.println("Short: " + cc.getLabel());
//    return cc.getLabel();
//  }
  
  @Override
  public String getName() {
    // Shown in popup autocomplete menu
//    System.out.println("Name: " + cc.getLabel());
    return cc.getElementName();
  }
}
