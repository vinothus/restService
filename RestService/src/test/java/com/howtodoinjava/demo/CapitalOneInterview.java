package com.howtodoinjava.demo;

import java.util.ArrayList;
import java.util.List;

public class CapitalOneInterview {

	public static void main(String[] args) {
		
		String s="This string test is to test string function" ;
		String output="";
		String array[]=s.split(" ");
		List<String> tempList=new ArrayList<String>();
		for (int i = 0; i < array.length; i++) {
			
			if(!tempList.contains(array[i]))
			{
				output=output+array[i]+" ";
				tempList.add(array[i]);
			}
			
		}
	
	System.out.println(output);
	}
// select * from department where row_num between 1 and 1000;
// select count(*) from department ;
// 	
}
