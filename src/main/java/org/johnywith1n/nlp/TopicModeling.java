package org.johnywith1n.nlp;

import gov.sandia.cognition.math.matrix.Vectorizable;
import gov.sandia.cognition.text.term.DefaultTermIndex;
import gov.sandia.cognition.text.term.TermOccurrence;
import gov.sandia.cognition.text.term.vector.BagOfWordsTransform;
import gov.sandia.cognition.text.topic.LatentDirichletAllocationVectorGibbsSampler;
import gov.sandia.cognition.text.topic.LatentDirichletAllocationVectorGibbsSampler.Result;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.MinMaxPriorityQueue;

public class TopicModeling
{
	private DefaultTermIndex termIndex;
	private int numTopics;
	private LatentDirichletAllocationVectorGibbsSampler lda;
	private Result result;
	private List<String> documentUrls;

	public TopicModeling(DefaultTermIndex index, int numTopics)
	{
		this.termIndex = index;
		this.numTopics = numTopics;
		this.lda = new LatentDirichletAllocationVectorGibbsSampler();
		lda.setTopicCount(numTopics);
	}

	/**
	 * Creates an ordering to the documents in the form of a list of URLS.
	 * 
	 * Stores the result of running LDA on the dataset.
	 * 
	 * @param map
	 *            The input data in the form of a map from urls to the filtered
	 *            terms for that url
	 */
	public void runLDA(Map<String, Iterable<TermOccurrence>> map)
	{
		BagOfWordsTransform transform = new BagOfWordsTransform(termIndex);
		List<Vectorizable> documentVectors = new ArrayList<Vectorizable>();
		documentUrls = new ArrayList<String>();
		for (String key : map.keySet())
		{
			documentVectors.add(transform.convertToVector(map.get(key)));
			documentUrls.add(key);
		}
		result = lda.learn(documentVectors);
	}

	/**
	 * Return the most likely topic for a document
	 * 
	 * @param documentIndex
	 *            The index of the document to get the topic for
	 * @return The index corresponding to the document's topic
	 */
	public int getTopicForDoucment(int documentIndex)
	{
		return getIndexOfLargest(result.getDocumentTopicProbabilities()[documentIndex]);
	}

	/**
	 * Get a list of terms and their probabilities most likely to be associated
	 * with the topic
	 * 
	 * @param topicIndex
	 *            The index of the topic
	 * @param numTerms
	 *            The number of terms to return
	 * @return a map of terms to their probabilities of how likely they are
	 *         associated with the topic
	 */
	public Map<String, Double> getTermsForTopic(int topicIndex, int numTerms)
	{
		Map<String, Double> terms = new HashMap<String, Double>();
		double[] termProbs = result.getTopicTermProbabilities()[topicIndex];
		int[] termIndices = getIndicesOfNLargest(termProbs, numTerms);
		for (int index : termIndices)
			terms.put(termIndex.getTerm(index).toString(), termProbs[index]);
		return terms;
	}

	public static int getIndexOfLargest(double[] array)
	{
		return getIndicesOfNLargest(array, 1)[0];
	}

	/**
	 * Given an array of doubles, return an array of indices to the input array
	 * where the N indices correspond to the N largest elements of the input
	 * array where the first element corresponds to the largest element
	 * 
	 * @param array
	 *            The input array
	 * @param n
	 *            The number of indices to get
	 * @return An array of indices corresponding to the N largest elements of
	 *         the input array where the first element corresponds to the
	 *         largest element
	 */
	public static int[] getIndicesOfNLargest(double[] array, int n)
	{
		if (n > array.length)
			n = array.length;

		int[] result = new int[n];
		MinMaxPriorityQueue<ArrayIndexValuePair> queue = MinMaxPriorityQueue
		        .expectedSize(n).create(convertArrayToPairList(array, n));

		int i = n;
		for (; i < array.length; i++)
		{
			ArrayIndexValuePair currentPair = new ArrayIndexValuePair(i,
			        array[i]);
			queue.offer(currentPair);
		}

		for (i = 0; i < n; i++)
		{
			result[i] = queue.removeLast().index;
		}

		return result;
	}

	/**
	 * Places the first n elements of the input array into a list of
	 * ArrayIndexValuePairs corresponding to the first n elements
	 * 
	 * @param array
	 *            input array of doubles
	 * @param n
	 *            How many elements to put into the list. This must be no more
	 *            than the length of the array
	 * @return
	 */
	private static List<ArrayIndexValuePair> convertArrayToPairList(
	        double[] array, int n)
	{
		List<ArrayIndexValuePair> result = new ArrayList<ArrayIndexValuePair>();
		double[] subarray = Arrays.copyOfRange(array, 0, n);
		int index = 0;
		for (double val : subarray)
		{
			result.add(new ArrayIndexValuePair(index++, val));
		}
		return result;
	}

	public DefaultTermIndex getTermIndex()
	{
		return termIndex;
	}

	public int getNumTopics()
	{
		return numTopics;
	}

	public LatentDirichletAllocationVectorGibbsSampler getLda()
	{
		return lda;
	}

	public Result getResult()
	{
		return result;
	}

	public List<String> getDocumentUrls()
	{
		return documentUrls;
	}

	public static class ArrayIndexValuePair implements
	        Comparable<ArrayIndexValuePair>
	{
		private final int index;
		private final Double value;

		public ArrayIndexValuePair(int index, Double value)
		{
			this.index = index;
			this.value = value;
		}

		@Override
		public int compareTo(ArrayIndexValuePair o)
		{
			return this.value.compareTo(o.value);
		}
	}
}
