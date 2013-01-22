package org.johnywith1n.nlp;

import gov.sandia.cognition.text.term.DefaultTermIndex;
import gov.sandia.cognition.text.term.TermOccurrence;

import java.util.Map;

public class TermIndexer
{
	/**
	 * Indexes all the terms that occurred in each document
	 * 
	 * @param map
	 *            A map from the url of each document to an Iterable of terms
	 *            that occurred in the article view of the document
	 * @return A term index of all the terms that occurred in all the documents
	 * 
	 */
	public static DefaultTermIndex indexTerms(
	        Map<String, Iterable<TermOccurrence>> map)
	{
		DefaultTermIndex index = new DefaultTermIndex();

		for (String key : map.keySet())
		{
			for (TermOccurrence term : map.get(key))
			{
				index.add(term.getTerm());
			}
		}

		return index;
	}
}
