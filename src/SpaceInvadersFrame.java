import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.plaf.basic.BasicMenuBarUI;


public class SpaceInvadersFrame extends JFrame implements ActionListener{

	JMenuItem newItem;
	
	public SpaceInvadersFrame(){
		super("Space Invaders");
		setLayout(new BorderLayout());
		//createMenus();
		this.setDefaultCloseOperation(this.EXIT_ON_CLOSE);
		this.setResizable(true);
		SpaceInvadersPanel sip = new SpaceInvadersPanel();
		this.add(sip);
		pack();
	}

	private void createMenus() {
		// TODO Auto-generated method stub
		JMenuBar menuBar = new JMenuBar();
		menuBar.setVisible(true);
		JMenu fileMenu = new JMenu("File");
		newItem = new JMenuItem("New");
		
		newItem.addActionListener(this);
		
		menuBar.add(fileMenu);
		fileMenu.add(newItem);
		
		//menuBar.setUI(new BasicMenuBarUI());

		setJMenuBar(menuBar);
		//add(menuBar, BorderLayout.NORTH);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if(e.getSource() == newItem){
			System.out.println("works");
		}
	}
}
