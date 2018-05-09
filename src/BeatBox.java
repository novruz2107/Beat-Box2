import javax.sound.midi.*;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.awt.event.*;

public class BeatBox {
	JFrame frame;
	Sequencer player;
	Sequence disc;
	Track track;
	ArrayList<JCheckBox> checkBoxes;
	JPanel panel;
	
	String[] insNames= {"Bass Drum", "Closed Hi-Hat", "Open Hi-Hat", "Acoustic Snare", "Crash Cymbal", "Hand Clap", "High Tom", 
			"HI Bongo", "Maracas", "Whistle", "Low Conga", "Cowbell", "Vibrasiap", "Low-mid Tom", "High Agogo", "Open Hi Conga"};
	int[] instruments= {35,42,46,38,49,39,50,60,70,72,64,56,58,47,67,63};
	
	
	public static void main(String[] args) {
		BeatBox alpha=new BeatBox();
		alpha.setUpGui();
	}
	
	public void setUpGui() {
		frame=new JFrame("BeatBox");
		checkBoxes=new ArrayList<JCheckBox>();
		Box eastBox=new Box(BoxLayout.Y_AXIS);
		Box westBox=new Box(BoxLayout.Y_AXIS);
		JButton start=new JButton("Start");
		JButton stop=new JButton("Stop");
		JButton upTempo=new JButton("Tempo up");
		JButton downTempo=new JButton("Tempo down");
		JButton extra=new JButton("Unselect boxes");
		westBox.add(start); westBox.add(stop); westBox.add(upTempo); westBox.add(downTempo); westBox.add(extra);
		
		start.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent a) {
				checkAndStart();
			}
		});
		
		stop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent a) {
				player.stop();
			}
		});
		
		upTempo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent a) {
				float tempo=player.getTempoFactor();
				player.setTempoFactor((float)(tempo*(1.03)));
			}
		});
		
		downTempo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent a) {
				float tempo=player.getTempoFactor();
				player.setTempoFactor((float)(tempo*0.97));
			}
		});
		
		
		
		for(int i=0; i<16; i++) {
			eastBox.add(new Label (insNames[i]));
		}
		frame.getContentPane().add(BorderLayout.EAST, westBox);
		frame.getContentPane().add(BorderLayout.WEST, eastBox);
		GridLayout grid=new GridLayout(16, 16);
		panel=new JPanel(grid);
		frame.getContentPane().add(BorderLayout.CENTER, panel);
		
		for(int i=0; i<256; i++) {
			JCheckBox c=new JCheckBox();
			c.setSelected(false);
			panel.add(c);
			checkBoxes.add(c);
		}
		extra.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent a) {
				for(int index=0; index<256; index++) {
					if(checkBoxes.get(index).isSelected()) {
						checkBoxes.get(index).setSelected(false);
					}
				}
			}
		});
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setBounds(50, 50,700, 500);
		frame.pack();
		frame.setVisible(true);
		SetupMidi();
	}
	
	public void checkAndStart() {
		int[] insBeats=null;
		disc.deleteTrack(track);
		track=disc.createTrack();
		
		for(int i=0; i<16; i++){
			insBeats=new int[16];
			int key=instruments[i];
			for(int j=0; j<16; j++) {
				JCheckBox jc=(JCheckBox) checkBoxes.get(j+(16*i));
				if(jc.isSelected()) {
					insBeats[j]=key;
				}
				else
					insBeats[j]=0;
			}
			makeTracks(insBeats);
			track.add(makeEvent (176, 1, 127, 0, 16));
		}
		track.add(makeEvent(192, 9, 1, 0, 15));
		try {
			player.setSequence(disc);
			player.setLoopCount(player.LOOP_CONTINUOUSLY);
			player.start();
			player.setTempoInBPM(120);
			
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}
		
	}
	
	public void makeTracks(int[] list) {
		for(int i=0; i<16; i++) {
			int key=list[i];
			if(key!=0) {
				track.add(makeEvent (144, 9, key,100, i));
				track.add(makeEvent (128, 9, key,100, i+1));
			}
				
		}
		
	}
	
	public MidiEvent makeEvent(int type, int channel, int note, int velocity, int tick) {
		MidiEvent event=null;
		
		try {
			ShortMessage sh=new ShortMessage();
		sh.setMessage(type, channel, note, velocity);
		event = new MidiEvent(sh, tick);
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}
		return event;
	}
	
	public void SetupMidi() {
		try {
			player=MidiSystem.getSequencer();
			player.open();
			disc=new Sequence(Sequence.PPQ, 4);
			track=disc.createTrack();
			player.setTempoInBPM(120);
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	
}
