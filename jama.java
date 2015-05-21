import java.awt.image.ConvolveOp;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.InputStreamReader;
import java.io.ObjectInputStream.GetField;

import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.util.Date;
import java.util.Map;

//This is some change

import javax.print.Doc;

public class scrap3 {

	private static final String Info = null;
	private static final CharSequence Info_flat = null;

	private scrap3() {
	}

	public static List<String> extractLinks(String url) throws IOException {

		final ArrayList<String> result = new ArrayList<String>();
		Document doc;
		try{
			doc = Jsoup.connect(url).get();

		} catch(IOException e){
			return null;
		}






		Element byId = doc.getElementById("docHolder");
		Elements ele = byId.getElementsByTag("a");

		// href ...

		for (Element link : ele) {

			List<Element> childElement = link.getElementsByTag("a");

			Pattern p = Pattern.compile("href=\"(.*?)\"");

			Matcher m = p.matcher(childElement.toString());

			if (m.find()) {

				result.add(m.group(1)); // this variable should contain the link
				// URL

			}
		}
		
		return result;

	}

	public final static void main(String[] args) throws Exception {

		String mainSite = "http://jama.careers.adicio.com/jobs/search/results?rows=15&locationType=text&page=";
		int num = 0;
		String innersite = "";
		Map<String, Integer> specility_ptr = new HashMap<String,Integer>();
		Map<String, String> label_check = new HashMap<String,String>();
		String speciality_file = null;
		String title_name = "";
		String label_list = "title,name,address,datePosted,employmentType,occupationalCategory,description";

		

		try
		{

			FileWriter writer_main = new FileWriter("/Users/preetishshiroor/Documents/Code/AutoScraper/src/Main.csv",true); //true tells to append data.
			writer_main.append(label_list);
			writer_main.append("\n");

			writer_main.flush();
			writer_main.close();

		}
		catch(IOException e)
		{
			e.printStackTrace();
		} 

        
		for (int i = 1;i <= 427; i++)

		{

			int skip = 0;
			String site = mainSite + innersite + i;
			String Details = "";
			String Details1 = "";
			
			
		
			
			List<String> links = scrap3.extractLinks(site);

			if(links == null){
				continue;
			}
			
			
			for(String link : links)
			{
				Details = "";

				if(link == null)
					continue;
				
				link = "http://jama.careers.adicio.com" + link;
			//System.out.println(site);
			Document doc;
			String newline = System.getProperty("line.separator");
			try{
				doc = Jsoup.connect(link).get();
			}
			catch(HttpStatusException e)
			{
				//System.out.println(site+"does not exist");
				continue;
			}
			catch(SocketTimeoutException e1)
			{
				continue;
			}
			catch(IllegalArgumentException e2)
			{
				continue;
			}
			catch(Exception ee)
			{
				continue;
			}
			

			Elements spans = doc.select("span[itemprop]");
			
			String title = "";
			String name= "";
			String addressLocality= "";
			String addressRegion= "";
			String postalCode= "";
			String datePosted= "";
			String employmentType= "";
			String occupationalCategory= "";
			String description= "";
			
			
			for(Element spanItem : spans)
			{
				if(spanItem.attr("itemprop").toString().equals("title"))
					title = spanItem.text();
				else if(spanItem.attr("itemprop").toString().equals("name"))
					name = spanItem.text();
				else if(spanItem.attr("itemprop").toString().equals("addressLocality"))
					addressLocality = spanItem.text();
				else if(spanItem.attr("itemprop").equals("addressRegion"))
					addressRegion = spanItem.text();
				else if(spanItem.attr("itemprop").equals("postalCode"))
					postalCode = spanItem.text();
				else if(spanItem.attr("itemprop").equals("datePosted"))
					datePosted = spanItem.text();
				else if(spanItem.attr("itemprop").equals("employmentType"))
					employmentType += spanItem.text();
				else if(spanItem.attr("itemprop").equals("occupationalCategory"))
					occupationalCategory = spanItem.text();
				else if(spanItem.attr("itemprop").equals("description"))
					description = spanItem.text();
			}
			
		
			Details +=  title.replaceAll(",", "");
			Details +=  "," + name.replaceAll(",", "");
			Details +=  "," + addressLocality.replaceAll(",", "");
			Details +=   addressRegion.replaceAll(",", "");
			Details +=   postalCode.replaceAll(",", "");
			Details +=  "," + datePosted.replaceAll(",", "");
			Details +=  "," + employmentType.replaceAll(",", "");
			Details +=  "," + occupationalCategory.replaceAll(",", "");
			Details +=  "," + description.replaceAll(",", "");



			speciality_file = occupationalCategory.trim().replaceAll(",", "");
			speciality_file = speciality_file.replaceAll("\\W+", "_");

			if(speciality_file == "")
				continue;

			if(specility_ptr.containsKey(speciality_file))
			{
				int num_sp = specility_ptr.get(speciality_file);
				num_sp++;
				specility_ptr.put(speciality_file, num_sp);

			}
			else
			{
				int new_specialty_found = 1;
				for (Entry<String, Integer> e : specility_ptr.entrySet()) {
					String specialty_listed = e.getKey();
					if (speciality_file.contains(specialty_listed)) {  
						speciality_file = specialty_listed;
						int num_sp = specility_ptr.get(speciality_file);
						num_sp++;
						specility_ptr.put(speciality_file, num_sp);
						new_specialty_found = 0;
						break;
					}
				}
				if(new_specialty_found == 1){
					specility_ptr.put(speciality_file, 1);
					Details = label_list + newline + Details;
				}
			}



			try
			{
				FileWriter writer = new FileWriter("/Users/preetishshiroor/Documents/Code/AutoScraper/src/csv/"+speciality_file+".csv",true);
				writer.append(Details);
				writer.append("\n");

				FileWriter writer_flat = new FileWriter("/Users/preetishshiroor/Documents/Code/AutoScraper/src/flat/"+speciality_file+".txt",true);
				writer_flat.append(description);
				writer_flat.append("\n");
				writer_flat.append(occupationalCategory);
				writer_flat.append("\n");
//				FileWriter writer_main = new FileWriter("/Users/preetishshiroor/Documents/Code/AutoScraper/src/Main.csv",true); //true tells to append data.
//
//				writer_main.append(Details1);
//				writer_main.append("\n");


				writer.flush();
				writer.close();

				writer_flat.flush();
				writer_flat.close();

				//writer_main.flush();
				//writer_main.close();

			}
			catch(IOException e)
			{
				System.out.println("exception in fil");	
				e.printStackTrace();
			} 








			System.out.println(i);
		}
		System.out.println(mainSite);

		System.out.println(num);
		System.out.println("done");	


		try
		{


			FileWriter writer_info = new FileWriter("/Users/preetishshiroor/Documents/Code/AutoScraper/src/Info/Info.txt",true); //true tells to append data.

			writer_info.append("Website "+mainSite);
			writer_info.append("\n");

			Date date = new Date();

			writer_info.append("Date   "+date.toString());
			writer_info.append("\n");

			writer_info.append("Website "+mainSite);
			writer_info.append("\n");

			writer_info.append("Total number of posts =");
			writer_info.append(Integer.toString(num));
			writer_info.append("\n");
			writer_info.append("Specialty \t\tNo of posts\n");
			for (Entry<String, Integer> e : specility_ptr.entrySet()) {

				String specialty_listed = e.getKey();
				int num_sp = specility_ptr.get(specialty_listed);
				writer_info.append(specialty_listed);
				writer_info.append("\t");
				writer_info.append(Integer.toString(num_sp));
				writer_info.append("\n");

			}

			writer_info.flush();
			writer_info.close();


		}
		catch(IOException e)
		{
			e.printStackTrace();
		} 
		}
	}






}
