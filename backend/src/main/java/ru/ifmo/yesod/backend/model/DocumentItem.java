package ru.ifmo.yesod.backend.model;

import ru.ifmo.elasticsearch.DocObor;

import java.util.ArrayList;
import java.util.Comparator;

public class DocumentItem  extends DocObor{
	
	private double pointsBell;
	private double pontsConcurrence;
	private int viewId;


	public DocumentItem(DocObor p, int viewId) {

		super(p.getName(), p.getCountry(), p.getType(), p.getOrganization(), p.getRelationType(), (ArrayList<String>) p.getTags(), p.getTS_1(), p.getTS_2(), p.getStatus(), p.getBody());
		this.pointsBell = (double)Math.round(Math.random() * 100) /100;
		this.pontsConcurrence = (double)Math.round(Math.random() * 100) /100;
		this.viewId = viewId;
	}
	public DocumentItem(String name, String country, String type, String organization, String relationType, ArrayList<String> tags, String TS_1, String TS_2, String status, String body, double pointsBell, double pontsConcurrence, int viewId) {

		super(name, country, type, organization, relationType, tags, TS_1, TS_2, status, body);
		this.pointsBell = pointsBell;
		this.pontsConcurrence = pontsConcurrence;
		this.viewId = viewId;
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


	public static Comparator<DocumentItem> bellCompare = new Comparator<DocumentItem>() {
		public int compare(DocumentItem r1, DocumentItem r2) {
			double point1 = r1.getPointsBell();
			double point2 = r2.getPointsBell();

			return (int) ((point1 - point2)*10);
		}
	};

	public static Comparator<DocumentItem> concurrenceCompare = new Comparator<DocumentItem>() {
		public int compare(DocumentItem r1, DocumentItem r2) {
			double point1 = r1.getPontsConcurrence();
			double point2 = r2.getPontsConcurrence();

			return (int) ((point1 - point2)*10);
		}
	};

}


