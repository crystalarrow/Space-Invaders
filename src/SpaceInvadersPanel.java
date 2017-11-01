import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.Timer;


public class SpaceInvadersPanel extends JPanel implements ActionListener {

	private static final int DEF_START_X = 100;
	private static final int DEF_START_Y = 700;
	public static final int BULLET_LIMIT = 10;
	public static final int SHIELD_WIDTH = 3;
	public static final int SHIELD_HEIGHT = 3;
	DefenderShip defShip = new DefenderShip();
	ArrayList<AlienShip> aliens = new ArrayList<AlienShip>();
	ArrayList<Bullet> alienBullets = new ArrayList<Bullet>(),defenderBullets = new ArrayList<Bullet>();
	ArrayList<Shield> shields = new ArrayList<Shield>();

	List<Shield> shieldList = new ArrayList<Shield>();

	BufferedImage defenderShip, alienShip, motherShip, bullet;

	int x, y;
	int numClicks = 0;
	int length = 1000;
	int width=800;
	Timer gameTimer;
	int count = 0;
	int defenderShipWidth, defenderShipHeight;
	int lives = 3;
	int alienShipWidth, alienShipHeight;
	int speed = 15;
	int bulletHeight, bulletWidth;
	int shieldWidth = 23;
	int shieldHeight = 18;
	boolean right = true;
	boolean gameoverDied = false;
	boolean gameoverWon = false;

	public SpaceInvadersPanel() {
		this.setPreferredSize(new Dimension(length,width));
		gameTimer = new Timer(150, this);
		defShip = new DefenderShip();
		defShip.setLocation(DEF_START_X, DEF_START_Y);

		gameTimer.start();

		try{
			defenderShip = ImageIO.read(new File ("DefenderShip.png"));
			alienShip = ImageIO.read(new File ("AlienShip.png"));
			motherShip = ImageIO.read(new File ("MotherShip.png"));
			bullet = ImageIO.read(new File ("bullet.png"));
		}catch(Exception e){

		}
		defenderShipWidth = defenderShip.getWidth()*2/5;
		defenderShipHeight = defenderShip.getHeight()*2/5;
		alienShipWidth = alienShip.getWidth()/12;
		alienShipHeight = alienShip.getHeight()/12;
		bulletWidth = 25;
		bulletHeight = 75;

		setUpKeyBindings();
		setUpDefender();
		setUpAliens();
		createShields();
	}

	private void setUpAliens() {
		for(int i = 125;i<=875;i+=75){
			for(int j=100;j<=300;j+=50){
				AlienShip as = new AlienShip();
				as.setLocation(i-alienShipWidth/2,j-alienShipHeight/2);
				aliens.add(as);
				//System.out.println(i-alienShipWidth/2);
			}
		}

	}

	private void setUpDefender() {
		this.defShip.setLocation(DEF_START_X, DEF_START_Y);

	}

