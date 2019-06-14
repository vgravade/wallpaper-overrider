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

public enum WallpaperStyle {
  SPAN(5),
  FILL(4),
  FIT(3),
  STRETCH(2),
  TILE(1),
  CENTER(0);

  private final int code;

  WallpaperStyle(int code) {
    this.code = code;
  }

  public int getCode() {
    return code;
  }

  @Override
  public String toString() {
    return App.I18N.getString(name());
  }
}
