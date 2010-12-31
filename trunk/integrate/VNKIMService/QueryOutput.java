package org.me.VNKIMService;

import java.util.List;

class QueryOutput
{

	public static String getHTMLCG(QueryBuffer buffer) throws Exception
	{
		String output = "";

		if ( ProcessingQuery.IsValidCG(buffer))
		{
			QueryBuffer.recognized++;
			for(int i=0; i<buffer.length; i++)
			{
				ItemType tmp = buffer.getItem(i);
				if (tmp.delete)
					continue;
				String className = tmp.className;
				if (className.compareTo("RW")==0)
				{
					String rel = tmp.relation;
					if (tmp.direction==1)
						output += " ---> " + "(<font color=\"blue\">" + rel + "</font>)" + " ---> ";
					else
						output += " <--- " + "(<font color=\"blue\">" + rel + "</font>)" + " <--- ";
				}
				else // entity or other
				{
					if (className.substring(0, 2).compareToIgnoreCase("UE")==0)
						className = className.substring(3);
					if (tmp.quantifier.compareToIgnoreCase("none")==0 || tmp.quantifier.compareTo("")==0)
						output += " [<font color=\"blue\">" + className + "</font>] ";
					else
						output += " [<font color=\"blue\">" + className + ": " + tmp.quantifier + "</font>] ";
					/*if (tmp.className.compareTo("UE")==0)
					{
						if ( i == 0 )
							output += " [<font color=\"blue\">" + tmp.value + ": ?</font>] ";
						else
							output += " [<font color=\"blue\">" + tmp.value + ": *</font>] ";

					}
					else if (tmp.className.substring(0, 2).compareToIgnoreCase("UE")==0)
						output += " [<font color=\"blue\">" + tmp.className.substring(3) + ": ?</font>] ";
					else
						output += " [<font color=\"blue\">" + tmp.className + ": " + tmp.value + "</font>] ";*/
				}
			}

		}
		else
		{
			QueryBuffer.unrecognized++;
			output = "<b><font color=blue>COULD NOT FIND CG</font></b>";
		}

		return output;
	}

	public static String ToUpperInitial(String text)
	{
		String first = text.substring(0, 1);
		String result = first.toUpperCase() + text.substring(1);
		return result;
	}

