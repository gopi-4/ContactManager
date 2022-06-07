package com.smart.entities;

public class Book {

	public String name;
	public String author_name;
	public String image;
	public int votes;
	public double rating;
	
	
	private Book() {
		super();
		// TODO Auto-generated constructor stub
	}

	private Book(String name, String author_name, String image, int votes, double rating) {
		super();
		this.name = name;
		this.author_name = author_name;
		this.image = image;
		this.votes = votes;
		this.rating = rating;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAuthor_name() {
		return author_name;
	}

	public void setAuthor_name(String author_name) {
		this.author_name = author_name;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public int getVotes() {
		return votes;
	}

	public void setVotes(int votes) {
		this.votes = votes;
	}

	public double getRating() {
		return rating;
	}

	public void setRating(double rating) {
		this.rating = rating;
	}

	@Override
	public String toString() {
		return "Book [name=" + name + ", author_name=" + author_name + ", image=" + image + ", votes=" + votes
				+ ", rating=" + rating + "]";
	}
	
}
