package processing.mode.java.rsta.autocomplete.docs;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashMap;

import org.fife.rsta.ac.java.classreader.constantpool.ConstantMethodHandleInfo;

import processing.app.Editor;

public class DocumentationHashMap {
  
  private static final String SYSTEM_VARS_LIST[] = {"focused", "frameCount",
    "frameRate", "height", "key", "keyCode", "keyPressed", "mouseButton",
    "mousePressed", "mouseX", "mouseY", "pixelHeight", "pixelWidth", "pixels",
    "pmouseX", "pmouseY", "width"};
  
  private HashMap<String, String> constantMap;
  private HashMap<String, String> methodMap;
  private HashMap<String, String> classMap;
  private HashMap<String, String> systemVarMap;
  private File referenceFolder;
  
  private static DocumentationHashMap docHashMap = null;
  
  public static DocumentationHashMap getDocumentationHashMap(Editor editor) {
    if (docHashMap == null) {
      docHashMap = new DocumentationHashMap(editor);
    }
    return docHashMap;
  }
  
  private DocumentationHashMap(Editor editor) {
    constantMap = new HashMap<>();
    classMap = new HashMap<>();
    methodMap = new HashMap<>();
    systemVarMap = new HashMap<>();
    
    referenceFolder = editor.getMode().getReferenceFolder();
    for (File f: referenceFolder.listFiles(new FileFilter() {
      // TODO: Doing this without docs or internet-> should a be used 
      // FilenameFilter instead? 
      @Override
      public boolean accept(File arg0) {
        // strip out .html part
        if (arg0.isDirectory()) {
//          System.out.println(arg0.getName());
          return false;
        }
        else if (arg0.getName().length()>5) {
          if (isUsefulReference(arg0.getName().substring(0, arg0.getName().length()-5)))
            return true;
          else {
//            System.out.println(arg0.getName());
            return false;
          }
        }
        else {
//          System.out.println(arg0.getName());
          return false;
        }
      }
    })) {
      addToHashMap(f);
    }
  }
  
  protected void addToHashMap(File f) {
    if (!f.getName().toLowerCase().endsWith(".html")) {
//      System.out.println(f.getName());
      return;
    }
    String name = f.getName().substring(0, f.getName().length()-5);
//    System.out.println("Name: " + name);
//    System.out.println("Parsed: " + parseDocs(f));
    if (name.toUpperCase().equals(name)) {
      constantMap.put(name, parseDocs(f));
    }
    else if (Character.isUpperCase(name.charAt(0))) {
      classMap.put(name, parseDocs(f));
    }
    else if (isSystemVariable(name)) {
      systemVarMap.put(name, parseDocs(f));
    }
    else if (name.charAt(name.length()-1) == '_') {
//      System.out.println(name.substring(0, name.length()-1));
      methodMap.put(name.substring(0, name.length()-1), parseDocs(f));
    }
    else {
      // Not happening
      ;
    }
  }
  
  protected String parseDocs(File f) {
    StringBuilder p5DocsToRSTADocs = new StringBuilder();
    String doc = readFile(f);
    if (doc == null) {
      return null;
    }
    p5DocsToRSTADocs.append("<html>");
    if (doc.indexOf("<table") == -1 || doc.lastIndexOf("</table>") == -1) {
//      System.out.println(doc);
//      System.out.println("Done.");
      return null;
    }
    
    String table = doc.substring(doc.indexOf("<table"), 
                                 doc.lastIndexOf("</table>")+8);
    String imageTable = table.replaceAll("<img src=.*?>", "");
    p5DocsToRSTADocs.append(imageTable);
    p5DocsToRSTADocs.append("</html>");
    return p5DocsToRSTADocs.toString();
  }
  
  public String readFile(File f) {
    try {
      byte[] bytes = Files.readAllBytes(f.toPath());
      return new String(bytes, "UTF-8");
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

  public String getDocumentation(String element) {
    String doc = null;
    /*if (isMethod) {
      doc = methodMap.get(element);
    }
    else*/ 
    if (element.toUpperCase().equals(element)) {
      doc = constantMap.get(element);
    }
    else if (Character.isUpperCase(element.charAt(0))) {
      doc = classMap.get(element);
    }
    else if (isSystemVariable(element)) {
      doc = systemVarMap.get(element);
    }
    else {
      doc = methodMap.get(element);
    }
    return doc;
  }

  private boolean isUsefulReference(String filename) {
    return (filename.toUpperCase().equals(filename) || 
        Character.isUpperCase(filename.charAt(0)) ||
        this.isSystemVariable(filename) ||
        filename.charAt(filename.length()-1 ) == '_');
  }
  
  public HashMap<String, String> getConstantMap() {
    return constantMap;
  }

  public HashMap<String, String> getMethodMap() {
    return methodMap;
  }

  public HashMap<String, String> getClassMap() {
    return classMap;
  }

  public HashMap<String, String> getSystemVarMap() {
    return systemVarMap;
  }
  
  public String getConstantReference(String name) {
    return constantMap.get(name);
  }
  
  public String getClassReference(String name) {
    return classMap.get(name);
  }
  
  public String getSystemVariableReference(String name) {
    return systemVarMap.get(name);
  }
  
  public String getMethodReference(String name) {
    return methodMap.get(name);
  }
  
  public String getVariableReference(String name) {
    if (isSystemVariable(name)) {
      return getSystemVariableReference(name);
    }
    else if (name.toUpperCase().equals(name)) {
      return getConstantReference(name);
    }
    else {
      return null;
    }
  }
  
  public boolean isSystemVariable(String v) {
    return Arrays.asList(SYSTEM_VARS_LIST).contains(v);
  }
}