    /**
	 *
	 * @param lsCG
	 * @return
	 * @throws Exception
	 */
	public static String generatelistJsCG(ListCG lsCG) throws Exception {
		int margintop = 5;
		int marginleft = 5;

		int currLeft = marginleft;
		int currTop = margintop;
		String output = "";
		List<QueryBuffer> listQuery = lsCG.getListQB();
		QueryBuffer.recognized++;
		boolean isCoverByBox = false;

		// Left splace for draw top relation
		int topRelHeight = 0;
		int bottomBoxCoverHeight = 0;
		int topBoxCoverHeight = 0;
/*
		if (lsCG.getTopRel() != null) {
			// Not apply showing COUNT relation for cases: query about price,
			// query about uncountable entity.
			System.out.println("\t - Not show COUNT relation for cases: price, uncountable entity");
			if (lsCG.getTopRel().name().equals(TopRelationType.COUNT.name())) {
				// Connect to first UE
				QueryBuffer firstQuery = listQuery.get(0);
				boolean hasUE = false;
				for (int i = 0; i < firstQuery.length; i++) {
					// Case for "how much".
					ItemType t = firstQuery.getItem(i);
					if (t.className.equalsIgnoreCase("String")
							|| t.className.equalsIgnoreCase("UE_String")) {
						lsCG.setTopRel(null);
					}
				}
			}
		}
*/
		if (lsCG.getTopRel() != null) {// Top relation exists

			currTop = currTop + 80 + 20;
			currLeft += 45;

			topRelHeight = 80;
			bottomBoxCoverHeight += 20;
			topBoxCoverHeight += 20;

		}

		if (lsCG.getListQB().size() > 1) {// Relation AND, OR exists
			isCoverByBox = true;
			currTop += 20;
			currLeft += 45;

			bottomBoxCoverHeight += (lsCG.getListQB().size())
					* 20;
			topBoxCoverHeight += (lsCG.getListQB().size())
					* 20;

		}

		// genreate div structure for drawing
		output += "<table border=\"0\">\r\n";
		output += "<tr>\r\n";

		// Get the maximum col and total row to declare div area to display CG
		int maxCol = 0;
		int totalRow = 0;
		for (int v = 0; v < lsCG.getListQB().size(); v++) {
			QueryBuffer qb = lsCG.getListQB().get(v);
			int tTotal = qb.totalcol;
			if (tTotal > maxCol) {
				maxCol = tTotal;
			}
			totalRow += qb.totalrow;
		}

		int divwidth = currLeft + maxCol * Constants.CG_ROW_WIDTH;
		int divheight = totalRow
				* Constants.CG_COL_HEIGHT
				+ ((lsCG.getListQB().size() - 1) * (Constants.CG_COL_HEIGHT) * 7)
				/ 2;

		output += "<td width="
				+ (divwidth + 2 * Constants.CG_LEFT_COVER_BOX)
				+ " height="
				+ (divheight + topRelHeight + bottomBoxCoverHeight + topBoxCoverHeight)
				+ "><div id=\"canvas"
				+ QueryBuffer.querycount
				+ "\" style=\"position:relative;height:"
				+ (divheight + topRelHeight + bottomBoxCoverHeight + topBoxCoverHeight)
				+ "px;width:" + (divwidth + 2 * Constants.CG_LEFT_COVER_BOX)
				+ "px;\"></div><canvasjs" + QueryBuffer.querycount
				+ "></td>\r\n";
		output += "</tr>\r\n";
		output += "</table>\r\n";

		String cgscript = "";
		cgscript += "<script type=\"text/javascript\">\r\n";
		cgscript += "<!--\r\n";
		cgscript += "var cnv = document.getElementById(\"canvas"
				+ QueryBuffer.querycount + "\");\r\n";
		cgscript += "var jg = new jsGraphics(cnv);\r\n";
		cgscript += "jg.setFont(\"verdana\",\"12px\",Font.BOLD);\r\n";
		cgscript += "jg.setStroke(2);\r\n";

		// draw component CG
		System.out.println("\t - Draw component CG(s).");

		for (int i = 0; i < listQuery.size(); i++) {
			// Caculated curr top at here ------------------------>
			// top = top / 2;
			cgscript += generateJsCG(listQuery.get(i), currLeft, currTop,
					isCoverByBox);
			// Re Calculated currLeft and currTop
			currTop += (listQuery.get(i).totalrow + 2)
					* Constants.CG_COL_HEIGHT;
			currTop += Constants.CG_BOTTOM_COVER_BOX;
			currTop += Constants.CG_TOP_COVER_BOX;
		}

		// end draw extra
		cgscript += "jg.paint();";
		cgscript += "//-->\r\n";
		cgscript += "</script>\r\n";

		String canvas = "<canvasjs" + QueryBuffer.querycount + ">";
		output = output.replaceAll(canvas, cgscript.replaceAll("\\$", ""));

		// Add top relation (if exists)
		System.out.println("\t - Draw top relation (if exists).");
		if (lsCG.getTopRel() != null) {
			String topRel = lsCG.getTopRel().name();
			int width = topRel.length() * Constants.CG_TEXT_WIDTH;
			// drawing
			output += "<script type=\"text/javascript\">\r\n";
			output += "<!--\r\n";
			output += "var cnv = document.getElementById(\"canvas"
					+ QueryBuffer.querycount + "\");\r\n";
			output += "var jg = new jsGraphics(cnv);\r\n";
			// cgscript += "var jg = new jsGraphics();\r\n";
			output += "jg.setFont(\"verdana\",\"12px\",Font.BOLD);\r\n";
			// cgscript += "jg.setColor(\"#000000\");\r\n";
			output += "jg.setStroke(2);\r\n";

			// Draw cover box
			System.out.println("\t\t + Draw cover box.");
			if (lsCG.getTopRel() != null) {
				output += "jg.setColor(\"black\");";
				output += genScriptDrawRectWithoutAngle(
						marginleft,
						(margintop + Constants.CG_TOP_RELATION_AREA),
						(divwidth - marginleft + 2 * Constants.CG_LEFT_COVER_BOX),
						(divheight + Constants.CG_BOTTOM_COVER_BOX + Constants.CG_TOP_COVER_BOX));
			}

			// DRAW top relation
			System.out.println("\t\t + Draw top relation.");
			output += "jg.setColor(\"black\");";

			int x1 = (marginleft + divwidth) / 2;
			int x2 = x1;
			int y1 = margintop;
			int elipseSize = 29;
			output += genScriptDrawEclipse(x1, y1, width, elipseSize);
			output += "jg.drawStringRect(\"" + topRel + "\","
					+ (x1 - width / 2) + "," + (y1 + 8) + "," + width
					+ ", \"center\");\r\n";
			output += genScriptDrawLine(x1, (y1 + elipseSize), x2,
					(y1 + Constants.CG_TOP_RELATION_AREA), true, false);

			// Draw connect from top relation to specified entity

			if (topRel.equals(TopRelationType.COUNT.name())) {
				System.out.println("\t\t + For COUNT case...");
				// Connect to first UE
				QueryBuffer firstQuery = listQuery.get(0);
				boolean hasUE = false;
				for (int i = 0; i < firstQuery.length; i++) {
					ItemType t = firstQuery.getItem(i);
					if (((t.classType.equals(Constants.UE) || (t.classType
							.length() > 2 && t.classType.substring(0, 3)
							.equals(Constants.UE))) && !t.className
							.equals(Constants.UE_AGENT))
							|| (t.classType.equals(Constants.IE) && firstQuery.length == 1)) {
						hasUE = true;
						int x3 = t.left;
						int y3 = t.top;
						output += "jg.setColor(\"green\");";
						output += genScriptDrawLine(x1 - (x3 - x1) * 3 / 100,
								y1 + elipseSize,
								(x3 - (x3 - x1) * 3 / 100 + t.width / 2), y3,
								false, false);
						output += genScriptDrawLine(x1 + (x3 - x1) * 3 / 100,
								y1 + elipseSize,
								(x3 + (x3 - x1) * 3 / 100 + t.width / 2), y3,
								false, false);
						output += "jg.setColor(\"black\");";
						break;
					}

				}
				// Special case: when don't have any UE, so, COUNT point to
				// first IE
				System.out.println("\t\t + Special case: when don't have any UE, so, COUNT point to first IE.");
				if (hasUE == false) {
					for (int i = 0; i < firstQuery.length; i++) {
						ItemType t = firstQuery.getItem(i);
						if (((t.classType.equals(Constants.IE)))) {
							hasUE = true;
							int x3 = t.left;
							int y3 = t.top;
							output += "jg.setColor(\"green\");";
							output += genScriptDrawLine(x1 - (x3 - x1) * 3
									/ 100, y1 + elipseSize, (x3 - (x3 - x1) * 3
									/ 100 + t.width / 2), y3, false, false);
							output += genScriptDrawLine(x1 + (x3 - x1) * 3
									/ 100, y1 + elipseSize, (x3 + (x3 - x1) * 3
									/ 100 + t.width / 2), y3, false, false);

							output += "jg.setColor(\"black\");";
							break;
						}
					}
				}
			} else if (topRel.equals(TopRelationType.MAX.name())
					|| topRel.equals(TopRelationType.MIN.name())) {
				System.out.println("\t\t + For MAX/MIN case...");

				for (int j = 0; j < listQuery.size(); j++) {
					QueryBuffer sub = listQuery.get(j);
					for (int i = 0; i < sub.length; i++) {
						ItemType t = sub.getItem(i);
						if (t.isMarkMaxMinRel()) {
							int x3 = t.left;
							int y3 = t.top;
							output += "jg.setColor(\"green\");";
							output += genScriptDrawLine(x1 - (x3 - x1) * 3
									/ 100, y1 + elipseSize, (x3 - (x3 - x1) * 3
									/ 100 + t.width / 2), y3, false, false);
							output += genScriptDrawLine(x1 + (x3 - x1) * 3
									/ 100, y1 + elipseSize, (x3 + (x3 - x1) * 3
									/ 100 + t.width / 2), y3, false, false);

							output += "jg.setColor(\"black\");";
							break;
						}
					}
				}
			} else if (topRel.equals(TopRelationType.AVERAGE.name())
					|| topRel.equals(TopRelationType.MOST.name())) {
				// Connect to an Entity following "average" or "most"
				System.out.println("\t\t + For AVERAGE/MOST case...");
				QueryBuffer firstQuery = listQuery.get(0);
				for (int i = 0; i < firstQuery.length; i++) {
					ItemType t = firstQuery.getItem(i);
					if ((((t.classType.equals(Constants.UE) || (t.classType
							.length() > 2 && t.classType.substring(0, 3)
							.equals(Constants.UE))) && !t.className
							.equals(Constants.UE_AGENT)) && (t.wordbefore
							.equalsIgnoreCase(Constants.AVERAGE_STRING) || t.wordbefore
							.equalsIgnoreCase(Constants.MOST_STRING)))
							|| (t.classType.equals(Constants.IE) && firstQuery.length == 1)) {
						int x3 = t.left;
						int y3 = t.top;
						output += "jg.setColor(\"green\");";
						output += genScriptDrawLine(x1 - (x3 - x1) * 3 / 100,
								y1 + elipseSize,
								(x3 - (x3 - x1) * 3 / 100 + t.width / 2), y3,
								false, false);
						output += genScriptDrawLine(x1 + (x3 - x1) * 3 / 100,
								y1 + elipseSize,
								(x3 + (x3 - x1) * 3 / 100 + t.width / 2), y3,
								false, false);

						output += "jg.setColor(\"black\");";
						break;
					}
				}
			}
			// end draw extra
			output += "jg.paint();";
			output += "//-->\r\n";
			output += "</script>\r\n";

		}

		// Draw co-link
		System.out.println("\t - Draw co-link (if exists).");
		if (lsCG.getListQB().size() > 1) {
			output += "<script type=\"text/javascript\">\r\n";
			output += "<!--\r\n";
			output += "var cnv = document.getElementById(\"canvas"
					+ QueryBuffer.querycount + "\");\r\n";
			output += "var jg = new jsGraphics(cnv);\r\n";
			output += "jg.setFont(\"verdana\",\"12px\",Font.BOLD);\r\n";
			output += "jg.setStroke(2);\r\n";

			for (int g = 0; g < lsCG.getListQB().size() - 1; g++) {
				QueryBuffer headQr = lsCG.getListQB().get(g);
				QueryBuffer tailQr = lsCG.getListQB().get(g + 1);

				int headX = 0;
				int headY = 0;
				int tailX = 0;
				int tailY = 0;
				boolean flagHasCoLink = false;
				ItemType head = null;
				ItemType tail = null;
				for (int i = 0; i < headQr.length; i++) {
					ItemType t = headQr.getItem(i);
					if (((t.classType.equals(Constants.UE) || (t.classType
							.length() > 2 && t.classType.substring(0, 3)
							.equals(Constants.UE))) && !t.className
							.equals(Constants.UE_AGENT))
							&& (t.quantifier.equals(Constants.QUESTION_SYMBOL))) {
						if (t.className.equals("String")) {
							for (int j = i - 2; j >= 0; j--) {
								t = headQr.getItem(j);
								if (!t.delete
										&& (t.classType.equals(Constants.UE)
												|| t.classType
														.equals(Constants.IE) || (t.classType
												.length() > 2
												&& (t.classType.substring(0, 3)
														.equals(Constants.UE)) || (t.classType
												.substring(0, 3)
												.equals(Constants.IE))))) {

									flagHasCoLink = true;
									head = t;
									headX = t.left + t.width / 2;
									headY = t.top + t.height;

									break;
								}
							}
						} else {
							head = t;
							flagHasCoLink = true;
							headX = t.left + t.width / 2;
							headY = t.top + t.height;

							break;
						}
					}
				}

				if (flagHasCoLink) {
					flagHasCoLink = false;
					for (int i = 0; i < tailQr.length; i++) {
						ItemType t = tailQr.getItem(i);
						if (((t.classType.equals(Constants.UE) || (t.classType
								.length() > 2 && t.classType.substring(0, 3)
								.equals(Constants.UE))) && !t.className
								.equals(Constants.UE_AGENT))
								&& (t.quantifier
										.equals(Constants.QUESTION_SYMBOL))) {
							if (t.className.equals("String")) {
								for (int j = i - 2; j >= 0; j--) {
									t = tailQr.getItem(j);
									if (!t.delete
											&& (t.classType
													.equals(Constants.UE)
													|| t.classType
															.equals(Constants.IE) || (t.classType
													.length() > 2
													&& (t.classType.substring(
															0, 3)
															.equals(Constants.UE)) || (t.classType
													.substring(0, 3)
													.equals(Constants.IE))))) {
										tail = t;
										flagHasCoLink = true;
										tailX = t.left + t.width / 2;
										tailY = t.top;

										break;
									}
								}
							} else {
								tail = t;
								flagHasCoLink = true;
								tailX = t.left + t.width / 2;
								tailY = t.top;

								break;
							}
						}
					}
				}
				if (flagHasCoLink) {
					if (!head.className.equalsIgnoreCase(tail.className)
							|| !head.classType.equalsIgnoreCase(tail.classType)) {
						flagHasCoLink = false;
					}else {
						if (head.classType.equals(Constants.IE)&& !head.value.equalsIgnoreCase(tail.value)){
							flagHasCoLink = false;
						}
					}
				}
				if (flagHasCoLink) {
					output += genScriptDrawLine(headX, headY, tailX, tailY,
							false, true);
				} else {
					lsCG.getListQB().get(g).setRelToNextAtomQuery(
							RelationType.UNION);
				}

			}
			// end draw extra
			output += "jg.paint();";
			output += "//-->\r\n";
			output += "</script>\r\n";
		}

		// Draw nextrelation
		// Add top relation (if exists)
		System.out.println("\t - Draw next relation (if exists).");

		if (lsCG.getListQB().size() > 1) {
			output += "<script type=\"text/javascript\">\r\n";
			output += "<!--\r\n";
			output += "var cnv = document.getElementById(\"canvas"
					+ QueryBuffer.querycount + "\");\r\n";
			output += "var jg = new jsGraphics(cnv);\r\n";
			// cgscript += "var jg = new jsGraphics();\r\n";
			output += "jg.setFont(\"verdana\",\"12px\",Font.BOLD);\r\n";
			// cgscript += "jg.setColor(\"#000000\");\r\n";
			output += "jg.setStroke(2);\r\n";
			int nRx1 = (marginleft + divwidth) / 2;
			int nRx2 = 0;
			int nRy1 = margintop;
			int nRy2 = 0;
			for (int g = 0; g < lsCG.getListQB().size() - 1; g++) {
				QueryBuffer qb = lsCG.getListQB().get(g);
				nRx2 = nRx1;

				if (lsCG.getTopRel() != null) {// Top relation exists
					if (g == 0) {
						nRy1 += (Constants.CG_TOP_RELATION_AREA + Constants.CG_TOP_COVER_BOX);
					}
				}
				if (g > 0) {
					nRy1 = nRy2;
				}
				if (lsCG.getListQB().size() > 1) {// Relation AND, OR exists
					nRy1 += (Constants.CG_TOP_COVER_BOX + Constants.CG_BOTTOM_COVER_BOX);
				}
				nRy1 += (lsCG.getListQB().get(g).totalrow)
						* Constants.CG_COL_HEIGHT;
				nRy2 = nRy1 + 2 * Constants.CG_COL_HEIGHT;

				String rel = "";

				if (lsCG.getListQB().get(g).getRelToNextAtomQuery() != null) {
					rel = lsCG.getListQB().get(g).getRelToNextAtomQuery()
							.name();
				}
				int width = rel.length() * Constants.CG_TEXT_WIDTH;
				int elipseSize = 29;

				int nRx1_ = nRx1;
				int nRy1_ = (nRy1 + nRy2) / 2 - elipseSize / 2;
				output += genScriptDrawLine(nRx1, nRy1, nRx1_, nRy1_, true,
						false);
				output += genScriptDrawEclipse(nRx1_, nRy1_, width, elipseSize);
				output += "jg.drawStringRect(\"" + rel + "\","
						+ (nRx1_ - width / 2) + "," + (nRy1_ + 8) + "," + width
						+ ", \"center\");\r\n";

				int nRx2_ = nRx1;
				int nRy2_ = nRy1_ + elipseSize;
				output += genScriptDrawLine(nRx2_, nRy2_, nRx2, nRy2, true,
						false);

			}
			// end draw extra
			output += "jg.paint();";
			output += "//-->\r\n";
			output += "</script>\r\n";
		}

		return output;
	}

