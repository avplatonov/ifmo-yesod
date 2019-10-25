package ru.ifmo.yesod.backend.model;

import ru.ifmo.elasticsearch.DocObor;
import ru.ifmo.yesod.common.model.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.text.ParseException;
import java.text.SimpleDateFormat;  
import java.util.Date;

public class DocumentItem  extends Document{
	
	private double pointsBell;
	private double pontsConcurrence;
	private double pointsTfIdf;
	
	
	



	private int viewId;


	public DocumentItem(DocObor p, int viewId) throws ParseException {
		super(p.getName(), p.getCountry(), p.getType(), p.getOrganization(), p.getRelationType(), ((ArrayList<String>) p.getTags()).toArray(new String[0]),p.getStatus(), new SimpleDateFormat("yyyy-MM-dd").parse(p.getTS_2()), p.getBody());
		
		this.pointsBell = (double)Math.round(Math.random() * 100) /100;
		this.pontsConcurrence = (double)Math.round(Math.random() * 100) /100;
		this.viewId = viewId;
		this.pointsTfIdf = 0;
	}
	public DocumentItem(String name, String country, String type, String organization, String relationType, String[] tags, String TS_1, Date TS_2, String status, String body, double pointsBell, double pontsConcurrence, int viewId) {

		super(name, country, type, organization, relationType, tags, status, TS_2, body);
		this.pointsBell = pointsBell;
		this.pontsConcurrence = pontsConcurrence;
		this.viewId = viewId;
	}

	
	public double getPointsTfIdf() {
		return pointsTfIdf;
	}
	public void setPointsTfIdf(double pointsTfIdf) {
		this.pointsTfIdf = pointsTfIdf;
	}
	

	public double getPointsBell() {
		return pointsBell;
	}

	public void setPointsBell(double pointsBell) {
		this.pointsBell = pointsBell;
	}

	public double getPontsConcurrence() {
		return pontsConcurrence;
	}

	public void setPontsConcurrence(double pontsConcurrence) {
		this.pontsConcurrence = pontsConcurrence;
	}


	public int getViewId() {
		return viewId;
	}
	public void setViewId(int viewId) {
		this.viewId = viewId;
	}


	public static Comparator<DocumentItem> bellCompareUp = new Comparator<DocumentItem>() {
		public int compare(DocumentItem r1, DocumentItem r2) {
			double point1 = r1.getPointsBell();
			double point2 = r2.getPointsBell();

			return (int) ((point1 - point2)*10);
		}
	};
	public static Comparator<DocumentItem> bellCompareDouwn = new Comparator<DocumentItem>() {
		public int compare(DocumentItem r1, DocumentItem r2) {
			double point1 = r1.getPointsBell();
			double point2 = r2.getPointsBell();

			return (int) ((point2 - point1)*100);
		}
	};

	public static Comparator<DocumentItem> concurrenceCompareUp = new Comparator<DocumentItem>() {
		public int compare(DocumentItem r1, DocumentItem r2) {
			double point1 = r1.getPontsConcurrence();
			double point2 = r2.getPontsConcurrence();

			return (int) ((point1 - point2)*10);
		}
	};
	
	public static Comparator<DocumentItem> concurrenceCompareDouwn = new Comparator<DocumentItem>() {
		public int compare(DocumentItem r1, DocumentItem r2) {
			double point1 = r1.getPontsConcurrence();
			double point2 = r2.getPontsConcurrence();

			return (int) ((point2 - point1)*100);
		}
	};
	
	public static Comparator<DocumentItem> tfIdfCompareUp = new Comparator<DocumentItem>() {
		public int compare(DocumentItem r1, DocumentItem r2) {
			double point1 = r1.getPointsTfIdf();
			double point2 = r2.getPointsTfIdf();

			return (int) ((point2 - point1)*100);
		}
	};
	
	
	public static Comparator<DocumentItem> tfIdfCompareDouwn = new Comparator<DocumentItem>() {
		public int compare(DocumentItem r1, DocumentItem r2) {
			double point1 = r1.getPointsTfIdf();
			double point2 = r2.getPointsTfIdf();

			return (int) ((point1 - point2)*100);
		}
	};

}


