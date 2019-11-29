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
import ru.ifmo.yesod.backend.controller.calcTfIdf;
import ru.ifmo.yesod.backend.db.DataCache;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;



import ru.ifmo.yesod.backend.controller.calcConcurrense;
import ru.ifmo.yesod.backend.controller.calcBell;
//import com.fasterxml.jackson.databind.ObjectMapper;



@Controller
public class MainController extends ElasticSearchYesod{
	

    
	private RestHighLevelClient restHighLevelClient;
    private PreparationDocument preparationDocuments;
    
    //flags for inverse sorting
    private boolean flagBellSort=false;
    private boolean flagConcurSort=false;
    private boolean flagTfIdfSort=false;
	
    //variables, that hold result of searching
    private static List<DocumentItem> viewResults =  new ArrayList<>();
	private static String queryBuffer = "";
    
    //connecting to base with data cache for Tf-Idf calculating 
    private static	DataCache cacher = new DataCache("index.db");
    static	List<Double> idf = cacher.getAllIdfData();
    static	List<String> docIds = cacher.getAllDocIdData();
    static	List<String> index = cacher.getAllIndexData();
    //static List<Double> UpdateConcurrence = new ArrayList<>();
	
	/**
	 * Function for getting search result from elastic base
	 * @param query
	 * @throws IOException
	 * @throws ParseException
	 */
	private void searcher(String query) throws IOException, ParseException {
		restHighLevelClient = makeConnection();
		viewResults.clear();
		String[] queryTokens = (query.toLowerCase()).split(" ");
		String calcQuery = "";
		if (queryTokens.length == 1) {
			calcQuery =  queryTokens + " " + queryTokens;
		}
		else if(queryTokens.length > 2) {
			calcQuery = queryTokens[0] + " " + queryTokens[1];
		}
		else {
			calcQuery = queryTokens[0] + " " + queryTokens[1];
		}
		int y =0;
		DocumentItem tmpItem;
		double tmp;
		
		for(DocObor i : searchDocsOborByTextFromBody(query)) {
			tmpItem = new DocumentItem(i,y);
			tmp = calcBell.solve(i.getBody(), calcQuery, 10);			
			tmpItem.setPointsBell((tmp==Double.NaN)? 0 :(double)Math.round(tmp * 1000)/1000);
			tmp = calcConcurrense.solve(i.getBody(), calcQuery, 0.3);			
			tmpItem.setPontsConcurrence((tmp==Double.NaN)? 0 :(double)Math.round(tmp * 1000)/1000);
			tmp = calcTfIdf.solve(calcQuery,i.getDocId(),i.getBody(),idf, docIds,index,cacher);
			tmpItem.setPointsTfIdf((tmp==Double.NaN)? 0 :(double)Math.round(tmp * 1000)/1000);
			//UpdateConcurrence.add(calcConcurrense.solve1(i.getBody(), query, 0.1));
			viewResults.add(tmpItem);
			y++;
		}

	}
	/**
	 * Getting index page
	 */
    @RequestMapping(value = { "/", "/index" }, method = RequestMethod.GET)
    public String index(Model model) throws IOException {
    	
    	return "index";
    }
    
    /**
     * If we have a request, this function process it and parameters 
     * @param model Spring notation for link to model
     * @param query 
     * @param sortType number metric from view for sorting
     * @param viewId  number of each instance of response for sorting in view
     * @return
     * @throws IOException
     */
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
					} catch (ParseException e) {
						
						e.printStackTrace();
					}
					
				}
    			if (viewId !=null) {
    				model.addAttribute("body", viewResults.get(Integer.parseInt(viewId)).getBody());
    				model.addAttribute("name", viewResults.get(Integer.parseInt(viewId)).getName());
    				model.addAttribute("date", viewResults.get(Integer.parseInt(viewId)).getTs());
    				System.out.println("Open document");

    				return "docView";
    			}
    			if(sortType == null || sortType.equals("0")){
					if(flagBellSort) {
						Collections.sort(viewResults, DocumentItem.bellCompareUp);
						flagBellSort = !flagBellSort;
						
					}
					else {
						Collections.sort(viewResults, DocumentItem.bellCompareDouwn);
						flagBellSort = !flagBellSort;
						
					}
					
					model.addAttribute("results", viewResults);
					model.addAttribute("query", query);
					//model.addAttribute("concur_1", UpdateConcurrence);
					
				}
    			else if(sortType.equals("1")){
					if(flagConcurSort) {
    					Collections.sort(viewResults, DocumentItem.concurrenceCompareUp);
    					flagConcurSort = !flagConcurSort;
    					
					}
					else {
						Collections.sort(viewResults, DocumentItem.concurrenceCompareDouwn);
						flagConcurSort = !flagConcurSort;
						
					}
					
					model.addAttribute("results", viewResults );
					model.addAttribute("query", query);
				}
    			else if(sortType.equals("2")){
					if(flagTfIdfSort) {
						Collections.sort(viewResults, DocumentItem.tfIdfCompareUp);
						flagTfIdfSort = !flagTfIdfSort;
						
					}
					else {
						Collections.sort(viewResults, DocumentItem.tfIdfCompareDouwn);
						flagTfIdfSort = !flagTfIdfSort;
					}
					
					model.addAttribute("results", viewResults );
					model.addAttribute("query", query);
				}
  			
    			return "response";
    		}
    	 		
    }
    
}