    public static String generateJsCG(QueryBuffer buffer, int currentleft,
			int currentTop, boolean isCoverByBox) throws Exception {

		int currleft = currentleft;
		int currtop = currentTop;
		int divwidth = currleft + buffer.totalcol * Constants.CG_ROW_WIDTH;
		int divheight = buffer.totalrow * Constants.CG_COL_HEIGHT;

		// calculate position of all elements
		int colleft[] = new int[buffer.totalcol + 1];// //Use to store the
		// left position of
		// previous item

		colleft[0] = currleft;

		for (int i = 0; i < buffer.length; i++) {

			ItemType tmp = buffer.getItem(i);
			if (tmp.delete) {
				continue;
			}
			if (tmp.quantifier == null || tmp.quantifier.trim().equals("")) {
				tmp.quantifier = Constants.ASTERISK_SYMBOL;
			}
			String className = tmp.className;
			className = ToUpperInitial(className);
			if (className.compareTo(Constants.RW) == 0) {// RW
				tmp.text = tmp.relation;
			} else // entity or other
			{
				if (className.length() > 3
						&& className.substring(0, 2).compareToIgnoreCase(
								Constants.UE) == 0) {
					className = className.substring(3);

					// Name of entity
					className = ToUpperInitial(className);
				}
				// Quantifier of entity
				if (className.equalsIgnoreCase(Constants.QUOTE_STRING)) {
					System.out.println("\t - Show content of quote phrase.");
					className = "String";
					// Modify to show String
					tmp.text = className + ": "
							+ tmp.value.replaceAll("\"", "");
					tmp.text = tmp.text.replaceAll(Constants.QUOTE_STRING,
							Constants.QUESTION_SYMBOL);

				} else if (className
						.equalsIgnoreCase(Constants.LANGUAGE_STRING)
						&& tmp.classType.equals(Constants.IE)) {
					System.out.println("\t - Correct displaying for Language entity.");
					// Modify to show String
					tmp.text = className
							+ ": "
							+ tmp.value.replaceAll(Constants.LANGUAGE_STRING
									.toLowerCase(), "");

				} else if (className.equalsIgnoreCase(Constants.TIGER_STRING)
						&& tmp.classType.equals(Constants.IE)) {
					System.out.println("\t - Correct displaying for Tiger entity.");
					// Modify to show String
					tmp.text = className
							+ ": "
							+ tmp.value.replaceAll(Constants.TIGER_STRING
									.toLowerCase(), "");

				} else {

					if (tmp.quantifier.compareToIgnoreCase("none") == 0
							|| tmp.quantifier.compareToIgnoreCase("") == 0) {
						if (tmp.classType.equals(Constants.IE)) {
							tmp.text = className + ": " + tmp.value;
						} else {
							tmp.text = className;
						}
					} else {
                        if (tmp.isValueOfNormalQTA()) {

                            tmp.text = className + ": " + tmp.value.toUpperCase();

                        } else {

                            tmp.text = className + ": " + tmp.quantifier;

                        }
					}
					tmp.text = tmp.text.replace("American", "America");
					tmp.text = tmp.text.replace("Date", "String");

				}
			}
			// MODIFY
			final int maxChars = 20;
			final int MAXChars = 24;
			if (tmp.text.length() <= maxChars) {
				tmp.width = tmp.text.length() * (Constants.CG_TEXT_WIDTH);
			} else {
				tmp.width = tmp.text.length()
						* (Constants.CG_TEXT_WIDTH - (Constants.CG_TEXT_WIDTH * (tmp.text
								.length() - maxChars))
								/ tmp.text.length());
			}
			tmp.height = 30;
			if (tmp.text.length() >= MAXChars) {
				tmp.height = 42;
			}

			// The width of each of character
			tmp.left = colleft[tmp.col - 1];
			if (colleft[tmp.col] == 0) {
				colleft[tmp.col] = tmp.left + tmp.width
						+ Constants.CG_ITEM_DISTANCE;
			}
			tmp.top = currtop + (tmp.row - 1) * Constants.CG_COL_HEIGHT;

		}

		// drawing
		String cgscript = "";
		for (int i = 0; i < buffer.length; i++) {
			ItemType tmp = buffer.getItem(i);
			if (tmp.delete) {
				continue;
			}
			String className = tmp.className;

			if (className.compareTo(Constants.RW) == 0) {
				cgscript += "jg.drawEllipse(" + tmp.left + "," + tmp.top + ","
						+ tmp.width + "," + tmp.height + ");\r\n";
				// draw 2 lines ---------
				int X1 = tmp.left;
				int Y1 = tmp.top + tmp.height / 2;
				ItemType subjitem = buffer.getItem(tmp.subindex);
				int X2 = subjitem.left + subjitem.width;
				int Y2 = subjitem.top + subjitem.height / 2;
				cgscript += "jg.drawLine(" + X1 + "," + Y1 + "," + X2 + ","
						+ Y2 + ");\r\n";
				int X3 = tmp.left + tmp.width;
				int Y3 = tmp.top + tmp.height / 2;
				ItemType objitem = buffer.getItem(tmp.objindex);
				int X4 = objitem.left;
				int Y4 = objitem.top + objitem.height / 2;
				cgscript += "jg.drawLine(" + X3 + "," + Y3 + "," + X4 + ","
						+ Y4 + ");\r\n";

				// draw arrow
				if (tmp.direction == 1) {
					// draw arrow subj <--- rel
					int Par = Constants.CG_SIZE_OF_ARROW; // size of arrow
					double slopy = Math.atan2(Y2 - Y1, X2 - X1);
					double cosy = Math.cos(slopy);
					double siny = Math.sin(slopy);
					int AX1 = (int) (X1 + (Par * cosy - (Par / 2.0 * siny)));
					int AY1 = (int) (Y1 + (Par * siny + (Par / 2.0 * cosy)));
					int AX2 = (int) (X1 + (Par * cosy + Par / 2.0 * siny));
					int AY2 = (int) (Y1 - (Par / 2.0 * cosy - Par * siny));
					cgscript += "jg.drawLine(" + AX1 + "," + AY1 + "," + X1
							+ "," + Y1 + ");\r\n";
					cgscript += "jg.drawLine(" + AX2 + "," + AY2 + "," + X1
							+ "," + Y1 + ");\r\n";

					// draw arrow rel ---> obj
					slopy = Math.atan2(Y3 - Y4, X3 - X4);
					cosy = Math.cos(slopy);
					siny = Math.sin(slopy);
					AX1 = (int) (X4 + (Par * cosy - (Par / 2.0 * siny)));
					AY1 = (int) (Y4 + (Par * siny + (Par / 2.0 * cosy)));
					AX2 = (int) (X4 + (Par * cosy + Par / 2.0 * siny));
					AY2 = (int) (Y4 - (Par / 2.0 * cosy - Par * siny));
					cgscript += "jg.drawLine(" + AX1 + "," + AY1 + "," + X4
							+ "," + Y4 + ");\r\n";
					cgscript += "jg.drawLine(" + AX2 + "," + AY2 + "," + X4
							+ "," + Y4 + ");\r\n";

					// output += " ---> " + "(<font color=\"blue\">" + rel +
					// "</font>)" + " ---> ";
				} else {
					// draw arrow subj <--- rel
					int Par = Constants.CG_SIZE_OF_ARROW; // size of arrow
					double slopy = Math.atan2(Y1 - Y2, X1 - X2);
					double cosy = Math.cos(slopy);
					double siny = Math.sin(slopy);
					int AX1 = (int) (X2 + (Par * cosy - (Par / 2.0 * siny)));
					int AY1 = (int) (Y2 + (Par * siny + (Par / 2.0 * cosy)));
					int AX2 = (int) (X2 + (Par * cosy + Par / 2.0 * siny));
					int AY2 = (int) (Y2 - (Par / 2.0 * cosy - Par * siny));
					cgscript += "jg.drawLine(" + AX1 + "," + AY1 + "," + X2
							+ "," + Y2 + ");\r\n";
					cgscript += "jg.drawLine(" + AX2 + "," + AY2 + "," + X2
							+ "," + Y2 + ");\r\n";

					// draw arrow rel <--- obj
					slopy = Math.atan2(Y4 - Y3, X4 - X3);
					cosy = Math.cos(slopy);
					siny = Math.sin(slopy);
					AX1 = (int) (X3 + (Par * cosy - (Par / 2.0 * siny)));
					AY1 = (int) (Y3 + (Par * siny + (Par / 2.0 * cosy)));
					AX2 = (int) (X3 + (Par * cosy + Par / 2.0 * siny));
					AY2 = (int) (Y3 - (Par / 2.0 * cosy - Par * siny));
					cgscript += "jg.drawLine(" + AX1 + "," + AY1 + "," + X3
							+ "," + Y3 + ");\r\n";
					cgscript += "jg.drawLine(" + AX2 + "," + AY2 + "," + X3
							+ "," + Y3 + ");\r\n";
				}
			} else {
				cgscript += "jg.drawRect(" + tmp.left + "," + tmp.top + ","
						+ tmp.width + "," + tmp.height + ");\r\n";
			}

			int X = tmp.left;
			int Y = tmp.top + 8;
			if (tmp.text.compareTo("live_in") == 0
					|| tmp.text.compareTo("work_in") == 0
					|| tmp.text.compareTo("born_in") == 0) {
				cgscript += "jg.setColor(\"red\");";
			} else if (tmp.text.contains("(")) {
                cgscript += "jg.setColor(\"DarkCyan\");";
                tmp.text = tmp.text.replaceAll("[(,)]", "");
            } else {
				cgscript += "jg.setColor(\"blue\");";
			}
			cgscript += "jg.drawStringRect(\"" + tmp.text + "\"," + X + "," + Y
					+ "," + tmp.width + ", \"center\");\r\n";
			cgscript += "jg.setColor(\"black\");";

		}

		// Draw cover box
		if (isCoverByBox) {
			cgscript += "jg.setColor(\"black\");";
			cgscript += genScriptDrawRectWithoutAngle(
					currentleft - Constants.CG_LEFT_COVER_BOX,
					(currentTop - Constants.CG_TOP_COVER_BOX),
					(divwidth - currentleft + 2 * Constants.CG_LEFT_COVER_BOX),
					(divheight + Constants.CG_BOTTOM_COVER_BOX + Constants.CG_TOP_COVER_BOX));
		}

		return cgscript;
	}

