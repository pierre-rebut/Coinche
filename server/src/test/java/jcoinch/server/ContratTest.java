package jcoinch.server;

import org.junit.Assert;
import org.junit.Test;

public class ContratTest {
	
	@Test(expected=Exception.class)
	public void testCheckContrat()
	{
		Contrat contrat = new Contrat();
		Player pl = new Player("tmp", 0, null, null);
		
		Assert.assertEquals(-1, contrat.checkContrat(pl, "passe"));
		Assert.assertEquals(-1, contrat.checkContrat(pl, "passe"));
		Assert.assertEquals(-1, contrat.checkContrat(pl, "passe"));
		Assert.assertEquals(2, contrat.checkContrat(pl, "passe"));
		contrat.reset();
		Assert.assertEquals(-2, contrat.checkContrat(pl, "coinche"));
	}
	
	@Test(expected=Exception.class)
	public void testCheckContrat2()
	{
		Contrat contrat = new Contrat();
		Player pl = new Player("tmp", 0, null, null);
		
		Assert.assertEquals(-2, contrat.checkContrat(pl, "surcoinche"));
	}
	
	@Test(expected=Exception.class)
	public void testCheckContrat3()
	{
		Contrat contrat = new Contrat();
		Player pl = new Player("tmp", 0, null, null);
		
		Assert.assertEquals(-4, contrat.checkContrat(pl, "80"));
		Assert.assertEquals(-2, contrat.checkContrat(pl, "surcoinche"));
	}
	
	@Test(expected=Exception.class)
	public void testCheckContrat4()
	{
		Contrat contrat = new Contrat();
		Player pl = new Player("tmp", 0, null, null);
		
		Assert.assertEquals(-4, contrat.checkContrat(pl, "80"));
		Assert.assertEquals(-2, contrat.checkContrat(pl, "60"));
	}
	
	@Test
	public void testCheckContrat5()
	{
		Contrat contrat = new Contrat();
		Player pl = new Player("tmp", 0, null, null);
		
		Assert.assertEquals(-4, contrat.checkContrat(pl, "80"));
		Assert.assertEquals(-1, contrat.checkContrat(pl, "passe"));
		Assert.assertEquals(-1, contrat.checkContrat(pl, "passe"));
		Assert.assertEquals(-4, contrat.checkContrat(pl, "100"));
		Assert.assertEquals(-1, contrat.checkContrat(pl, "passe"));
		Assert.assertEquals(-1, contrat.checkContrat(pl, "passe"));
		Assert.assertEquals(1, contrat.checkContrat(pl, "passe"));
	}

}
