package mpx8;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class cfgFile
{
	public int readInCfgFile(String cfgFile) throws IOException
	{
		BufferedReader br=null;
		try 
		{
			br = new BufferedReader(new FileReader(cfgFile));
			
			if(br!=null)
			{
		        String line = br.readLine();
		        while (line != null) 
		        {
		        	System.out.println(line);
		        	line = br.readLine();
		        }
			}
		} 
		catch (FileNotFoundException e) 
		{
			e.printStackTrace();
		} catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally
		{
			if(br!=null)
			br.close();
		}
		return 0;
	}
}