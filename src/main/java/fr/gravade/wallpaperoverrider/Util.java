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

import static com.sun.jna.platform.win32.WinReg.HKEY_CURRENT_USER;

import com.sun.jna.platform.win32.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

final class Util {

  private static final Logger LOGGER = Logger.getLogger(Util.class.getName());

  private static final String SYSTEM_KEY_PATH =
      "Software\\Microsoft\\Windows\\CurrentVersion\\Policies\\System";

  static void configureWallpaperInRegistry(Path wallpaper, WallpaperStyle style) {
    Advapi32Util.registrySetStringValue(
        HKEY_CURRENT_USER, SYSTEM_KEY_PATH, "Wallpaper", wallpaper.toString());
    Advapi32Util.registrySetStringValue(
        HKEY_CURRENT_USER, SYSTEM_KEY_PATH, "WallpaperStyle", Integer.toString(style.getCode()));
  }

  static Optional<File> getCurrentForcedWallpaper() {
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
    return Optional.empty();
  }

  static WallpaperStyle getCurrentForcedWallpaperStyle() {
    try {
      String regWallpaperStyle =
          Advapi32Util.registryGetStringValue(HKEY_CURRENT_USER, SYSTEM_KEY_PATH, "WallpaperStyle");
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
    return WallpaperStyle.FILL;
  }

  static void refreshDesktop() throws IOException, InterruptedException {
    Process p =
        Runtime.getRuntime()
            .exec(
                System.getenv("SystemRoot")
                    + "\\System32\\rundll32.exe user32.dll,UpdatePerUserSystemParameters");
    p.waitFor();
  }

  static File getPicturesDirectory() {
    try {
      return new File(
          Shell32Util.getKnownFolderPath(new Guid.GUID("{33E28130-4E1E-4676-835A-98395C3BC3BB}")));
    } catch (Win32Exception ex) {
      LOGGER.log(Level.FINE, ex.getMessage(), ex);
      return new File(System.getProperty("user.home"));
    }
  }
}
