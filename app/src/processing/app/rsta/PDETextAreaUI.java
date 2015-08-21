package processing.app.rsta;

import java.awt.Toolkit;
import java.awt.event.KeyEvent;

import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.KeyStroke;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextAreaUI;
import org.fife.ui.rtextarea.RTextAreaEditorKit;

/**
 * The <b>sole</b> purpose of extending this is to get undo to work by removing
 * Ctrl+Z from the input map... <br/> 
 * (Ah... The things I do for love...)
 */
public class PDETextAreaUI extends RSyntaxTextAreaUI {

  public PDETextAreaUI(JComponent textArea) {
    super(textArea);
  }
  
  @Override
  protected InputMap getRTextAreaInputMap() {
    InputMap in = super.getRTextAreaInputMap();
    int defaultModifier = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
    in.put(KeyStroke.getKeyStroke(KeyEvent.VK_Z, defaultModifier), "none");
    in.put(KeyStroke.getKeyStroke(KeyEvent.VK_Y, defaultModifier), "none");
    return in;
  }
  
}
