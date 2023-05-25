import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Random;

public class GamePanel extends JPanel implements ActionListener {

    static final int SCREEN_WIDTH = 1000;
    static final int SCREEN_HEIGHT = 1000;
    static final int UNIT_SIZE = 50;
    static final int GAME_UNITS = (SCREEN_WIDTH * SCREEN_HEIGHT) / UNIT_SIZE;
    static final int DELAY = 150;
    final int[] x = new int[GAME_UNITS];
    final int[] y = new int[GAME_UNITS];
    int bodyParts = 6;
    int applesEaten;
    int appleX;
    int appleY;
    char direction = 'R';
    boolean running = false;
    Timer timer;
    int step;
    Random random;


    GamePanel() {
        random = new Random();
        this.setPreferredSize((new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT)));
        this.setBackground(Color.black);
        this.setFocusable(true);
        this.addKeyListener(new MyKeyAdapter());
        startGame();
    }

    public void startGame() {
        newApple();
        running = true;
        timer = new Timer(DELAY, this);
        timer.start();

    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {

        if (running) {
//            for (int i = 0; i < SCREEN_HEIGHT / UNIT_SIZE; i++) {
//                g.drawLine(i * UNIT_SIZE, 0, i * UNIT_SIZE, SCREEN_HEIGHT);
//                g.drawLine(0, i * UNIT_SIZE, SCREEN_WIDTH, i * UNIT_SIZE);
//            }
            g.setColor(Color.red);
            g.fillOval(appleX, appleY, UNIT_SIZE, UNIT_SIZE);

            for (int i = 0; i < bodyParts; i++) {
                if (i == 0) {
//                    g.setColor(Color.green);
                    g.setColor(new Color(0,0,240));
                    g.fillRoundRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE, UNIT_SIZE / 4, UNIT_SIZE / 4);
                } else {
//                    g.setColor(new Color(45, 180, 0));
                    g.setColor(new Color(0, 0, 180));
//                    g.setColor(new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255)));
                    g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
                }

            }
            g.setColor(Color.red);
            g.setFont(new Font("InkFree", Font.BOLD, 40));
            FontMetrics scoreMetrics = getFontMetrics(g.getFont());
            g.drawString("Score: " + applesEaten, (SCREEN_WIDTH - scoreMetrics.stringWidth("Score: " + applesEaten)) / 2,
                    g.getFont().getSize());

            g.setColor(Color.red);
            g.setFont(new Font("InkFree", Font.BOLD, 20));
            FontMetrics timeMetrics = getFontMetrics(g.getFont());
            g.drawString("Steps: " + step, UNIT_SIZE, SCREEN_HEIGHT - UNIT_SIZE);

        } else {
            gameOver(g);
        }
    }

    public void newApple() {
        appleX = random.nextInt((int) (SCREEN_WIDTH / UNIT_SIZE)) * UNIT_SIZE;
        appleY = random.nextInt((int) (SCREEN_HEIGHT / UNIT_SIZE)) * UNIT_SIZE;
        for (int i = 0; i < bodyParts; i++) {
            if ((x[i] == appleX) && (y[i] == appleY)) {
                newApple();
            }
        }
    }

    public void move() {
        for (int i = bodyParts; i > 0; i--) {
            x[i] = x[i - 1];
            y[i] = y[i - 1];
        }
        switch (direction) {
            case 'U' -> y[0] = y[0] - UNIT_SIZE;
            case 'D' -> y[0] = y[0] + UNIT_SIZE;
            case 'L' -> x[0] = x[0] - UNIT_SIZE;
            case 'R' -> x[0] = x[0] + UNIT_SIZE;
        }

    }

    public void checkApple() {
        if ((x[0] == appleX) && (y[0] == appleY)) {
            bodyParts++;
            applesEaten++;
            newApple();
        }
    }

    public void checkCollisions() {
        // head collides with body
        for (int i = bodyParts; i > 0; i--) {
            if ((x[0] == x[i]) && (y[0] == y[i])) {
                running = false;
            }
        }

        // border touch allowed
        if (x[0] < 0) {
            x[0] = x[0] + SCREEN_WIDTH;
        }
        if (x[0] > SCREEN_WIDTH) {
            x[0] = x[0] - SCREEN_WIDTH - UNIT_SIZE;
        }
        if (y[0] < 0) {
            y[0] = y[0] + SCREEN_HEIGHT;
        }
        if (y[0] > SCREEN_HEIGHT) {
            y[0] = y[0] - SCREEN_HEIGHT - UNIT_SIZE;
        }

        // border touch not allowed
//        if ( (x[0] < 0) || (x[0] > SCREEN_WIDTH) || (y[0] < 0) || (y[0] > SCREEN_HEIGHT)) {
//            running = false;
//        }
//        if (!running) {
//            timer.stop();
//        }


    }

    public void gameOver(Graphics g) {
        g.setColor(Color.red);
        g.setFont(new Font("InkFree", Font.BOLD, 75));
        FontMetrics metrics = getFontMetrics(g.getFont());
        g.drawString("Game Over", (SCREEN_WIDTH - (metrics.stringWidth("Game Over")) ) / 2, SCREEN_HEIGHT / 2);

        g.setColor(Color.red);
        g.setFont(new Font("InkFree", Font.BOLD, 40));
        FontMetrics scoreMetrics = getFontMetrics(g.getFont());
        g.drawString("Score: " + applesEaten, (SCREEN_WIDTH - scoreMetrics.stringWidth("Score: " + applesEaten)) / 2,
                (SCREEN_HEIGHT / 2) + (metrics.stringWidth("G")) * 2);

        g.setColor(Color.red);
        g.setFont(new Font("InkFree", Font.BOLD, 20));
        FontMetrics timeMetrics = getFontMetrics(g.getFont());
        g.drawString("Steps: " + step, (SCREEN_WIDTH - timeMetrics.stringWidth("Steps: " + step) )/ 2,
                (SCREEN_HEIGHT / 2) + (metrics.stringWidth("G")) * 2 + (timeMetrics.stringWidth("S") * 2));
        repaint();
    }

    @Override
    public void unregisterKeyboardAction(KeyStroke aKeyStroke) {
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (running) {
            move();
            checkCollisions();
            checkApple();
            step++;


        }
        repaint();
    }

    public class MyKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT:
                    if (direction != 'R') {
                        direction = 'L';
                        break;
                    }
                case KeyEvent.VK_RIGHT:
                    if (direction != 'L') {
                        direction = 'R';
                        break;
                    }
                case KeyEvent.VK_UP:
                    if (direction != 'D') {
                        direction = 'U';
                        break;
                    }
                case KeyEvent.VK_DOWN:
                    if (direction != 'U') {
                        direction = 'D';
                        break;
                    }
            }
        }
    }
}