package Listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;

import javax.swing.Timer;

import Control.InterfaceController;

public class ResizeListener implements ComponentListener, WindowStateListener{

	/**
	 * The controller for the application.
	 */
	private InterfaceController controller;
	
	/**
	 * The basic constructor for the listener.
	 */
	public ResizeListener(InterfaceController controller) {
		this.controller = controller;
	}
	
	@Override
	public void componentHidden(ComponentEvent arg0) {}

	@Override
	public void componentMoved(ComponentEvent arg0) {}

	/**
	 * The listener that will enable logging.
	 */
	private ActionListener resizeListener = new ActionListener() {
		public void actionPerformed(ActionEvent event){
			controller.setDoNotLog(false);
		}
	};
	
	/**
	 * The timer that will enable logging.
	 */
	private Timer resizeTimer = new Timer(200, this.resizeListener);
	
	/**
	 * Do not allow logging as a result of the frame being resized. 
	 * @param arg0 - The component event.
	 */
	@Override
	public void componentResized(ComponentEvent arg0) {
		// Do not allow logging while the frame is being resized. 
		this.controller.setDoNotLog(true);
		
		// Enable logging when it is safe to do so.
		this.resizeTimer.setRepeats(false);
		if (this.resizeTimer.isRunning()) {
			this.resizeTimer.restart();
		}
		else {
			this.resizeTimer.start();
		}
		
		// Do not allow the page to change as a result of resizing.
		this.stateTimer.setRepeats(false);
		if (this.stateTimer.isRunning()) {
			this.stateTimer.restart();
		}
		else {
			this.stateTimer.start();
		}
	}

	@Override
	public void componentShown(ComponentEvent arg0) {}

	/**
	 * The listener that will show the correct page when the state changes.
	 */
	private ActionListener stateListener = new ActionListener(){
		public void actionPerformed(ActionEvent event){
			controller.showCurrentPage();
		}
	};
	
	/**
	 * The listener that will show the correct page when the state is changed.
	 */
	private Timer stateTimer = new Timer(10, this.stateListener);
	
	/**
	 * Do not allow logging as a result of the state change.
	 * @param arg0 - The window event.
	 */
	@Override
	public void windowStateChanged(WindowEvent arg0) {
		// Do not allow logging while the frame's state is changing. 
		this.controller.setDoNotLog(true);

		// Enable logging when it is safe to do so.
		this.resizeTimer.setRepeats(false);
		if (this.resizeTimer.isRunning()) {
			this.resizeTimer.restart();
		}
		else {
			this.resizeTimer.start();
		}
		
		// Do not allow the page to change as a result of the state changing.
		this.stateTimer.setRepeats(false);
		if (this.stateTimer.isRunning()) {
			this.stateTimer.restart();
		}
		else {
			this.stateTimer.start();
		}
	}
}
