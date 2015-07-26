package processing.mode.java.rsta.autocomplete;

import org.fife.ui.autocomplete.AbstractCompletion;
import org.fife.ui.autocomplete.CompletionProvider;

import processing.mode.java.pdex.CompletionCandidate;

public class PDECompletionCandidateFactory {
  public static AbstractCompletion getCandidate(CompletionProvider provider, CompletionCandidate cc) {
//    provider.setParameterizedCompletionParams('(', ", ", ')');
    if (cc.isField()) {
      return new PDEBasicCompletionCandidate(provider, cc);
    }
    else if (cc.isMethod() && cc.isPredefined()) {
      // HACK: Done since we can't remove that bracket :(
      // TODO: Fix this if ParameterizedCompletionInsertionInfo is made public 
//      System.out.println("Here");
//      provider.setParameterizedCompletionParams(' ', ", ", ')');
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
