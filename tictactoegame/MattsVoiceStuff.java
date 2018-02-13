package edu.sru.nao.version2gui.tictactoegame;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import com.aldebaran.qi.CallError;
import com.aldebaran.qi.Session;
import com.aldebaran.qi.helper.proxies.ALAnimatedSpeech;
import com.aldebaran.qi.helper.proxies.ALMemory;
import com.aldebaran.qi.helper.proxies.ALSpeechRecognition;
import com.aldebaran.qi.helper.proxies.ALTextToSpeech;

import edu.sru.nao.version2gui.awareness.Awareness;
import edu.sru.nao.version2gui.awareness.enums.EngagementModes;
import edu.sru.nao.version2gui.connection.SynchronizedConnectDemo;
import edu.sru.nao.version2gui.events.EventPair;
import edu.sru.nao.version2gui.events.NaoEvents;
import edu.sru.nao.version2gui.nao.DebugSettings;
import edu.sru.nao.version2gui.storytelling.IBroadcast;
import edu.sru.nao.version2gui.storytelling.storytelling.StoryElementNode;
import edu.sru.nao.version2gui.storytelling.xml.StoryFromXML;
import edu.sru.nao.version2gui.system.MemoryHelper;

/**
 * @author Justin Cather
 *
 */

public class MattsVoiceStuff extends Thread implements IBroadcast{
	
	private static final String STORY = "personToRobot";
	private static final String PORT = "9559";
	private static final String[] PHRASES = {
			"test" 
			};
	
	private ALSpeechRecognition rec;
	private ALAnimatedSpeech atts;
	private ALTextToSpeech tts;
	private ALMemory memory;
	private Awareness awareness;
	private Session session;
	private EventPair wordHeard;
	private EventPair personNear;	
	private EventPair pause;
	private EventPair stop;
	private EventPair resume;
	private StoryElementNode[] currentNode;
	//private Connect connect;
	
	private volatile boolean isPaused = false;
	private volatile boolean isListening = false;
	private volatile boolean nodeChanged = false;
	private volatile boolean isRunning = false;
	private volatile int currentOption;
	private volatile String currentHeard;
	private volatile States state;
			
	public MattsVoiceStuff(SynchronizedConnectDemo connect, String name) throws Exception{
		//this.session = new Session("tcp://" + ip + ":" + PORT);
		//connect = new Connect(ip);
		//connect.run();
		this.session = connect.getSession(name);
		
		
		currentNode = new StoryElementNode[1];
		wordHeard = new EventPair();
		personNear = new EventPair();
		
		//pause = new EventPair();
		//pause.eventName = NaoEvents.FrontTactilTouched;
		stop = new EventPair();
		stop.eventName = NaoEvents.FrontTactilTouched;
		resume = new EventPair();
		resume.eventName = NaoEvents.RearTactilTouched;
		
		// NAO stuff.
		awareness = new Awareness(session);
		rec = new ALSpeechRecognition(session);
		memory = new ALMemory(session);
		atts = new ALAnimatedSpeech(session);
		tts = new ALTextToSpeech(session);
		tts.setParameter("speed", 50f);
		tts.setParameter("pitchShift", 1.0f);
		
		
		// Set the vocabulary
		ArrayList<String> wordsForNAO = new ArrayList<String>(Arrays.asList(PHRASES));
		rec.setVocabulary(wordsForNAO, false);
		
		wordHeard.eventName = NaoEvents.WordRecognized;	
		personNear.eventName = NaoEvents.EngagementZones_PersonEnteredZone1;
		currentOption = -1;
	}
	
	/** Checks if the Nao is speaking currently.
	 * @return True if speaking, false if not.
	 */
	public boolean isTalking(){
		return MemoryHelper.isTalking(session);
	}
	
	/** Checks if the story telling is over or not.
	 * @return True if story is being told, false if not.
	 */
	public boolean isRunning(){
		return isRunning;
	}
	
	@Override
	public String getStoryText(){
		return currentNode[0].getStoryText();
	}
	
	@Override
	public String getOptions(){
		String options = "";
		Iterator<String> i = currentNode[0].getOptions();
		int count = 1;
		
		while(i.hasNext()){
			options += "Option " + count + ": " + i.next() + "\n";
			count++;
		}
		
		return options;
	}
	
	@Override
	public boolean storyChanged(){
		return nodeChanged;
	}
	
	@Override
	public void updatedStory(){
		nodeChanged = false;
	}
	
	@Override
	public String currentWord(){
		return MemoryHelper.getCurrentTTSWord(session);
	}
	
