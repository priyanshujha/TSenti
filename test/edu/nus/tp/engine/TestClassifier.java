package edu.nus.tp.engine;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Lists;

import edu.nus.tp.engine.saver.InMemoryPersistence;
import edu.nus.tp.engine.saver.RedisPersistence;
import edu.nus.tp.engine.utils.Category;
import edu.nus.tp.engine.utils.FilterUtils;
import edu.nus.tp.web.tweet.ClassifiedTweet;

public class TestClassifier {

	private static final double DELTA = 1e-30;
	BayesClassifier bayesClassifier=null;
	SentiWordClassifier sentiClassifier=null;
	ClassifiedTweet tweet1, tweet2,tweet3,tweet4,testTweetPositive, testTweetNegative;

	@Before
	public void setUp(){

		String tweet1String = "Chinese Beijing Chinese";
		String tweet2String = "Chinese Chinese Shanghai";
		String tweet3String = "Chinese Macao";
		String tweet4String = "Tokyo Japan Chinese";

		String testTweetString="Chinese Chinese Chinese Tokyo Japan";

		tweet1=new ClassifiedTweet(tweet1String, Category.POSITIVE,"");
		tweet2=new ClassifiedTweet(tweet2String, Category.POSITIVE,"");
		tweet3=new ClassifiedTweet(tweet3String, Category.POSITIVE,"");
		tweet4=new ClassifiedTweet(tweet4String, Category.NEGATIVE,"");

		testTweetPositive=new ClassifiedTweet(testTweetString);

		bayesClassifier=new BayesClassifier(new InMemoryPersistence());
		sentiClassifier=new SentiWordClassifier(new RedisPersistence());
		//classifier=new BayesClassifier(new RedisPersistence());
	}

	//@Test
	public void testLearning(){


		bayesClassifier.train(Lists.newArrayList(tweet1,tweet2,tweet3,tweet4));

		assertEquals(0.75, bayesClassifier.getPriorForCategory(Category.POSITIVE),DELTA);
		assertEquals(0.25, bayesClassifier.getPriorForCategory(Category.NEGATIVE),DELTA);

	}

	//@Test
	public void testClassify(){
		bayesClassifier.train(Lists.newArrayList(tweet1,tweet2,tweet3,tweet4));
		ClassifiedTweet classifiedPositiveTweet=bayesClassifier.classify(testTweetPositive);

		assertEquals(Category.POSITIVE,classifiedPositiveTweet.getClassification());

	}

	@Test
	public void testClassifySentiWord(){
		
		ClassifiedTweet classifiedTweet=sentiClassifier.classify(new ClassifiedTweet("Hello I am a good boy. this is super"));
		ClassifiedTweet classifiedNegativeTweet=sentiClassifier.classify(new ClassifiedTweet("Hello I am a bad boy. this is awful"));

		assertEquals(Category.POSITIVE,classifiedTweet.getClassification());


		assertEquals(Category.NEGATIVE,classifiedNegativeTweet.getClassification());
	}
	
	//@Test
	public void testFilter(){

		assertEquals("hello world what a day it is brilliant",
				FilterUtils.stripSpecialCharacters("hello world!! what a day it is. brilliant??????!!!!!!"));

		assertEquals("I am simply amazed by how good #bing social feature is way to go #Microsoft #InLoveWithMicrosoft",
				FilterUtils.stripSpecialCharacters("I am simply amazed by how good #bing social feature is, " +
						"way to go #Microsoft #InLoveWithMicrosoft"));


		assertEquals(Lists.newArrayList("hello", "world", "day", "brilliant"),
				FilterUtils.filterStopWords("hello world what a day it is brilliant"));

		assertEquals(Lists.newArrayList("perfect","perfection","cats","running","ran","cactus","cactuses","community","communities"),
				FilterUtils.filterStopWords("the perfect perfection a cats running ran cactus cactuses community communities"));

		assertEquals(Lists.newArrayList("the", "perfect","perfection","a", "cat","run","run","cactus","cactus","community","community"),
				FilterUtils.lemmatize("the perfect perfection a cats running ran cactus cactuses community communities"));

		assertEquals("hello helloo helo world what a day it is brilliant. How are you?",
				FilterUtils.filterTriplicateCharacters("hello helloo helllllooooo wooooorld what a day it is brilliant. Hooow areee youuuu???"));

	}

}