package org.johnywith1n.nlp;

import gov.sandia.cognition.text.term.TermOccurrence;
import gov.sandia.cognition.text.token.LetterNumberTokenizer;
import gov.sandia.cognition.text.token.Token;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;

public class Tokenizer
{
	private static final Logger LOGGER = LoggerFactory
	        .getLogger(Tokenizer.class);

	/**
	 * Tokenizes the article content
	 * 
	 * @param articleMap
	 *            A map from urls to the the extracted article content from that
	 *            url
	 * @return an map from urls to an iterable of tokens from the url's article
	 *         content
	 */
	public static Map<String, Iterable<TermOccurrence>> tokenize(
	        Map<String, String> articleMap)
	{
		LetterNumberTokenizer tokenizer = new LetterNumberTokenizer();

		Map<String, Iterable<TermOccurrence>> result = new HashMap<String, Iterable<TermOccurrence>>();
		for (String url : articleMap.keySet())
		{
			result.put(url, transform(tokenizer.tokenize(articleMap.get(url))));
		}
		return result;
	}

	public static Iterable<TermOccurrence> transform(Iterable<Token> input)
	{
		return Iterables.transform(input, new Function<Token, TermOccurrence>()
		{
			@Override
			public TermOccurrence apply(Token input)
			{
				TermOccurrence output = input;
				return output;
			}
		});
	}
}
