package com.mobigain.dict.compare;

public class KR_Compare
{
	public KR_Compare()
	{
	
	}
	
	public int compare(String s1, String s2)
	{
		int n = Math.min(s1.length(), s2.length());
		int i = 0;
		int j = 0;
	
		while (n-- != 0) 
		{
			char c1 = s1.charAt(i); i++;
			char c2 = s2.charAt(j); j++;
			if (c1 != c2) 
				return c1 - c2;
		}
		return s1.length() - s2.length();
		
	}
}

