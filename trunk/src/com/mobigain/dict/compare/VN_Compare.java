package com.mobigain.dict.compare;

public class VN_Compare
{
	char[] V_ALPHABET = 
	{
		'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
		'a',  
		225, 224, 7843, 227, 7841, 259, 7855, 7857, 7859, 7861, 7863, 226, 7845, 7847, 7849, 7851, 7853, 
		'b', 'c', 
		'd',  
		273, 
		'e',
		233, 232, 7867, 7869, 7865, 234, 7871, 7873, 7875, 7877, 7879,
		'f', 'g', 'h', 
		'i', 
		237, 236, 7881, 297, 7883,
		'j', 'k', 'l', 'm', 'n', 
		'o',  
		243, 242, 7887, 245, 7885, 244, 7889, 7891, 7893, 7895, 7897, 417, 7899, 7901, 7903, 7905, 7907,
		'p', 'q', 'r', 's', 't', 
		'u',  
		250, 249, 7911, 361, 7909, 432, 7913, 7915, 7917, 7919, 7921, 
		'v', 'x',
		'y',
		253, 7923, 7927, 7929, 7925,
		'z'
	};
	
	char[] UV_ALPHABET = 
	{
		'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
		'A',  
		193, 192, 7842, 195, 7840, 258, 7854, 7856, 7858, 7860, 7862, 194, 7844, 7846 , 7848, 7850, 7852,
		'B', 'C', 
		'D',  
		272, 
		'E',
		201, 200, 7866, 7868, 7864, 202, 7870, 7872, 7874, 7876, 7878,
		'F', 'G', 'H', 
		'I', 
		205, 204, 7880, 296, 7882,
		'J', 'K', 'L', 'M', 'N', 
		'O',  
		211, 210, 7886, 213, 7884, 212, 7888, 7890, 7892, 7894, 7896, 416, 7898, 7900, 7902, 7904, 7906,
		'P', 'Q', 'R', 'S', 'T', 
		'U',  
		218, 217, 7910, 360, 7908, 431, 7912, 7914, 7916, 7918, 7920,
		'V', 'X',
		'Y',
		221, 7922, 7926, 7928, 7924,
		'Z'
	};
	char[] HV_ALPHABET = null; 
	public VN_Compare()
	{	
		int len = 0;
		for (int i = 0; i < V_ALPHABET.length; ++i) 
		{
			len = Math.max(V_ALPHABET[i], len);
			len = Math.max(UV_ALPHABET[i], len);
		}
	
		HV_ALPHABET = new char[len + 1];
		for (int i = 0; i < HV_ALPHABET.length; ++i) 
		{
			HV_ALPHABET[i] = (char)-1;
		}
	
		for (int i = 0; i < V_ALPHABET.length; ++i) 
		{
			HV_ALPHABET[V_ALPHABET[i]] = (char)i;
		}
	
		for (int i = 0; i < V_ALPHABET.length; ++i) 
		{
			HV_ALPHABET[UV_ALPHABET[i]] = (char)i;
		}
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
			if (c < HV_ALPHABET.length)
			{
				char str = HV_ALPHABET[c];
				if (str == -1)
				{
					sb.append(c);
				}
				else
				{
					sb.append(str);
				}			
			}
			else
			{
				sb.append(c); 
			}
		}
		return sb.toString().trim().toLowerCase();
	}

}