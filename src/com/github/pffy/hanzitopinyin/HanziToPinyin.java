/*
 * This is free and unencumbered software released into the public domain.
 * 
 * Anyone is free to copy, modify, publish, use, compile, sell, or distribute
 * this software, either in source code form or as a compiled binary, for any
 * purpose, commercial or non-commercial, and by any means.
 * 
 * In jurisdictions that recognize copyright laws, the author or authors of this
 * software dedicate any and all copyright interest in the software to the
 * public domain. We make this dedication for the benefit of the public at large
 * and to the detriment of our heirs and successors. We intend this dedication
 * to be an overt act of relinquishment in perpetuity of all present and future
 * rights to this software under copyright law.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN
 * ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * 
 * For more information, please refer to <http://unlicense.org/>
 */

package com.github.pffy.hanzitopinyin;

import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.net.URI;

import javax.swing.JOptionPane;
import javax.swing.text.DefaultCaret;

import com.github.pffy.chinese.HanyuPinyin;
import com.github.pffy.chinese.Tone;

/**
 * name     : HanziToPinyin.java
 * version  : 1.8.5
 * updated  : 2015-11-03
 * license  : http://unlicense.org/ The Unlicense
 * git      : https://github.com/pffy/java-swing-hanzitopinyin
 *
 */

@SuppressWarnings("serial")
public class HanziToPinyin extends javax.swing.JFrame {

  // CONSTANTS

  private final String CRLF = System.lineSeparator();
  private final String DBL_CRLF = this.CRLF + this.CRLF;

  // meta
  private final String PRODUCT_NAME = "InPinyin";
  private final String PRODUCT_TITLE = this.PRODUCT_NAME
      + ": Hanzi-to-Pinyin Converter";
  private final String VERSION = " v1.8.5";

  private final String AUTHOR = "The Pffy Authors";
  private final String AUTHOR_URL = "https://github.com/pffy/";

  private final String LICENSE =
      "This is free, libre and open source software.";
  private final String LICENSE_URL = "http://unlicense.org/";

  private final String PROJECT_URL = AUTHOR_URL + "java-swing-hanzitopinyin";
  private final String ISSUES_URL = PROJECT_URL + "/issues";

  // menu
  private final String MENU_FILE = "File";
  private final String MENU_OPTIONS = "Options";
  private final String MENU_HELP = "Help";
   
  // file menu text
  private final String ITEM_FILE_NEW = "New";
  private final String ITEM_FILE_CONVERT = "Convert / Refresh";
  private final String ITEM_FILE_EXIT = "Exit";

  // options menu text
  private final String ITEM_TONE_NUMBERS = "Convert to Tone Numbers";
  private final String ITEM_TONE_MARKS = "Convert to Tone Marks";
  private final String ITEM_TONES_OFF = "Convert with Tones Off";

  // help menu text
  private final String ITEM_HELP_REPORT = "Report Issue...";
  private final String ITEM_HELP_HOMEPAGE = "Project Home Page";
  private final String ITEM_HELP_ABOUT = "About " + this.PRODUCT_NAME;

  // size
  private final Dimension DIMENSION_300 = new Dimension(300, 300);
  private final Dimension DIMENSION_350 = new Dimension(350, 350);
  private final Dimension DIMENSION_600 = new Dimension(600, 600);

  // shortcuts
  private final int SHORTCUTS_FONT_SIZE = 10;
  private final String SHORTCUTS_FONT_FACE = "Arial";
  private final String SHORTCUTS_TEXT =
	      "[F5] Refresh, [F7] Tone Numbers, [F8] Marks, [F9] Off, [Ctrl/⌘ + V] Paste Convert";
	 

  // OTHER FIELDS

  // HanyuPinyin object
  HanyuPinyin hp = new HanyuPinyin(null, Tone.TONE_MARKS);

  // flags
  private boolean pasteAndConvertEnabled = false;

  /**
   * Swing UI Elements below.
   */

  // menu
  private javax.swing.JMenuBar menubarMenu;

  // file menu
  private javax.swing.JMenu menuFile;
  private javax.swing.JMenuItem fileNew;
  private javax.swing.JMenuItem fileConvert;
  private javax.swing.JPopupMenu.Separator fileSeparator;
  private javax.swing.JMenuItem fileExit;

