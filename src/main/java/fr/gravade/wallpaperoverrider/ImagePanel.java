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
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.*;

class ImagePanel extends JPanel implements Runnable {

  private static final Logger LOGGER = Logger.getLogger(ImagePanel.class.getName());
  private static final int WIDTH = 300;
  private static final int HEIGHT = 200;

  private static Image NO_IMAGE;

  static {
    try {
      NO_IMAGE = ImageIO.read(App.class.getResource("/Missing-image-232x150.png"));
    } catch (IOException ex) {
      LOGGER.log(Level.SEVERE, ex.getMessage(), ex);
      System.exit(1);
    }
  }

  private final WallpaperChoiceDialog dlg;

  private Image image = NO_IMAGE;

  ImagePanel(WallpaperChoiceDialog dlg) {
    this.dlg = dlg;
    setBackground(Color.WHITE);
    setPreferredSize(new Dimension(WIDTH, HEIGHT));
  }

  void setImage(Image img) {
    new ScaleImageWorker(img).execute();
  }

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    if (image != null) {
      int imgWidth = image.getWidth(this);
      int imgHeight = image.getHeight(this);
      g.drawImage(image, 0, 0, imgWidth, imgHeight, this);
    }
  }

  @Override
  public void run() {}

  private class ScaleImageWorker extends SwingWorker<Image, Void> {
    private final Image rawImage;

    ScaleImageWorker(Image rawImage) {
      this.rawImage = rawImage;
    }

    @Override
    protected Image doInBackground() {
      int imgWidth, imgHeight;
      double contRatio = (double) WIDTH / (double) HEIGHT;
      double imgRatio = (double) rawImage.getWidth(null) / (double) rawImage.getHeight(null);

      // width limited
      if (contRatio < imgRatio) {
        imgWidth = WIDTH;
        imgHeight = (int) (WIDTH / imgRatio);

        // height limited
      } else {
        imgWidth = (int) (HEIGHT * imgRatio);
        imgHeight = HEIGHT;
      }
      return rawImage.getScaledInstance(imgWidth, imgHeight, Image.SCALE_SMOOTH);
    }

    @Override
    protected void done() {
      try {
        image = get();
      } catch (InterruptedException | ExecutionException ex) {
        ex.printStackTrace();
        image = null;
        JOptionPane.showMessageDialog(
            ImagePanel.this,
            ex.getLocalizedMessage(),
            App.I18N.getString("error"),
            JOptionPane.ERROR_MESSAGE);
      } finally {
        paintImmediately(0, 0, getWidth(), getHeight());
        dlg.setLoading(false);
      }
    }
  }
}
