package src.main.java.gui;

import org.jetbrains.annotations.NotNull;
import src.main.java.Game;
import src.main.java.exception.EndGameException;
import src.main.java.exception.GameException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;

public class GameWindow extends JFrame {

  /**
   * Returns how many text rows are shown in the Window.
   */
  private static final int ROWS = 30;
  private static final int COLUMNS = 100;
  private static final int FONT_SIZE = 15;
  private static final Font FONT = getMonospacedFont();
  private static final String WINDOW_TITLE = "Dungeon";

  /**
   * The border, in pixels.
   */
  private static final int MARGIN = 5;
  private transient SwappingStyledDocument document;
  private JTextField textField;
  private JTextPane textPane;
  private volatile boolean acceptingNextCommand;

  /**
   * Constructs a new GameWindow.
   */
  public GameWindow() {
    initComponents();
    document = new SwappingStyledDocument(textPane);
    setVisible(true);
  }

  public static int getRows() {
    return ROWS;
  }

  public static int getColumns() {
    return COLUMNS;
  }

  /**
   * Returns the monospaced font used by the game interface.
   */
  private static Font getMonospacedFont() {
    Font font = new Font(Font.MONOSPACED, Font.PLAIN, FONT_SIZE);
    ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
    if (contextClassLoader == null) {
      System.out.println("getContextClassLoader() returned null. Not attempting to get custom font.");
    } else {
      try (InputStream fontStream = contextClassLoader.getResourceAsStream("DroidSansMono.ttf")) {
        font = Font.createFont(Font.TRUETYPE_FONT, fontStream).deriveFont(Font.PLAIN, FONT_SIZE);
      } catch (FontFormatException bad) {
        System.out.println("threw FontFormatException during font creation.");
      } catch (IOException bad) {
        System.out.println("threw IOException during font creation.");
      }
    }
    return font;
  }

  private static void logExecutionExceptionAndExit(Throwable fatal) {
    System.out.println(fatal);
    System.exit(1);
  }

  private Object readResolve() {
    document = new SwappingStyledDocument(textPane);
    return this;
  }

