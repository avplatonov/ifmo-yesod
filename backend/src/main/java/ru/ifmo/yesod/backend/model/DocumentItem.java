package ru.ifmo.yesod.backend.model;

import java.util.Comparator;



public class DocumentItem {
	private String title;
	private double pointsBell;
	private double pontsConcurrence;
	
	public DocumentItem(String title, double pointsBell, double pontsConcurrence) {
		this.title = title;
		this.pointsBell = pointsBell;
		this.pontsConcurrence = pontsConcurrence;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
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
	

