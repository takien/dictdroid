package com.mobigain.dict.compare;
public class EN_Compare
{
	 char[] accented = 
		{
			228, 196, 229, 197, 225, 193, 224, 192, 226, 
			194, 233, 201, 232, 200, 234, 202, 235, 203, 
			239, 207, 237, 205, 236, 204, 238, 206, 246, 
			214, 243, 211, 242, 210, 244, 212, 252, 220, 
			250, 218, 249, 217, 251, 219, 253, 255, 231, 
			199
		};
	
	char[] unaccented = 
		{
			97, 65, 97, 65, 97, 65, 97, 65, 97,
			65, 101, 69, 101, 69, 101, 69, 101,	69, 
			105, 73, 105, 73, 105, 73, 105, 73, 111, 
			79, 111, 79, 111, 79, 111, 79, 117, 85, 
			117, 85, 117, 85, 117, 85, 121, 121, 99, 
			67
		};
	
	char[] accentedH = null;


	public EN_Compare()
	{
		int len = 0;
	
		for(int i = 0; i < accented.length; i++)
		{
			len = Math.max(accented[i], len);
		}
		accentedH = new char[len + 1];	
		
		for(int i = 0; i < accentedH.length; i++)
			accentedH[i] = (char)-1;
	
		for(int i = 0; i < accented.length; i++)
			accentedH[accented[i]] = (char)i;
	}
	
	private String getComparableKey(String key)
	{
		return removeAccents(key);
	}
	
	public int compare(String s1, String s2)
	{
		String v1 = getComparableKey(s1);	
		String v2 = getComparableKey(s2);
	
		int n = Math.min(v1.length(), v2.length());
		int i = 0;
		int j = 0;
	
		while (n-- != 0) 
		{
			char c1 = v1.charAt(i); i++;
			char c2 = v2.charAt(j); j++;		
			
			if (c1 != c2) 
			{
				return c1 - c2;
			}
		}
		return v1.length() - v2.length();
	}
	
	private String removeAccents(String s)
	{
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < s.length(); ++i) 
		{
			char c = s.charAt(i);
			if (((c == ' ') || Character.isLetterOrDigit(c)) && (c < accentedH.length))
			{
				char str = accentedH[c];
				if (str == -1)
				{
					sb.append(c);				
				}
				else
				{
					sb.append(unaccented[str]);
				}			
			}
		}
		return sb.toString().trim().toLowerCase();
	}
}