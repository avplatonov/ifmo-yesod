package ru.ifmo.yesod.backend.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
 
import ru.ifmo.yesod.backend.model.DocumentItem;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;





@Controller
public class MainController {

    private static List<DocumentItem> results = new ArrayList<DocumentItem>();
    private static String query;
    private static double sortValue;
    //stub. TODO: external import
    static {
    	results.add(new DocumentItem("СОГЛАШЕНИЕ между Правительством Российской Федерации и Правительством Федеративной Республики Бразилии",0.2,0.1));
    	results.add(new DocumentItem("ОГЛАШЕНИЕ  между Правительством Российской Федерации   и Правительством Республики Абхазия",0.3,0.2));
    	results.add(new DocumentItem("РЕГИОНАЛЬНАЯ КОНВЕНЦИЯ О ПРИЗНАНИИ УЧЕБНЫХ КУРСОВ, ДИПЛОМОВ О ВЫСШЕМ ОБРАЗОВАНИИ И УЧЕНЫХ СТЕПЕНЕЙ В ГОСУДАРСТВАХ АЗИИ ",0.4,0.6));
    	results.add(new DocumentItem("СОГЛАШЕНИЕ между Правительством Российской Федерации и Правительством  Азербайджанской Республики",0.1,0.3));
    	results.add(new DocumentItem("СОГЛАШЕНИЕ   между Правительством Российской Федерации  и Правительством Республики Беларусь",0.1,0.8));
    	results.add(new DocumentItem(" Соглашение между Правительством Российской Федерации и Правительством Соединенного Королевства Великобритании",0.3,0.5));
    	results.add(new DocumentItem("СОГЛАШЕНИЕ  между Правительством Союза Советских Социалистических   Республик и Правительством Соединенного Королевства",0.4,0.2));
    	results.add(new DocumentItem("Соглашение между Правительством Российской Федерации и  Правительством Социалистической Республики Вьетнам",0.7,0.4));
    	
    }
    
    
    @RequestMapping(value = { "/", "/index" }, method = RequestMethod.GET)
    public String index(Model model, @RequestParam(value="query",required = false) String text, @RequestParam(value="sort",required = false) String sortType) {
    	
    	if (text == null || text == ""){
    		return "index";
    	}    	
    	else {
    		model.addAttribute("query", text);
    		if(sortType == null || sortType.equals("1") ) {    			
    	    	Collections.sort(results, DocumentItem.bellCompare); 
    			model.addAttribute("results", results );
    		}
    		else if (sortType.equals("0")){
    			Collections.sort(results, DocumentItem.concurrenceCompare); 
    			model.addAttribute("results", results );
    		}
    		return "response";
    	}
    }
}
