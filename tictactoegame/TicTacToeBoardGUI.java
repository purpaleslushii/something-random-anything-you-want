package edu.sru.nao.version2gui.tictactoegame;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.ImageIcon;
import javax.swing.JButton;

import java.awt.FlowLayout;

import javax.swing.UIManager;

import edu.sru.nao.version2gui.connection.SynchronizedConnectDemo;
import edu.sru.nao.version2gui.gui.StatusFeed;
import edu.sru.nao.version2gui.tictactoegame.MattsVoiceStuff;

import java.awt.Dimension;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


/**
 * File:TicTacToeBoardGUI.java
 * @author Brian
 * edited by Emily Day
 * edited by Marijana Risovic
 * Description: open as a new window when game is selected and start
 * for testing the AIs.
 *
 */
// @Marijana : changed to JPanel
public class TicTacToeBoardGUI extends JPanel implements TurnListener, GameStatusListener {

	private JPanel contentPane;
	private JPanel settingsPanel;
	private XOButton[] btnArray = new XOButton[9];
	private Game game = new Game();
	private PlayerAIEnforcer playerAI;
	private MinMaxTreeAI mmTreeAI;
	private NAOResponse naoResponse;
	//private VoiceInteraction voiceInteraction;
	//private VoiceRecognition voiceRecognition;
	//private VoiceToSpeechTicTac vtpTicTac;
	//private VoiceInteraction voiceInteraction;
	private MattsVoiceStuff test;
	
	private boolean isChangable 		= true;
	private boolean isResetable			= true;
	private boolean useCustomCursor		= false; 	// @Marijana - dont use custom cursor before entering the game
	private boolean showAlerts			= true;
	//private boolean showChoicePanel		= false;	// @Marijana - should user choice (first play yes/no) panel be displayed 
	
	private Cursor xCursor;
	private Cursor oCursor;
	
	private String currentWord;
	
	private Markers curTurn = Markers.EMPTY;
	
	public static final String RESOURCE_DIR = "resources\\";
	public static final String XPNG_FILENAME = "xcursor.png";
	public static final String OPNG_FILENAME = "ocursor.png";
	
	private SynchronizedConnectDemo c;
	private String n;
	
	private TicTacToeChoicePanel userChoicePanel;
	private Markers userChoiceMarker = Markers.OUTOFBOUNDS;				// @Marijana - first play yes / no
	
	JFrame gameFrame = null;
	
	private boolean naoSpeaking = false;
	private final Lock lock = new ReentrantLock();
	private final Lock cursorLock = new ReentrantLock();

	/**
	 * Constructor for invoking a NAO to play the Tic Tac Toe game. Otherwise, mimics original constructor.
	 * @author Emily Day
	 * @param isResetable
	 * @param isChangable
	 * @param useCustomCursor
	 * @param showAlerts
	 * @param name
	 * @param connect
	 * @throws Exception 
	 */
	
	public TicTacToeBoardGUI(boolean isResetable, boolean isChangable, boolean showAlerts, String name, 
			SynchronizedConnectDemo connect, Markers userChoiceMarker, TicTacToeChoicePanel userChoicePanel) throws Exception {
		
		this();
		setUserChoicePanel(userChoicePanel, userChoiceMarker);
		naoResponse = new NAOResponse (name, connect);
		//voiceInteraction = new VoiceInteraction(name, connect, game, naoResponse);
		test = new MattsVoiceStuff(connect, name);
		//voiceRecognition = new VoiceRecognition(name, connect);
		//vtpTicTac = new VoiceToSpeechTicTac(connect.getSession(name), game, naoResponse);
		setChangable(isChangable);
		setResetable(isResetable);
		setShowAlerts(showAlerts);	
		setUserChoice(userChoiceMarker);
		
		c = connect;
		n = name;
	}

