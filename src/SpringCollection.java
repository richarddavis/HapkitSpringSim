

import java.lang.reflect.Constructor;
import java.util.ArrayList;

import processing.core.PApplet;
import shiffman.box2d.Box2DProcessing;

public class SpringCollection {
	
	ArrayList<SpringInterface> springs;
	SpringInterface activeSpring; 
	ResearchData rData;
	Hapkit hapkit;
	
	public SpringCollection(ResearchData rData, Hapkit _hapkit){
		this.springs = new ArrayList<SpringInterface>();
		this.rData = rData;
		this.hapkit = _hapkit;
	}
	
	public boolean add(SpringInterface s){
		return springs.add(s);
	}
	
	public float getActiveForce() {
		return this.activeSpring.getForce();
	}
	
	public void displayForces(boolean display_on) {
		for (SpringInterface s : springs) {
			s.displayForce(display_on);
		}
	}
	
	public void displayStiffness(boolean b) {
		for (SpringInterface s : springs) {
			s.displayK(b);
		}
	}
	
	public void draw() {
		for (SpringInterface s : springs) {
			if(s != null){
				s.draw();
			}
		}
	}
	
	public void spaceSpringsProportionately(int w){
		for(int i=0;i<springs.size();i++){
			if(springs.get(i) !=null){
				int interval_width = (w/springs.size());
				int x = (interval_width*i)+(interval_width/2);
				springs.get(i).setX(x);
			}
		}
	}
	
	public void setActive(SpringInterface s){
		if(activeSpring == null){
			activeSpring = s;
			activeSpring.getHand().swapIcon();
		}else{
			activeSpring.getHand().swapIcon();
			s.hand.swapIcon();
			activeSpring = s;
		}
		if(rData.getInputMode() == ResearchData.HAPKIT_MODE){
			System.out.println("Setting Hapkit k-constant to:");
			System.out.println(s.getK());
			this.hapkit.setKConstant(s.getK());
			// MAKES ALL OTHER SPRING ACT NORMALLY AGAIN:
			destroyOldHapkitJoints();
		}
	}
	
	public void updateActiveSpring(int mx, int my, boolean pressed, Hapkit hapkit) {
		for (SpringInterface s : springs) {
			if (s!= null && s.getHand().contains(mx, my)) {
				this.setActive(s);
				rData.logEvent(s.getK(), -1, "SWITCHING BETWEEN SPRINGS");
				break;
			}
		}
		
		if(rData.getInputMode() == ResearchData.MOUSE_MODE){
			this.activeSpring.mouseUpdate(mx, my, pressed);
		}else{
			// Why was the following line included?
			//this.activeSpring.hapkitUpdate(my);
		}
	}
	
	private void destroyOldHapkitJoints() {
		for (SpringInterface s : springs) {
			if(s != null && !s.equals(activeSpring)){
				s.hand.destroy();
			}
		}
	}

	public void updateActiveSpringYPosition(double hapkitPos) {
		int currentY = (int) this.activeSpring.getY()+this.activeSpring.originalLen+10;
		int newY = (int) (currentY + hapkitPos);
		//System.out.println(hapkitPos);
		this.activeSpring.hapkitUpdate(newY);	
	}

	public void delete(int value) {
		springs.remove(value);
		springs.add(value, null);
	}

	public void add(int x_i, SpringInterface s) {
		springs.add(x_i, s);
	}

}