	/**
	 * The current state that the story FSM is in.
	 * @return The current state of the FSM.
	 */
	public States getCurrentState(){
		return state;
	}
	
	private boolean getIsListening(){
		return this.isListening;
	}
	
	private void setIsListening(boolean b) throws CallError, InterruptedException{
		this.isListening = b;
		this.rec.pause(!this.isListening);
	}
	
	public void run(){
		try {
			System.out.println("HERE");
			atts.setBodyLanguageMode(2);
			awareness.setEngagementMode(EngagementModes.FullyEngaged);
			awareness.setFirstZoneDistance(0.45f);
			
			// init the story.
			rec.subscribe(STORY);
			Thread.sleep(500);
			//rec.pause(true);
			this.setIsListening(false);
			Thread.sleep(1000);
					//System.out.println("HERE");
			state = States.Start;
			
			wordHeard.eventID = memory.subscribeToEvent(wordHeard.eventName, (value) -> {
				if (state == States.WaitingForInput || state == States.KillTime){
					DebugSettings.printDebug(DebugSettings.COMMENT, "Heard: " + value.toString() + " from " + Thread.currentThread());
					
					// Pause recognition
					//rec.pause(true);
					this.setIsListening(false);
					
					// getting data from event
					currentOption = -1;
					currentHeard = ((ArrayList) value).get(0).toString();
					float confidence = (float)((ArrayList) value).get(1);
					
					// check the input and set next state.

						switch (currentHeard) {
						case "test":
							currentOption = 0;
							atts.say("Thank you.");
							state = States.Test;
							break;

						default:
							currentOption = -1;
							state = States.WaitingForInput;
							break;
					}
					
				} // waiting for input //
				else {
					//rec.pause(true);
					this.setIsListening(false);
				}
				
			}); // end of NAO event //
			
			stop.eventID = memory.subscribeToEvent(stop.eventName, value -> {
				if (((Float) value) > 0.0) {
					synchronized (rec) {
						this.state = States.EndStory;
						
						if (this.isTalking()) {
							tts.stopAll();

							while (this.isTalking()) {
								Thread.sleep(500);
								System.out.println("Still talking...");
							}
						}

						if (this.getIsListening()) {
							this.setIsListening(false);
							Thread.sleep(500);
						}
					}
				}
			});
			
			resume.eventID = memory.subscribeToEvent(resume.eventName, value -> {
				if (((Float) value) > 0.0 && state == States.Pause) {
					state = States.Resume;
				}
			});
			
			isRunning = true;
			this.storyHandler();
		} catch (Exception e) {
			DebugSettings.printDebug(DebugSettings.ERROR, e.getMessage());
		}
	}
	
	/** FSM for handling story stage. An infinite loop until the end of the story is reached.
	 * @throws Exception
	 */
	private void storyHandler() throws Exception{
		while (isRunning) {		
			switch (state) {
				case Test:
					this.TestState();
					break;
				case WaitingForInput:
					this.waitForInput();
					break;
					
				case ReadOptions:
					this.readOptions();
					break;
					
				case KillTime:
					this.waitForInput();
					break;
					
				case EndStory:
					this.setIsListening(false);
					rec.unsubscribe(STORY);
					memory.unsubscribeToEvent(wordHeard.eventID);
					memory.unsubscribeToEvent(personNear.eventID);
					atts.say("AAAAAAAAA!");
					tts.resetSpeed();
					tts.setParameter("pitchShift", 0f);
					isRunning = false;
					
				default:
					break;

			}
		}
	}
	
	/** Reads the story text and checks to continue or not.
	 * @throws Exception
	 */
	private void TestState() throws Exception{
		if (state == States.Test) {
			
			atts.say("Loud and clear, fam!");
			/*atts.async().say(currentNode[0].repeatText());
			Thread.sleep(500);
			while(isTalking()){
				Thread.sleep(500);
			}*/
		}
	}
	
	private void readOptions() throws Exception{
		if (state == States.ReadOptions){
			atts.say(currentNode[0].repeatOptions());
			
		if (state == States.ReadOptions){
			synchronized (state) {
				state = States.WaitingForInput;
			}
		}
		}
	}

	private void waitForInput() throws Exception{	
		if (state == States.WaitingForInput) {
			DebugSettings.printDebug(DebugSettings.COMMENT, "Entering waitForInput with " + Thread.currentThread());
			//rec.pause(false);
			this.setIsListening(true);
			
			if (state == States.WaitingForInput) {
				synchronized (state) {
					state = States.KillTime;
				}
			}
		} 
		else if (state == States.KillTime){/* wasting cycles*/}
	}
}
