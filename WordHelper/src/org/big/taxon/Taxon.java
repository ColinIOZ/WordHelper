package org.big.taxon;

import java.util.List;

public class Taxon {
	public String taxonid;
	public String ScientificName;
	public String Authorship;
	public String NomRef;
	public String CName;
	public String Rank;
	public int RankWeight;
	public String CommonNames;
	public String Distribution;
	public List<Synonym> Synonyms;
	public String Citations;
	
	public String getTaxonid() {
		return taxonid;
	}

	public void setTaxonid(String taxonid) {
		this.taxonid = taxonid;
	}

	public String getScientificName() {
		return ScientificName;
	}

	public void setScientificName(String scientificName) {
		ScientificName = scientificName;
	}

	public String getAuthorship() {
		return Authorship;
	}

	public void setAuthorship(String authorShip) {
		Authorship = authorShip;
	}

	public String getRank() {
		return Rank;
	}

	public void setRank(String rank) {
		Rank = rank;
	}

	public int getRankWeight() {
		return RankWeight;
	}

	public void setRankWeight(int rankWeight) {
		RankWeight = rankWeight;
	}

	public String getCommonNames() {
		return CommonNames;
	}

	public void setCommonNames(String commonNames) {
		CommonNames = commonNames;
	}

	public List<Synonym> getSynonyms() {
		return Synonyms;
	}

	public void setSynonyms(List<Synonym> synonyms) {
		Synonyms = synonyms;
	}

	public String getCitations() {
		return Citations;
	}

	public void setCitations(String citations) {
		Citations = citations;
	}

	public String getCName() {
		return CName;
	}

	public void setCName(String cName) {
		CName = cName;
	}

	public String getDistribution() {
		return Distribution;
	}

	public void setDistribution(String distribution) {
		Distribution = distribution;
	}

	public String getNomRef() {
		return NomRef;
	}

	public void setNomRef(String nomRef) {
		NomRef = nomRef;
	}
	
}
