package org.big.taxon;

public class Synonym {
	public String Synid;
	public String Synonym;
	public String Authorship;
	public String Citations;
	
	public String getSynid() {
		return Synid;
	}
	public void setSynid(String synid) {
		Synid = synid;
	}
	public String getSynonym() {
		return Synonym;
	}
	public void setSynonym(String synonym) {
		Synonym = synonym;
	}
	public String getAuthorship() {
		return Authorship;
	}
	public void setAuthorship(String authorship) {
		Authorship = authorship;
	}
	public String getCitations() {
		return Citations;
	}
	public void setCitations(String citations) {
		Citations = citations;
	}
}
