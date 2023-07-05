import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.awt.image.CropImageFilter;
import java.awt.image.FilteredImageSource;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class Game extends JFrame implements ActionListener {
    private final File PUZZLE_PATH = new File("puzzleGame/src/images");
    private final int DESIRED_WIDTH = 400;
    private final int DESIRED_HEIGHT = 400;

    private ArrayList<Point> solution;
    private JButton autoMoveJB;
    private JButton closeJB;
    private ArrayList<ButtonPuzzle> buttons;
    private JPanel gamePanel;
    private JPanel controlPanel;
    private JPanel originalPanel;
    private BufferedImage source;
    private BufferedImage resize;
    private BufferedImage puzzle;
    private int width, height;
    private Image img;
    private ButtonPuzzle firstBtn = null;
    private ButtonPuzzle secondBtn = null;
    private int indexFirst = 0;
    private int indexSecond = 0;

    public Game() {
        initUI();
    }

    public void initUI() {
        solution = new ArrayList<>();
        solution.add(new Point(0, 0));
        solution.add(new Point(0, 1));
        solution.add(new Point(0, 2));
        solution.add(new Point(0, 3));
        solution.add(new Point(1, 0));
        solution.add(new Point(1, 1));
        solution.add(new Point(1, 2));
        solution.add(new Point(1, 3));
        solution.add(new Point(2, 0));
        solution.add(new Point(2, 1));
        solution.add(new Point(2, 2));
        solution.add(new Point(2, 3));
        solution.add(new Point(3, 0));
        solution.add(new Point(3, 1));
        solution.add(new Point(3, 2));
        solution.add(new Point(3, 3));

        controlPanel = new JPanel();
        autoMoveJB = new JButton();
        originalPanel = new JPanel();
        gamePanel = new JPanel();
        closeJB = new JButton();
        buttons = new ArrayList<>();

        setUndecorated(true);

        originalPanel.setBackground(new Color(196, 164, 132));
        originalPanel.setFocusable(false);
        originalPanel.setMaximumSize(new Dimension(200, 200));
        originalPanel.setMinimumSize(new Dimension(200, 200));
        originalPanel.setPreferredSize(new Dimension(420, 400));

        controlPanel.setFocusable(false);
        controlPanel.setOpaque(false);
        controlPanel.setPreferredSize(new Dimension(300, 40));
        controlPanel.setLayout(new GridLayout(1, 2));

        autoMoveJB.setFont(new Font("Tahoma", Font.BOLD, 16));
        autoMoveJB.setForeground(new Color(0, 0, 0));
        autoMoveJB.setText("Auto sort");
        autoMoveJB.setToolTipText("Auto sort Tiles");
        autoMoveJB.setCursor(new Cursor(java.awt.Cursor.HAND_CURSOR));
        autoMoveJB.setFocusable(false);
        autoMoveJB.setOpaque(false);
        autoMoveJB.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                autoMove();
            }
        });
        controlPanel.add(autoMoveJB);

        closeJB.setFont(new Font("Tahoma", 1, 16));
        closeJB.setForeground(new Color(0, 0, 0));
        closeJB.setHorizontalAlignment(SwingConstants.CENTER);
        closeJB.setText("Exit");
        closeJB.setToolTipText("Close");
        closeJB.setCursor(new Cursor(Cursor.HAND_CURSOR));
        closeJB.setFocusable(false);
        closeJB.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                if (evt.getButton() == MouseEvent.BUTTON1) {
                    dispose();
                    System.exit(1);
                }
            }
        });
        controlPanel.add(closeJB, BorderLayout.EAST);

        gamePanel.setBorder(BorderFactory.createLineBorder(Color.gray));
        gamePanel.setSize(new java.awt.Dimension(400, 400));
        gamePanel.setLayout(new GridLayout(4, 4));

        try {
            source = loadImage();
            resize = resizeImage(source, DESIRED_WIDTH, DESIRED_HEIGHT, BufferedImage.TYPE_INT_ARGB);
        } catch (IOException ex) {
            System.err.println("Some issues occured while loading image" + ex.fillInStackTrace());
        }

        width = resize.getWidth();
        height = resize.getHeight();

        source = resizeImage(source, 300, 300, BufferedImage.TYPE_INT_ARGB);

        JLabel picLabel = new JLabel(new ImageIcon(source));
        picLabel.setBorder(BorderFactory.createLineBorder(Color.gray));
        picLabel.setBorder(BorderFactory.createStrokeBorder(new BasicStroke(5)));
        picLabel.setHorizontalAlignment(JLabel.CENTER);

        originalPanel.setBorder(BorderFactory.createEmptyBorder(15, 10, 20, 10));

        JLabel originalJL = new JLabel("Original Image");
        originalJL.setFont(new Font("Tahoma", 1, 16));
        originalJL.setForeground(new Color(0, 0, 0));
        originalJL.setHorizontalAlignment(SwingConstants.CENTER);

        originalPanel.add(picLabel);
        originalPanel.add(originalJL, BorderLayout.SOUTH);

        add(originalPanel, BorderLayout.EAST);
        add(gamePanel, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.SOUTH);

        int k = 0;

        for (int i = 0; i < 4; i++) { // y

            for (int j = 0; j < 4; j++) { // x
                img = createImage(new FilteredImageSource(resize.getSource(),
                        new CropImageFilter(j * width / 4, i * height / 4, width / 4, height / 4)));
                puzzle = toBufferedImage(img); // Create puzzle
                savePuzzle(puzzle, k);
                ButtonPuzzle button = new ButtonPuzzle(puzzle);
                button.putClientProperty("position", new Point(i, j));
                k++;
                int orientation = (int) (Math.random() * 4); // Set random orientation for puzzle
                int angle = orientation * 90;
                button.putClientProperty("orientation", orientation);
                button.setRotate(angle);
                buttons.add(button);
            }
        }

        Collections.shuffle(buttons);

        for (ButtonPuzzle but : buttons) {
            gamePanel.add(but);
            but.addActionListener(this);
        }

        add(gamePanel);
        pack();
        setSize(800, resize.getHeight());
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JOptionPane.showMessageDialog(gamePanel, "Hi!\nI`m very glad that you decided to play our puzzle game <3\n" +
                "How to play:\n1. To switch puzzles you need to click once on each of them\n2. To rotate the puzzle by 90 degrees, you need to click on it twice\n3. Do not forget to look at the original picture when assembling the puzzle\nGood luck!");
    }

    private BufferedImage resizeImage(BufferedImage original, int width, int height, int type) { // resize our Image
        BufferedImage resizedImage = new BufferedImage(width, height, type);
        Graphics2D g = resizedImage.createGraphics();

        g.drawImage(original, 0, 0, width, height, null);
        g.dispose();

        return resizedImage;
    }

    // load image from file
    private BufferedImage loadImage() throws IOException {
        File file = new File("puzzleGame/src/images");
        BufferedImage bing = ImageIO.read(new File(PUZZLE_PATH.getAbsolutePath() + "\\bake.png"));
        return bing;
    }

    // save puzzles in file
    private void savePuzzle(BufferedImage img, int k) {
        try {
            File f = new File(
                    PUZZLE_PATH + "\\puzzles\\Puzzle" + k
                            + ".png");
            ImageIO.write(img, "PNG", f);
        } catch (IOException e) {
            System.err.println("Cannot save image, try again" + e);
        }
    }

    // transform Image to BufferedImage
    public static BufferedImage toBufferedImage(Image img) {
        if (img instanceof BufferedImage) {
            return (BufferedImage) img;
        }

        BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

        Graphics2D bGr = bimage.createGraphics();
        bGr.drawImage(img, 0, 0, null);
        bGr.dispose();

        return bimage;
    }

    // check if puzzles are in the right order
    public void check(ArrayList<Point> solution, ArrayList<ButtonPuzzle> but) {
        boolean won = false;

        for (int i = 0; i < 16; i++) {
            Point solutionPoint = solution.get(i);
            Point positionPoint = (Point) but.get(i).getClientProperty("position");
            int orientationInt = (int) but.get(i).getClientProperty("orientation");
            if (solutionPoint.equals(positionPoint) && orientationInt == 0) {
                won = true;
            } else {
                won = false;
                break;
            }
        }

        if (won) {
            JOptionPane.showMessageDialog(gamePanel, "Congratulation!");
        }
    }

    // swap two puzzles
    public void move(ButtonPuzzle btn) {
        if (firstBtn != btn) {
            if (firstBtn == null) {
                firstBtn = btn;
                indexFirst = findIndex(buttons, btn);
            } else if (secondBtn == null) {
                secondBtn = btn;
                indexSecond = findIndex(buttons, btn);

                buttons.set(indexFirst, secondBtn);
                buttons.set(indexSecond, firstBtn);

                gamePanel.add(secondBtn, indexFirst);
                gamePanel.add(firstBtn, indexSecond);
                gamePanel.updateUI();

                firstBtn = null;
                secondBtn = null;
                indexFirst = 0;
                indexSecond = 0;
            }
        } else {
            firstBtn = null;
        }
    }

    // find index of ButtonPuzzle
    public int findIndex(ArrayList<ButtonPuzzle> buttons, ButtonPuzzle but) {
        int index = 0;

        for (int i = 0; i < buttons.size(); i++) {
            Point k = (Point) buttons.get(i).getClientProperty("position");
            Point l = (Point) but.getClientProperty("position");
            if (k.equals(l)) {
                index = i;
            }
        }

        return index;
    }

    // autosort puzzles
    public void autoMove() {
        for (int j = 1; j < buttons.size(); j++) {

            Point key = (Point) buttons.get(j).getClientProperty("position");
            ButtonPuzzle keyBtn = buttons.get(j);
            int i = j - 1;
            while ((i > -1) && ((Point) buttons.get(i).getClientProperty("position")).x > key.x) {
                firstBtn = buttons.get(i);
                secondBtn = buttons.get(i + 1);
                buttons.set(i + 1, firstBtn);
                gamePanel.add(firstBtn, i + 1);
                i--;

            }
            buttons.set(i + 1, keyBtn);
            gamePanel.add(keyBtn, i + 1);
        }

        int l = -1;

        for (int j = 1; j < buttons.size(); j++) {

            for (int k = 0; k < 3; k++) {
                Point key = (Point) buttons.get(j).getClientProperty("position");
                ButtonPuzzle keyBtn = buttons.get(j);
                int i = j - 1;
                while ((i > l) && ((Point) buttons.get(i).getClientProperty("position")).y > key.y) {
                    firstBtn = buttons.get(i);
                    secondBtn = buttons.get(i + 1);
                    buttons.set(i + 1, firstBtn);
                    gamePanel.add(firstBtn, i + 1);
                    i--;
                }
                buttons.set(i + 1, keyBtn);
                gamePanel.add(keyBtn, i + 1);
                j++;
            }

            l += 4;
        }

        for (ButtonPuzzle btn : buttons) {
            int orientation = (int) btn.getClientProperty("orientation");
            if (orientation != 0) {
                btn.setRotate(360 - (orientation * 90));
                btn.putClientProperty("orientation", 0);
            }
        }

        gamePanel.updateUI();
        check(solution, buttons);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        move((ButtonPuzzle) e.getSource());
        check(solution, buttons);
    }
}
