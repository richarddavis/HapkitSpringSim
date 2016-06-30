package springsim;


import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import controlP5.ControlEvent;
import controlP5.ControlP5;
import processing.core.PApplet;
import processing.core.PFont;
import processing.serial.Serial;

public class Main extends PApplet {
	
	static int HAND_BITS = 1;
	static int ANCHOR_BITS = 2;
	static int MOUSE_MODE = 0;
	static int HAPKIT_MODE = 1;
	static int SCALE_FACTOR = 60;
	int inputMode;

	//Container properties, dynamic generated from overall width, height
	int width = 1000+30;
	int height = 600;
	int spacing = (int) (width*0.02);
	
	//component widths
	int leftColWidth = (int) (width*0.22);
	int centerColWidth = (int) (width*0.45);
	int rightColWidth = (int) (width*0.22)+30;
	
	//designPalette coordinates
	int dPX = leftColWidth+(2*spacing);
	int dPY = spacing;
	int dPW = centerColWidth;
	int dPH = height-(spacing*2);
	
	//forceFeedbackOption coordinates
	int fFOX = spacing;
	int fFOY = spacing;
	int fFOW = leftColWidth;
	int fFOH = 200;
	
	//forceDisplayOutput coord
	int fDOX = (spacing*3)+leftColWidth+centerColWidth;
	int fDOY = spacing;
	int fDOW = rightColWidth;
	int fDOH = 420;
	
	//participantSelection coordinates
	int pSX = (spacing*3)+leftColWidth+centerColWidth;
	int pSY = fDOH+spacing;
	int pSW = rightColWidth;
	int pSH = 80;
	
	//physicsPlayground coord
	int pPX = spacing;
	int pPY = (spacing*2)+fFOH;
	int pPW = leftColWidth;
	int pPH = 100;
	
	//expSettings coord
	int eSX = (spacing*3)+leftColWidth+centerColWidth;
	int eSY = (spacing*2)+fDOH;
	int eSW = rightColWidth;
	int eSH = 140;
	
	//hapkitFeedbackPanel coord
	int hfx = spacing;
	int hfy = (spacing*3)+fFOH+pPH;
	int hfw = leftColWidth;
	int hfh = 155;
	
	//Components
	Hapkit hapkit;
	Canvas designCanvas;
	ForceDisplaySettings forceFeedbackOption;
	HapkitFeedbackSettings hapkitFeedbackPanel;
	//ExperimentSettings expSettings;
	SpringFactory springFactory;
	PhysicsPlayground physicsPlayground;
	ParticipantSelection participantSelection;
	
	List<Component> components = new ArrayList<Component>();
	
	ControlP5 cp5;
	
	int participantId;
	static ResearchData researchData;
	
	static public void main(String args[]) {
		   PApplet.main(new String[] { "springsim.Main" });
			
			Runtime.getRuntime().addShutdownHook(new Thread()
			{
			    @Override
			    public void run()
			    {
			        endProcedure();
			    }
			});
	}
	
	public void setup() {
		size(1030, 600);
		//frame.setResizable(false);
		background(255);
		
		String pID = JOptionPane.showInputDialog(null,
				  "Enter Participant ID",
				  "Participant ID",
				  JOptionPane.QUESTION_MESSAGE);
		
		String[] choices = { "Hapkit Condition", "Mouse Condition"};
	    String input = (String) JOptionPane.showInputDialog(null, "Select a Condition",
	        "Study 2 Condition", JOptionPane.QUESTION_MESSAGE, null,
	        choices, choices[0]);
	    
	    if(input.equals("Hapkit Condition")){
	    	inputMode = HAPKIT_MODE;
	    }else{
	    	inputMode = MOUSE_MODE;
	    }
		
		participantId = Integer.parseInt(pID);
		
		researchData = new ResearchData(participantId, inputMode);
		
		cp5 = new ControlP5(this);
		
		// change the default font to Verdana
		PFont p = createFont("Verdana",12); 
		cp5.setControlFont(p);
		  
		// change the original colors
		cp5.setColorForeground(0xffaa0000);
		cp5.setColorBackground(0xff660000);
		cp5.setColorLabel(0xffdddddd);
		cp5.setColorValue(0xffff88ff);
		cp5.setColorActive(0xffff0000);
		  
		if(inputMode == HAPKIT_MODE){
			String serInput = (String) JOptionPane.showInputDialog(null, "Available serial devices:",
			        "Serial Device", JOptionPane.QUESTION_MESSAGE, null,
			        Serial.list(), Serial.list()[0]);
			
			if (serInput == null) {
				JOptionPane.showMessageDialog(null, "No Hapkit Selected. Quitting now.");
				System.exit(0);
			}
			
			for(int i = 0; i < Serial.list().length; i++) {
				if(Serial.list()[i].equals(serInput)) {
					hapkit = new Hapkit(this, Serial.list(), i, researchData);
				    break;
				}
			}
		}
		
		//participantSelection = new ParticipantSelection(this, cp5, pSX, pSY, pSW, pSH, participantId);
		designCanvas = new Canvas(this, cp5, dPX, dPY, dPW, dPH, hapkit, researchData);
		forceFeedbackOption = new ForceDisplaySettings(this, cp5, fFOX, fFOY, fFOW, fFOH,  designCanvas);
		//expSettings = new ExperimentSettings(this, cp5, eSX, eSY, eSW, eSH);
		springFactory = new SpringFactory(this, cp5, researchData, designCanvas, fDOX, fDOY, fDOW, fDOH);
		physicsPlayground = new PhysicsPlayground(this, cp5, designCanvas, pPX, pPY, pPW, pPH);
		
		if(inputMode == HAPKIT_MODE){
			hapkitFeedbackPanel = new HapkitFeedbackSettings(this, cp5, hfx, hfy, hfw, hfh, hapkit, designCanvas.getSpringCollection());
			components.add(hapkitFeedbackPanel);
		}
		
		//components.add(participantSelection);
		components.add(designCanvas);
		//components.add(expSettings);
		components.add(springFactory);
		components.add(physicsPlayground);
		components.add(forceFeedbackOption);
	}

	public void draw() {
		background(255);
		stroke(255);
		
		for(int i=0; i<components.size(); i++){
			Component c = components.get(i);
			c.draw();
			c.step();	
		}
	}	

	public void mousePressed() {
		designCanvas.mousePressed();	
	}
	
	public void mouseReleased() {
		designCanvas.mouseReleased();
	}

	public void serialEvent(Serial p){
		try {
			hapkit.serialEvent(p);
		} 
		catch(RuntimeException e) {
		}
    }
	
	/**
	 * Generate CSV Log when program closes
	 * 
	 */
	public void stop() {
		System.out.println("stop: GENERATING LOG");
		researchData.generateCSVLog();
	} 
	
	public static void endProcedure(){
		System.out.println("endProcedure: GENERATING LOG");
		researchData.generateCSVLog();
	}
	
}



