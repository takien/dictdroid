package com.mobigain.util;

public class HtmlConverter
{
	public static String String_htmlEncode(String inputStr)
	{
		StringBuilder enStr = new StringBuilder();
		int i;
		int stringSize = 0;

		if ((inputStr == null) || (inputStr.length() == 0))
		{
			return enStr.toString();
		}
		
		enStr.append("<html xmlns=\"http://www.w3.org/1999/xhtml\">");
		enStr.append("\n");
		enStr.append("<head>");
		enStr.append("\n");
		enStr.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />");
		enStr.append("\n");
		enStr.append("<title>OM Dictionary</title>");
		enStr.append("\n");
		enStr.append("<style type=\"text/css\">");
		enStr.append("\n");

		//style header
		enStr.append(".styleheader {");
		enStr.append("\n");
		enStr.append("	color: #003366;");
		enStr.append("\n");
		enStr.append("	font-weight: bold;");
		enStr.append("\n");
		enStr.append("font-size: 15px;");
		enStr.append("\n");
		enStr.append("}");
		enStr.append("\n");

		//style 1
		enStr.append(".style1 {");
		enStr.append("\n");
		enStr.append("	color: #333333;");
		enStr.append("\n");
		enStr.append("	font-style: italic;");
		enStr.append("\n");
		enStr.append("font-size: 13px;");
		enStr.append("\n");
		enStr.append("}");
		enStr.append("\n");

		//style 2
		enStr.append(".style2 {");
		enStr.append("\n");
		enStr.append("	color: #000000;");
		enStr.append("\n");
		enStr.append("	font-weight: bold;");
		enStr.append("\n");
		enStr.append("	font-style: italic;");
		enStr.append("\n");
		enStr.append("font-size: 13px;");
		enStr.append("\n");
		enStr.append("}");
		enStr.append("\n");

		//Style 3
		enStr.append(".style3 {");
		enStr.append("\n");
		enStr.append("	color: #CC0033;");
		enStr.append("\n");
		enStr.append("	font-weight: bold;");
		enStr.append("\n");
		enStr.append("	margin-left: 20px;");
		enStr.append("\n");
		enStr.append("font-size: 13px;");
		enStr.append("\n");
		enStr.append("}");
		enStr.append("\n");

		//Style 4
		enStr.append(".style4 {");
		enStr.append("\n");
		enStr.append("	color: #000000;");
		enStr.append("\n");
		enStr.append("	font-weight: bold;");
		enStr.append("\n");
		enStr.append("	margin-left: 30px;");
		enStr.append("\n");
		enStr.append("font-size: 13px;");
		enStr.append("\n");
		enStr.append("}");
		enStr.append("\n");

		//Style 5
		enStr.append(".style5 {");
		enStr.append("\n");
		enStr.append("	color: #000000;");
		enStr.append("\n");
		enStr.append("	font-weight: normal;");
		enStr.append("\n");
		//body.append("	margin-left: 30px;");
		//body.append("\n");
		enStr.append("font-size: 13px;");
		enStr.append("\n");
		enStr.append("}");
		enStr.append("\n");

		enStr.append("</style>");
		enStr.append("\n");
		enStr.append("</head>");
		enStr.append("\n");
		enStr.append("<body>");
		enStr.append("\n");

		stringSize = inputStr.length();

		
		String startTagStyle = "";
		for(i = 0; i< stringSize; i++)
		{
			String subStr;
			subStr = "";
			subStr = inputStr.substring(i, i + 1);
			if(subStr.equals("&"))
			{
				enStr.append(GetStartTag("&"));
				startTagStyle = "&";
			}
			else if(subStr.equals("@"))
			{
				enStr.append(GetStartTag("@"));
				startTagStyle = "@";
			}
			else if(subStr.equals("*"))
			{
				enStr.append(GetStartTag("*"));
				enStr.append("+");
				startTagStyle = "*";
			}
			else if(subStr.equals("-"))
			{
				String subStr2;
				subStr2 = "";
				subStr2 = inputStr.substring(i-1, i);
				if(subStr2.equals("\n"))
				{
					enStr.append(GetStartTag("-"));
					enStr.append("-");
					startTagStyle = "-";
				}
				else
				{
					enStr.append(subStr);
				}
			}
			else if(subStr.equals("="))
			{
				String subStr2;
				subStr2 = "";
				subStr2 = inputStr.substring(i-1, i);
				if(subStr2.equals("\n"))
				{
					enStr.append(GetStartTag("="));
					enStr.append("-");
					startTagStyle = "=";
				}
				else
				{
					enStr.append(subStr);
				}
			}
			else if(subStr.equals("\n") || subStr.equals("+") || i+1 == stringSize)
			{
				if(subStr.equals("+"))
				{
					enStr.append(GetEndTag(startTagStyle));
					enStr.append(GetStartTag("+"));
					enStr.append(":");
					startTagStyle = "=";
				}
				else
				{
					enStr.append(GetEndTag(startTagStyle));
					enStr.append("<br>");
					enStr.append("\n");
					startTagStyle = "";
				}
			}
			else
			{
				enStr.append(subStr);
			}

		}
		enStr.append("</body>");
		enStr.append("</html>");
		return enStr.toString();
	}

	static String GetStartTag(String style)
	{
		String strTag = "";

		if(style.equals("&"))
		{
			strTag = "<span class=\"styleheader\">";
		}
		else if(style.equals("@"))
		{
			strTag = "<span class=\"style1\">";
		}
		else if(style.equals("*"))
		{
			strTag = "<span class=\"style2\">";
		}
		else if(style.equals("-"))
		{
			strTag = "<span class=\"style3\">";
		}
		else if(style.equals("="))
		{
			strTag = "<span class=\"style4\">";
		}
		else if(style.equals("+"))
		{
			strTag = "<span class=\"style5\">";
		}
		return strTag;
	}

	static String GetEndTag(String style)
	{
		String strTag = "";

		if(style.equals("&"))
		{
			strTag = "</span>";
		}
		else if(style.equals("@"))
		{
			strTag = "</span>";
		}
		else if(style.equals("*"))
		{
			strTag = "</span>";
		}
		else if(style.equals("-"))
		{
			strTag = "</span>";
		}
		else if(style.equals("="))
		{
			strTag = "</span>";
		}
		else if(style.equals("+"))
		{
			strTag = "</span>";
		}
		return strTag;
	}
}
