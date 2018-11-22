package mpx8;

import java.io.*;
import java.util.Arrays;

public class createKit
{

	private final byte HEADER1[]={0x4B,0x49,0x54,0x48,(byte)0x80,0x00,0x00,0x00};
	
//								  C    D    E    F    G    A    B    C
	private final byte KEYMAP[]={0x00,0x02,0x04,0x05,0x07,0x09,0x0b,0x0c};
	
	private final byte HEADER2[]={0x00,0x08,0x08,0x00,0x20,0x00,0x20,0x00,0x00,
							0x00,0x03,0x00,0x10,0x00,0x20,0x00,0x00,0x00,
							0x00,0x00,0x00,0x0C,0x18,0x00,0x00,0x0C,0x18,
							0x00,0x00,0x0C,0x18,0x00,0x00,0x0C,0x18,0x00,
							0x00,0x0C,0x18,0x00,0x00,0x0C,0x18,0x00,0x00,
							0x0C,0x18,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
							0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00}; 
	private final byte HEADER3[]={0x4b,0x49,0x54};
	private final int HEADER4LEN=50;

	
	private final byte INTERNAL1[]={0x4B,0x49,0x54,0x49,0x50,0x00,0x00};
	private final int INTERNAL2LEN=7;
	private final byte INTERNAL3[]={0x00,0x00,0x00,0x15,0x00,0x06,0x00,0x00,0x00};
	private final byte INTERNAL4[]={0x00,0x20,0x01};
	private final byte INTERNAL5[]={0x08,0x10,0x02};
	private final byte INTERNAL6[]={0x08,0x10,0x03};
	private final byte INTERNAL7[]={0x00,0x20,0x08};
	private final byte INTERNAL8[]={0x00,0x7F,0x09,0x00,0x00};
	

	private final int MIDI_C0=36;
	
	private final byte PAN_CENTER=4;

	private final byte TRIGGER_ONE_SHOT=0;
	
	private final byte EXTERNAL1[]={(byte)0xaa,0x00,0x00,0x00,0x00,0x00};
	private final byte EXTERNAL2[]={0x00,0x00,0x01,0x00,0x00,0x06,0x00,0x00,0x00};
 	
	
	private final String TESTNAME=new String("808 Kick");
	private final String EMPTYEXTERNAL=new String("<Empty> ");
	
	private byte workData[]=new byte[256];
	private byte outData[]=new byte[1536];
	private int outDataIdx;
	private String kitStr;
	private sampleUtils _theSampleUtil;
	
	public createKit(sampleUtils theSampleUtil)
	{
		_theSampleUtil=theSampleUtil;
	}


	public int createKitFile(int kitNo,String outDir)
	{	
		Arrays.fill(workData,(byte)0);
		kitStr=String.format("%03d",kitNo);
		String fileName=outDir+"KITS\\KIT"+kitStr+".KIT";
		System.out.println("File name:"+fileName);

		writeHeader(kitNo);
		boolean active=true;
		for(int i=0;i<8;i++)
			writeInternalBlock(i);
	
			
		for(int i=0;i<8;i++)
		{
			if(i<this._theSampleUtil.NumberFiles)
				active=true;
			else
				active=false;
			writeExternalBlock(i,active);
		}
			
		setCheckSum();
		try
		{
			FileOutputStream kitOut = new FileOutputStream(fileName);
			if(kitOut!=null)
			{
				System.out.println("writing to file");
				kitOut.write(outData);
				kitOut.close();
			}
		}
		catch (Exception outEx)
		{
			outEx.printStackTrace();
		}
		System.out.println("Complete");
				
		return 0;

	}

	private void setCheckSum()
	{
		int chkSum=0;
		for(int i=9;i<556;i++)
		{
			chkSum+=outData[i]&0xff;
			chkSum=chkSum&0xff;
		}
		outData[8]=(byte)chkSum;
	}
	private int writeHeader(int kitNo)
	{	
//zero index
		outDataIdx=0;

//write initial header		
		writeArray(HEADER1);
//next byte is the checksum, this has to be set after we've built up the rest of the file
		writeByte((byte)0);
//write out second bit of header
		writeArray(HEADER2);
//kit number
		writeArray(HEADER3);
		writeArray(kitStr.getBytes());
			
//padding
		writeZeros(HEADER4LEN);		
		
		return 0;
	}
	