  private void initComponents() {
    JPanel panel = new JPanel(new GridBagLayout());
    panel.setBackground(SharedConstants.MARGIN_COLOR);

    textPane = new JTextPane();
    textPane.setEditable(false);
    textPane.setBackground(SharedConstants.INSIDE_COLOR);
    textPane.setFont(FONT);

    JScrollPane scrollPane = new JScrollPane();
    scrollPane.setViewportView(textPane);
    scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
    scrollPane.setBorder(BorderFactory.createEmptyBorder());
    scrollPane.getVerticalScrollBar().setBackground(SharedConstants.INSIDE_COLOR);
    scrollPane.getVerticalScrollBar().setUI(new DungeonScrollBarUi());

    textField = new JTextField();
    textField.setBackground(SharedConstants.INSIDE_COLOR);
    textField.setForeground(Color.LIGHT_GRAY);
    textField.setCaretColor(Color.WHITE);
    textField.setFont(FONT);
    textField.setFocusTraversalKeysEnabled(false);
    textField.setBorder(BorderFactory.createEmptyBorder());

    textField.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent actionEvent) {
        textFieldActionPerformed();
      }
    });

    textField.addKeyListener(new KeyAdapter() {
      @Override
      public void keyPressed(KeyEvent event) {
        textFieldKeyPressed(event);
      }
    });

    GridBagConstraints constants = new GridBagConstraints();
    constants.insets = new Insets(MARGIN, MARGIN, MARGIN, MARGIN);
    panel.add(scrollPane, constants);

    constants.gridy = 1;
    constants.fill = GridBagConstraints.HORIZONTAL;
    constants.insets = new Insets(0, MARGIN, MARGIN, MARGIN);
    panel.add(textField, constants);

    setGameWindowTitle(WINDOW_TITLE);

    setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
    addWindowListener(new ClosingListener());

    textField.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK), "SAVE");
    textField.getActionMap().put("SAVE", new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent event) {
        if (acceptingNextCommand) {
          clearTextPane();
          //Loader.saveGame(src.main.java.Game.getGameState());
          System.out.println("save game not implemented yet");
        }
      }
    });

    textField.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_U, InputEvent.CTRL_DOWN_MASK), "DELETE_BEFORE");
    textField.getActionMap().put("DELETE_BEFORE", new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent event) {
        if (acceptingNextCommand) {
          int caretPosition = textField.getCaretPosition();
          String text = textField.getText();
          if (text != null) {
            textField.setText(text.substring(caretPosition));
            textField.setCaretPosition(0);
          }
        }
      }
    });

    textField.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_K, InputEvent.CTRL_DOWN_MASK), "DELETE_AFTER");
    textField.getActionMap().put("DELETE_AFTER", new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent event) {
        if (acceptingNextCommand) {
          int caretPosition = textField.getCaretPosition();
          String text = textField.getText();
          if (text != null) {
            textField.setText(text.substring(0, caretPosition));
          }
        }
      }
    });

    textField.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_W, InputEvent.CTRL_DOWN_MASK), "DELETE_WORD_BEFORE");
    textField.getActionMap().put("DELETE_WORD_BEFORE", new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent event) {
        if (acceptingNextCommand) {
          String text = textField.getText();
          if (text == null) {
            return;
          }
          boolean gotToken = false;
          int caretPosition = textField.getCaretPosition();
          for (int i = caretPosition - 1; i >= 0; i--) {
            if (Character.isWhitespace(text.charAt(i))) {
              if (gotToken) {
                int endIndex = i + 1;
                int offset = caretPosition - endIndex;
                textField.setText(text.substring(0, endIndex) + text.substring(caretPosition));
                textField.setCaretPosition(caretPosition - offset);
                return;
              }
            } else {
              if (!gotToken) {
                gotToken = true;
              }
            }
          }
          textField.setText(text.substring(caretPosition));
          textField.setCaretPosition(0);
        }
      }
    });

    add(panel);

    setResizable(false);
    resize();
  }

  private void setGameWindowTitle(String title) {
    setTitle(title);
    // Set the ClassName so that Gnome presents the window title correctly.
    // See https://github.com/mafagafogigante/dungeon/issues/307 for more information.
    Toolkit toolkit = Toolkit.getDefaultToolkit();
    try {
      Field awtAppClassNameField = toolkit.getClass().getDeclaredField("awtAppClassName");
      awtAppClassNameField.setAccessible(true);
      awtAppClassNameField.set(toolkit, title);
    } catch (NoSuchFieldException ignored) {
      // Not a problem.
    } catch (IllegalAccessException logged) {
      System.out.println(logged);
    }
  }

  /**
   * Resizes and centers the frame.
   */
  private void resize() {
    textPane.setPreferredSize(calculateTextPaneSize());
    pack();
    setLocationRelativeTo(null);
  }

  /**
   * Evaluates the preferred size for the TextPane.
   *
   * @return a Dimension with the preferred TextPane dimensions.
   */
  private Dimension calculateTextPaneSize() {
    FontMetrics fontMetrics = getFontMetrics(FONT);
    int width = fontMetrics.charWidth(' ') * (COLUMNS + 1); // Add a padding column.
    int height = fontMetrics.getHeight() * ROWS;
    return new Dimension(width, height);
  }

  /**
   * The method that gets called when the player presses ENTER.
   */
  private void textFieldActionPerformed() {
    if (acceptingNextCommand) {
      final String text = getTrimmedTextFieldText();
      if (!text.isEmpty()) {
        clearTextField();
        acceptingNextCommand = false;
        SwingWorker<Void, Void> inputRenderer = new SwingWorker<>() {
          @Override
          protected Void doInBackground() {
//            if (IssuedCommand.isValidSource(text)) {
//              try {
//                src.main.java.Game.renderTurn(new IssuedCommand(text), stopWatch);
//              } catch (Throwable throwable) {
//                logExecutionExceptionAndExit(throwable);
//              }
//              DungeonLogger.logCommandRenderingReport(text, "finished doInBackGround", stopWatch);
//            } else {
//              DungeonLogger.warning("Input is not a valid command source.");
//            }
            clearTextPane();
            try {
              Game.getGame().command.execute(Game.getGame(), text.split(" "));
            } catch (GameException e) {
              clearTextPane();
              if (e instanceof EndGameException) {
                  System.exit(0);
              }
              Writer.write(e.toString());
            }
            Game.getGame().context.print();
            acceptingNextCommand = true;
            return null;
          }
        };
        inputRenderer.execute();
      }
    }
  }

  /**
   * Handles a key press in the text field. This method checks for a command history access by the keys UP, DOWN, or TAB
   * and, if this is the case, processes this query.
   *
   * @param event the KeyEvent.
   */
  private void textFieldKeyPressed(KeyEvent event) {
//    int keyCode = event.getKeyCode();
//    if (isUpDownOrTab(keyCode)) { // Check if the event is of interest.
//      GameState gameState = src.main.java.Game.getGameState();
//      if (gameState != null) {
//        CommandHistory commandHistory = gameState.getCommandHistory();
//        if (keyCode == KeyEvent.VK_UP) {
//          textField.setText(commandHistory.getCursor().moveUp().getSelectedCommand());
//        } else if (keyCode == KeyEvent.VK_DOWN) {
//          textField.setText(commandHistory.getCursor().moveDown().getSelectedCommand());
//        } else if (keyCode == KeyEvent.VK_TAB) {
//          // Using the empty String to get the last similar command will always retrieve the last command.
//          // Therefore, there is no need to check if there is something in the text field.
//          String lastSimilarCommand = commandHistory.getLastSimilarCommand(getTrimmedTextFieldText());
//          if (lastSimilarCommand != null) {
//            textField.setText(lastSimilarCommand);
//          }
//        }
//      }
//    }
  }

  private boolean isUpDownOrTab(int keyCode) {
    return keyCode == KeyEvent.VK_UP || keyCode == KeyEvent.VK_DOWN || keyCode == KeyEvent.VK_TAB;
  }

  /**
   * Convenience method that returns the text in the text field after trimming it.
   *
   * @return a trimmed String.
   */
  private String getTrimmedTextFieldText() {
    String textFieldContent = textField.getText();
    if (textFieldContent == null) {
      return "";
    } else {
      return textFieldContent.trim();
    }
  }

  /**
   * Schedules the writing of the contents of a src.main.java.gui.Writable with the provided specifications on the Event Dispatch Thread.
   * This method can be called on any thread.
   *
   * @param writable a src.main.java.gui.Writable object
   * @param specifications a WritingSpecifications object
   */
  public void scheduleWriteToTextPane(@NotNull final Writable writable,
      @NotNull final WritingSpecifications specifications) {
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        writeToTextPane(writable, specifications);
      }
    });
  }

  /**
   * Effectively updates the text pane. Should only be invoked on the Event Dispatch Thread.
   *
   * @param writable a src.main.java.gui.Writable object, not empty
   * @param specifications a WritingSpecifications object
   */
  private void writeToTextPane(Writable writable, WritingSpecifications specifications) {
    // This is the only way to write text to the screen. One should never modify the contents of the document currently
    // assigned to the JTextPane directly. It must be done through the SwappingStyledDocument object.
    document.write(writable, specifications);
  }

  /**
   * Clears the TextPane by erasing everything in the local Document.
   *
   * <p>This schedules the operation to be ran on the EDT, so it is safe to invoke this on any thread.
   */
  public void clearTextPane() {
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        document.clear();
      }
    });
  }

  /**
   * Schedules a focus request on the text field.
   */
  public void requestFocusOnTextField() {
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        textField.requestFocusInWindow();
      }
    });
  }

  private void clearTextField() {
    textField.setText(null);
  }

  /**
   * Signalizes to this window that it should start accepting commands.
   *
   * <p>This must be done after the first GameState is loaded. Other changes of GameState do not need to be protected
   * this way because the SwingWorker toggles the acceptingNextCommand variable to false and just changes it back to
   * true after it is finished (and the GameState is loaded).
   */
  public void startAcceptingCommands() {
    acceptingNextCommand = true;
  }

  private static class ClosingListener extends WindowAdapter {
    @Override
    public void windowClosing(WindowEvent event) {
      super.windowClosing(event);
      System.exit(0);
    }
  }

}
