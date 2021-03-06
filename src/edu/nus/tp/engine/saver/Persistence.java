package edu.nus.tp.engine.saver;

import java.util.Collection;

import redis.clients.jedis.Transaction;

import edu.nus.tp.engine.utils.Category;

public interface Persistence {

	public void saveTermsAndClassification(Collection<String> eachParsedTweet, Category category);

	public double getPriorClassificationForCategory(Category category);
		
	public long getTermFrequencyInCategory(String term, Category category);
	
	public long getTermCountByCategory(Category category);
	
	public long getUniqueTermsInVocabulary();
	
	public double getSentiScoreForWord(String word);

	public Transaction startBatch();

	void endBatch(Transaction txn);

	void saveTermsAndClassificationBatch(Collection<String> eachParsedTweet,
			Category category, Transaction txn);
	
}
