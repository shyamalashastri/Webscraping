import java.awt.image.ConvolveOp;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
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

		String mainSite = "http://www.nejmcareercenter.org";
        int num = 0;
		String innersite = "/jobs/";
		
		Map<String, Integer> specility_ptr = new HashMap<String,Integer>();
		Map<String, String> label_check = new HashMap<String,String>();
		
		try
		{
		   	        
			FileWriter writer_main = new FileWriter("C:/Users/shyamala/Desktop/Info/Main.csv",true); //true tells to append data.
			writer_main.append("TITLE,Employer,Posted,Location,Specialty,Position Type,Seniority,Detailed Info");
			writer_main.append("\n");
			
		    writer_main.flush();
		    writer_main.close();
		    
		}
		catch(IOException e)
		{
		     e.printStackTrace();
		} 
		
		/* Preetish     183 corresponds to how many pages are there in (http://www.nejmcareercenter.org/jobs/144/) this can be found out by tying a big number say
		(http://www.nejmcareercenter.org/jobs/1000/) and the page ends up with the last available number */
		
		for (int i = 1;i < 183; i++)

		{
			
			String site = mainSite + innersite + i;
			System.out.println(site);
			System.out.println(i);
			System.out.println(num);
			List<String> links = web_scraping_main.extractLinks(site);
			
			if(links == null){
				
				
				break;
			}
				

		 	//System.out.println(link);

				List<String> innerLinks = web_scraping_main.extractLinks(site);

				for (String innerLink : innerLinks)

				{
					System.out.println(innerLink);
					String rows = "";
					Document doc;
				    String newline = System.getProperty("line.separator");
				    try{
					doc = Jsoup.connect(mainSite + innerLink).get();
				    }
					catch(HttpStatusException e)
					{
					     
						continue;
					} 
				    Elements titles = doc.getElementsByClass("wbreak");
				    String speciality_file = null;
				    int sp_flag = 0;
				    int skip_entry = 0;
				    String title_name = "";
				    String label_list = "TITLE,Employer,Posted,Location,Specialty,Position Type,Seniority,Detailed Info" ;
				    label_check.put("employer", "");
				    label_check.put("posted", "");
				    label_check.put("location", "");
				    label_check.put("specialty", "");
				    label_check.put("position type", "");
				   			    
				    
				    String Details = "";
				    String Details1 = null;
				    String Info = "";
				    String Info_flat = "";
				    
					for (Element title : titles)
					{
						String new_string = title.text().replaceAll(",", "");
						
						if(title.text().toLowerCase().contains("information"))
						{
								Info = Info +  new_string;
								Info_flat = Info_flat + new_string;
								
						}
						else
						{
							Details = new_string;
							
						}
				
					}
					Elements l1 = doc.getElementsByClass("fieldWrapper");
					
					for (Element pageLink : l1)

					{

						Elements labels = pageLink.getElementsByClass("label");
						String label_match = null;
						Elements textFields = pageLink.getElementsByClass("textField");
                        
						for(Element label : labels)
						{
						
							
							String new_string = label.text().replaceAll(",", "");
							
											    
						    
						    if(label_check.containsKey(label.text().toLowerCase()))
							{
								label_match = label.text().toLowerCase();
								
								
									
							}
						    
					    
						}
						for(Element textField : textFields)
						{
						
							
							String new_string = textField.text().replaceAll(",", "");
							
													
							label_check.put(label_match, new_string);
														
							
							
						}
						
					}
					
					speciality_file = label_check.get("specialty").toLowerCase();
					speciality_file = speciality_file.replaceAll("\\W+", "_");
					
					Details = Details + "," + label_check.get("employer");
					Details = Details + "," + label_check.get("posted");
					Details = Details + "," + label_check.get("location");
					Details = Details + "," + label_check.get("specialty");
					Details = Details + "," + label_check.get("position type");
					
					if((speciality_file.contains("chiefs")) || (speciality_file.contains("directors")) || (speciality_file.contains("heads")))
			    	{
						Details = Details +","+ "Senior";
					}
					else
					{
						Details = Details +","+ "Regular";
					}
					
					Details = Details +","+ Info;
					Details1 = Details ;
					
					
					
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
						FileWriter writer = new FileWriter("C:/Users/shyamala/Desktop/csv/"+speciality_file+".csv",true);
					    writer.append(Details);
				        writer.append("\n");
				        
                        FileWriter writer_flat = new FileWriter("C:/Users/shyamala/Desktop/flat/"+speciality_file+".txt",true);
					   
                        writer_flat.append(Info_flat);
                        writer_flat.append("\n");
                        FileWriter writer_main = new FileWriter("C:/Users/shyamala/Desktop/Info/Main.csv",true); //true tells to append data.
						
                        writer_main.append(Details1);
						writer_main.append("\n");
						
				 
					    writer.flush();
					    writer.close();
					    
					    writer_flat.flush();
					    writer_flat.close();
					    
					    writer_main.flush();
					    writer_main.close();
					    
					}
					catch(IOException e)
					{
					     e.printStackTrace();
					} 
					
					// Total number of ads				
					num++;
					

				}
				
								
			//}
				System.out.println("end of "+i);
		}
		System.out.println(mainSite);
		
		System.out.println(num);

				
		try
		{
			
	        
            FileWriter writer_info = new FileWriter("C:/Users/shyamala/Desktop/Info/Info.txt",true); //true tells to append data.
			
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
