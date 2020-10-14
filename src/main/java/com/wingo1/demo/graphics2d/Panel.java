package com.wingo1.demo.graphics2d;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Transparency;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.VolatileImage;
import java.io.IOException;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class Panel {

	public static void main(String[] args) {
		new Panel();
	}

	public Panel() {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
				} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
						| UnsupportedLookAndFeelException ex) {
					ex.printStackTrace();
				}

				JFrame frame = new JFrame("Testing");
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				ViewPane pane = new ViewPane();
				frame.add(pane);
				frame.pack();
				frame.setLocationRelativeTo(null);
				frame.setVisible(true);
				pane.engine.gameStart();

			}
		});
	}

	public interface View {
		public BufferStrategy getBufferStrategy();

		public VolatileImage getOffscreenBuffer();

		public void show(VolatileImage img);

		public boolean isIncompatiable(VolatileImage img);

		public int getWidth();

		public int getHeight();

	}

	public enum KeyState {

		UP, DOWN, LEFT, RIGHT;
	}

	public class ViewPane extends JPanel implements View {

		private VolatileImage offscreen;
		private BufferedImage onscreen;
		private ReentrantLock lckBuffers;

		public Engine engine;
		private Canvas canvas;

		public ViewPane() {
			canvas = new Canvas() {

				@Override
				public void addNotify() {
					super.addNotify();
					if (canvas.getBufferStrategy() == null) {
						canvas.createBufferStrategy(2);
					}

				}

			};
			this.add(canvas);
			lckBuffers = new ReentrantLock();

			engine = new Engine(this);
			// engine.gameStart();

			InputMap im = getInputMap(WHEN_IN_FOCUSED_WINDOW);
			im.put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0, false), "up_pressed");
			im.put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0, false), "down_pressed");
			im.put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0, false), "left_pressed");
			im.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0, false), "right_pressed");
			im.put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0, true), "up_released");
			im.put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0, true), "down_released");
			im.put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0, true), "left_released");
			im.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0, true), "right_released");

			ActionMap am = getActionMap();
			am.put("up_pressed", new AddState(engine, KeyState.UP));
			am.put("up_released", new RemoveState(engine, KeyState.UP));
			am.put("down_pressed", new AddState(engine, KeyState.DOWN));
			am.put("down_released", new RemoveState(engine, KeyState.DOWN));
			am.put("left_pressed", new AddState(engine, KeyState.LEFT));
			am.put("left_released", new RemoveState(engine, KeyState.LEFT));
			am.put("right_pressed", new AddState(engine, KeyState.RIGHT));
			am.put("right_released", new RemoveState(engine, KeyState.RIGHT));
			addMouseListener(new MouseAdapter() {

				@Override
				public void mouseClicked(MouseEvent e) {
					System.out.println("panel click");
				}

			});
			canvas.setEnabled(false);
		}

		@Override
		public void invalidate() {
			super.invalidate();
			onscreen = null;
//            offscreen = null;
		}

		@Override
		public Dimension getPreferredSize() {
			return new Dimension(400, 400);
		}

		@Override
		protected void paintComponent(Graphics g) {
			canvas.setBounds(getVisibleRect());
			/*
			 * 
			 * super.paintComponent(g); try { lckBuffers.lock(); // Make sure the buffer is
			 * okay for painting.... if (onscreen != null) { Graphics2D g2d = (Graphics2D)
			 * g.create(); g2d.drawImage(onscreen, 0, 0, this); g2d.dispose(); } } finally {
			 * lckBuffers.unlock(); }
			 * 
			 */}

		protected VolatileImage createVolatileImage(int width, int height, int transparency) {

			GraphicsConfiguration gc = getGraphicsConfiguration();
			VolatileImage image = null;

			if (gc != null && width > 0 && height > 0) {

				image = gc.createCompatibleVolatileImage(width, height, transparency);

				int valid = image.validate(gc);

				if (valid == VolatileImage.IMAGE_INCOMPATIBLE) {

					image = this.createVolatileImage(width, height, transparency);

				}

			}

			return image;

		}

		@Override
		public VolatileImage getOffscreenBuffer() {

			if (isIncompatiable(offscreen)) {
				offscreen = createVolatileImage(getWidth(), getHeight(), Transparency.TRANSLUCENT);
			}

			return offscreen;
		}

		@Override
		public void show(VolatileImage img) {

			try {
				lckBuffers.lock();
				GraphicsConfiguration gc = getGraphicsConfiguration();
				if (gc != null) {
					if (onscreen == null) {
						onscreen = gc.createCompatibleImage(getWidth(), getHeight(), Transparency.TRANSLUCENT);
					}
					if (isOkay(img)) {
						Graphics2D g2d = onscreen.createGraphics();
						g2d.drawImage(img, 0, 0, this);
						g2d.dispose();
						repaint();
					}
				}
			} finally {
				lckBuffers.unlock();
			}

		}

		@Override
		public boolean isIncompatiable(VolatileImage offscreen) {

			boolean isIncompatiable = true;
			GraphicsConfiguration gc = getGraphicsConfiguration();
			if (gc != null) {
				if (offscreen != null) {
					if (offscreen.getWidth() == getWidth() && offscreen.getHeight() == getHeight()) {
						if (offscreen.validate(gc) != VolatileImage.IMAGE_INCOMPATIBLE) {
							isIncompatiable = false;
						}
					}
				}
			}

			return isIncompatiable;

		}

		public boolean isOkay(VolatileImage buffer) {

			boolean isOkay = false;
			GraphicsConfiguration gc = getGraphicsConfiguration();
			if (gc != null) {
				if (buffer != null) {
					if (buffer.getWidth() == getWidth() && buffer.getHeight() == getHeight()) {
						if (buffer.validate(gc) == VolatileImage.IMAGE_OK) {
							isOkay = true;
						}
					}
				}
			}

			return isOkay;

		}

		@Override
		public BufferStrategy getBufferStrategy() {

			return canvas.getBufferStrategy();
		}
	}

	public static class Engine {

		public static final int MAP_WIDTH = 15 * 4;
		public static final int MAP_HEIGHT = 9 * 4;
		public static final int X_DELTA = 4;
		public static final int Y_DELTA = 4;

		public boolean isGameFinished = false;

		// This value would probably be stored elsewhere.
		public static final long GAME_HERTZ = 60;
		// Store the last time we rendered.
		static long lastRenderTime = System.nanoTime();

		// If we are able to get as high as this FPS, don't render again.
		final static long TARGET_FPS = GAME_HERTZ;
		final static long TARGET_TIME_BETWEEN_RENDERS = Math.round(1000000000 / (double) TARGET_FPS);

		// Simple way of finding FPS.
		static int lastSecondTime;

		public int fps = 30;
		public int frameCount = 0;

		private View view;
		private int camX, camY;
		private Set<KeyState> keyStates;

		private BufferedImage map;
		private BufferedImage tiles[];

		public Engine(View view) {
			this.view = view;
			keyStates = new HashSet<>(4);
			tiles = new BufferedImage[22];
			Random rnd = new Random();
			GraphicsConfiguration gc = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice()
					.getDefaultConfiguration();
			map = gc.createCompatibleImage(MAP_WIDTH * 128, MAP_HEIGHT * 128, Transparency.TRANSLUCENT);

			Graphics2D g2d = map.createGraphics();
			for (int row = 0; row < MAP_HEIGHT; row++) {
				for (int col = 0; col < MAP_WIDTH; col++) {
					int tile = rnd.nextInt(6);
					int x = col * 128;
					int y = row * 128;
					g2d.drawImage(getTile(tile), x, y, null);
				}
			}
			g2d.dispose();
		}

		protected BufferedImage getTile(int tile) {
			BufferedImage img = tiles[tile];
			if (img == null) {
				try {
					img = ImageIO.read(getClass().getResource("1.png"));
					img = img.getSubimage(0, 64, 128, 128);
					img = toCompatiableImage(img);
				} catch (IOException ex) {
					ex.printStackTrace();
				}
				tiles[tile] = img;
			}
			return img;
		}

		public void gameStart() {

			Thread gameThread = new Thread() {
				// Override run() to provide the running behavior of this thread.
				@Override
				public void run() {
					gameLoop();
				}
			};
			// Start the thread. start() calls run(), which in turn calls gameLoop().
			gameThread.start();
		}

		public void gameLoop() {
			while (!isGameFinished) {
				long startTime = System.nanoTime();
				updateGame();
				// renerGame();
				renderGame2();
				frameCount++;
				lastRenderTime = startTime;
				long duration = System.nanoTime() - startTime;
				int thisSecond = (int) (System.nanoTime() / 1000000000);
				if (thisSecond > lastSecondTime) {
					fps = frameCount;
					frameCount = 0;
					lastSecondTime = thisSecond;
				}

				if (duration < TARGET_TIME_BETWEEN_RENDERS) {

					duration = TARGET_TIME_BETWEEN_RENDERS - duration;
					long milli = TimeUnit.NANOSECONDS.toMillis(duration);
					try {
						Thread.sleep(milli);
					} catch (InterruptedException ex) {
					}
				}
			}
		}

		protected void updateGame() {
			if (keyStates.contains(KeyState.DOWN)) {
				camY -= Y_DELTA;
			} else if (keyStates.contains(KeyState.UP)) {
				camY += Y_DELTA;
			}
			if (camY < -(map.getHeight() - view.getHeight())) {
				camY = -(map.getHeight() - view.getHeight());
			} else if (camY > 0) {
				camY = 0;
			}
			if (keyStates.contains(KeyState.RIGHT)) {
				camX -= Y_DELTA;
			} else if (keyStates.contains(KeyState.LEFT)) {
				camX += Y_DELTA;
			}
			if (camX < -(map.getWidth() - view.getWidth())) {
				camX = -(map.getWidth() - view.getWidth());
			} else if (camX > 0) {
				camX = 0;
			}
		}

		protected void renderGame2() {
			BufferStrategy strategy = view.getBufferStrategy();
			do {
				// The following loop ensures that the contents of the drawing buffer
				// are consistent in case the underlying surface was recreated
				do {
					// Get a new graphics context every time through the loop
					// to make sure the strategy is validated
					Graphics g2d = strategy.getDrawGraphics();

					if (g2d != null) {
						g2d.drawImage(map, camX, camY, null);
						// Draw effects here...

						FontMetrics fm = g2d.getFontMetrics();
						g2d.setColor(Color.RED);
						g2d.drawString(Integer.toString(fps), 0, fm.getAscent());
						g2d.dispose();
					}

					// Repeat the rendering if the drawing buffer contents
					// were restored
				} while (strategy.contentsRestored());

				// Display the buffer
				strategy.show();

				// Repeat the rendering if the drawing buffer was lost
			} while (strategy.contentsLost());

		}

		protected void renerGame() {
			VolatileImage buffer = view.getOffscreenBuffer();
			if (buffer != null) {
				Graphics2D g2d = null;
				do {

					if (view.isIncompatiable(buffer)) {
						buffer = view.getOffscreenBuffer();
					}

					try {
						g2d = buffer.createGraphics();
					} finally {
						if (g2d != null) {
							g2d.drawImage(map, camX, camY, null);
							// Draw effects here...

							FontMetrics fm = g2d.getFontMetrics();
							g2d.setColor(Color.RED);
							g2d.drawString(Integer.toString(fps), 0, fm.getAscent());
							g2d.dispose();
						}
					}

				} while (buffer.contentsLost());

				view.show(buffer);
			}
		}

		public void addKeyState(KeyState state) {
			keyStates.add(state);
		}

		public void removeKeyState(KeyState state) {
			keyStates.remove(state);
		}

		protected BufferedImage toCompatiableImage(BufferedImage img) {
			GraphicsConfiguration gc = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice()
					.getDefaultConfiguration();
			BufferedImage compImg = gc.createCompatibleImage(img.getWidth(), img.getHeight(), img.getTransparency());
			Graphics2D g2d = compImg.createGraphics();
			g2d.drawImage(img, 0, 0, null);
			g2d.dispose();
			return compImg;
		}

	}

	public class AddState extends AbstractAction {

		private Engine engine;
		private KeyState state;

		public AddState(Engine engine, KeyState state) {
			this.engine = engine;
			this.state = state;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			engine.addKeyState(state);
		}

	}

	public class RemoveState extends AbstractAction {

		private Engine engine;
		private KeyState state;

		public RemoveState(Engine engine, KeyState state) {
			this.engine = engine;
			this.state = state;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			engine.removeKeyState(state);
		}

	}
}