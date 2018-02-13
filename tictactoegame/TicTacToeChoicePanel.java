package edu.sru.nao.version2gui.tictactoegame;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import edu.sru.nao.version2gui.demo.Demo;
import edu.sru.nao.version2gui.demo.TicTacInteraction;
import edu.sru.nao.version2gui.gui.RibbonMenu;
import edu.sru.nao.version2gui.gui.RibbonMenuDemoPanel;

/**
 * 
 * 
 * @author Marijana Risovic
 *
 * Tic Tac Toe choice panel (who's turn is first)
 */

public class TicTacToeChoicePanel extends JPanel {
	
	// @Marijana: instead of option dialog - show and hide user choice panel
	private JPanel userChoicePanel;
	private JLabel userChoiceTextLabel;
	private JButton userChoiceNoButton;
	private JButton userChoiceYesButton;
	
	private TicTacToeBoardGUI mainWindow = null;
	private RibbonMenuDemoPanel demoPanel = null;
	
	public TicTacToeChoicePanel(TicTacToeBoardGUI window, RibbonMenuDemoPanel demoPanel) {
		
		super();
		FlowLayout flowLayout = (FlowLayout) getLayout();
		flowLayout.setVgap(0);
		flowLayout.setHgap(0);
		setPreferredSize(new Dimension(170, 60));
		setMaximumSize(new Dimension(160, 60));
		
		this.mainWindow = window;
		this.demoPanel = demoPanel;
		
		userChoicePanel = new JPanel();
		userChoicePanel.setPreferredSize(new Dimension(170, 60));
		userChoicePanel.setMaximumSize(new Dimension(160, 60));
		add(userChoicePanel);
		
		userChoiceTextLabel = new JLabel("Would you like to play first?");
		userChoiceTextLabel.setVerticalTextPosition(SwingConstants.TOP);
		userChoiceTextLabel.setMinimumSize(new Dimension(160, 15));
		userChoiceTextLabel.setMaximumSize(new Dimension(160, 15));
		userChoiceTextLabel.setVerticalAlignment(SwingConstants.TOP);
		userChoiceTextLabel.setHorizontalAlignment(SwingConstants.CENTER);
		userChoiceTextLabel.setPreferredSize(new Dimension(160, 15));
		userChoicePanel.add(userChoiceTextLabel);
		
		userChoiceYesButton = new JButton("YES");
		userChoiceYesButton.setMinimumSize(new Dimension(50, 25));
		userChoiceYesButton.setMaximumSize(new Dimension(50, 25));
		userChoiceYesButton.setPreferredSize(new Dimension(60, 25));
		userChoicePanel.add(userChoiceYesButton);
		
		
		userChoiceYesButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				startGame(Markers.O);
			}
		});
		
		userChoiceNoButton = new JButton("NO");
		userChoiceNoButton.setMinimumSize(new Dimension(50, 25));
		userChoiceNoButton.setMaximumSize(new Dimension(50, 25));
		userChoiceNoButton.setPreferredSize(new Dimension(60, 25));
		userChoicePanel.add(userChoiceNoButton);
		
		userChoiceNoButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				startGame(Markers.X);
			}
		});
	}
	
	private void startGame(Markers marker)
	{
		// @Marijana 
		// - if mainWindow is null that means that option is chosen from demo panel (and marker is therefore valid) 
		// - if mainWindow is not null - that means mainWindow is panels parent and startGame is called after choosing the option
		//     -- in that case just start game
		
		if(this.mainWindow != null && marker != Markers.OUTOFBOUNDS)
		{
			if(demoPanel != null)
			{
				demoPanel.clearTicTacToePanel();
			}
			mainWindow.setUserChoice(marker);
			mainWindow.startGame(marker);
		}
		else
		{
			TicTacInteraction demo = (TicTacInteraction)RibbonMenu.getInstance().getDemo();
			
			if(demo != null)
			{
				try {
					demo.setUserChoice(marker, this);
					demo.executeAll();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	public RibbonMenuDemoPanel getDemoPanel()
	{
		return demoPanel;
	}
	
	public void setMainWindow(TicTacToeBoardGUI window)
	{
		this.mainWindow = window;
	}
}
