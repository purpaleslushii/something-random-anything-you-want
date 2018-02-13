package edu.sru.nao.version2gui.tictactoegame;

import java.awt.EventQueue;
import java.util.ArrayList;
import java.util.Arrays;

import com.aldebaran.qi.CallError;
import com.aldebaran.qi.helper.proxies.ALMemory;
import com.aldebaran.qi.helper.proxies.ALMotion;
import com.aldebaran.qi.helper.proxies.ALRobotPosture;
import com.aldebaran.qi.helper.proxies.ALTextToSpeech;
import com.aldebaran.qi.helper.proxies.ALSpeechRecognition;

import edu.sru.nao.version2gui.connection.SynchronizedConnectDemo;
import edu.sru.nao.version2gui.nao.DebugSettings;

/**
 * File: NAOResponse.java
 * @author Emily Day
 * This is the class that runs the NAO responses to the tic tac toe game
 * as it progresses. All methods here can be placed in game event listeners
 * so they occur at the correct times.
 *
 */

public class NAOResponse {

	private ALMemory memory;
	private ALMotion motion;
	private ALRobotPosture posture;
	private ALTextToSpeech speech;

	//private TicTacToeBoardGUI tictacGUI;


	/**
	 * Creates an instance of NAOResponse - allows a NAO to react during
	 * a tic tac toe game
	 * 
	 * @param name
	 * @param appName
	 * @param connect
	 * @throws Exception
	 */
	public NAOResponse(String name, SynchronizedConnectDemo connect)   {
		try {
			this.memory = new ALMemory(connect.getSession(name));
			this.motion = new ALMotion(connect.getSession(name));
			this.posture = new ALRobotPosture(connect.getSession(name));
			this.speech = new ALTextToSpeech(connect.getSession(name));
			motion.wakeUp();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * Switch statement takes in the AI choice for tic tac toe
	 * and verbalizes it.
	 * @param position
	 */
	public void speakAIChoose(int position){
		try {
			switch (position) {
			case 1:				
				speech.say("I choose square one!");
				raiseRightHand(1);
				lowerRightHand();
				break;
			case 2:
				speech.say("I choose square two!");
				raiseRightHand(2);
				lowerRightHand();
				break;
			case 3:
				speech.say("I choose square three!");
				raiseRightHand(3);
				lowerRightHand();
				break;
			case 4:
				speech.say("I choose square four!");
				raiseRightHand(4);
				lowerRightHand();
				break;
			case 5:
				speech.say("I choose square five!");
				raiseRightHand(5);
				lowerRightHand();
				break;
			case 6:
				speech.say("I choose square six!");
				raiseRightHand(6);
				lowerRightHand();
				break;
			case 7:
				speech.say("I choose square seven!");
				raiseRightHand(7);
				lowerRightHand();
				break;
			case 8:
				speech.say("I choose square eight!");
				raiseRightHand(8);
				lowerRightHand();
				break;
			case 0:
				speech.say("I choose square zero!");
				raiseRightHand(0);
				lowerRightHand();
				break;
			default:
				break;		
			}						
		}
		catch (Exception ex) 
		{
			System.out.println("Error: speakAIChoose");
			ex.printStackTrace();
		}
	}
	
	public void raiseRightHand(int position) throws CallError, InterruptedException {
		try {	
			if(position == 0) {
			motion.setBreathEnabled("All", false);
			motion.setStiffnesses("RArm", 1.0f);
			motion.setAngles("RShoulderPitch", -.5f, 0.5f);
			motion.setAngles("RShoulderRoll", 1f, 0.5f);
			//motion.setAngles("RWristYaw", -1.0f, 0.5f);
			motion.openHand("RHand");
			}
			if(position == 1) {
				motion.setBreathEnabled("All", false);
				motion.setStiffnesses("RArm", 1.0f);
				motion.setAngles("RShoulderPitch", -.5f, 0.5f);
				motion.setAngles("RShoulderRoll", 0f, 0.5f);
				//motion.setAngles("RWristYaw", 1.0f, 0.5f);
				motion.openHand("RHand");
				}
			if(position == 2) {
				motion.setBreathEnabled("All", false);
				motion.setStiffnesses("RArm", 1.0f);
				motion.setAngles("RShoulderPitch", -.5f, 0.5f);
				motion.setAngles("RShoulderRoll", -.35f, 0.5f);
				//motion.setAngles("RWristYaw", 0f, 0.5f);
				motion.openHand("RHand");
				}
			if(position == 3) {
				motion.setBreathEnabled("All", false);
				motion.setStiffnesses("RArm", 1.0f);
				motion.setAngles("RShoulderPitch", -.5f, 0.5f);
				motion.setAngles("RShoulderRoll", 1f, 0.5f);
				//motion.setAngles("RWristYaw", -1.0f, 0.5f);
				motion.openHand("RHand");
				}
			if(position == 4) {
				motion.setBreathEnabled("All", false);
				motion.setStiffnesses("RArm", 1.0f);
				motion.setAngles("RShoulderPitch", -.5f, 0.5f);
				motion.setAngles("RShoulderRoll", 0f, 0.5f);
				//motion.setAngles("RWristYaw", -1.0f, 0.5f);
				motion.openHand("RHand");
				}
			if(position == 5) {
				motion.setBreathEnabled("All", false);
				motion.setStiffnesses("RArm", 1.0f);
				motion.setAngles("RShoulderPitch", -.75f, 0.5f);
				motion.setAngles("RShoulderRoll", -.35f, 0.5f);
				//motion.setAngles("RWristYaw", -1.0f, 0.5f);
				motion.openHand("RHand");
				}
			if(position == 6) {
				motion.setBreathEnabled("All", false);
				motion.setStiffnesses("RArm", 1.0f);
				motion.setAngles("RShoulderPitch", -.1f, 0.5f);
				motion.setAngles("RShoulderRoll", 1f, 0.5f);
				//motion.setAngles("RWristYaw", -1.0f, 0.5f);
				motion.openHand("RHand");
				}
			if(position == 7) {
				motion.setBreathEnabled("All", false);
				motion.setStiffnesses("RArm", 1.0f);
				motion.setAngles("RShoulderPitch", -.1f, 0.5f);
				motion.setAngles("RShoulderRoll", 0f, 0.5f);
				//motion.setAngles("RWristYaw", -1.0f, 0.5f);
				motion.openHand("RHand");
				}
			if(position == 8) {
				motion.setBreathEnabled("All", false);
				motion.setStiffnesses("RArm", 1.0f);
				motion.setAngles("RShoulderPitch", -.1f, 0.5f);
				motion.setAngles("RShoulderRoll", -.35f, 0.5f);
				//motion.setAngles("RWristYaw", -1.0f, 0.5f);
				motion.openHand("RHand");
				}
		} catch (CallError | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		DebugSettings.printDebug(DebugSettings.COMMENT, " raised right hand.");
	}
	
	public void lowerRightHand() throws CallError, InterruptedException {
		try {	
			motion.setBreathEnabled("All", false);
			motion.setStiffnesses("RArm", 1.0f);
			motion.setAngles("RShoulderPitch", 1.0f, 0.5f);
			motion.setAngles("RWristYaw", -1.0f, 0.5f);
			motion.openHand("RHand");	
		} catch (CallError | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * Can be called at the end of an AI/NAO's turn
	 */
	public void yourTurn() {
		try {
			speech.say("Your turn!");
		} catch (CallError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * Can be called at the end of a human player's turn
	 */
	public void myTurn() {
		try {
			speech.say("My turn!");
		} catch (CallError e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	/**
	 * Called in the event of the NAO winning the game
	 */
	public void naoWins() {
		try {
			speech.say("I win! Let's play again.");
		} catch (CallError e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	/**
	 * Called in the event of the NAO losing the game
	 */
	public void userWins() {
		try {
			speech.say("Oh no! You win.");
		} catch (CallError e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	/**
	 * Called in the event of a tie.
	 */
	public void tieGame() {
		try {
			speech.say("It's a tie!");
		} catch (CallError e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}