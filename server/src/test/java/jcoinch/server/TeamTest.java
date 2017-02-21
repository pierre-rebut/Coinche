package jcoinch.server;

import org.junit.Assert;
import org.junit.Test;

public class TeamTest {
	
	@Test
	public void testAddtionScore() {
		Team tm = new Team();
		Assert.assertEquals(tm.getTotalScore(), 0);
		tm.addScore(100);
		Assert.assertEquals(tm.getTotalScore(), 100);
		tm.addScore(100);
		Assert.assertEquals(tm.getTotalScore(), 200);
	}

}
