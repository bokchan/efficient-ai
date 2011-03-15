package andreas.fourconnect.itu;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexTester {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		StringBuilder sb = new StringBuilder();
		try{
			// Open the file that is the first 
			// command line parameter
			FileInputStream fstream = new FileInputStream("C:\\RegexInput.txt");
			// Get the object of DataInputStream
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;
			//Read File Line By Line
			
			while ((strLine = br.readLine()) != null)   {
				// Print the content on the console
				if (strLine.length()< 40)
				sb.append(strLine);
			}
			//Close the input stream
			in.close();
		}catch (Exception e){//Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}
	
		
		String[] regs = sb.toString().split(";");
		StringBuilder regbuilder = new StringBuilder();
		
		Map<Integer, Integer> count = new HashMap<Integer, Integer>();
		
		for (String re : regs) {
			if (count.containsKey(re.length())) {
				Integer i = count.get(re.length());
				i++;
				count.put(re.length(), i);
			}
			else {
				count.put(re.length(), 1);
			}
			if (re.length() < 50) {
				regbuilder.append(re + "|");
			}
		}
		
		//Pattern p = Pattern.compile(regbuilder.toString());
		
		Pattern p = Pattern.compile(sb.toString());
		System.out.println(p.pattern());
		
		String s3 = "2,1,2,1,2,1,0,2,0,1,0,0,0,1,2,0,2,1,0,1,0,0,2,0,0,0,0,0,0,0,0,1,0,1,0,0,0,1,0,0,0,1;";
		String[] sa= s3.split(",");
		String[] state = new String[(int) Math.pow(7, 6)]; 
		for (int i = 0; i < Math.pow(7, 6); i++) {
			String regexex = "";
			StdRandom.shuffle(sa);
			for (int j = 0; j< 42; j++ ) {
				regexex += sa[j];
			}
			state[i] = regexex;
		}
		
		int matches = 0;
		long t1 = System.nanoTime();
		for (String r : state) {
			Matcher m = p.matcher(r); 
			while (m.find()) matches++;
		}
		
		System.out.println(count.toString());
		System.out.println("Match: " + ((System.nanoTime()-t1)/ 1000000));
		System.out.println("Matches: "+ matches);
	}

}
