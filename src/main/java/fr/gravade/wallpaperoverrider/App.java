/*
Copyright (C) 2019 Vincent Gravade.

This file is part of Wallpaper Overrider.

Wallpaper Overrider is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

Wallpaper Overrider is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with Wallpaper Overrider.  If not, see <https://www.gnu.org/licenses/>.
 */
package fr.gravade.wallpaperoverrider;

import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;

class App {
  static final ResourceBundle I18N = ResourceBundle.getBundle("strings");

  private static final Logger LOGGER = Logger.getLogger(App.class.getName());

  public static void main(String[] args) {
    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch (Exception ex) {
      LOGGER.log(Level.WARNING, ex.getMessage(), ex);
    }
    SwingUtilities.invokeLater(App::createAndShowGui);
  }

  private static void createAndShowGui() {
    new WallpaperChoiceDialog().setVisible(true);
  }
}