	/**
	 * Create the frame.
	 */
	public TicTacToeBoardGUI() {
		setPreferredSize(new Dimension(500, 700));
		
		xCursor = Toolkit.getDefaultToolkit().createCustomCursor(
				new ImageIcon(RESOURCE_DIR+XPNG_FILENAME).getImage(),
				new Point(15,15),"X Cursor");
		
		oCursor = Toolkit.getDefaultToolkit().createCustomCursor(
				new ImageIcon(RESOURCE_DIR+OPNG_FILENAME).getImage(),
				new Point(15,15),"O Cursor");
		
		game.addTurnListener(this);
		game.addGameStatusListener(this);
		
		//playerAI = new PlayerAIEnforcer(new JavaRulesAI(), Markers.X, game.getCurTurn(), game);
		mmTreeAI = new MinMaxTreeAI();
		mmTreeAI.setFileName(MinMaxTreeAI.DEFAULT_FILE_NAME);
		mmTreeAI.loadTree();
		playerAI = new PlayerAIEnforcer(mmTreeAI, Markers.X, game.getCurTurn(), game);
		
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setPreferredSize(new Dimension(500, 500));
		contentPane.setMinimumSize(new Dimension(400, 400));
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		
		add(contentPane);
		
		contentPane.setLayout(new BorderLayout(0, 0));
		
		settingsPanel = new JPanel();
		contentPane.add(settingsPanel, BorderLayout.NORTH);
		settingsPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		JButton btnReset = new JButton("Reset");
		btnReset.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onResetBtnClicked(e);
			}
		});
		settingsPanel.add(btnReset);
		
		if(!this.isResetable)
		{
			settingsPanel.setVisible(false);
		}
		
		JPanel panel_1 = new JPanel();
		panel_1.setMinimumSize(new Dimension(250, 250));
		panel_1.setPreferredSize(new Dimension(300, 300));
		contentPane.add(panel_1, BorderLayout.CENTER);
		panel_1.setLayout(new GridLayout(3, 3, 0, 0));
		
		for(int i=0;i<btnArray.length;i++)
		{
			btnArray[i] = new XOButton("Button "+i, i);
			final XOButton temp = btnArray[i];
			btnArray[i].setEnabled(this.isChangable);
			btnArray[i].addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					onBoardBtnClicked(e);
				}
				
			});
			panel_1.add(btnArray[i]);
		}
	}
	
	// @Marijana - start game with chosen marker
	
	public void startGame(Markers marker)
	{
		setUserChoice(marker);
		
		contentPane.setVisible(true);
		
		if(userChoicePanel != null)
			userChoicePanel.setVisible(false);
		
		setUseCustomCursor(true);	// @Marijana - use custom cursor when the game starts
		
		game.reset(marker);
		
		game.start();
	}
	
	public void openGameWindow()
	{
		// @Marijana - Initialize and open game window
		gameFrame = new JFrame();
		gameFrame.setContentPane(this);
		gameFrame.setTitle("Tic Tac Toe");
		ImageIcon tictactoe = new ImageIcon(System.getProperty("user.dir") + "\\libs\\tictactoe.png");
		gameFrame.setIconImage(tictactoe.getImage());
		
		// @Marijana- Dimension parameters 
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		gameFrame.setBounds(60, 0, 1280, 800);
		gameFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		gameFrame.addWindowListener(new java.awt.event.WindowAdapter() {
		    @Override
		    public void windowClosing(java.awt.event.WindowEvent windowEvent) {
		       if(userChoicePanel != null && userChoicePanel.getDemoPanel() != null)
		       {
		    	   userChoicePanel.getDemoPanel().resetTicTacToePanel();
		       }
		    }
		});
		
		gameFrame.setVisible(true);
		
		if(userChoiceMarker != Markers.OUTOFBOUNDS)
		{
			startGame(userChoiceMarker);
		}
		else
		{
			displayUserChoicePanel();
		}
	}

	/**
	 * called when end turn event from Game class
	 * NAO Robot states its turn
	 */
	@Override
	public void onOTurnEnd(Markers marker, int pos) {
		
		StatusFeed.updateStatus("On O turn end\n");
		
		// @Marijana - call on turn end to mark button
		onTurnEnd(marker, pos);
		
		// @Marijana - nao should say "my turn" in background thread (so that he doesn't block UI)
		(new NAOBackgroundTask()).SpeakMyTurn();
		
		/*
		 * try {
			MattsVoiceStuff robotPerson = new MattsVoiceStuff(c, n);
			String currentWord = "";
			
			// Start the thread...waste time until it starts.
			robotPerson.start();
			while(!robotPerson.isRunning()){}
			
			// While running, update the output window with story text.
			while(robotPerson.isRunning()){		
			}
			
			// Story over. Join threads and hide output window.
			robotPerson.join();
			robotPerson = null;
			//outputGUI.getFrame().setVisible(false);						
		} 
		catch (Exception e1) {
			MessageBox.show(e1.getMessage(), "Error");
			e1.printStackTrace();
		}
		*/
		
		//voiceRecognition.atOTurnEnd();
		//vtpTicTac.atOTurnEnd();
	}
	

	/**
	 * called when x end turn event from Game class
	 * NAO Robot states its choice and passes turn along to human player
	 */
	@Override
	public void onXTurnEnd(Markers marker, int pos) {
		StatusFeed.updateStatus("On X turn end\n");
		
		// @Marijana - nao should say its choice in background thread (so that he doesn't block UI)
		(new NAOBackgroundTask()).SpeakPos(pos);
		
		// @Marijana = TODO - turn end here should wait for nao speech to be done
		onTurnEnd(marker, pos);
		
		// @Marijana - nao should say "your turn" in background thread (so that he doesn't block UI)
		(new NAOBackgroundTask()).SpeakYourTurn();
	}

	/**
	 * called when O end turn event from Game class - paint board button
	 */
	@Override
	public void onTurnEnd(Markers marker, int pos) {
		(new NAOBackgroundTask()).MarkPosition(pos, marker);
	}

	/**
	 * called when turn start event from Game Class
	 */
	@Override
	public void onTurnStart(Markers marker) {
		// TODO Auto-generated method stub
		curTurn = marker;
		StatusFeed.updateStatus("Start turn! Marker: " + marker + "\n");
		setCustomCursor();
	}
	
	/**
	 * Sets the custom cursor for the GUI
	 */
	protected void setCustomCursor()
	{
		if(useCustomCursor)
		{
			if(curTurn == Markers.X)
			{
				this.setCursor(xCursor);
			}
			else if(curTurn == Markers.O)
			{
				this.setCursor(oCursor);
			}
		}
	}
	
	/**
	 * called when a button on the GUI is clicked
	 * @param e
	 */
	public void onBoardBtnClicked(ActionEvent e)
	{
		int pos=0;
		XOButton temp = (XOButton) e.getSource();
		pos = temp.getPosOnBoard();
		
		Markers lastTurn;
		lastTurn = curTurn;
		
		//onTurnEnd(curTurn, pos);
		
		int results = game.takeTurn(pos);
		
		StatusFeed.updateStatus("TakeTurn: " + lastTurn + "\n");
		
		
		StatusFeed.updateStatus("pos:" + pos + "!\n");
		StatusFeed.updateStatus("Button Clicked! \n");
		
	}
	
	/**
	 * Called when the reset button is clicked
	 * @param e
	 */
	public void onResetBtnClicked(ActionEvent e)
	{
		displayUserChoicePanel();
		
		setUseCustomCursor(false);	// @Marijana - don't use custom cursor when the game starts
		
		game.start();
	}
	
	public void setUserChoicePanel(TicTacToeChoicePanel choicePanel, Markers userChoiceMarker)
	{
		this.userChoicePanel = choicePanel;
		this.userChoicePanel.setMainWindow(this);
		add(userChoicePanel);
		
		if(userChoiceMarker == Markers.OUTOFBOUNDS)
			displayUserChoicePanel();
	}
	
	// @Marijana - create if null and display user choice (first play yes/no) panel
	public void displayUserChoicePanel()
	{
		if(userChoicePanel != null)
		{
			userChoicePanel.setVisible(true);
		}
		else
		{
			userChoicePanel = new TicTacToeChoicePanel(this, null);
			add(userChoicePanel);
		}
		
		setUseCustomCursor(false);
		contentPane.setVisible(false);
	}
	
	// @Marijana - user choice marker setter
	public void setUserChoice(Markers option)
	{
		userChoiceMarker = option;
	}
	
	// @Marijana - user choice marker getter
	public Markers getUserChoice()
	{	
		return userChoiceMarker;
	}

	/**
	 * Called on start of game
	 */
	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Called on the end of game
	 */
	@Override
	public void onEnd(Markers marker, int rowNum, boolean isTie) {
		// TODO Auto-generated method stub
		StatusFeed.updateStatus("Game Ended!\n");
		int[] pos;
		
		pos = game.getWonRowPos();
		
		for(int i=0;i<btnArray.length;i++)
		{
			btnArray[i].setEnabled(false);
		}
		
		if(pos != null)
		{
			for(int i=0; i<pos.length; i++)
			{
				btnArray[pos[i]].setBackground(Color.GREEN);
			}
		}
		/*NAO states it won*/
		if(Markers.X == marker)
		{
			setMessage("X won!!");
			naoResponse.naoWins();
		}
		/*NAO states it lost*/
		if(Markers.O == marker)
		{
			setMessage("O won!!");
			naoResponse.userWins();
		}
		/*NAO states it's a tie*/
		if(isTie)
		{
			StatusFeed.updateStatus("No body wins it is a tie!\n");
			setMessage("No body wins it is a tie!");
			naoResponse.tieGame();
		}
		//voiceRecognition.atGameEnd();
		//vtpTicTac.atGameEnd();
	}

	/**
	 * Called when the game is paused
	 */
	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Called on reset
	 */
	@Override
	public void onReset(Markers startMarker) {
		// TODO Auto-generated method stub
		this.setTitle("");
		for(int i=0;i<btnArray.length;i++)
		{
			btnArray[i].setEnabled(this.isChangable);
			btnArray[i].setDisplayState(Markers.EMPTY);
			btnArray[i].setBackground(UIManager.getColor("Button.background"));
			//btnArray[i].validate();
			btnArray[i].repaint();
		}
	}
	
	/**
	 * Display a dialog message
	 * @param msg
	 */
	public void setMessage(String msg)
	{
		this.setTitle(msg);
		System.out.println(msg);
		if(showAlerts)
		{
			JOptionPane.showMessageDialog(this, msg);
		}
	}
	
	/**
	 * set the title of the JFrame
	 */
	public void setTitle(String msg)
	{
		//super.setTitle("TicTacToe: "+msg);
	}
	
	/**
	 * Set whether the GUI is changable
	 * @param value
	 */
	public void setChangable(boolean value)
	{
		this.isChangable = value;
		for(int i=0;i<btnArray.length;i++)
		{
			btnArray[i].setEnabled(value);
		}
		
	}
	
	/**
	 * Get if the GUI is changable
	 * @return
	 */
	public boolean getChangable()
	{
		return this.isChangable;
	}
	
	/**
	 * Set if the GUI is resetable
	 * @param value
	 */
	public void setResetable(boolean value)
	{
		this.isResetable=value;
		settingsPanel.setVisible(value);
	}
	
	/**
	 * is the GUI resetable
	 * @return
	 */
	public boolean getResetable()
	{
		return this.isResetable;
	}
	
	/**
	 * Set the if the gui should use a custom cursor
	 * @param useCustomCursor
	 */
	public void setUseCustomCursor(boolean useCustomCursor)
	{
		this.useCustomCursor = useCustomCursor;
		if(this.useCustomCursor)
		{
			setCustomCursor();
		}
		else
		{
			this.setCursor(null);
		}
	}
	
	/**
	 * get if the GUI should use a custom cursor
	 * @return
	 */
	public boolean getUseCustomCursor()
	{
		return useCustomCursor;
	}
	
	/**
	 * Get the Game class
	 * @return
	 */
	public Game getGame()
	{
		return this.game;
	}
	
	/**
	 * Set the GUI is alertable
	 */
	public void setShowAlerts(boolean value)
	{
		showAlerts=value;
	}
	
	/**
	 * Get if the GUI is alertable
	 * @return
	 */
	public boolean getAlerts()
	{
		return showAlerts;
	}
	
public class NAOBackgroundTask {
		
		public void SpeakPos(int pos)
		{
			execute(new Runnable() {
		         public void run() {
		        	 lock.lock();
	 				 naoResponse.speakAIChoose(pos);
	 				 lock.unlock();
		    }});
		}
		
		public void SpeakMyTurn()
		{
			execute(new Runnable() {
		         public void run() {
		        	lock.lock();
	 				naoResponse.myTurn();
	 				lock.unlock();
		    }});
		}
		
		public void SpeakYourTurn()
		{
			execute(new Runnable() {
		         public void run() {
		        	lock.lock();
	 				naoResponse.yourTurn();
	 				lock.unlock();
		    }});
		}
		
		public void MarkPosition(int pos, Markers marker)
		{
			execute(new Runnable() {
		         public void run() {
		        	lock.lock();
		        	btnArray[pos].setDisplayState(marker);
		         	btnArray[pos].repaint();
	 				lock.unlock();
		    }});
		}
		
		// @Marijana - run given runnable in background
	    public void execute(Runnable r) {
	    	ExecutorService executor = Executors.newCachedThreadPool();
		    executor.submit(r);
	    }

	}

}