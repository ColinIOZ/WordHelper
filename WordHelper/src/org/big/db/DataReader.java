package org.big.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.big.taxon.Synonym;
import org.big.taxon.Taxon;

public class DataReader {

	public HashMap<String,String> refs=new  HashMap<String,String>();
	Set<String> ranks=null;
	
	public DataReader()
	{
		ranks=new TreeSet<String>();
		ranks.add("kingdom");
		ranks.add("phylum");
		ranks.add("class");
		ranks.add("family");
		ranks.add("genus");
		ranks.add("species");
		ranks.add("subsp.");
		ranks.add("forma");
		ranks.add("var.");
	}
	public List<Taxon>getTaxa() throws SQLException
	{
		List<Taxon> taxa =new ArrayList<>();
		//EF162D52-81E5-4279-B35D-E066616D3FBE;{887F9FC4-C5D4-42CD-BC4D-C2F697487552}
		String treeid="{887F9FC4-C5D4-42CD-BC4D-C2F697487552}";
		String rootid="00000000-0000-0000-0000-000000000000";
		//{EE887A07-6277-4474-B904-B501C1685F4D}
		//{43CE950B-2B3D-49C6-83ED-071A73A22043}aves
		String parentid="{B3B3FE44-0CCE-4063-97F4-DF3D4607D836}";//"{50359D71-B76F-4AE1-B579-EECBA6AEB42B}";//{DE80FE5E-475A-4B11-99FF-3484A651068E}";
		Connection con=ConnectionHelper.getConnection();
		
		readData(taxa,parentid,treeid,con);
		for(int i=0;i<taxa.size();i++)
		{
			String taxonid=taxa.get(i).getTaxonid();
			taxa.get(i).setSynonyms(getSynonyms(taxonid,con));
		}
		Statement st=con.createStatement();
		Iterator iter = refs.entrySet().iterator();
		while(iter.hasNext())
		{
			Map.Entry entry=(Map.Entry) iter.next();
			String key=(String) entry.getKey();
			String ref=(String) entry.getValue();
			System.out.println(key+"\t"+ref);
			
			String sql="insert into sp2000_ref_ids (paperid) values('"+key+"')";
			st.executeUpdate(sql);
		}
		st.close();
		con.close();
		return taxa;
	}
	
