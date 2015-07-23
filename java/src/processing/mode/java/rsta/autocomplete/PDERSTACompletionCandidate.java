package processing.mode.java.rsta.autocomplete;

import javax.swing.Icon;

import org.fife.ui.autocomplete.AbstractCompletion;
import org.fife.ui.autocomplete.Completion;
import org.fife.ui.autocomplete.CompletionProvider;

import processing.mode.java.pdex.CompletionCandidate;

public class PDERSTACompletionCandidate extends AbstractCompletion {

  protected CompletionCandidate cc;

  protected PDERSTACompletionCandidate(CompletionProvider provider, CompletionCandidate cc) {
    super(provider);
    this.cc = cc;
  }

  protected PDERSTACompletionCandidate(CompletionProvider provider, Icon icon, CompletionCandidate cc) {
    super(provider, icon);
    this.cc = cc;
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

}