    private static String genScriptDrawEclipse(int x, int y, int width,
			int height) {
		String ret = "";
		ret = "jg.drawEllipse(" + (x - width) + "," + (y) + "," + 2 * width
				+ "," + height + ");\r\n";
		return ret;
	}

	/**
	 *
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 * @return
	 */
	private static String genScriptDrawLine(int x1, int y1, int x2, int y2,
			boolean isArrow, boolean isDotLine) {
		String ret = "";
		int num = 10;
		if (isDotLine) {
			for (int i = 0; i < num; i++) {
				double xt1 = ((i * (x2 - x1)) / num) + x1;
				double yt1 = ((i * (y2 - y1)) / num) + y1;
				double xt2 = (((i + 0.55) * (x2 - x1)) / num) + x1;
				double yt2 = (((i + 0.55) * (y2 - y1)) / num) + y1;
				ret += "jg.drawLine(" + xt1 + "," + yt1 + "," + xt2 + "," + yt2
						+ ");\r\n";
			}
		} else {
			ret += "jg.drawLine(" + x1 + "," + y1 + "," + x2 + "," + y2
					+ ");\r\n";
		}
		if (isArrow) {
			int Par = Constants.CG_SIZE_OF_ARROW;
			double slopy = Math.atan2(y1 - y2, x1 - x2);
			double cosy = Math.cos(slopy);
			double siny = Math.sin(slopy);
			int AX1 = (int) (x2 + (Par * cosy - (Par / 2.0 * siny)));
			int AY1 = (int) (y2 + (Par * siny + (Par / 2.0 * cosy)));
			int AX2 = (int) (x2 + (Par * cosy + Par / 2.0 * siny));
			int AY2 = (int) (y2 - (Par / 2.0 * cosy - Par * siny));
			ret += "jg.drawLine(" + AX1 + "," + AY1 + "," + x2 + "," + y2
					+ ");\r\n";
			ret += "jg.drawLine(" + AX2 + "," + AY2 + "," + x2 + "," + y2
					+ ");\r\n";
		}
		return ret;
	}

