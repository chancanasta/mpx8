package mpx8;

import java.io.File;
import java.io.IOException;


//import tester.pack.SimpleAdd;

public class MainClass {

	
	public static void main(String[] args) {

		cfgFile theCfgFile=new cfgFile();
		sampleUtils theSampleUtils=new sampleUtils();
//read WAVs from one directory and copy them to the root of the SD card
		
		if(args.length<3)
		{
			
			if(args.length==2 && args[0].equals("-file"))
			{
				try {
					theCfgFile.readInCfgFile(args[0]);
				} catch (IOException e) {
					e.printStackTrace();
				}
				return;
			}
			else
			{
				System.out.println("Usage is: ");
				System.out.println("  <kit no> <directory of samples> <target drive> [del - delete existing samples from root of target drive]");
				System.out.println("\nIt will copy 8 samples from the source directoy into the root of the target drive");
				System.out.println("convert them to a format readable by the MPX8 and create a kit file of number <kit no>");
				System.out.println("\ni.e.\n   1 c:\\nofdrive d");
				System.out.println("will copy 8 samples from c:\\nofdrive to the root of the d drive and create a KIT001 file");
			}
			return;
		}
		
	
		String outDir=args[2]+":\\";
//check for del flag		
		if(args.length==4 && args[3].equals("del"))
		{
			System.out.println("\nDeleting existing samples in root of target drive : "+outDir);
			File folder = new File(outDir);
			File fList[] = folder.listFiles();
			for (int i = 0; i < fList.length; i++)
			{
				if(fList[i].getName().endsWith(".wav"))
					fList[i].delete();
			}
		}
		
		System.out.println("\ncopying samples from "+args[1]+" to SD card "+outDir+" creating kit number :"+args[0]);
		theSampleUtils.readDirectory(args[1],outDir);
		for(int i=0;i<8;i++)
		{
			System.out.println(" Sample "+(i+1) +" "+ theSampleUtils.getInName(i)+" -> "+ theSampleUtils.getSampleName(i));
		}
		
	
		createKit theKit=new createKit(theSampleUtils);
		theKit.createKitFile(Integer.parseInt(args[0]),outDir);	
	}

}