  // help menu
  private javax.swing.JMenu menuHelp;
  private javax.swing.JMenuItem helpReport;
  private javax.swing.JPopupMenu.Separator helpSeparator;
  private javax.swing.JMenuItem helpHomepage;
  private javax.swing.JMenuItem helpAbout;

  // options
  private javax.swing.JMenu menuOptions;
  private javax.swing.JCheckBoxMenuItem optionsToneNumbers;
  private javax.swing.JCheckBoxMenuItem optionsToneMarks;
  private javax.swing.JCheckBoxMenuItem optionsTonesOff;

  // shortcuts
  private javax.swing.JMenu menuShortcuts;

  // inputs and outputs
  private javax.swing.JScrollPane scrollPaneInput;
  private javax.swing.JScrollPane scrollPaneOutput;
  private javax.swing.JTextPane textPaneInput;
  private javax.swing.JTextPane textPaneOutput;


  /**
   * Builds this object.
   */
  public HanziToPinyin() {
    initComponents();
    updateMenusByOptions();
    updateToneOptions();
  };


  // setup components
  private void initComponents() {

    // inputs and outputs
    this.scrollPaneInput = new javax.swing.JScrollPane();
    this.textPaneInput = new javax.swing.JTextPane();
    this.scrollPaneOutput = new javax.swing.JScrollPane();
    this.textPaneOutput = new javax.swing.JTextPane();

    // input pane
    this.textPaneInput.addKeyListener(this.keyHandler);
    this.textPaneInput.setMinimumSize(this.DIMENSION_300);
    this.textPaneInput.setPreferredSize(this.DIMENSION_300);

    this.scrollPaneInput.setViewportView(this.textPaneInput);

    // output pane
    this.textPaneOutput.setEditable(false);

    this.textPaneOutput.setMinimumSize(this.DIMENSION_300);
    this.textPaneOutput.setPreferredSize(this.DIMENSION_300);
    this.scrollPaneOutput.setViewportView(this.textPaneOutput);

    // prevents scrolling in the output pane
    DefaultCaret outputCaret = (DefaultCaret) this.textPaneOutput.getCaret();
    outputCaret.setUpdatePolicy(DefaultCaret.NEVER_UPDATE);


    // frame properties
    setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
    setTitle(this.PRODUCT_TITLE);
    setMinimumSize(this.DIMENSION_350);
    setPreferredSize(this.DIMENSION_600);

    // BoxLayout for left-right panes
    getContentPane().setLayout(
        new javax.swing.BoxLayout(getContentPane(),
            javax.swing.BoxLayout.X_AXIS));


    getContentPane().add(this.scrollPaneInput);
    getContentPane().add(this.scrollPaneOutput);


    /**
     * Swing UI Menu components below
     */

    // menu
    this.menubarMenu = new javax.swing.JMenuBar();

    // file menu
    this.menuFile = new javax.swing.JMenu();
    this.menuFile.setMnemonic('f');
    this.menuFile.setText(this.MENU_FILE);

    this.fileNew = new javax.swing.JMenuItem();
    this.fileConvert = new javax.swing.JMenuItem();
    this.fileSeparator = new javax.swing.JPopupMenu.Separator();
    this.fileExit = new javax.swing.JMenuItem();

    // options menu
    this.menuOptions = new javax.swing.JMenu();
    this.menuOptions.setMnemonic('o');
    this.menuOptions.setText(this.MENU_OPTIONS);

    this.optionsToneNumbers = new javax.swing.JCheckBoxMenuItem();
    this.optionsToneMarks = new javax.swing.JCheckBoxMenuItem();
    this.optionsTonesOff = new javax.swing.JCheckBoxMenuItem();

    // help menu
    this.menuHelp = new javax.swing.JMenu();
    this.menuHelp.setMnemonic('H');
    this.menuHelp.setText(this.MENU_HELP);

    this.helpReport = new javax.swing.JMenuItem();
    this.helpSeparator = new javax.swing.JPopupMenu.Separator();
    this.helpHomepage = new javax.swing.JMenuItem();
    this.helpAbout = new javax.swing.JMenuItem();

    // FILE

    // file > new
    this.fileNew.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
        java.awt.event.KeyEvent.VK_N, java.awt.event.InputEvent.CTRL_MASK));
    this.fileNew.setMnemonic('N');
    this.fileNew.setText(this.ITEM_FILE_NEW);
    this.fileNew.addActionListener(this.menuHandler);

    this.menuFile.add(this.fileNew);

    // file > convert
    this.fileConvert.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
        java.awt.event.KeyEvent.VK_F5, 0));
    this.fileConvert.setMnemonic('C');
    this.fileConvert.setText(this.ITEM_FILE_CONVERT);
    this.fileConvert.addActionListener(this.menuHandler);
    this.menuFile.add(this.fileConvert);

    this.menuFile.add(this.fileSeparator);

    // file > exit
    this.fileExit.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
        java.awt.event.KeyEvent.VK_W, java.awt.event.InputEvent.CTRL_MASK));
    this.fileExit.setMnemonic('x');
    this.fileExit.setText(this.ITEM_FILE_EXIT);
    this.fileExit.addActionListener(this.menuHandler);


    this.menuFile.add(this.fileExit);
    this.menubarMenu.add(this.menuFile);

    // OPTIONS

    // options > tone numbers
    this.optionsToneNumbers.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
        java.awt.event.KeyEvent.VK_F7, 0));
    this.optionsToneNumbers.setText(this.ITEM_TONE_NUMBERS);
    this.optionsToneNumbers.addActionListener(this.menuHandler);

    this.menuOptions.add(this.optionsToneNumbers);

    // options > tone marks (default option)
    this.optionsToneMarks.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
        java.awt.event.KeyEvent.VK_F8, 0));
    this.optionsToneMarks.setText(this.ITEM_TONE_MARKS);
    this.optionsToneMarks.addActionListener(this.menuHandler);
    this.menuOptions.add(this.optionsToneMarks);

    // options > tones off
    this.optionsTonesOff.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
        java.awt.event.KeyEvent.VK_F9, 0));
    this.optionsTonesOff.setText(this.ITEM_TONES_OFF);
    this.optionsTonesOff.addActionListener(this.menuHandler);

    this.menuOptions.add(this.optionsTonesOff);

    this.menubarMenu.add(this.menuOptions);

    // HELP

    // help > report issue
    this.helpReport.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
        java.awt.event.KeyEvent.VK_F2, 0));
    this.helpReport.setMnemonic('i');
    this.helpReport.setText(this.ITEM_HELP_REPORT);
    this.helpReport.addActionListener(this.menuHandler);

    this.menuHelp.add(this.helpReport);
    this.menuHelp.add(this.helpSeparator);

    // help > home page
    this.helpHomepage.setMnemonic('h');
    this.helpHomepage.setText(this.ITEM_HELP_HOMEPAGE);
    this.helpHomepage.addActionListener(this.menuHandler);

    this.menuHelp.add(this.helpHomepage);

    // help > about
    this.helpAbout.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
        java.awt.event.KeyEvent.VK_F1, 0));
    this.helpAbout.setMnemonic('a');
    this.helpAbout.setText(this.ITEM_HELP_ABOUT);
    this.helpAbout.addActionListener(this.menuHandler);

    this.menuHelp.add(this.helpAbout);
    this.menubarMenu.add(this.menuHelp);

    // never enabled shortcuts
    this.menuShortcuts = new javax.swing.JMenu();
    this.menuShortcuts.setText(this.SHORTCUTS_TEXT);
    this.menuShortcuts
        .setFont(new Font(this.SHORTCUTS_FONT_FACE, 0, this.SHORTCUTS_FONT_SIZE));
    this.menuShortcuts.setEnabled(false);

    this.menubarMenu.add(this.menuShortcuts);

    setJMenuBar(this.menubarMenu);
    pack();
  };

  // clears both input and output
  private void clearAllText() {
    this.textPaneInput.setText("");
    this.textPaneOutput.setText("");
  };

  // shows about box
  private void showAboutBox() {
    JOptionPane.showMessageDialog(null, this.PRODUCT_TITLE + this.CRLF
        + this.VERSION + this.DBL_CRLF + this.AUTHOR + this.CRLF
        + this.AUTHOR_URL + this.DBL_CRLF + this.LICENSE + this.CRLF
        + this.LICENSE_URL, this.ITEM_HELP_ABOUT, JOptionPane.PLAIN_MESSAGE);
  };

  // opens web page in default browser
  private void openWebpage(URI uri) {

    // code solution found at:
    // http://stackoverflow.com/a/10967469

    Desktop desktop =
        Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
    if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
      try {
        desktop.browse(uri);
      } catch (IOException ioe) {
        // may fail due to permissions restrictions
        // it is okay to fail quietly
      }
    }
  };

  // updates menus based on options
  private void updateMenusByOptions() {
    convertInput();
  };

  // updates tone options based on HanyuPinyin object
  private void updateToneOptions() {

    resetAllToneOptions();

    // tone marks is default option
    switch (hp.getMode()) {
      case TONE_NUMBERS:
        this.optionsToneNumbers.setSelected(true);
        break;
      case TONES_OFF:
        this.optionsTonesOff.setSelected(true);
        break;
      default:
        this.optionsToneMarks.setSelected(true);
        break;
    }

    convertInput();
  };

  // convert input and display output
  private void convertInput() {
    this.hp.setInput(this.textPaneInput.getText());
    this.textPaneOutput.setText(this.hp.toString());
  };

  // clear all tone options
  private void resetAllToneOptions() {
    this.optionsToneNumbers.setSelected(false);
    this.optionsToneMarks.setSelected(false);
    this.optionsTonesOff.setSelected(false);
  };

  /**
   * Event Listeners
   * 
   */

  // handles key strokes
  private KeyListener keyHandler = new KeyListener() {

    public void keyTyped(KeyEvent e) {
      // leave empty
    }

    public void keyPressed(KeyEvent e) {

      // if Ctrl + V or Meta/Cmd + V, then auto-convert just once.
      if ((e.getKeyCode() == KeyEvent.VK_V)
          && ((e.getModifiers() & (KeyEvent.CTRL_MASK | KeyEvent.META_MASK)) != 0)) {
        pasteAndConvertEnabled = true;
      }
    }

    public void keyReleased(KeyEvent e) {

      // temporary auto-convert feature (with a latch)
      if (pasteAndConvertEnabled) {
        convertInput();

        // reset
        pasteAndConvertEnabled = false;
      }


    }
  };

  // handles JMenu actions
  private ActionListener menuHandler = new ActionListener() {

    public void actionPerformed(ActionEvent e) {

      // file menu items

      if (e.getSource() == fileNew) {
        clearAllText();
      }

      if (e.getSource() == fileConvert) {
        convertInput();
      }

      if (e.getSource() == fileExit) {
        System.exit(0);
      }

      // options menu items

      if (e.getSource() == optionsToneNumbers) {
        resetAllToneOptions();
        optionsToneNumbers.setSelected(true);
        hp.setMode(Tone.TONE_NUMBERS);
        convertInput();
      }

      if (e.getSource() == optionsToneMarks) {
        resetAllToneOptions();
        optionsToneMarks.setSelected(true);
        hp.setMode(Tone.TONE_MARKS);
        convertInput();
      }

      if (e.getSource() == optionsTonesOff) {
        resetAllToneOptions();
        optionsTonesOff.setSelected(true);
        hp.setMode(Tone.TONES_OFF);
        convertInput();
      }

      // help menu items

      if (e.getSource() == helpReport) {
        openWebpage(URI.create(ISSUES_URL));
      }

      if (e.getSource() == helpHomepage) {
        openWebpage(URI.create(PROJECT_URL));
      }

      if (e.getSource() == helpAbout) {
        showAboutBox();
      }

    };
  };

  /**
   * Main method. Creates and shows the form.
   * 
   * @param args
   */
  public static void main(String args[]) {

    // create form, make form visible
    java.awt.EventQueue.invokeLater(new Runnable() {
      public void run() {
        new HanziToPinyin().setVisible(true);
      }
    });
  };

};