	private void setUpKeyBindings() {
		this.getInputMap().put(KeyStroke.getKeyStroke("SPACE"),"fire");
		this.getInputMap().put(KeyStroke.getKeyStroke("LEFT"),"left");
		this.getInputMap().put(KeyStroke.getKeyStroke("RIGHT"),"right");

		this.getActionMap().put("fire",new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				defenderLaunchWeapon();
			}
		});
		this.getActionMap().put("right",new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setDefenderDir(1);// 1 moves right, 0 moves left
			}

		});
		this.getActionMap().put("left",new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setDefenderDir(0);// 1 moves right, 0 moves left
			}
			
		});
	}

	protected void setDefenderDir(int i) {
		if(i == 0) {
			defShip.x -=25;
			if(defShip.x < 0){
				defShip.x = 0;
			}
//			System.out.println("Ship will move left");
		}
		else if(i == 1) {
			defShip.x += 25;
			if(defShip.x > this.getWidth()-defenderShipWidth){
				defShip.x = this.getWidth()-defenderShipWidth;
			}
//			System.out.println(this.getWidth());
//			System.out.println("Ship will move right");
		}
		repaint();
	}

	protected void defenderLaunchWeapon() {
//		System.out.println("Launching weapon now!!");
		if(defenderBullets.size() >= BULLET_LIMIT){
			return;
		}
		
		Bullet b = new Bullet();
		b.setLocation(defShip.x+defenderShipWidth/2-bulletWidth/2, defShip.y-bulletHeight);
		defenderBullets.add(b);
	}

	private void alienLaunchWeapon(){
//		System.out.println(aliens.size());
		for(int i = 0; i < aliens.size(); i++){
			Random r = new Random();
			int rand = r.nextInt(50);
//			System.out.println(rand);
			if(rand == 0){
				Bullet b = new Bullet();
				b.setLocation(aliens.get(i).x +alienShipWidth/2-bulletWidth/2, aliens.get(i).y+alienShipHeight);
				alienBullets.add(b);
			}
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// what do you want to do every 10th of a second?
		//System.out.println("just clicked! "+numClicks);
		moveEverything();
		checkForCollision();
		alienLaunchWeapon();
		//System.out.println(alienBullets.size());
		repaint();
	}
	private void checkForCollision() {
		for(int i = 0; i < alienBullets.size(); i++){ //checks alien collision
			Rectangle a = new Rectangle (alienBullets.get(i).x,alienBullets.get(i).y, bulletWidth, bulletHeight);
			for(int j = 0; j < shields.size(); j++){ //checks if shield is hit
				Rectangle b = new Rectangle (shields.get(j).x,shields.get(j).y, shieldWidth, shieldHeight);
				if(a.intersects(b)){
					alienBullets.remove(i);
					shields.remove(j);
//					System.out.println("shield broke");
				}
			}
			
			Rectangle b = new Rectangle (defShip.x,defShip.y, defenderShipWidth, defenderShipHeight);
			if(a.intersects(b)){
				lives--;
				checkGameOver();
				alienBullets.remove(i);
//				System.out.println("lost a life, you have " +lives+ " lives left");
			}
		}
		for(int i = 0; i < defenderBullets.size(); i++){ //checks defender for alien collision
			Rectangle a = new Rectangle (defenderBullets.get(i).x,defenderBullets.get(i).y, bulletWidth, bulletHeight);
			for(int j = 0; j < aliens.size(); j++){
				Rectangle b = new Rectangle (aliens.get(j).x,aliens.get(j).y, alienShipWidth, alienShipHeight);
				if(a.intersects(b)){
					defenderBullets.remove(i);
					aliens.remove(j);
					checkGameOver();
//					System.out.println("alien dieded");
				}
			}
//			System.out.println(defenderBullets.size());
			for(int j = 0; j < shields.size(); j++){ //checks if shield is hit
				Rectangle b = new Rectangle (shields.get(j).x,shields.get(j).y, shieldWidth, shieldHeight);
				if(a.intersects(b)){
					defenderBullets.remove(i);
					shields.remove(j);
//					System.out.println("shield broke");
					break;
				}
			}
		}
	
	}
	
	private void checkGameOver(){
		if(lives == 0){
			this.gameTimer.stop();
			gameoverDied = true;
//			System.out.println("GAME OVER");
		}
		if(aliens.size() == 0){
			this.gameTimer.stop();
			gameoverWon = true;
//			System.out.println("YOU WIN");
		}
	}

	private boolean intersect(Rectangle a, Rectangle b){

		return a.intersects(b);
	}

	private void moveEverything() {
		moveAliens();
		moveBullets();
	}
	private void moveBullets(){
		for(int i = 0; i < defenderBullets.size(); i++){
			defenderBullets.get(i).y-=Bullet.VELOCITY;
			if(defenderBullets.get(i).y <= 0)
				defenderBullets.remove(i--);
		}

		for(int i = 0; i < alienBullets.size(); i++){
			alienBullets.get(i).y+=Bullet.VELOCITY;
			if(alienBullets.get(i).y >= length)
				alienBullets.remove(i--);
		}
	}
	private void moveAliens(){
		int maxX =0;
		int maxY =0;
		int minX =Integer.MAX_VALUE;
		int minY =Integer.MAX_VALUE;
		for(int i = 0; i < aliens.size(); i++){
			maxX = (aliens.get(i).x > maxX)? aliens.get(i).x:maxX;
			maxY = (aliens.get(i).y > maxY)? aliens.get(i).y:maxY;
			minX = (aliens.get(i).x < minX)? aliens.get(i).x:minX;
			minY = (aliens.get(i).y < minY)? aliens.get(i).y:minY;
		}

		if((right && maxX>=this.getWidth()-alienShipWidth) || (!right && minX<=0)){
			right = !right;
			count ++;
			if(count >= 6 ){
				for(int i = 0; i < aliens.size(); i++){
					aliens.get(i).y += 25;
				}
				count = 0;
			}
		}
		for(int i = 0; i < aliens.size(); i++){
			aliens.get(i).x += right? speed:-speed;
		}
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		drawBackground(g);
		drawDefenderShip(g);	
		drawAlienShip(g);
		drawLives(g);
		drawBullet(g);
		drawShields(g);
		if(gameoverDied || gameoverWon){
			drawBackground(g);
			drawGameOver(g);
		}
	}

	private void drawBackground(Graphics g){
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, length, width);
	}

	private void drawDefenderShip(Graphics g){
		g.drawImage(defenderShip, defShip.x, defShip.y, defenderShipWidth, defenderShipHeight, null);
	}

	private void drawAlienShip(Graphics g){
		for(int i = 0; i < aliens.size(); i++){
			g.drawImage(alienShip, aliens.get(i).x, aliens.get(i).y, alienShipWidth, alienShipHeight, null);
		}
	}
	
	private void drawLives(Graphics g){
		g.setColor(Color.WHITE);
		g.setFont(new Font("Arial", Font.PLAIN,20));
		g.drawString("Lives: " + lives, length/10*9+10, 17);
	}
	
	private void drawBullet(Graphics g){
		for(int i = 0; i < defenderBullets.size(); i++){
			g.drawImage(bullet, defenderBullets.get(i).x,defenderBullets.get(i).y, bulletWidth, bulletHeight, null);
		}
		for(int i = 0; i < alienBullets.size(); i++){
			g.drawImage(bullet, alienBullets.get(i).x,alienBullets.get(i).y, bulletWidth, bulletHeight, null);
		}
	}
	
	private void drawGameOver(Graphics g){
		String temp = null;
		Font f = new Font("Arial", Font.BOLD,40);
		g.setFont(f);
		
		if(gameoverDied){
			g.setColor(Color.RED);
			temp = "GAME OVER!!!";
		}
		if(gameoverWon){
			g.setColor(Color.YELLOW);
			temp = "YOU WIN!!!";
		}
		
		Rectangle2D r = f.getStringBounds(temp,new FontRenderContext(f.getTransform(),true,true));
		g.drawString(temp, length/2-(int)r.getWidth()/2,width/2-(int)r.getHeight()/2);
	}
	
	private void drawShields(Graphics g){
		for(int i = 0; i < shields.size(); i++){
			g.setColor(Color.GREEN);
			g.fillRect(shields.get(i).x,shields.get(i).y,shieldWidth,shieldHeight);
		}
	}

	private void createShields(){
		for(int i = length/5; i < length - 5; i+=length/5){
			for(int j = 0; j < SHIELD_WIDTH; j++){
				for(int k = 0; k < SHIELD_HEIGHT; k++){
					Shield temp = new Shield();
					temp.setLocation(i+j*shieldWidth-SHIELD_WIDTH*shieldWidth/2, 4*width/5+k*shieldHeight-SHIELD_HEIGHT*shieldHeight/2);
					shields.add(temp);
				}
			}
		}
	}
	
	public void start() {
//		System.out.println("Just started a new game...");
		this.gameTimer.start();

	}


}
