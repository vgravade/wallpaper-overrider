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

import static com.sun.jna.platform.win32.WinReg.HKEY_CURRENT_USER;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.*;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIOptions;
import java.io.File;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

final class Util {

  private static final Logger LOGGER = Logger.getLogger(Util.class.getName());

  private static final String SYSTEM_KEY_PATH =
      "Software\\Microsoft\\Windows\\CurrentVersion\\Policies\\System";

  private static final boolean IS_WINDOWS =
      System.getProperty("os.name").toLowerCase().contains("windows");

  static void updateForcedWallpaper(File wallpaper, WallpaperStyle style) {
    if (IS_WINDOWS) {
      Advapi32Util.registrySetStringValue(
          HKEY_CURRENT_USER, SYSTEM_KEY_PATH, "Wallpaper", wallpaper.getAbsolutePath());
      Advapi32Util.registrySetStringValue(
          HKEY_CURRENT_USER, SYSTEM_KEY_PATH, "WallpaperStyle", Integer.toString(style.getCode()));
      User32Ext.INSTANCE.SystemParametersInfo(0x14, 0, wallpaper.getAbsolutePath(), 1);
    }
  }

  static Optional<File> getCurrentForcedWallpaper() {
    if (IS_WINDOWS) {
      try {
        String path =
            Advapi32Util.registryGetStringValue(HKEY_CURRENT_USER, SYSTEM_KEY_PATH, "Wallpaper");
        File file = new File(path);
        if (file.exists()) {
          return Optional.of(file);
        }
      } catch (Exception ex) {
        LOGGER.log(Level.WARNING, ex.getMessage(), ex);
      }
    }
    return Optional.empty();
  }

  static WallpaperStyle getCurrentForcedWallpaperStyle() {
    if (IS_WINDOWS) {
      try {
        String regWallpaperStyle =
            Advapi32Util.registryGetStringValue(
                HKEY_CURRENT_USER, SYSTEM_KEY_PATH, "WallpaperStyle");
        if (regWallpaperStyle != null && !regWallpaperStyle.isEmpty()) {
          int styleCode = Integer.valueOf(regWallpaperStyle);
          for (WallpaperStyle ws : WallpaperStyle.values()) {
            if (ws.getCode() == styleCode) {
              return ws;
            }
          }
        }
      } catch (Exception ex) {
        LOGGER.log(Level.WARNING, ex.getMessage(), ex);
      }
    }
    return WallpaperStyle.FILL;
  }

  static File getPicturesDirectory() {
    if (IS_WINDOWS) {
      try {
        return new File(
            Shell32Util.getKnownFolderPath(
                new Guid.GUID("{33E28130-4E1E-4676-835A-98395C3BC3BB}")));
      } catch (Win32Exception ex) {
        LOGGER.log(Level.FINE, ex.getMessage(), ex);
      }
    }
    File home = new File(System.getProperty("user.home"));
    File pictures = new File(home, "Pictures");
    return pictures.isDirectory() ? pictures : home;
  }

  public interface User32Ext extends StdCallLibrary {
    User32Ext INSTANCE = Native.load("user32", User32Ext.class, W32APIOptions.DEFAULT_OPTIONS);

    boolean SystemParametersInfo(int uiAction, int uiParam, String pvParam, int fWinIni);
  }
}
