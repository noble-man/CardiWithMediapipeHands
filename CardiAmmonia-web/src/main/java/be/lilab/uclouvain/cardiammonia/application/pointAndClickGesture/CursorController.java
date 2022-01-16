package be.lilab.uclouvain.cardiammonia.application.pointAndClickGesture;

import java.awt.Dimension;
import java.awt.Robot;
import java.awt.Toolkit;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.awt.event.InputEvent;


@RestController
@CrossOrigin(origins = "http://localhost:4200")
public class CursorController {

	private boolean buttonPressed = false;
	private boolean middleFingerAdded = false;
	private int robotClickCount = 0;
	//@RequestMapping(method=RequestMethod.PUT, value="/api/cursor/{x}/{y}")
	/*@RequestMapping(method=RequestMethod.PUT, value="/api/cursor")
	public void bckpMouseCursorControl(@PathVariable float x, @PathVariable float y) {
		
	System.out.println("I received something !");
   	 try {
            Robot robot = new Robot();
            robot.mouseMove((int)x * 100, (int)y * 100);
    
 
        } catch (Exception e) {
            e.printStackTrace();
        }
   }*/
	
	@RequestMapping("/api/cursor/{x}/{y}")
	public char[] MouseCursorControl(@PathVariable float x, @PathVariable float y) {
		
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		double screenWidth = screenSize.getWidth();
		
		int xCoord = ((int)(x * screenWidth));
		int yCoord = ((int)(y * screenWidth));
		
		xCoord = (int) (screenWidth - xCoord);
		
		System.out.println("screenWidth:" + screenWidth);
		System.out.println("xCoord:" + xCoord);
		System.out.println("yCoord:" + yCoord);
		
	   	 try {
	            Robot robot = new Robot();
	            robot.mouseMove(xCoord, yCoord);
	            
	           // if(buttonPressed && middleFingerAdded) 
	           // {
	            	middleFingerAdded = false;
	           // }
	           // else 
	            //{
	            	//robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
	            	buttonPressed = false;
	            	System.out.println("Released!");
	            //}
	    
	        } catch (Exception e) {
	            //e.printStackTrace();
	        }
	   	 
	   	
	   	 
	   	return new char[] {'o', 'k'};
	 }
	
	
	@RequestMapping("/api/click/{x}/{y}")
	public char[] MouseClick(@PathVariable float x, @PathVariable float y) {
		
		int xCoord = ((int)(x * 1000)) + 100;
		int yCoord = ((int)(y * 1000)) - 100;
		
	   	 try {
	            Robot robot = new Robot();
	            ///-->robot.mouseMove(xCoord, yCoord);
	            
	            if(robotClickCount == 0) {
	            //if(!buttonPressed) 
	            //{
	            	robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
	            	robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
	            	buttonPressed = true;
	            	middleFingerAdded = true;
	            	System.out.println("Button clicked");
	            	//Thread.sleep(2000);
	            //}
	            }
	            robotClickCount = (robotClickCount + 1) % 3;
	            
	        } catch (Exception e) {
	            //e.printStackTrace();
	        }
	   	 
	   	return new char[] {'o', 'k'};
	 }
	
	
	
}