	/**
	 *
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 * @return
	 */
	private static String genScriptDrawRectWithoutAngle(int x1, int y1,
			int width, int height) {
		String ret = "";
		int par = 9;
		ret += genScriptDrawLine(x1 + par, y1, x1 + width - par, y1, false,
				false);
		ret += genScriptDrawLine(x1 + width - par, y1, x1 + width, y1 + par,
				false, false);

		ret += genScriptDrawLine(x1 + width, y1 + par, x1 + width, y1 + height
				- par, false, false);
		ret += genScriptDrawLine(x1 + width, y1 + height - par, x1 + width
				- par, y1 + height, false, false);

		ret += genScriptDrawLine(x1 + width - par, y1 + height, x1 + par, y1
				+ height, false, false);
		ret += genScriptDrawLine(x1 + par, y1 + height, x1, y1 + height - par,
				false, false);

		ret += genScriptDrawLine(x1, y1 + height - par, x1, y1 + par, false,
				false);
		ret += genScriptDrawLine(x1, y1 + par, x1 + par, y1, false, false);

		return ret;
	}

	public static String generateJsCG2(QueryBuffer buffer) throws Exception
	{
		String output = "";
		int marginleft = 250;
		int margintop = 5;
		int rowwidth = 150;
		int colheight = 50;

		QueryBuffer.recognized++;
		//genreate div structure for drawing
		output += "<table border=\"0\">\r\n";
		output += "<tr>\r\n";
		int divwidth = marginleft + buffer.totalcol*rowwidth;
		int divheight = margintop + buffer.totalrow*colheight;
		output += "<td width=" + divwidth + " height=" + divheight + "><div id=\"canvas" + QueryBuffer.querycount + "\" style=\"position:relative;height:" + divheight + "px;width:" + divwidth + "px;\"></div><canvasjs" + QueryBuffer.querycount + "></td>\r\n";
		output += "</tr>\r\n";
		output += "</table>\r\n";

		//calculate position of all elements
		int distance = 150;
		int left=marginleft;
		for(int i=0; i<buffer.length; i++)
		{
			ItemType tmp = buffer.getItem(i);
			if (tmp.delete)
				continue;
			String className = tmp.className;

			if (className.compareTo("RW")==0)
			{
				tmp.text = tmp.relation;
				continue;
			}
			else // entity or other
			{
				/*if(tmp.quantifier.trim().compareToIgnoreCase("*")==0)
				{
					//if(tmp.className.compareToIgnoreCase(tmp.value)!=0)
					{
						tmp.quantifier="*"+tmp.value;
					}
				}*/
				if (className.substring(0, 2).compareToIgnoreCase("UE")==0)
					className = className.substring(3);
				className = ToUpperInitial(className);
				//if(tmp.quantifier.compareTo("*")==0) tmp.quantifier=tmp.quantifier+tmp.value;
				if (tmp.quantifier.compareToIgnoreCase("none")==0 || tmp.quantifier.compareToIgnoreCase("")==0)
					tmp.text = className;
				else
					tmp.text = className + ": " + tmp.quantifier;
			}

			tmp.width = tmp.text.length()*10;
			tmp.height = 30;
			tmp.left = left;
			left = tmp.left + tmp.width + distance;
			tmp.top = margintop + (tmp.row)*colheight;
		}

		//drawing
		String cgscript = "";
		cgscript += "<script type=\"text/javascript\">\r\n";
		cgscript += "<!--\r\n";
		cgscript += "var cnv = document.getElementById(\"canvas" + QueryBuffer.querycount + "\");\r\n";
		cgscript += "var jg = new jsGraphics(cnv);\r\n";
		//cgscript += "var jg = new jsGraphics();\r\n";
		cgscript += "jg.setFont(\"verdana\",\"12px\",Font.BOLD);\r\n";
		//cgscript += "jg.setColor(\"#000000\");\r\n";
		cgscript += "jg.setStroke(2);\r\n";
		for(int i=0; i<buffer.length; i++)
		{
			ItemType tmp = buffer.getItem(i);
			if (tmp.delete)
				continue;

			cgscript += "jg.drawRect(" + tmp.left + "," + tmp.top + "," + tmp.width + "," + tmp.height + ");\r\n";

			int X = tmp.left;
			int Y = tmp.top + 8;
			if (tmp.text.compareTo("live_in")==0 || tmp.text.compareTo("work_in")==0 || tmp.text.compareTo("born_in")==0)
				cgscript += "jg.setColor(\"red\");";
			else
				cgscript += "jg.setColor(\"blue\");";
			cgscript += "jg.drawStringRect(\"" + tmp.text + "\"," + X + "," + Y + "," + tmp.width + ", \"center\");\r\n";
			cgscript += "jg.setColor(\"black\");";
			//cgscript += "jg.drawString(\"" + text + "\"," + X + "," + Y + ");\r\n";

		}
		cgscript += "jg.paint();";
		cgscript += "//-->\r\n";
		cgscript += "</script>\r\n";
		String canvas = "<canvasjs" + QueryBuffer.querycount + ">";
		output = output.replaceAll(canvas, cgscript);

		return output;
	}
	public static String generateJsCG(QueryBuffer buffer) throws Exception
	{
		String output = "";
		int marginleft = 5;
		int margintop = 5;
		int rowwidth = 200;
		int colheight = 50;

		QueryBuffer.recognized++;
		//genreate div structure for drawing
		output += "<table border=\"0\">\r\n";
		output += "<tr>\r\n";
		int divwidth = 5 + buffer.totalcol*rowwidth;
		int divheight = 5 + buffer.totalrow*colheight;
//		int divwidth = marginleft + buffer.totalcol*rowwidth;
//		int divheight = margintop + buffer.totalrow*colheight;
		output += "<td width=" + divwidth + " height=" + divheight + "><div id=\"canvas" + QueryBuffer.querycount + "\" style=\"position:relative;height:" + divheight + "px;width:" + divwidth + "px;\"></div><canvasjs" + QueryBuffer.querycount + "></td>\r\n";
		output += "</tr>\r\n";
		output += "</table>\r\n";

		//calculate position of all elements
		int distance = 100;
		int colleft[] = new int[buffer.totalcol+1];
		colleft[0] = marginleft;
		for(int i=0; i<buffer.length; i++)
		{

			ItemType tmp = buffer.getItem(i);
			if (tmp.delete)
				continue;
			String className = tmp.className;

			if (className.compareTo("RW")==0)
			{
				tmp.text = tmp.relation;
			}
			else // entity or other
			{
				if (className.substring(0, 2).compareToIgnoreCase("UE")==0)
					className = className.substring(3);
				className = ToUpperInitial(className);
				/*if(tmp.quantifier.trim().compareToIgnoreCase("*")==0)
				{
					if(className.compareToIgnoreCase(tmp.value)!=0)
					{
						tmp.quantifier="*"+tmp.value;
					}
				}*/
				//if(tmp.quantifier.compareTo("*")==0) tmp.quantifier=tmp.quantifier+tmp.value;
				if (tmp.quantifier.compareToIgnoreCase("none")==0 || tmp.quantifier.compareToIgnoreCase("")==0)
					tmp.text = className;
				else
					tmp.text = className + ": " + tmp.quantifier;
				tmp.text=tmp.text.replace("American", "America");
				tmp.text=tmp.text.replace("Date", "String");
			}

			tmp.width = tmp.text.length()*10;
			tmp.height = 30;
			tmp.left = colleft[tmp.col-1];
			if (colleft[tmp.col] == 0)
				colleft[tmp.col] = tmp.left + tmp.width + distance;
			tmp.top = margintop + (tmp.row - 1)*colheight;
		}


		//drawing
		String cgscript = "";
		cgscript += "<script type=\"text/javascript\">\r\n";
		cgscript += "<!--\r\n";
		cgscript += "var cnv = document.getElementById(\"canvas" + QueryBuffer.querycount + "\");\r\n";
		cgscript += "var jg = new jsGraphics(cnv);\r\n";
		//cgscript += "var jg = new jsGraphics();\r\n";
		cgscript += "jg.setFont(\"verdana\",\"12px\",Font.BOLD);\r\n";
		//cgscript += "jg.setColor(\"#000000\");\r\n";
		cgscript += "jg.setStroke(2);\r\n";
                int currentwidth = 0;
		for(int i=0; i<buffer.length; i++)
		{
			ItemType tmp = buffer.getItem(i);
			if (tmp.delete)
				continue;
			String className = tmp.className;

			if ( className.compareTo("RW")==0 )
			{
				cgscript += "jg.drawEllipse(" + tmp.left + "," + tmp.top + "," + tmp.width + "," + tmp.height + ");\r\n";
				// draw 2 lines ---------
				int X1 = tmp.left;
				int Y1 = tmp.top + tmp.height/2;
				ItemType subjitem = buffer.getItem(tmp.subindex);
				int X2 = subjitem.left + subjitem.width;
				int Y2 = subjitem.top + subjitem.height/2;
				cgscript += "jg.drawLine(" + X1 + "," + Y1 + "," + X2 + "," + Y2 + ");\r\n";
				int X3 = tmp.left + tmp.width;
				int Y3 = tmp.top + tmp.height/2;
				ItemType objitem = buffer.getItem(tmp.objindex);
				int X4 = objitem.left;
				int Y4 = objitem.top + objitem.height/2;
				cgscript += "jg.drawLine(" + X3 + "," + Y3 + "," + X4 + "," + Y4 + ");\r\n";

				//draw arrow
				if (tmp.direction==1)
				{
					//draw arrow subj <--- rel
					int Par = 10; // size of arrow
					double slopy = Math.atan2(Y2-Y1, X2-X1);
					double cosy = Math.cos( slopy );
					double siny = Math.sin( slopy );
					int AX1 = (int) (X1 + ( Par * cosy - ( Par / 2.0 * siny )));
					int AY1 = (int) (Y1 + ( Par * siny + ( Par / 2.0 * cosy )));
					int AX2 = (int) (X1 + ( Par * cosy + Par / 2.0 * siny ));
					int AY2 = (int) (Y1 - ( Par / 2.0 * cosy - Par * siny ));
					cgscript += "jg.drawLine(" + AX1 + "," + AY1 + "," + X1 + "," + Y1 + ");\r\n";
					cgscript += "jg.drawLine(" + AX2 + "," + AY2 + "," + X1 + "," + Y1 + ");\r\n";

					//draw arrow rel ---> obj
					slopy = Math.atan2(Y3-Y4, X3-X4);
					cosy = Math.cos( slopy );
					siny = Math.sin( slopy );
					AX1 = (int) (X4 + ( Par * cosy - ( Par / 2.0 * siny )));
					AY1 = (int) (Y4 + ( Par * siny + ( Par / 2.0 * cosy )));
					AX2 = (int) (X4 + ( Par * cosy + Par / 2.0 * siny ));
					AY2 = (int) (Y4 - ( Par / 2.0 * cosy - Par * siny ));
					cgscript += "jg.drawLine(" + AX1 + "," + AY1 + "," + X4 + "," + Y4 + ");\r\n";
					cgscript += "jg.drawLine(" + AX2 + "," + AY2 + "," + X4 + "," + Y4 + ");\r\n";

					//output += " ---> " + "(<font color=\"blue\">" + rel + "</font>)" + " ---> ";
				}
				else
				{
					//draw arrow subj <--- rel
					int Par = 10; // size of arrow
					double slopy = Math.atan2(Y1-Y2, X1-X2);
					double cosy = Math.cos( slopy );
					double siny = Math.sin( slopy );
					int AX1 = (int) (X2 + ( Par * cosy - ( Par / 2.0 * siny )));
					int AY1 = (int) (Y2 + ( Par * siny + ( Par / 2.0 * cosy )));
					int AX2 = (int) (X2 + ( Par * cosy + Par / 2.0 * siny ));
					int AY2 = (int) (Y2 - ( Par / 2.0 * cosy - Par * siny ));
					cgscript += "jg.drawLine(" + AX1 + "," + AY1 + "," + X2 + "," + Y2 + ");\r\n";
					cgscript += "jg.drawLine(" + AX2 + "," + AY2 + "," + X2 + "," + Y2 + ");\r\n";

					//draw arrow rel <--- obj
					slopy = Math.atan2(Y4-Y3, X4-X3);
					cosy = Math.cos( slopy );
					siny = Math.sin( slopy );
					AX1 = (int) (X3 + ( Par * cosy - ( Par / 2.0 * siny )));
					AY1 = (int) (Y3 + ( Par * siny + ( Par / 2.0 * cosy )));
					AX2 = (int) (X3 + ( Par * cosy + Par / 2.0 * siny ));
					AY2 = (int) (Y3 - ( Par / 2.0 * cosy - Par * siny ));
					cgscript += "jg.drawLine(" + AX1 + "," + AY1 + "," + X3 + "," + Y3 + ");\r\n";
					cgscript += "jg.drawLine(" + AX2 + "," + AY2 + "," + X3 + "," + Y3 + ");\r\n";
				}
			}
			else
				cgscript += "jg.drawRect(" + tmp.left + "," + tmp.top + "," + tmp.width + "," + tmp.height + ");\r\n";

			int X = tmp.left;
			int Y = tmp.top + 8;
                        currentwidth =tmp.left + tmp.width;
			if (tmp.text.compareTo("live_in")==0 || tmp.text.compareTo("work_in")==0 || tmp.text.compareTo("born_in")==0)
				cgscript += "jg.setColor(\"red\");";
			else
				cgscript += "jg.setColor(\"blue\");";
			cgscript += "jg.drawStringRect(\"" + tmp.text + "\"," + X + "," + Y + "," + tmp.width + ", \"center\");\r\n";
			cgscript += "jg.setColor(\"black\");";
			//cgscript += "jg.drawString(\"" + text + "\"," + X + "," + Y + ");\r\n";

		}
                //draw nested
/*                String nestedrelation = "MAX";
                int width = nestedrelation.length()*10;
        	cgscript += "jg.setColor(\"red\");";
		cgscript += "jg.drawRect(" + (marginleft- 20) + "," + (margintop-20) + "," + (currentwidth-marginleft+40) + "," + (divheight+20) + ");\r\n";
                int AX1 = (marginleft- 20) + currentwidth/2;
                int X3 = AX1;
                int AY1 = (margintop-20);
                int Y3 = AY1-40;
		cgscript += "jg.drawLine(" + AX1 + "," + AY1 + "," + X3 + "," + Y3 + ");\r\n";
        	cgscript += "jg.drawEllipse(" + (AX1-width) + "," + (Y3-30) + "," + 2*width + "," + 29 + ");\r\n";
        	cgscript += "jg.drawStringRect(\"" + nestedrelation + "\"," + (AX1-width/2) + "," + (Y3-22) + "," + width + ", \"center\");\r\n";
                //draw next
                String logicrel="OR";
                width = logicrel.length()*10;
                int X1 = currentwidth+20;
                int Y1 = margintop- 10 + divheight/2;
		cgscript += "jg.drawLine(" + X1 + "," + Y1 + "," + (X1+30) + "," + Y1 + ");\r\n";
		cgscript += "jg.drawLine(" + (X1+30+2*width) + "," + Y1 + "," + (X1+60+2*width) + "," + Y1 + ");\r\n";
        	cgscript += "jg.drawEllipse(" + (X1+30) + "," + (Y1-15) + "," + (2*width-1) + "," + 30 + ");\r\n";
        	cgscript += "jg.drawStringRect(\"" + logicrel + "\"," + (X1+30+width/2) + "," + (Y1-8) + "," + width + ", \"center\");\r\n";*/
                //end draw extra
		cgscript += "jg.paint();";
		cgscript += "//-->\r\n";
		cgscript += "</script>\r\n";
		String canvas = "<canvasjs" + QueryBuffer.querycount + ">";
		output = output.replaceAll(canvas, cgscript);

		return output;
	}


	public static String getHTMLAnnotatedQuery(QueryBuffer buffer)
	{
		String output = "";
		boolean matchstart = false, matchend = false;
		int j = 0;
		String query = buffer.getQuery();
		for(int i=0; i < query.length(); i++)
		{
			matchstart = false;
			matchend = false;
			for(j=0; j < buffer.length; j++)
			{
				ItemType tmp = buffer.getItem(j);
				if ( tmp.start == i)
				{
					matchstart = true;
					break;
				}

				if ( tmp.end == i || (tmp.end == i+1 && i == (query.length()-1)))
				{
					matchend = true;
					break;
				}

			}

			if ( matchstart )
				output += "<b>";
			output += buffer.query.charAt(i);
			if ( matchend)
			{
				output += "</b>";
				output += "<sub>" + buffer.getItem(j).className + "</sub> ";
			}

		}

		return output;
	}

	public static String getSequenceElement(QueryBuffer buffer)
	{
		String output = "";
		for(int i=0; i < buffer.length; i++)
		{
			output += buffer.getItem(i).value + ":" + buffer.getItem(i).className;
			output += "<br>";
		}
		return output;
	}


}