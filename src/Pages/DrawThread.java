package Pages;

/**
 * A simple thread class that redraws the images smoothly.
 * @author nvg081
 */
public class DrawThread extends Thread{

	/**
	 * The thumbnails that will be redrawn.
	 */
	private SFTThumbnail[] thumbs;
	
	/**
	 * The boolean flag to inidicate execution is to continue. 
	 */
	private boolean execute;
	
	/**
	 * The constructor for the DrawThread.
	 * @param thumbs - The collection of thumbnails that will be drawn.
	 */
	public DrawThread(SFTThumbnail[] thumbs){
		this.thumbs = thumbs;
		this.execute = true;
	}
	
	/**
	 * The method that will be run by the thread.
	 * @throws RuntimeException when the hint is not initialized.
	 */
	@Override
	public void run() throws RuntimeException{
		// Sleep for a tenth of a second to allow all image sizes to be determined.
		try {
			Thread.sleep(100);
		} 
		catch (InterruptedException e) {
			this.execute = false;
		}

		// Redraw the images smoothly.
		for (SFTThumbnail thumb : this.thumbs){
			if (!this.execute || this.isInterrupted()) {
				return;
			}
			thumb.resizeImageSmooth();
			thumb.repaint(0);
		}
	}
	
	/**
	 * Flag the thread to stop drawing the thumbnails.
	 */
	public void flag(){
		this.execute = false;
	}
}
