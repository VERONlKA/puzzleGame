import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

public class ButtonPuzzle extends JButton {
    private BufferedImage image;

    public ButtonPuzzle() {
        super();
        initUI();
    }

    public ButtonPuzzle(BufferedImage image) {
        super(new ImageIcon(image));
        this.image = image;
        initUI();
    }

    private void initUI() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                setBorder(BorderFactory.createLineBorder(Color.green, 2));
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);

                if (e.getClickCount() == 2) {
                    setRotate(90);
                    int orientationInt = (int) getClientProperty("orientation");
                    if (orientationInt != 3) {
                        putClientProperty("orientation", orientationInt + 1);
                    } else {
                        putClientProperty("orientation", 0);
                    }
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                setBorder(BorderFactory.createLineBorder(Color.gray));
            }
        });
    }

    // rotate puzzle icon
    public void setRotate(int angle) {
        AffineTransform at = new AffineTransform();

        at.rotate(Math.toRadians(angle), image.getWidth() / 2, image.getHeight() / 2);

        AffineTransformOp op = new AffineTransformOp(at,
                AffineTransformOp.TYPE_BILINEAR);

        BufferedImage imgRotate = op.filter(image, null);
        image = imgRotate;

        setIcon(new ImageIcon(image));
    }
}
