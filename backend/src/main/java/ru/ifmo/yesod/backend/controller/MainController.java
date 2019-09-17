package ru.ifmo.yesod.backend.controller;

import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.search.Scroll;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import ru.ifmo.elasticsearch.DocObor;
import ru.ifmo.elasticsearch.ElasticSearchYesod;
import ru.ifmo.elasticsearch.PreparationDocument;
import ru.ifmo.yesod.backend.model.DocumentItem;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

//import com.fasterxml.jackson.databind.ObjectMapper;





@Controller
public class MainController extends ElasticSearchYesod{
	
	private static final String HOST = "localhost";
    private static final int PORT_ONE = 9200;
    private static final int PORT_TWO = 9300;
    private static final String SCHEME = "http";
	private static final String INDEX = "doc";

    private RestHighLevelClient restHighLevelClient;
    private PreparationDocument preparationDocuments;
    final Scroll scroll = new Scroll(TimeValue.timeValueMinutes(1L));

	

	private static List<DocumentItem> viewResults =  new ArrayList<>();;
	private static String queryBuffer = "";
	public void searcher(String query) throws IOException {
		restHighLevelClient = makeConnection();
		viewResults.clear();
		int y =0;
		for(DocObor i : searchDocsOborByTextFromBody(query)) {
			viewResults.add(new DocumentItem(i,y));
			y++;			
		}
		

	}

    @RequestMapping(value = { "/", "/index" }, method = RequestMethod.GET)
    public String index(Model model) throws IOException {
    	return "index";
    }
    
    @RequestMapping(value = { "/search" }, method = RequestMethod.GET)
    public String search(Model model,
    		@RequestParam(value="query",required = false) String query,
    		@RequestParam(value="sort",required = false) String sortType,
    		@RequestParam(value="viewId",required = false) String viewId
    	) throws IOException{
    		if (query == null || query == ""){
    			return "index";
    		}
    		else {
    			if(!queryBuffer.equals(query)) {
					try {
						searcher(query);
						queryBuffer = query;
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
    			if (viewId !=null) {
    				model.addAttribute("body", viewResults.get(Integer.parseInt(viewId)).getBody());
    				model.addAttribute("name", viewResults.get(Integer.parseInt(viewId)).getName());
    				model.addAttribute("date", viewResults.get(Integer.parseInt(viewId)).getTS_1());
    				System.out.println("Open document");
    				//System.out.println(viewResults.get(Integer.parseInt(viewId)).getBody());
    				return "docView";
    			}
    			if(sortType == null || sortType.equals("1")){
					Collections.sort(viewResults, DocumentItem.bellCompare);
					model.addAttribute("results", viewResults);
					model.addAttribute("query", query);
					
				}

    			else if(sortType.equals("0")){
					Collections.sort(viewResults, DocumentItem.concurrenceCompare);
					model.addAttribute("results", viewResults );
					model.addAttribute("query", query);
				}
    			return "response";
    		}
    	 		
    }
    
}
