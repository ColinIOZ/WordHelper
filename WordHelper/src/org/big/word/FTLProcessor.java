package org.big.word;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.big.db.DataReader;
import org.big.taxon.Synonym;
import org.big.taxon.Taxon;

import freemarker.template.*;

public class FTLProcessor {

	private List<Taxon> GetData()
	{
		List<Taxon> taxa=new ArrayList<Taxon>();
		/*
		Taxon a=new Taxon();
		a.setTaxonid("1");
		a.setScientificName("Aix galariculata");
		a.setAuthorship("Linneaus, 1758");
		a.setCName("ԧ��");
		a.setNomRef("Linneaus, 1758, 1-2.");
		a.setRank("species");
		a.setRankWeight(60);
		a.setCitations("");
		a.setCommonNames("ˮ��ˮ��");
		a.setDistribution("����,����");
		List<Synonym>syns= new ArrayList<>();
		Synonym syn=new Synonym();
		syn.setSynid("1");
		syn.setSynonym("Aix galariculata");
		syn.setAuthorship("Linneaus, 1758");
		syn.setCitations("1-2; Zheng, 2012, 3-4.");
		syns.add(syn);
		a.setSynonyms(syns);
		taxa.add(a);*/
		return taxa;
	}
	
	@SuppressWarnings("unchecked")
	public void ProcessFTL() throws SQLException
	{
		List<Taxon> taxa=new DataReader().getTaxa();
		@SuppressWarnings("rawtypes")
		Map dataMap = new HashMap();
		dataMap.put("taxa", taxa);
		Configuration cfg = new Configuration();
		Template temp=null;
		try {
			cfg.setDirectoryForTemplateLoading(
					new File("D:/projects/sp2000/word"));
			cfg.setObjectWrapper(ObjectWrapper.BEANS_WRAPPER);
			cfg.setEncoding(Locale.CHINA, "UTF-8");
			temp = cfg.getTemplate("sp2000_ff.ftl");
			//temp.setOutputEncoding("UTF-8");
			//temp.setEncoding("UTF-8");
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		
		File outFile = new File("D:/projects/sp2000/word/output.ftl");
        Writer out = null;
        try {
            out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outFile), "UTF-8"));
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        
        try {  
            temp.process(dataMap, out);
            out.close();  
        } catch (TemplateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
	}
}
