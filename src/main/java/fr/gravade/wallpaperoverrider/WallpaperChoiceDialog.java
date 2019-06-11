/*
Wallpaper Overrider
Copyright (C) 2019  Vincent Gravade

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package fr.gravade.wallpaperoverrider;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.*;

class WallpaperChoiceDialog extends JFrame implements ActionListener {

  private static final Logger LOGGER = Logger.getLogger(WallpaperChoiceDialog.class.getName());

  private static final Font BASE_FONT = new Font("Segoe UI", Font.PLAIN, 13);

  private static final Font HEADER_FONT = BASE_FONT.deriveFont(22.0f);
  private static final Font NORMAL_FONT = BASE_FONT.deriveFont(15.0f);
  private static final Font ITALIC_FONT = BASE_FONT.deriveFont(Font.ITALIC);

  private final ImagePanel forcedImagePanel = new ImagePanel(this);
  private final JLabel loadingLabel =
      createLabelWithCustomFont(App.I18N.getString("loading"), ITALIC_FONT);
  private final JButton browseButton = new JButton(App.I18N.getString("browse"));
  private final JComboBox<WallpaperStyle> styleComboBox = new JComboBox<>(WallpaperStyle.values());
  private final JButton applyButton = new JButton(App.I18N.getString("apply"));
  private final JButton closeButton = new JButton(App.I18N.getString("close"));

  private boolean initialLoading = true;
  private File selectedFile = null;

  WallpaperChoiceDialog() {
    super("Wallpaper Overrider");
    setDefaultCloseOperation(DISPOSE_ON_CLOSE);

    forceFontForComponent(browseButton, styleComboBox, applyButton, closeButton);

    JPanel browsePanel = new JPanel(new BorderLayout(5, 0));
    browsePanel.setOpaque(false);
    browsePanel.add(browseButton, BorderLayout.WEST);
    browsePanel.add(Box.createHorizontalGlue(), BorderLayout.CENTER);

    JPanel wallpaperPanel = new JPanel(new BorderLayout(5, 20));
    wallpaperPanel.setOpaque(false);
    wallpaperPanel.add(loadingLabel, BorderLayout.NORTH);
    wallpaperPanel.add(forcedImagePanel, BorderLayout.WEST);
    wallpaperPanel.add(Box.createHorizontalGlue(), BorderLayout.CENTER);
    wallpaperPanel.add(browsePanel, BorderLayout.SOUTH);

    JPanel wallpaperStylePanel = new JPanel(new BorderLayout(5, 0));
    wallpaperStylePanel.setOpaque(false);
    wallpaperStylePanel.add(styleComboBox, BorderLayout.CENTER);
    wallpaperStylePanel.add(Box.createHorizontalStrut(500), BorderLayout.EAST);

    JPanel buttonsPanel = new JPanel();
    buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.LINE_AXIS));
    buttonsPanel.setOpaque(false);
    buttonsPanel.add(applyButton);
    buttonsPanel.add(closeButton);

    JPanel contentPanel = new JPanel(new GridBagLayout());
    contentPanel.setOpaque(true);
    contentPanel.setBackground(Color.WHITE);
    contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 150));

    GridBagConstraints gc = new GridBagConstraints();
    gc.anchor = GridBagConstraints.BASELINE_LEADING;
    gc.fill = GridBagConstraints.HORIZONTAL;

    gc.gridx = 0;
    gc.gridy = 0;
    gc.insets = new Insets(0, 0, 20, 0);
    contentPanel.add(
        createLabelWithCustomFont(App.I18N.getString("override_wallpaper"), HEADER_FONT), gc);
    gc.gridy++;

    gc.insets = new Insets(0, 0, 10, 0);
    contentPanel.add(
        createLabelWithCustomFont(App.I18N.getString("choose_picture"), NORMAL_FONT), gc);
    gc.gridy++;
    gc.insets = new Insets(0, 0, 40, 0);
    contentPanel.add(wallpaperPanel, gc);

    gc.gridy++;
    gc.insets = new Insets(0, 0, 10, 0);
    contentPanel.add(createLabelWithCustomFont(App.I18N.getString("choose_fit"), NORMAL_FONT), gc);

    gc.gridy++;
    gc.insets = new Insets(0, 0, 50, 0);
    contentPanel.add(wallpaperStylePanel, gc);

    gc.gridy++;
    gc.insets = new Insets(0, 0, 20, 0);
    contentPanel.add(buttonsPanel, gc);

    gc.gridy++;
    contentPanel.add(
        createLabelWithCustomFont(App.I18N.getString("should_logout"), ITALIC_FONT), gc);

    setContentPane(contentPanel);
    loadingLabel.setVisible(false);

    Util.getCurrentForcedWallpaper()
        .ifPresent(
            file -> {
              setLoading(true);
              new LoadImageWorker(file).execute();
            });

    styleComboBox.setSelectedItem(Util.getCurrentForcedWallpaperStyle());

    pack();
    setResizable(false);

    browseButton.addActionListener(this);
    styleComboBox.addActionListener(this);
    applyButton.addActionListener(this);
    closeButton.addActionListener(this);
    applyButton.setEnabled(false);
  }

  void setLoading(boolean loading) {
    browseButton.setEnabled(!loading);
    styleComboBox.setEnabled(!loading);
    loadingLabel.setVisible(loading);
    if (!loading) {
      if (initialLoading) {
        initialLoading = false;
      } else {
        applyButton.setEnabled(true);
      }
    }
  }

  private void forceFontForComponent(JComponent... components) {
    for (JComponent component : components) {
      component.setFont(NORMAL_FONT);
    }
  }

  private JLabel createLabelWithCustomFont(String text, Font font) {
    JLabel label = new JLabel(text);
    label.setFont(font);
    return label;
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    Object source = e.getSource();
    if (source == closeButton) {
      dispose();
    } else if (source == applyButton) {
      onApplyButton();
    } else if (source == browseButton) {
      onBrowseButton();
    } else if (source == styleComboBox) {
      applyButton.setEnabled(true);
    }
  }

  private void onApplyButton() {

    Path image = selectedFile.toPath();
    if (Files.isRegularFile(image) && Files.exists(image)) {
      try {
        WallpaperStyle style =
            styleComboBox.getSelectedItem() != null
                ? (WallpaperStyle) styleComboBox.getSelectedItem()
                : WallpaperStyle.FILL;
        Util.configureWallpaperInRegistry(image, style);
        Util.refreshDesktop();
        applyButton.setEnabled(false);
      } catch (Exception ex) {
        LOGGER.log(Level.SEVERE, ex.getMessage(), ex);
        JOptionPane.showMessageDialog(
            this, ex.getLocalizedMessage(), App.I18N.getString("error"), JOptionPane.ERROR_MESSAGE);
      }
    } else {
      JOptionPane.showMessageDialog(
          this,
          App.I18N.getString("invalid_image"),
          App.I18N.getString("error"),
          JOptionPane.ERROR_MESSAGE);
    }
  }

  private void onBrowseButton() {
    JFileChooser chooser = new JFileChooser();
    if (selectedFile != null) {
      chooser.setSelectedFile(selectedFile);
    } else {
      chooser.setCurrentDirectory(Util.getPicturesDirectory());
    }
    chooser.showOpenDialog(browseButton);
    File newFile = chooser.getSelectedFile();
    if (newFile != null && !newFile.equals(selectedFile)) {
      setLoading(true);
      new LoadImageWorker(newFile).execute();
    }
  }

  private class LoadImageWorker extends SwingWorker<Image, Void> {

    private final File file;

    LoadImageWorker(File file) {
      this.file = file;
    }

    @Override
    protected Image doInBackground() throws Exception {
      return ImageIO.read(file);
    }

    @Override
    protected void done() {
      try {
        forcedImagePanel.setImage(get());
        selectedFile = file;
      } catch (InterruptedException | ExecutionException ex) {
        LOGGER.log(Level.SEVERE, ex.getMessage(), ex);
        setLoading(false);
        JOptionPane.showMessageDialog(
            WallpaperChoiceDialog.this,
            ex.getLocalizedMessage(),
            App.I18N.getString("error"),
            JOptionPane.ERROR_MESSAGE);
      }
    }
  }
}