	private void writeArray(byte Input[])
	{
		for(int i=0;i<Input.length;i++)
			outData[outDataIdx++]=Input[i];
	}
	
	private void writeByte(byte Byte)
	{
		outData[outDataIdx++]=Byte;
	}
	
	private void writeZeros(int len)
	{
		for(int i=0;i<len;i++)
			outData[outDataIdx++]=0;
	}
	
	private int writeInternalBlock(int blockNo)
	{
		String Name;
		byte external=0;
		int Volume=10;
		int Tune=4;
		int MIDINote=MIDI_C0;
		int Pan=PAN_CENTER;
		int Trigger=TRIGGER_ONE_SHOT;	
		int Reverb=0;
//header
		writeArray(INTERNAL1);
			
//internal/external flag
		writeByte(external);
			
//filler
		writeZeros(INTERNAL2LEN);
		Name=TESTNAME;
//length of name
		int nameLen=Name.length();
		writeByte((byte)nameLen);
//name
		writeArray(Name.getBytes());
		writeZeros((16-nameLen));
//more filler
		writeArray(INTERNAL3);
//volume
		writeByte((byte)Volume);
			
//a bit more filler
		writeArray(INTERNAL4);
//tune value
		writeByte((byte)Tune);
//some filler
		writeArray(INTERNAL5);
//pan
		writeByte((byte)Pan);
//more filler
		writeArray(INTERNAL6);
//trigger
		writeByte((byte)Trigger);
//filler
		writeArray(INTERNAL7);
//note
		writeByte((byte)(MIDINote+KEYMAP[blockNo]));
//filler
		writeArray(INTERNAL8);
//reverb
		writeByte((byte)Reverb);
//zero padding
		writeZeros(24);
		return 0;
	}

	private String formatWavName(String inName)
	{
//split at wav
		String[] splitArray = inName.split(".wav");
		String outName=splitArray[0];
//pad to 8 characters
		outName=String.format("%" + -8 + "s", outName).toUpperCase(); 
		return outName;
	}
	
	private int writeExternalBlock(int blockNo,boolean isExternal)
	{
		String Name;
		byte external;
		int Volume=10;
		int Tune=4;
		int MIDINote=36;
		int Pan=PAN_CENTER;
		int Trigger=TRIGGER_ONE_SHOT;	
		int Reverb=3;
		int ExtFlag;
		if(isExternal)
		{
			external=1;
			ExtFlag=0xaa;
		}
		else
		{
			external=0;
			ExtFlag=0xff;
		}
//header
		writeArray(INTERNAL1);
//internal/external flag
		writeByte(external);
		writeByte((byte)ExtFlag);
			
//filler
		writeArray(EXTERNAL1);
		if(isExternal)
			Name=formatWavName(_theSampleUtil.FinalName[blockNo]);
		else
			Name=EMPTYEXTERNAL;
		
//length of name
		int nameLen=Name.length();
		writeByte((byte)nameLen);
//name
		writeArray(Name.getBytes());
		writeZeros((16-nameLen));
//more filler
		writeArray(EXTERNAL2);
//volume
		writeByte((byte)Volume);
//a bit more filler
		writeArray(INTERNAL4);
//tune value
		writeByte((byte)Tune);
//some filler
		writeArray(INTERNAL5);
//pan
		writeByte((byte)Pan);
//more filler
		writeArray(INTERNAL6);
//trigger
		writeByte((byte)Trigger);
//filler
		writeArray(INTERNAL7);
//note
		writeByte((byte)MIDINote);
//filler
		writeArray(INTERNAL8);
//reverb
		writeByte((byte)Reverb);
//zero padding
		writeZeros(24);	
		return 0;
	}
}



