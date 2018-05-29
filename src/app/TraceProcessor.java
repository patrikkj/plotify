package app;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;

public class TraceProcessor implements Runnable {
	//Trace to evaluate
	private Trace trace;
	
	//Initialize JFX toolkit
	private final JFXPanel initToolkit = new JFXPanel();
	
	//Constructor
	public TraceProcessor(Trace trace) {
		this.trace = trace;
	}
	
	@Override
	public void run() {
		//Before lifting, tell me your name :)
		System.out.println("Processing: " + trace.getName() + " on " + Thread.currentThread());
		
		//Do some heavy lifting bruh
		trace.trace();
		
		//Print finish to console
		System.out.println("Finished: " + trace.getName() + " on " + Thread.currentThread());
		
		//Perform GUI Updates in FX Application Thread
		Platform.runLater(() -> trace.updateTrace());
	}

}
