package org.big.db;

import static org.junit.Assert.*;

import java.sql.SQLException;

import org.junit.Test;

public class DataReaderTest {

	@Test
	public void testGetTaxa() throws SQLException {
		DataReader dr=new DataReader();
		dr.getTaxa();
	}

	@Test
	public void testReadData() {
		
	}
	
	@Test
	public void testupdateScientificName() throws SQLException {
		String treeid="{887F9FC4-C5D4-42CD-BC4D-C2F697487552}";
		DataReader dr=new DataReader();
		dr.updateScientificName(treeid);
	}
}