	public void readData(List<Taxon> taxa, String parentid,String treeid,Connection con) throws SQLException
	{
		
		String sql="Select a.*,isnull(b.Named_Person,'') as author,isnull(b.Named_Date,'') as namedYear,c.ranken,c.power from (taxa a left join species b on a.id=b.taxaid) left join rank_list c on a.rankid=c.id where a.treeid='"+treeid+"' and a.parentid='"+parentid+"' "
				+ "and a.statusid='67280F4A-D8D6-4CAD-BCDA-843866010852' order by a.latin_name";
		Statement st=con.createStatement();
		ResultSet rs=st.executeQuery(sql);
		while(rs.next())
		{
			Taxon taxon=new Taxon();
			String taxonid=rs.getString("id");
			String sciname=rs.getString("fullname").trim();
			String cname=rs.getString("chinese_name").trim();
			String author=rs.getString("author").trim();
			String authorYear=rs.getString("namedYear").trim();
			String rank=rs.getString("ranken");
			int rankw=rs.getInt("power");
			String authorship="";
			authorship=dealAuthorShip(author,authorYear);
			
			taxon.setScientificName(sciname);
			taxon.setTaxonid(taxonid);
			taxon.setAuthorship(authorship);
			taxon.setCName(cname);
			taxon.setRank(rank);
			taxon.setRankWeight(rankw);
			taxon.setCommonNames(getCommonNames(taxonid,con));
			//taxon.setSynonyms(getSynonyms(taxonid,con));
			taxon.setNomRef(getSpeciesCitations(taxonid,con,true));
			taxon.setCitations(getSpeciesCitations(taxonid,con,false));
			taxon.setDistribution(getDistributions(taxonid,con));
			if(ranks.contains(taxon.getRank().toLowerCase()))
			{taxa.add(taxon);}
			System.out.println(sciname+"\t"+cname+"\t"+rank);
			parentid=taxonid;
			readData(taxa,parentid,treeid,con);
		}
		rs.close();
		st.close();
	}
	public String getFullSciname(String pid,Connection con)
	{
		String fullname="";
		String sql="select";
		
		return fullname;
	}
	public String dealAuthorShip(String author,String year)
	{
		String authorship=author;
		
		if(!author.equals(""))
		{
			//authorship=author;
			authorship=authorship.replace("（", "(");
			authorship=authorship.replace("）", ")");
			if(authorship.contains("(")||authorship.contains(")"))
			{
				authorship=authorship.replace("(", "").replace(")", "");
				if(!year.equals(""))
				{
					authorship+=", "+year;
				}
				authorship="("+authorship+")";
			}
			else
			{
				authorship+=year.equals("")?"":(", "+year);
			}
			
		}
		authorship=authorship.trim();
		return authorship;
	}
	public String getCommonNames(String taxonid,Connection con) throws SQLException
	{
		String sql="select * from CommonName where taxaid='"+taxonid+"' and lang='zh-CN' order by comName";
		Statement st=con.createStatement();
		ResultSet rs=st.executeQuery(sql);
		String comNames="";
		while(rs.next())
		{
			comNames+=rs.getString("comname").trim()+"，";
		}
		comNames=comNames.equals("")?"":comNames.substring(0, comNames.length()-1);
		rs.close();
		st.close();
		return comNames;
	}
	public List<Synonym> getSynonyms(String taxonid,Connection con) throws SQLException
	{
		List<Synonym> syns=new ArrayList<>();
		
		String sql="select a.id,a.latin_name,isnull(b.Named_Person,'') as author,isnull(b.Named_Date,'') as namedYear "
				+ "from taxa a left join species b on a.id=b.taxaid where a.synonymof='"+taxonid+"' and "
						+ "a.statusid<>'67280F4A-D8D6-4CAD-BCDA-843866010852' order by b.named_date";
		//String sql="select id,latin_name from taxa where synonymof='"+taxonid+"' and statusid";
		Statement st=con.createStatement();
		ResultSet rs=st.executeQuery(sql);
		
		while(rs.next())
		{
			String synid=rs.getString("id");
			Synonym syn=new Synonym();
			String synonym=rs.getString("latin_name").trim();
			synonym=synonym.replaceAll("  ", " ");
			String author=rs.getString("author");
			String year=rs.getString("namedYear");
			String authorship=dealAuthorShip(author,year);
			syn.setSynid(synid);
			syn.setSynonym(synonym);
			syn.setAuthorship(authorship);
			syn.setCitations(getSynonymCitations(synid,con));
			syns.add(syn);
		}
		
		rs.close();
		st.close();
		
		return syns;
	}
	public String getSynonymCitations(String synid,Connection con) throws SQLException
	{
		String citation="";
		String sql="select a.pcode,a.is_first,b.id,b.source,b.author,b.years from Synonym_Paper a left join papers b on a.paperid=b.id where a.SynID='"+synid+"'";
		Statement st=con.createStatement();
		ResultSet rs=st.executeQuery(sql);
		String original="";
		while(rs.next())
		{
			String paperid=rs.getString("id");
			String source=rs.getString("source");
			if(!refs.containsKey(paperid))
			{
				refs.put(paperid, source);
			}
			if(rs.getBoolean("is_first"))
			{
				original=rs.getString("pcode")==null?"":rs.getString("pcode").trim();
			}
			else
			{
				String author=rs.getString("author").equals("不详")||rs.getString("author").equals("unknown")?"":rs.getString("author").trim();
				String year=rs.getString("years").equals("不详")||rs.getString("years").equals("unknown")?"":rs.getString("years").trim();
				String pcode=rs.getString("pcode")==null?"":rs.getString("pcode").trim();
				String tmp=author+", "+year+", "+pcode+"; ";
				citation+=tmp;
			}
		}
		citation=original+"; "+citation;
		citation=citation.trim();
		citation=citation.substring(0,citation.length()-1);
		st.close();
		rs.close();
		return citation;
	}
	public String getSpeciesCitations(String taxonid,Connection con,boolean nomref) throws SQLException
	{
		String sql="";
		if(nomref)
		{
			sql="select a.*,b.author,b.years,b.id,b.source from species_Paper a left join papers b on a.paperid=b.id "
					+ "where a.taxaid='"+taxonid+"' and is_first=1";
		}
		else
		{
			sql="select a.*,b.author,b.years,b.id,b.source from species_Paper a left join papers b on a.paperid=b.id "
					+ "where a.taxaid='"+taxonid+"' and is_first=0 order by b.years";
		}
		String citation="";
		Statement st=con.createStatement();
		ResultSet rs=st.executeQuery(sql);
		while(rs.next())
		{
			String paperid=rs.getString("id");
			String source=rs.getString("source");
			if(!refs.containsKey(paperid)&&paperid!=null)
			{
				refs.put(paperid, source);
			}
			
			String author=rs.getString("author")==null||rs.getString("author").equals("不详")||rs.getString("author").equals("unknown")?"":rs.getString("author").trim();
			String year=rs.getString("years")==null||rs.getString("years").equals("不详")||rs.getString("years").equals("unknown")?"":rs.getString("years").trim();
			String pcode=rs.getString("pcode")==null?"":rs.getString("pcode").trim();
			String tmp=author+", "+year+", "+pcode+"; ";
			if(author.equals("")&&year.equals(""))
			{
				tmp="";
			}
			else if(!pcode.equals("")&&nomref)
			{
				tmp=author+", "+year+", "+pcode+"; ";
			}
			else
			{
				tmp=author+", "+year+"; ";
			}
			citation+=tmp;
		}
		citation=citation.trim();
		citation=citation.equals("")?"":citation.substring(0,citation.length()-1);
		rs.close();
		st.close();
		return citation;
	}
	public String getDistributions(String taxonid,Connection con) throws SQLException
	{
		String dis="";
		System.out.println(taxonid);
		//String sql="select distinct(provinceid) as provinceid,b.* from Location_Species_Detail a left join Location_province b on a.provinceid=b.id where a.taxaid='"+taxonid+"'";
		String sql="select * from Location_province where id in (select provinceid from Location_Species_Detail where taxaid='"+taxonid+"') order by cn";
		Statement st=con.createStatement();
		ResultSet rs=st.executeQuery(sql);
		while(rs.next())
		{
			String cn=rs.getString("cn");
			String code=rs.getString("codechar");
			String item="";
			if(rs.getString("type").equals("s"))
			{
				item=cn;
			}
			else
			{
				item=cn+"("+code+")";
			}
			dis+=item+"，";
		}
		rs.close();
		st.close();
		dis=dis.trim();
		dis=dis.equals("")?"":dis.substring(0,dis.length()-1);
		if(dis.equalsIgnoreCase("全中国()"))
		{
			dis="全国分布";
		}
		sql="select * from Description_Species where Description_Type_ID='9895D860-EACA-4B5D-8875-3636366FF29A' and taxaid='"+taxonid+"'";
		st=con.createStatement();
		rs=st.executeQuery(sql);
		while(rs.next())
		{
			dis+="；"+rs.getString("Description_Content").trim();
		}
		rs.close();
		st.close();
		return dis;
	}
	public void updateScientificName(String treeid) throws SQLException
	{
		String sql="select a.*,b.ranken,b.power from taxa a left join rank_list b on a.rankid=b.id"
				+ " where a.treeid='"+treeid+"' and a.statusid='67280F4A-D8D6-4CAD-BCDA-843866010852'";
		Connection con=ConnectionHelper.getConnection();
		Statement st=con.createStatement();
		ResultSet rs=st.executeQuery(sql);
		int count=1;
		while(rs.next())
		{
			String id=rs.getString("id");
			String sciname=rs.getString("latin_name").trim();
			int weight=rs.getInt("power");
			String ranken=rs.getString("ranken");
			String pid=rs.getString("parentid");
			String cname=rs.getString("chinese_name");
			//System.out.println(sciname+"---"+cname);
			if(weight>=60)
			{
				Statement stt=con.createStatement();
				String rank="";
				String genus="";
				String epithet="";
				while(!rank.equals("genus"))
				{
					sql="select a.id,a.parentid,a.latin_name,b.ranken,b.power from taxa a left join rank_list b on a.rankid=b.id"
							+ " where a.id='"+pid+"' and a.statusid='67280F4A-D8D6-4CAD-BCDA-843866010852'";
					
					ResultSet rss=stt.executeQuery(sql);
					if(rss.next())
					{
						if(rss.getString("ranken").equals("genus"))
						{
							genus=rss.getString("latin_name").trim();
						}
						if(rss.getString("ranken").equals("species"))
						{
							epithet=rss.getString("latin_name").trim();
						}
						rank=rss.getString("ranken");
						pid=rss.getString("parentid");
					}
					rss.close();
					
				}
				stt.close();
				sciname=genus+" "+epithet+" "+sciname;
				sciname=sciname.replace("  ", " ");
			}
			System.out.println(count++);
			System.out.println(sciname+"\t"+cname);
			sql="update taxa set fullname='"+sciname+"' where id='"+id+"'";
			Statement stt=con.createStatement();
			stt.executeUpdate(sql);
			stt.close();
		}
		rs.close();
		st.close();
		con.close();
	}
}
