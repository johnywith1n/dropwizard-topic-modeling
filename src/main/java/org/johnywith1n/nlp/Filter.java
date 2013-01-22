package org.johnywith1n.nlp;

import gov.sandia.cognition.text.term.TermOccurrence;
import gov.sandia.cognition.text.term.filter.DefaultStopList;
import gov.sandia.cognition.text.term.filter.LowerCaseTermFilter;
import gov.sandia.cognition.text.term.filter.StopListFilter;
import gov.sandia.cognition.text.term.filter.TermFilter;
import gov.sandia.cognition.text.term.filter.TermLengthFilter;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Filter
{
	private static final Logger LOGGER = LoggerFactory.getLogger(Filter.class);

	public static void filter(Map<String, Iterable<TermOccurrence>> map)
	{
		filter(map, getStopListFilter());
		filter(map, new AlphabetFilter());
		filter(map, new TermLengthFilter());
		filter(map, new LowerCaseTermFilter());
	}

	public static StopListFilter getStopListFilter()
	{
		StopListFilter filter;
		try
		{
			filter = new StopListFilter(DefaultStopList.loadFromText(new File(
			        "src/main/resources/stopwords.txt")));
		}
		catch (IOException e)
		{
			LOGGER.error("Unable to load stop words.", e);
			filter = new StopListFilter(new DefaultStopList());
		}
		return filter;
	}

	public static void filter(Map<String, Iterable<TermOccurrence>> map,
	        TermFilter filter)
	{
		for (String key : map.keySet())
		{
			map.put(key, filter.filterTerms(map.get(key)));
		}
	}
}
