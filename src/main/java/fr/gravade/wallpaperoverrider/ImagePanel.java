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

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.swing.*;

class ImagePanel extends JPanel {

  private static final Image MONITOR =
      Toolkit.getDefaultToolkit().getImage(App.class.getResource("/images/monitor.png"));

  private static final Image NO_IMAGE =
      Toolkit.getDefaultToolkit().getImage(App.class.getResource("/images/missing.png"));

  private static final int MONITOR_X = 12;
  private static final int MONITOR_Y = 12;
  private static final int MONITOR_WIDTH = 316;
  private static final int MONITOR_HEIGHT = 198;
  private static final double MONITOR_RATIO = (double) MONITOR_WIDTH / (double) MONITOR_HEIGHT;
  private static final int MONITOR_STD_WIDTH = Toolkit.getDefaultToolkit().getScreenSize().width;
  private static final double STD_SCALE_FACTOR =
      (double) MONITOR_WIDTH / (double) MONITOR_STD_WIDTH;

  private Image image = NO_IMAGE;
  private WallpaperStyle style = WallpaperStyle.FILL;

  ImagePanel() {
    setBackground(Color.WHITE);
    setPreferredSize(new Dimension(340, 280));
  }

  void setImage(File imageFile) {
    setImage(Toolkit.getDefaultToolkit().getImage(imageFile.getAbsolutePath()));
  }

  void setImage(Image image) {
    this.image = image;
    paintImmediately(0, 0, getWidth(), getHeight());
  }

  public void setStyle(WallpaperStyle style) {
    this.style = style;
    paintImmediately(0, 0, getWidth(), getHeight());
  }

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    g.drawImage(MONITOR, 0, 0, MONITOR.getWidth(this), MONITOR.getHeight(this), this);

    double imgRatio = (double) image.getWidth(this) / (double) image.getHeight(this);
    int imgWidth = MONITOR_WIDTH;
    int imgHeight = MONITOR_HEIGHT;
    int x = MONITOR_X;
    int y = MONITOR_Y;
    Image img = image;

    if (image != null) {
      switch (style) {
        case CENTER:
          img = getCenterImage();
          break;

        case FILL:
        case SPAN:
          img = getFillImage(imgRatio);
          break;

        case TILE:
          img = getTileImage();
          break;

        case FIT:
          if (MONITOR_RATIO < imgRatio) { // width limited
            imgHeight = (int) (MONITOR_WIDTH / imgRatio);
            y = MONITOR_Y + (MONITOR_HEIGHT - imgHeight) / 2;
          } else { // height limited
            imgWidth = (int) (MONITOR_HEIGHT * imgRatio);
            x = MONITOR_X + (MONITOR_WIDTH - imgWidth) / 2;
          }
          break;

        case STRETCH:
        default:
          // nothing
          break;
      }

      g.drawImage(img, x, y, imgWidth, imgHeight, this);
    }
  }

  private Image getCenterImage() {
    int imgWidth = (int) (image.getWidth(this) * STD_SCALE_FACTOR);
    int imgHeight = (int) (image.getHeight(this) * STD_SCALE_FACTOR);
    return getTempImage(imgWidth, imgHeight);
  }

  private Image getFillImage(double imgRatio) {
    int imgWidth, imgHeight;
    if (MONITOR_RATIO < imgRatio) { // width limited
      imgWidth = (int) (MONITOR_HEIGHT * imgRatio);
      imgHeight = MONITOR_HEIGHT;
    } else { // height limited
      imgWidth = MONITOR_WIDTH;
      imgHeight = (int) (MONITOR_WIDTH / imgRatio);
    }
    return getTempImage(imgWidth, imgHeight);
  }

  private Image getTempImage(int imgWidth, int imgHeight) {
    int x = (MONITOR_WIDTH - imgWidth) / 2;
    int y = (MONITOR_HEIGHT - imgHeight) / 2;
    Image img = new BufferedImage(MONITOR_WIDTH, MONITOR_HEIGHT, BufferedImage.TYPE_INT_ARGB);
    if (imgWidth > 0 && imgHeight > 0) {
      img.getGraphics().drawImage(image, x, y, imgWidth, imgHeight, this);
    }
    return img;
  }

  private Image getTileImage() {
    int imgWidth = (int) (image.getWidth(this) * STD_SCALE_FACTOR);
    int imgHeight = (int) (image.getHeight(this) * STD_SCALE_FACTOR);
    Image img = new BufferedImage(MONITOR_WIDTH, MONITOR_HEIGHT, BufferedImage.TYPE_INT_ARGB);
    if (imgWidth > 0 && imgHeight > 0) {
      for (int x = 0; x < MONITOR_WIDTH; x += imgWidth) {
        for (int y = 0; y < MONITOR_HEIGHT; y += imgHeight) {
          img.getGraphics().drawImage(image, x, y, imgWidth, imgHeight, this);
        }
      }
    }
    return img;
  }
}
