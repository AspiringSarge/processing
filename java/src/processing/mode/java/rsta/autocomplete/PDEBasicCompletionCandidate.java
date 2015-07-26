package processing.mode.java.rsta.autocomplete;

import javax.swing.Icon;

import org.fife.ui.autocomplete.AbstractCompletion;
import org.fife.ui.autocomplete.BasicCompletion;
import org.fife.ui.autocomplete.Completion;
import org.fife.ui.autocomplete.CompletionProvider;

import processing.mode.java.pdex.CompletionCandidate;

//TODO: Check the overridden funcitons and ensure they're returning 
//what they're supposed to be
public class PDEBasicCompletionCandidate extends BasicCompletion {

  protected CompletionCandidate cc;

  protected PDEBasicCompletionCandidate(CompletionProvider provider, CompletionCandidate cc) {
    super(provider, cc.getCompletionString(), cc.getLabel());
    this.cc = cc;
  }

//  protected PDEBasicCompletionCandidate(CompletionProvider provider, Icon icon, CompletionCandidate cc) {
//    super(provider, icon);
//    this.cc = cc;
//  }

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
    return cc.getCompletionString();
  }

  @Override
  public String getSummary() {
    return cc.getLabel();
  }

  @Override
  public String getToolTipText() {
    return getSummary();
  }
  
  @Override
  public String getShortDescription() {
//    System.out.println("\"" + cc.getLabel() + "\"");
    return cc.getLabel();
  }

}
