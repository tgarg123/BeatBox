 
package beatbox;
import java.awt.*;
import javax.swing.*;
import javax.sound.midi.*;
import java.util.*;
import java.awt.event.*;

public class BeatBox {
    JPanel mainPanel;
    ArrayList<JCheckBox> checkboxList;
    Sequencer seqR;
    Sequence seq;
    Track track;
    JFrame frame;
    
    String[] instNames={"Brass Drum","Closed Hi-Hat",
            "Open Hi-Hat","Acoustic Snare","Crash Cymbal",
            "Hand Clap","High Tom","Hi Bango","Maracas",
            "Whistle","Low Conga","Cowbell","Vibraslap","Low-Mid Tom",
            "High Agogo","Open Hi Conga"};
    int[] instruments={35,42,46,38,49,39,50,60,70,72,64,56,58,47,67,63};
    
    public static void main(String[] args) {
       new BeatBox().buildGUI();
    }
    
    public void buildGUI(){
        frame =new JFrame("Beat Box");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ImageIcon icon =new ImageIcon("E://beatbox.png");
        
        
        BorderLayout layout=new BorderLayout();
        JPanel bg=new JPanel(layout);
        
        checkboxList= new ArrayList<JCheckBox>();
        Box buttonBox=new Box(BoxLayout.Y_AXIS);
        
        JButton start =new JButton("Start");
        start.addActionListener(new MyStartListener());
        buttonBox.add(start);
        
        JButton stop=new JButton("Stop");
        stop.addActionListener(new MyStopListener());
        buttonBox.add(stop);
        
        JButton upTempo=new JButton("Tempo Up");
        upTempo.addActionListener(new MyUpTempoListener());
        buttonBox.add(upTempo);
        
        JButton downTempo=new JButton("Tempo Down");
        downTempo.addActionListener(new MyDownTempoListener());
        buttonBox.add(downTempo);
        
        JMenuBar menu=new JMenuBar();
        JMenu fileMenu=new JMenu("File");
        JMenuItem newMenu=new JMenuItem("New");
        JMenuItem saveMenu=new JMenuItem("Save");
        JMenuItem openMenu=new JMenuItem("Save");
        fileMenu.add(newMenu);
        fileMenu.add(openMenu);
        fileMenu.add(saveMenu);
        menu.add(fileMenu);
        frame.add(BorderLayout.NORTH,menu);
        
        
        
        Box nameBox =new Box(BoxLayout.Y_AXIS);
        for(int i=0;i<16;i++)
            nameBox.add(new Label(instNames[i]));
        
        bg.add(BorderLayout.EAST,buttonBox);
        bg.add(BorderLayout.WEST,nameBox);
        
        frame.add(bg);
        
        GridLayout grid=new GridLayout(16,16);
        grid.setVgap(1);
        grid.setVgap(2);
        mainPanel=new JPanel(grid);
        bg.add(BorderLayout.CENTER,mainPanel);
        
        for(int i=0;i<256;i++){
            JCheckBox c=new JCheckBox();
            c.setSelected(false);
            checkboxList.add(c);
            mainPanel.add(c);
            }
        
        setUpMidi();
        
        frame.setBounds(50,50,300,300);
        frame.pack();
        frame.setVisible(true);
        frame.setIconImage(icon.getImage());
    
    }
    
    public void setUpMidi(){
        try{
            seqR=MidiSystem.getSequencer();
            seqR.open();
            seq=new Sequence(Sequence.PPQ,4);
            track=seq.createTrack();
            seqR.setTempoInBPM(120);
        }
        catch(Exception e){
        e.printStackTrace();}
    
    }
    
    public void buildTrackAndStart(){
        int[] trackList=null;
        seq.deleteTrack(track);
        track=seq.createTrack();
        
        for(int i=0;i<16;i++){
            trackList=new int[16];
            int key=instruments[i];
            
            for(int j=0;j<16;j++){
            JCheckBox jc=(JCheckBox)checkboxList.get(j+(16*i));
            if(jc.isSelected())
                trackList[j]=key;
            else
                trackList[j]=0;
            }
            
            makeTracks(trackList);
            track.add(makeEvent(176,1,127,0,16));
    
        }
        track.add(makeEvent(192,9,1,0,15));
        try{
            seqR.setSequence(seq);
            seqR.setLoopCount(seqR.LOOP_CONTINUOUSLY);
            seqR.start();
            seqR.setTempoInBPM(120);
            
        }
        catch(Exception e){
        e.printStackTrace();}

    }
    
    public class MyStartListener implements ActionListener{
        public void actionPerformed(ActionEvent a){
            buildTrackAndStart();
        } 
    }
    
    public class MyStopListener implements ActionListener{
        public void actionPerformed(ActionEvent a){
            seqR.stop();
        } 
    }
    
    public class MyUpTempoListener implements ActionListener{
        public void actionPerformed(ActionEvent a){
            float tempoFactor=seqR.getTempoFactor();
            seqR.setTempoFactor((float)(tempoFactor * 1.03));
        } 
    }
    
    public class MyDownTempoListener implements ActionListener{
        public void actionPerformed(ActionEvent a){
             float tempoFactor=seqR.getTempoFactor();
            seqR.setTempoFactor((float)(tempoFactor * .97));
        } 
    }
    
    public void makeTracks(int[] list){
        for(int i=0;i<16;i++){
            int key=list[i];
            
            if(key!=0){
                track.add(makeEvent(144,9,key,100,i));
                track.add(makeEvent(144,9,key,100,i+1));
            }
        }
    }
    
    public MidiEvent makeEvent(int comd,int chan,int one,int two,int tick){
        MidiEvent event=null;
        try{
            ShortMessage a=new ShortMessage();
            a.setMessage(comd,chan,one,two);
            event=new MidiEvent(a,tick);
        }
        catch(Exception e){
        e.printStackTrace();}
        return event;
    
    }
    
    
}
