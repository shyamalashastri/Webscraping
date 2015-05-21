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

import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.util.Date;
import java.util.Map;

public class web_scraping_main {

	private static final String Info = null;
	private static final CharSequence Info_flat = null;

	private web_scraping_main() {
	}

	public static List<String> extractLinks(String url) throws IOException {

		final ArrayList<String> result = new ArrayList<String>();
		Document doc;
		try{
			doc = Jsoup.connect(url).get();

		} catch(IOException e){
			return null;
		}






		List<Element> l1 = doc.getElementsByTag("h4");

		// href ...

		for (Element link : l1) {

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

		String mainSite = "http://www.nchcr.com";
		int num = 0;
		String innersite = "/viewdetails.aspx?JobID=";
		Map<String, Integer> specility_ptr = new HashMap<String,Integer>();
		Map<String, String> label_check = new HashMap<String,String>();
		String speciality_file = null;
		String title_name = "";
		String label_list = "";
		label_list =  "Job Id,Job Title,Specialty,Location,Recruiter,Inhouse,Recemail,Companyname,Recruitername,Phone,Seniority,Detailed Info,Community" ;

		

		try
		{

			FileWriter writer_main = new FileWriter("C:/Users/shyamala/Desktop/Info/Main.csv",true); //true tells to append data.
			writer_main.append(label_list);
			writer_main.append("\n");

			writer_main.flush();
			writer_main.close();

		}
		catch(IOException e)
		{
			e.printStackTrace();
		} 

        //EDIT 306148 correspons to the first job id (highest number) at http://www.nchcr.com/SearchJobs.aspx  - replace it with the highest while running.
		for (int i = 306148;i >= 0; i--)

		{

			int skip = 0;
			String site = mainSite + innersite + i;
			String Details = "";
			String Details1 = "";
			//System.out.println(site);
			//System.out.println(i);
			//System.out.println(num);
			List<String> links = web_scraping_main.extractLinks(site);

			if(links == null){
				continue;
			}

			//System.out.println(site);
			Document doc;
			String newline = System.getProperty("line.separator");
			try{
				doc = Jsoup.connect(site).get();
			}
			catch(HttpStatusException e)
			{
				//System.out.println(site+"does not exist");
				continue;
			}
			catch(SocketTimeoutException e1)
			{
				i++;
				continue;
			}

			Elements tables = doc.getElementsByClass("datatext");




			for (Element table : tables)
			{
				String new_string1 = table.text();
				if(new_string1.contains("We apologize, but this position is no longer available"))
				{
					//System.out.println("We apologize, but this position is no longer available");
					skip = 1;
					continue;
				}
				// Total number of ads				
				num++;

				Elements l1 = doc.getElementsByTag("span");

				for (Element pageLink : l1)

				{
					String label_id ="";
					String new_string = "";

					label_id= pageLink.id().replaceAll(",", "");
					new_string = pageLink.text().replaceAll(",", "");
					label_check.put(label_id, new_string);

				}
			}
			if(skip ==1)
			{
				skip = 0;
				continue;
			}
			Details = label_check.get("jobid_lb");
			Details = Details + "," + label_check.get("jobtitle");
			Details = Details + "," + label_check.get("specialty_lb");
			Details = Details + "," + label_check.get("city_lb")+"  "+label_check.get("state_lb");
			Details = Details + "," + label_check.get("recruiter_lb");
			Details = Details + "," + label_check.get("inhouse_lb");
			Details = Details + "," + label_check.get("recemail_lb");
			Details = Details + "," + label_check.get("companyname_lb");
			Details = Details + "," + label_check.get("recruitername_lb");
			Details = Details + "," + label_check.get("phone_lb");


			speciality_file = label_check.get("specialty_lb").toLowerCase();
			speciality_file = speciality_file.replaceAll("\\W+", "_");
			if((speciality_file.contains("chiefs")) || (speciality_file.contains("directors")) || (speciality_file.contains("heads")))
			{
				Details = Details +","+ "Senior";
			}
			else
			{
				Details = Details +","+ "Regular";
			}



			Details = Details + "," + label_check.get("jobdetail_lb");
			Details = Details + "," + label_check.get("community_lb");
			Details1 = Details;


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
				FileWriter writer = new FileWriter("csv/"+speciality_file+".csv",true);
				writer.append(Details);
				writer.append("\n");

				FileWriter writer_flat = new FileWriter("flat/"+speciality_file+".txt",true);

				writer_flat.append(label_check.get("jobdetail_lb"));
				writer_flat.append("\n");
				writer_flat.append(label_check.get("community_lb"));
				writer_flat.append("\n");
				FileWriter writer_main = new FileWriter("Info/Main.csv",true); //true tells to append data.

				writer_main.append(Details1);
				writer_main.append("\n");


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


			FileWriter writer_info = new FileWriter("Info/Info.txt",true); //true tells to append data.

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
