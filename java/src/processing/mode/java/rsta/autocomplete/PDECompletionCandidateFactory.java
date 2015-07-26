package processing.mode.java.rsta.autocomplete;

import org.fife.ui.autocomplete.AbstractCompletion;
import org.fife.ui.autocomplete.CompletionProvider;

import processing.mode.java.pdex.CompletionCandidate;

public class PDECompletionCandidateFactory {
  public static AbstractCompletion getCandidate(CompletionProvider provider, CompletionCandidate cc) {
    if (cc.isField() || cc.isVar()) {
      return new PDEVariableCompletionCandidate(provider, cc);
    }
    else if (cc.isMethod()) {// && cc.isPredefined()) {
      if (cc.getLabel().contains("...")) {
        return new PDEOverloadedFunctionCompletionCandidate(provider, cc);
      }
      else {
        return new PDEFunctionCompletionCandidate(provider, cc);
      }
    }
//    else if (cc.isMethod() && cc.isLocal()) {
//      return new PDEFunctionCompletionCandidate(provider, cc);
//    }
    else {
      return new PDEBasicCompletionCandidate(provider, cc);
    }
  }
}
