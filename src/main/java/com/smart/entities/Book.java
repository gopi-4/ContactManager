package com.smart.entities;

import lombok.*;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Builder
@AllArgsConstructor
public class Book {

	public String name;
	public String author_name;

	public String image;
	public int votes;
	public double rating;

}
