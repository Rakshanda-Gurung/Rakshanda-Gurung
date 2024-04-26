import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.LinkedList;
import java.util.Random;

public class SnakeGame extends JPanel implements KeyListener, ActionListener {
    private final int WIDTH = 600;
    private final int HEIGHT = 600;
    private final int UNIT_SIZE = 30;
    private LinkedList<Point> snake;
    private Point snack;
    private char direction = 'R';
    private boolean running = false;
    private Timer timer;
    private int score = 0;
    private int directionChangeCounter = 0;
    private int directionChangeLimit = 5;
    private Color[] snakeColors = {Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW, Color.ORANGE};

    public SnakeGame() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(this);
        snake = new LinkedList<>();
        startGame();
    }

    public void startGame() {
        snake.clear();
        snake.add(new Point(WIDTH / 2, HEIGHT / 2));
        snake.add(new Point(WIDTH / 2 - UNIT_SIZE, HEIGHT / 2));
        snake.add(new Point(WIDTH / 2 - 2 * UNIT_SIZE, HEIGHT / 2));
        snake.add(new Point(WIDTH / 2 - 3 * UNIT_SIZE, HEIGHT / 2));
        snake.add(new Point(WIDTH / 2 - 4 * UNIT_SIZE, HEIGHT / 2));
        snake.add(new Point(WIDTH / 2 - 5 * UNIT_SIZE, HEIGHT / 2));
        generateSnack();
        running = true;
        timer = new Timer(80, this);
        timer.start();
        score = 0;
        directionChangeCounter = 0;
    }

    public void generateSnack() {
        Random random = new Random();
        int x, y;
        do {
            x = random.nextInt(WIDTH / UNIT_SIZE) * UNIT_SIZE;
            y = random.nextInt(HEIGHT / UNIT_SIZE) * UNIT_SIZE;
        } while (snake.contains(new Point(x, y)));
        snack = new Point(x, y);
    }

    public void move() {
        Point head = snake.getFirst();
        Point newHead = new Point(head);

        if (directionChangeCounter >= directionChangeLimit) {
            changeDirection();
            directionChangeCounter = 0;
        }

        if (snack.x < head.x && direction != 'R') {
            direction = 'L';
        } else if (snack.x > head.x && direction != 'L') {
            direction = 'R';
        } else if (snack.y < head.y && direction != 'D') {
            direction = 'U';
        } else if (snack.y > head.y && direction != 'U') {
            direction = 'D';
        }

        switch (direction) {
            case 'U':
                newHead.y -= UNIT_SIZE;
                break;
            case 'D':
                newHead.y += UNIT_SIZE;
                break;
            case 'L':
                newHead.x -= UNIT_SIZE;
                break;
            case 'R':
                newHead.x += UNIT_SIZE;
                break;
        }

        newHead.x = (newHead.x + WIDTH) % WIDTH;
        newHead.y = (newHead.y + HEIGHT) % HEIGHT;

        if (newHead.equals(snack)) {
            snake.addFirst(newHead);
            generateSnack();
            score++;
        } else {
            if (snake.contains(newHead)) {
                gameOver();
                return;
            }

            snake.addFirst(newHead);
            snake.removeLast();
        }
        directionChangeCounter++;
    }

    public void changeDirection() {
        Random random = new Random();
        int newDirection = random.nextInt(4);
        switch (newDirection) {
            case 0:
                if (direction != 'D') direction = 'U';
                break;
            case 1:
                if (direction != 'U') direction = 'D';
                break;
            case 2:
                if (direction != 'R') direction = 'L';
                break;
            case 3:
                if (direction != 'L') direction = 'R';
                break;
        }
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        doDrawing(g);
    }

    public void doDrawing(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        if (running) {
            g2d.setColor(Color.RED);
            g2d.fillRect(snack.x, snack.y, UNIT_SIZE, UNIT_SIZE);

            for (int i = 0; i < snake.size(); i++) {
                Color color = snakeColors[i % snakeColors.length];
                g2d.setColor(color);
                Point point = snake.get(i);
                g2d.fillRect(point.x, point.y, UNIT_SIZE, UNIT_SIZE);
            }

            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Arial", Font.BOLD, 20));
            g2d.drawString("Score: " + score, 20, 20);
        } else {
            gameOver(g2d);
        }
    }

    public void gameOver() {
        running = false;
        timer.stop();
        repaint();
    }

    public void gameOver(Graphics2D g2d) {
        g2d.setColor(Color.RED);
        g2d.setFont(new Font("Arial", Font.BOLD, 50));
        g2d.drawString("Game Over!", WIDTH / 2 - 150, HEIGHT / 2 - 50);
        g2d.setFont(new Font("Arial", Font.PLAIN, 30));
        g2d.drawString("Press Space to Restart", WIDTH / 2 - 180, HEIGHT / 2 + 50);
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 20));
        g2d.drawString("Final Score: " + score, WIDTH / 2 - 80, HEIGHT / 2);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (running) {
            move();
            repaint();
            if (score >= 30) {
                running = false;
                timer.stop();
            }
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_SPACE && !running) {
            startGame();
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Snake Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.add(new SnakeGame());
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
