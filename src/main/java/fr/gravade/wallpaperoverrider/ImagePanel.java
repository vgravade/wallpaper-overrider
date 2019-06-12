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
import java.io.File;
import javax.swing.*;

class ImagePanel extends JPanel {

  private static final Image NO_IMAGE =
      Toolkit.getDefaultToolkit().getImage(App.class.getResource("/Missing-image-232x150.png"));

  private Image image = NO_IMAGE;

  ImagePanel() {
    setBackground(Color.WHITE);
    setPreferredSize(new Dimension(300, 200));
  }

  void setImage(File imageFile) {
    setImage(Toolkit.getDefaultToolkit().getImage(imageFile.getAbsolutePath()));
  }

  void setImage(Image image) {
    this.image = image;
    paintImmediately(0, 0, getWidth(), getHeight());
  }

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    if (image != null) {
      int imgWidth, imgHeight;
      double contRatio = (double) getWidth() / (double) getHeight();
      double imgRatio = (double) image.getWidth(null) / (double) image.getHeight(null);

      // width limited
      if (contRatio < imgRatio) {
        imgWidth = getWidth();
        imgHeight = (int) (getWidth() / imgRatio);

        // height limited
      } else {
        imgWidth = (int) (getHeight() * imgRatio);
        imgHeight = getHeight();
      }
      g.drawImage(image, 0, 0, imgWidth, imgHeight, this);
    }
  }
}
