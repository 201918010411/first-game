package src.main.java.gui;

import java.util.List;

/**
 * Defines a single toColoredStringList method that Writer uses to write text to the screen.
 */
public abstract class Writable {

  public abstract List<ColoredString> toColoredStringList();

  /**
   * Converts all writable text of this src.main.java.gui.Writable object to a plain Java String.
   */
  protected String toJavaString() {
    StringBuilder builder = new StringBuilder();
    for (ColoredString coloredString : toColoredStringList()) {
      builder.append(coloredString.getString());
    }
    return builder.toString();
  }

}
